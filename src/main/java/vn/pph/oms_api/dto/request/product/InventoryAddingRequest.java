package vn.pph.oms_api.dto.request.product;

import jakarta.validation.constraints.Min;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class InventoryAddingRequest {
    private Long productId;
    private String skuNumber;
    private String location;
    @Min(value = 1, message = "Quantity must be greater than 0")
    private int stock;
    private Long shopId;
}
