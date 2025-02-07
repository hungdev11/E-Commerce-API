package vn.pph.oms_api.dto.request.order;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShopDiscount {
    private Long shopId;
    private Long discountId;
    private String codeId;
}

