package vn.pph.oms_api.service;

import vn.pph.oms_api.dto.request.AttributeCreationRequest;
import vn.pph.oms_api.dto.response.AttributeResponse;

public interface AttributeService {
    AttributeResponse createAttribute (AttributeCreationRequest request);
}
