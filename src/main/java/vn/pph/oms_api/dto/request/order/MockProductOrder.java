package vn.pph.oms_api.dto.request.order;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class  MockProductOrder {
    private Long shopId;
    private Long productId;
    private BigDecimal price;
    private int quantity;
}
