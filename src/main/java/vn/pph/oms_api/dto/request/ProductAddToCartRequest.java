package vn.pph.oms_api.dto.request;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ProductAddToCartRequest {
    private Long userId;
    private Long productId;
    private Long shopId;
    private int quantity;
}
