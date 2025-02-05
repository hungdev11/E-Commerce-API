package vn.pph.oms_api.dto.request.cart;

import lombok.*;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class CartItem {
    private int oldQuantity;
    private int newQuantity;
    private BigDecimal price;
    private Long productId;
}
