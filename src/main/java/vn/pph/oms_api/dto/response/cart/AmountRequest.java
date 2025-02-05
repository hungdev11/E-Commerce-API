package vn.pph.oms_api.dto.response.cart;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class AmountRequest {
    private Long userId;
    private BigDecimal originPrice;
    private BigDecimal discountPrice;
}
