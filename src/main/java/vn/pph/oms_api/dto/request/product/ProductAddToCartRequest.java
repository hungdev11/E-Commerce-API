package vn.pph.oms_api.dto.request.product;

import jakarta.validation.constraints.Min;
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
    @Min(value = 1, message = "Quantity must greater or equal 1")
    private int quantity;
}
