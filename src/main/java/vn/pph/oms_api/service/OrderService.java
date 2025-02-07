package vn.pph.oms_api.service;

import vn.pph.oms_api.dto.request.order.CompleteOrderRequest;

public interface OrderService {
    Object completeOrder (CompleteOrderRequest request);
}
