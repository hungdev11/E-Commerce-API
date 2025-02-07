package vn.pph.oms_api.service.Impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.pph.oms_api.dto.request.order.CompleteOrderRequest;
import vn.pph.oms_api.dto.response.order.CheckoutResponse;
import vn.pph.oms_api.dto.response.order.Item;
import vn.pph.oms_api.dto.response.order.ShopOrderRes;
import vn.pph.oms_api.exception.AppException;
import vn.pph.oms_api.exception.ErrorCode;
import vn.pph.oms_api.model.Cart;
import vn.pph.oms_api.model.CartProduct;
import vn.pph.oms_api.model.Inventory;
import vn.pph.oms_api.model.Order.Checkout;
import vn.pph.oms_api.model.Order.Order;
import vn.pph.oms_api.model.Order.OrderProduct;
import vn.pph.oms_api.model.ReservationItem;
import vn.pph.oms_api.model.sku.Sku;
import vn.pph.oms_api.repository.*;
import vn.pph.oms_api.service.CheckoutService;
import vn.pph.oms_api.service.OrderService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderServiceImp implements OrderService {

    private final CartRepository cartRepository;
    private final CheckoutService checkoutService;
    private final InventoryRepository inventoryRepository;
    private final SkuRepository skuRepository;
    private final OrderRepository orderRepository;
    private final CartProductRepository cartProductRepository;

    @Override
    @Transactional
    public Object completeOrder(CompleteOrderRequest request) {
        log.info("Starting order completion for user {}", request.getCheckoutRequest().getUserId());

        CheckoutResponse checkoutResponse = checkoutService.review(request.getCheckoutRequest());
        List<ShopOrderRes> shopOrders = checkoutResponse.getShopOrders();
        List<OrderProduct> orderProducts = new ArrayList<>();
        Order savedOrder = null;

        for (ShopOrderRes shopOrder : shopOrders) {
            List<Item> items = shopOrder.getItems();
            for (Item item : items) {
                log.info("Processing item SKU: {} for user {}", item.getSkuNo(), request.getCheckoutRequest().getUserId());

                Sku sku = skuRepository.findBySkuNo(item.getSkuNo())
                        .orElseThrow(() -> new AppException(ErrorCode.SKU_NOT_FOUND));

                Inventory inventory = inventoryRepository.findBySku(sku)
                        .orElseThrow(() -> new AppException(ErrorCode.INVENTORY_NOT_FOUND));

                synchronized (this) { // Lock ở mức ứng dụng, tránh lỗi khi không dùng DB lock
                    if (inventory.getStock() < item.getQuantity()) {
                        log.warn("Not enough stock for SKU: {} (Requested: {}, Available: {})",
                                item.getSkuNo(), item.getQuantity(), inventory.getStock());
                        throw new AppException(ErrorCode.INVENTORY_OUT_OF_STOCK);
                    }

                    log.info("Reserving stock for SKU: {} (Quantity: {})", item.getSkuNo(), item.getQuantity());
                    inventory.getReservations().add(ReservationItem.builder()
                            .cartId(request.getCheckoutRequest().getCartId())
                            .createTime(LocalDateTime.now())
                            .quantity(item.getQuantity())
                            .inventory(inventory)
                            .build());
                }

                orderProducts.add(OrderProduct.builder()
                        .skuNo(item.getSkuNo())
                        .price(sku.getSkuPrice())
                        .productId(item.getProductId())
                        .quantity(item.getQuantity())
                        .build());
            }

            Order order = Order.builder()
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

            savedOrder = orderRepository.save(order);
            for (OrderProduct op : orderProducts) {
                op.setOrder(savedOrder);
            }
        }

        List<OrderProduct> orderProductList = savedOrder.getOrderProducts();
        Cart cart = cartRepository.findByUserId(request.getCheckoutRequest().getUserId())
                .orElseThrow(() -> {
                    log.error("Cart not found for user {}", request.getCheckoutRequest().getUserId());
                    return new AppException(ErrorCode.CART_NOT_FOUND);
                });

        Set<String> orderSkuNos = orderProductList.stream()
                .map(OrderProduct::getSkuNo)
                .collect(Collectors.toSet());

        cart.getProducts().removeIf(cartProduct -> orderSkuNos.contains(cartProduct.getSkuNo()));

        log.info("Removed ordered products from cart for user {}", request.getCheckoutRequest().getUserId());
        return savedOrder;
    }
}
