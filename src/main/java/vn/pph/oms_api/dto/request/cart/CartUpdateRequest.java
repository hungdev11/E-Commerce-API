package vn.pph.oms_api.dto.request.cart;

import lombok.*;

import java.util.List;
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartUpdateRequest {
    private Long cartId;
    private List<OrderShop> orderShopsList;
}
