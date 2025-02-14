package vn.pph.oms_api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import vn.pph.oms_api.dto.request.order.CompleteOrderRequest;
import vn.pph.oms_api.dto.request.order.ReviewOrderRequest;
import vn.pph.oms_api.dto.response.APIResponse;
import vn.pph.oms_api.service.CheckoutService;
import vn.pph.oms_api.service.OrderService;
import vn.pph.oms_api.utils.OrderStatus;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
@Slf4j
public class OrderController {
    private final CheckoutService checkoutService;
    private final OrderService orderService;

    @PostMapping("/checkout")
    public APIResponse<?> review(@RequestBody ReviewOrderRequest request) {
        log.info("Request received to review order: {}", request);
        return APIResponse.builder()
                .code(200)
                .message("Review OK")
                .data(checkoutService.review(request, true))
                .build();
    }

    @PostMapping("/complete")
    public APIResponse<?> completeOrder(@RequestBody CompleteOrderRequest request) {
        log.info("Request received to complete order for user: {}", request.getCheckoutRequest().getUserId());
        return APIResponse.builder()
                .code(200)
                .message("Create order OK")
                .data(orderService.completeOrder(request))
                .build();
    }

    @GetMapping("/get/{orderId}")
    public APIResponse<?> getOrderById(@PathVariable Long orderId) {
        log.info("Fetching order details for orderId: {}", orderId);
        return APIResponse.builder()
                .code(200)
                .message("Order details fetched successfully")
                .data(orderService.getOrderById(orderId))
                .build();
    }

    @GetMapping("/user-orders")
    public APIResponse<?> getUserOrders(@RequestParam Long userId,
                                        @RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "2") int size) {
        log.info("Fetching orders for userId: {} | Page: {} | Size: {}", userId, page, size);
        return APIResponse.builder()
                .code(200)
                .message("User orders fetched successfully")
                .data(orderService.getUserOrders(userId, page, size))
                .build();
    }

    @PostMapping("/cancel")
    public APIResponse<?> cancelOrder(@RequestParam Long userId,
                                      @RequestParam Long orderId) {
        log.info("User {} is requesting to cancel order {}", userId, orderId);
        orderService.cancelOrder(userId, orderId);
        return APIResponse.builder()
                .code(200)
                .message("Order canceled successfully")
                .build();
    }

    @PostMapping("/update-status")
    public APIResponse<?> updateOrderStatus(@RequestParam Long orderId,
                                            @RequestParam Long shopId,
                                            @RequestParam OrderStatus status) {
        log.info("Shop {} is updating status of order {} to {}", shopId, orderId, status);
        orderService.updateOrderStatus(orderId, shopId, status);
        return APIResponse.builder()
                .code(200)
                .message("Order status updated successfully")
                .build();
    }
}
