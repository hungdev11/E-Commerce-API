package vn.pph.oms_api.dto.response;

import lombok.*;
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttributeResponse {
    private String name;
    private String attrType;
}
