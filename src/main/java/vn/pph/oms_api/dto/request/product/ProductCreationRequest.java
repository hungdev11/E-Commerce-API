package vn.pph.oms_api.dto.request.product;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class ProductCreationRequest {
    private Long shopId;
    private String productName;
    private String productThumb;
    private String productDesc;
    private List<SkuCreationRequest> skuList;
}
