package vn.pph.oms_api.dto.response.product;


import lombok.*;

import java.math.BigDecimal;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SkuResponse {
    private String skuNo;
    private String skuName;
    private String skuDescription;
    private boolean isDefault;
    private Integer skuStock;
    private BigDecimal skuPrice;
}
