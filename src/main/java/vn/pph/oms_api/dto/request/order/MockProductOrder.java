package vn.pph.oms_api.dto.request.order;

import lombok.*;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class  MockProductOrder {
    private Long shopId;
    private Long productId;
    private BigDecimal price;
    private int quantity;
}
