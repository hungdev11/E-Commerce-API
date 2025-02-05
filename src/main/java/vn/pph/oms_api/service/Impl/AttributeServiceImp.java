package vn.pph.oms_api.service.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.pph.oms_api.dto.request.product.AttributeCreationRequest;
import vn.pph.oms_api.dto.response.product.AttributeResponse;
import vn.pph.oms_api.exception.AppException;
import vn.pph.oms_api.exception.ErrorCode;
import vn.pph.oms_api.model.sku.Attribute;
import vn.pph.oms_api.repository.AttributeRepository;
import vn.pph.oms_api.service.AttributeService;

@Service
@RequiredArgsConstructor
public class AttributeServiceImp implements AttributeService {
    private final AttributeRepository attributeRepository;

    @Override
    public AttributeResponse createAttribute(AttributeCreationRequest request) {
        if (attributeRepository.existsByName(request.getName())) {
            throw new AppException(ErrorCode.ATTR_EXISTED);
        }
        Attribute attribute = Attribute.builder()
                .name(request.getName())
                .attrType(request.getAttrType())
                .description(request.getDescription())
                .build();
        attributeRepository.save(attribute);
        return AttributeResponse.builder()
                .name(attribute.getName())
                .attrType(attribute.getAttrType())
                .build();
    }
}
