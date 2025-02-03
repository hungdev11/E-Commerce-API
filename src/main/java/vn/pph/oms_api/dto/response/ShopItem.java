package vn.pph.oms_api.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ShopItem {
    private int quantity;
    private BigDecimal price;
    private Long productId;
}
