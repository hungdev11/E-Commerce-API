package vn.pph.oms_api.dto.request.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShopOrder {
    private Long shopId;
    private List<ShopDiscount> shopDiscounts;
    private List<ItemProduct> itemProducts;
}

