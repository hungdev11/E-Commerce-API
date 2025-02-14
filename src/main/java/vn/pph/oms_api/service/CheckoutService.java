package vn.pph.oms_api.service;

import vn.pph.oms_api.dto.request.order.ReviewOrderRequest;
import vn.pph.oms_api.dto.response.order.CheckoutResponse;

public interface CheckoutService {
    CheckoutResponse review(ReviewOrderRequest request, boolean justReview);
}
