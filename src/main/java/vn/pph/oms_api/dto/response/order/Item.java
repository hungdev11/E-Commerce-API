package vn.pph.oms_api.dto.response.order;

import lombok.*;

import java.math.BigDecimal;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Item {
    private BigDecimal price;
    private int quantity;
    private Long productId;
}
