package vn.pph.oms_api.service;

import org.springframework.stereotype.Service;
import vn.pph.oms_api.dto.request.DiscountCreationRequest;
import vn.pph.oms_api.dto.response.DiscountCreationResponse;

public interface DiscountService {
    DiscountCreationResponse createNewDiscount(DiscountCreationRequest request);
}
