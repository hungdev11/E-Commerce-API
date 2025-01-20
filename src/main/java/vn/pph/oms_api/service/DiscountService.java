package vn.pph.oms_api.service;

import org.springframework.stereotype.Service;
import vn.pph.oms_api.dto.request.DiscountCreationRequest;
import vn.pph.oms_api.dto.request.RequestGetAmountDiscount;
import vn.pph.oms_api.dto.response.AmountRequest;
import vn.pph.oms_api.dto.response.DiscountCreationResponse;
import vn.pph.oms_api.dto.response.PageResponse;
import vn.pph.oms_api.utils.DiscountStatus;

public interface DiscountService {
    DiscountCreationResponse createNewDiscount(DiscountCreationRequest request);
    PageResponse<?> getAllDiscountsForShop(Long shopId, int page, int size, DiscountStatus status);
    AmountRequest getDiscountAmount(RequestGetAmountDiscount request);
}
