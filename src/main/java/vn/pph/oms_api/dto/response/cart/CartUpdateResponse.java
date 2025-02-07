package vn.pph.oms_api.dto.response.cart;

import lombok.*;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CartUpdateResponse {
    private Long shopId;
    private Long productId;
    private String skuNo;
    private int quantity;
    private int cardCount;
}
