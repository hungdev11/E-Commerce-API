package vn.pph.oms_api.model;

import lombok.*;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class CartProduct {
    private Long productId;
    private Long shopId;
    private int quantity;
}
