package vn.pph.oms_api.dto.request.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemProduct {
    private Long productId;
    private String skuCode;
    private int quantity;
    private double price;
}

