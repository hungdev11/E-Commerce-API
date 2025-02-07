package vn.pph.oms_api.service.Impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.pph.oms_api.dto.request.order.CompleteOrderRequest;
import vn.pph.oms_api.dto.response.order.*;
import vn.pph.oms_api.exception.AppException;
import vn.pph.oms_api.exception.ErrorCode;
import vn.pph.oms_api.model.Cart;
import vn.pph.oms_api.model.CartProduct;
import vn.pph.oms_api.model.Inventory;
import vn.pph.oms_api.model.Order.Checkout;
import vn.pph.oms_api.model.Order.Order;
import vn.pph.oms_api.model.Order.OrderProduct;
import vn.pph.oms_api.model.ReservationItem;
import vn.pph.oms_api.model.sku.Product;
import vn.pph.oms_api.model.sku.Sku;
import vn.pph.oms_api.repository.*;
import vn.pph.oms_api.service.CheckoutService;
import vn.pph.oms_api.service.OrderService;
import vn.pph.oms_api.utils.ProductUtils;
import vn.pph.oms_api.utils.UserUtils;

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

                orderProducts.add(OrderProduct.builder()
                        .skuNo(item.getSkuNo())
                        .price(sku.getSkuPrice())
                        .productId(item.getProductId())
                        .quantity(item.getQuantity())
                        .build());
            }
        }
        Order order = Order.builder()
                .user(userRepository.findById(request.getCheckoutRequest().getUserId()).get())
                .shipping(request.getAddress())
                .paymentMethod(request.getPaymentMethod())
                .checkout(Checkout.builder()
                        .totalPrice(checkoutResponse.getTotalPrice())
                        .feeShip(checkoutResponse.getFeeShip())
                        .totalCheckout(checkoutResponse.getTotalCheckout())
                        .totalDiscount(checkoutResponse.getTotalDiscount())
                        .build())
                .orderProducts(orderProducts)
                .build();

        for (OrderProduct op : orderProducts) {
            op.setOrder(order);
        }
        Order savedOrder = orderRepository.save(order);
        savedOrder.setTrackingOrder(savedOrder.getId() + LocalDate.now().toString().replace("-", ""));
        orderRepository.save(savedOrder);

        List<OrderProductResponse> orderProductList = savedOrder.getOrderProducts()
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
}
