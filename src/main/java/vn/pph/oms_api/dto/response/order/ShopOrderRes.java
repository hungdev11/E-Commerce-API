package vn.pph.oms_api.dto.response.order;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShopOrderRes {
    private Long shopId;

    @Builder.Default
    BigDecimal discount = BigDecimal.ZERO;

    BigDecimal price;
    BigDecimal newPrice;
    private List<Item> items;
}
