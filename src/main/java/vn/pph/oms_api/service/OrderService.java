package vn.pph.oms_api.service;

import vn.pph.oms_api.dto.request.order.CompleteOrderRequest;
import vn.pph.oms_api.dto.response.order.OrderResponse;

public interface OrderService {
    OrderResponse completeOrder (CompleteOrderRequest request);
}
