package vn.pph.oms_api.dto.response;

import lombok.*;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CartUpdateResponse {
    private Long shopId;
    private Long productId;
    private int quantity;
    private int cardCount;
}
