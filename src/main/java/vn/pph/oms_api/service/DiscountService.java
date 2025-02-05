package vn.pph.oms_api.service;

import vn.pph.oms_api.dto.request.discount.DiscountCreationRequest;
import vn.pph.oms_api.dto.request.discount.RequestGetAmountDiscount;
import vn.pph.oms_api.dto.response.cart.AmountRequest;
import vn.pph.oms_api.dto.response.discount.DiscountCreationResponse;
import vn.pph.oms_api.dto.response.PageResponse;
import vn.pph.oms_api.utils.DiscountStatus;

public interface DiscountService {
    DiscountCreationResponse createNewDiscount(DiscountCreationRequest request);
    PageResponse<?> getAllDiscountsForShop(Long shopId, int page, int size, DiscountStatus status);
    AmountRequest getDiscountAmount(RequestGetAmountDiscount request);
    boolean deleteDiscount(Long shopId, Long codeId);
    boolean cancelDiscount(Long shopId, Long codeId, Long userId);
}
