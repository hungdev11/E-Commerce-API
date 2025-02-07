package vn.pph.oms_api.dto.request.order;

import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShopOrder {
    private Long shopId;
    private List<ShopDiscount> shopDiscounts;
    private List<ItemProduct> itemProducts;
}

