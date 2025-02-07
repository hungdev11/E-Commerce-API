package vn.pph.oms_api.dto.request.order;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemProduct {
    private Long productId;
    private String skuCode;
    private int quantity;
    private double price;
}

