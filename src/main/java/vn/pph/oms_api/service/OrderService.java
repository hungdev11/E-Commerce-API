package vn.pph.oms_api.service;

import vn.pph.oms_api.dto.request.order.CompleteOrderRequest;
import vn.pph.oms_api.dto.response.PageResponse;
import vn.pph.oms_api.dto.response.order.OrderResponse;
import vn.pph.oms_api.utils.OrderStatus;

public interface OrderService {
    OrderResponse completeOrder (CompleteOrderRequest request);
    OrderResponse getOrderById(Long orderId);
    PageResponse<?> getUserOrders(Long userId, int page, int size);
    void cancelOrder(Long userId, Long orderId);
    void updateOrderStatus(Long orderId, Long shopId, OrderStatus status);
}
