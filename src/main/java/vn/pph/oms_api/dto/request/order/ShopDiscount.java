package vn.pph.oms_api.dto.request.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShopDiscount {
    private Long shopId;
    private Long discountId;
    private String codeId;
}

