package vn.pph.oms_api.dto.request.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class AttributeCreationRequest {
    String name;
    String attrType;
    String description;
}
