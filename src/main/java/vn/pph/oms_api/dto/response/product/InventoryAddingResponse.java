package vn.pph.oms_api.dto.response.product;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class InventoryAddingResponse {
    private String skuNumber;
    private String location;
    private int stock;
}
