package vn.pph.oms_api.service;

import vn.pph.oms_api.dto.request.product.AttributeCreationRequest;
import vn.pph.oms_api.dto.response.product.AttributeResponse;

public interface AttributeService {
    AttributeResponse createAttribute (AttributeCreationRequest request);
}
