package vn.pph.oms_api.service.Impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.pph.oms_api.dto.request.order.CompleteOrderRequest;
import vn.pph.oms_api.dto.response.PageResponse;
import vn.pph.oms_api.dto.response.order.*;
import vn.pph.oms_api.dto.response.product.ProductResponse;
import vn.pph.oms_api.exception.AppException;
import vn.pph.oms_api.exception.ErrorCode;
import vn.pph.oms_api.model.*;
import vn.pph.oms_api.model.Order.Checkout;
import vn.pph.oms_api.model.Order.Order;
import vn.pph.oms_api.model.Order.OrderProduct;
import vn.pph.oms_api.model.sku.Product;
import vn.pph.oms_api.model.sku.Sku;
import vn.pph.oms_api.repository.*;
import vn.pph.oms_api.service.CheckoutService;
import vn.pph.oms_api.service.OrderService;
import vn.pph.oms_api.utils.OrderStatus;
import vn.pph.oms_api.utils.ProductUtils;
import vn.pph.oms_api.utils.UserUtils;
import vn.pph.oms_api.utils.Utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderServiceImp implements OrderService {
    UserRepository userRepository;
    UserUtils userUtils;
    CheckoutService checkoutService;
    InventoryRepository inventoryRepository;
    SkuRepository skuRepository;
    OrderRepository orderRepository;

    @Override
    @Transactional
    public OrderResponse completeOrder(CompleteOrderRequest request) {
        log.info("Check cart of user {}", request.getCheckoutRequest().getUserId());

        Cart cart = userUtils.checkCartOfUser(request.getCheckoutRequest().getUserId());
        if (!cart.getId().equals(request.getCheckoutRequest().getCartId())) {
            log.error("cart user: {}, cart request: {}", cart.getId(), request.getCheckoutRequest().getCartId());
            throw new AppException(ErrorCode.CART_NOT_FOUND);
        }

        log.info("Starting order completion for user {}", request.getCheckoutRequest().getUserId());

        CheckoutResponse checkoutResponse = checkoutService.review(request.getCheckoutRequest());
        // check product, sku, shop is OK
        List<ShopOrderRes> shopOrders = checkoutResponse.getShopOrders();
        List<OrderProduct> orderProducts = new ArrayList<>();
        for (ShopOrderRes shopOrder : shopOrders) {
            if (shopOrder.getItems().isEmpty()) {
                log.info("Order of shop {} must have at least one product", shopOrder.getShopId());
                throw new AppException(ErrorCode.SOME_THING_WENT_WRONG);
            }
            List<OrderProduct> shopProducts = new ArrayList<>();
            List<Item> items = shopOrder.getItems();
            for (Item item : items) {
                log.info("Processing item SKU: {} for user {}", item.getSkuNo(), request.getCheckoutRequest().getUserId());
                Sku sku = skuRepository.findBySkuNo(item.getSkuNo())
                        .orElseThrow(() -> new AppException(ErrorCode.SKU_NOT_FOUND));

                // Have multiple location so .... Simulate get location have enough stock to handle, decreasing order
                List<Inventory> inventories = inventoryRepository.findBySku(sku);
                inventories.sort(Comparator.comparingInt(Inventory::getStock).reversed());

                synchronized (this) { // Lock ở mức ứng dụng
                    if (inventories.isEmpty() || inventories.get(0).getStock() < item.getQuantity()) {
                        log.error("Sku doesn't have inventory");
                        throw new AppException(ErrorCode.INVENTORY_OUT_OF_STOCK);
                    }

                    Inventory inventory = inventories.get(0);
                    if (inventory.getStock() < item.getQuantity()) {
                        log.warn("Not enough stock for SKU: {} (Requested: {}, Available: {})",
                                item.getSkuNo(), item.getQuantity(), inventory.getStock());
                        throw new AppException(ErrorCode.INVENTORY_OUT_OF_STOCK);
                    }
                    log.info("Reserving stock for SKU: {} (Quantity: {})", item.getSkuNo(), item.getQuantity());
                    inventory.setStock(inventory.getStock()- item.getQuantity());
                    inventory.getReservations().add(ReservationItem.builder()
                            .cartId(request.getCheckoutRequest().getCartId())
                            .createTime(LocalDateTime.now())
                            .quantity(item.getQuantity())
                            .inventory(inventory)
                            .build());
                    inventoryRepository.save(inventory);
                }
                shopProducts.add(OrderProduct.builder()
                        .skuNo(item.getSkuNo())
                        .price(sku.getSkuPrice())
                        .productId(item.getProductId())
                        .quantity(item.getQuantity())
                        .build());
            }
            Order order = createOrder(shopOrder, checkoutResponse, request, shopProducts);
            for (OrderProduct op : shopProducts) {
                op.setOrder(order);
            }
            Order savedOrder = orderRepository.save(order);
            savedOrder.setTrackingOrder(savedOrder.getId() + LocalDate.now().toString().replace("-", ""));
            orderRepository.save(savedOrder);

            orderProducts.addAll(savedOrder.getOrderProducts());
        }

        List<OrderProductResponse> orderProductList = orderProducts
                .stream()
                .map(op -> OrderProductResponse.builder()
                        .productId(op.getProductId())
                        .skuNo(op.getSkuNo())
                        .price(op.getPrice())
                        .quantity(op.getQuantity())
                        .build())
                .toList();

        Set<String> orderSkuNos = orderProductList.stream()
                .map(OrderProductResponse::getSkuNo)
                .collect(Collectors.toSet());

        cart.getProducts().removeIf(cartProduct -> orderSkuNos.contains(cartProduct.getSkuNo()));

        log.info("Removed ordered products from cart for user {}", request.getCheckoutRequest().getUserId());
        Checkout checkout = Checkout.builder()
                .totalPrice(checkoutResponse.getTotalPrice())
                .feeShip(checkoutResponse.getFeeShip())
                .totalDiscount(checkoutResponse.getTotalDiscount())
                .totalCheckout(checkoutResponse.getTotalCheckout())
                .build();
        return OrderResponse.builder()
                .userId(request.getCheckoutRequest().getUserId())
                .checkout(checkout)
                .shipping(request.getAddress())
                .paymentMethod(request.getPaymentMethod())
                .orderProducts(orderProductList)
                .build();
    }

    @Override
    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository
                .findById(orderId)
                .orElseThrow(()-> new AppException(ErrorCode.ORDER_NOT_FOUND));
        List<OrderProductResponse> responses = order.getOrderProducts().stream()
                .map(op -> OrderProductResponse.builder()
                        .productId(op.getProductId())
                        .skuNo(op.getSkuNo())
                        .price(op.getPrice())
                        .quantity(op.getQuantity())
                        .build())
                .toList();
        return OrderResponse.builder()
                .shopId(order.getShopId())
                .userId(order.getUser().getId())
                .paymentMethod(order.getPaymentMethod())
                .status(order.getStatus())
                .shipping(order.getShipping())
                .checkout(order.getCheckout())
                .orderProducts(responses)
                .build();
    }

    @Override
    public PageResponse<?> getUserOrders(Long userId, int page, int size) {
        if (page < 0) {
            log.warn("Invalid page number: {}. Defaulting to 0.", page);
            page = 0;
        }

        log.info("Fetching orders for userId: {} | Page: {} | Size: {}", userId, page, size);

        User user = userUtils.checkUserExists(userId);
        Pageable pageable = PageRequest.of(page, size);

        Page<Order> orderPage = orderRepository.findByUser(user, pageable);

        if (orderPage.isEmpty()) {
            log.info("No orders found for user {}", userId);
        } else {
            log.info("Total orders found: {} | Current Page: {} / {}",
                    orderPage.getTotalElements(), orderPage.getNumber() + 1, orderPage.getTotalPages());
        }

        List<OrderResponse> orderResponses = orderPage.getContent().stream()
                .map(op -> this.getOrderById(op.getId()))
                .toList();

        return PageResponse.<List<OrderResponse>>builder()
                .page(orderPage.getNumber())
                .size(orderPage.getSize())
                .total(orderPage.getTotalPages())
                .items(orderResponses)
                .build();
    }



    @Override
    public void cancelOrder(Long userId, Long orderId) {
        User user = userUtils.checkUserExists(userId);
        int orderIdx = user.getOrders().stream().map(BaseEntity::getId).toList().indexOf(orderId);
        if (orderIdx == -1) {
            throw new AppException(ErrorCode.ORDER_NOT_BELONG_TO_USER);
        }
        Order order = user.getOrders().get(orderIdx);
        if (!(order.getStatus().equals(OrderStatus.PENDING) || order.getStatus().equals(OrderStatus.CONFIRMED))) {
            throw new AppException(ErrorCode.ORDER_STATUS_NOT_CHANGEABLE);
        }
        order.setStatus(OrderStatus.CANCELED);
        orderRepository.save(order);
    }

    @Override
    public void updateOrderStatus(Long orderId, Long shopId, OrderStatus status) {
        User shop = userUtils.checkUserExists(shopId);
        Order order = orderRepository.findById(orderId).orElseThrow(()->new AppException(ErrorCode.ORDER_NOT_FOUND));
        if (!order.getShopId().equals(shop.getId())) {
            throw new AppException(ErrorCode.ORDER_NOT_BELONG_TO_SHOP);
        }
        order.setStatus(status);
        orderRepository.save(order);
    }

    private Order createOrder(ShopOrderRes shopOrder, CheckoutResponse checkoutResponse, CompleteOrderRequest request, List<OrderProduct> shopProducts) {
        return Order.builder()
                .shopId(shopOrder.getShopId())
                .user(userRepository.findById(request.getCheckoutRequest().getUserId()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND)))
                .shipping(request.getAddress())
                .paymentMethod(request.getPaymentMethod())
                .checkout(Checkout.builder()
                        .totalPrice(shopOrder.getPrice())
                        .feeShip(checkoutResponse.getFeeShip())
                        .totalCheckout(shopOrder.getNewPrice())
                        .totalDiscount(shopOrder.getPrice().subtract(shopOrder.getNewPrice()))
                        .build())
                .orderProducts(shopProducts)
                .build();
    }

}
