package vn.pph.oms_api.dto.request.cart;

import lombok.*;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem {
    private int oldQuantity;
    private int newQuantity;
    private BigDecimal price;
    private Long productId;
    private String skuNo;
}
