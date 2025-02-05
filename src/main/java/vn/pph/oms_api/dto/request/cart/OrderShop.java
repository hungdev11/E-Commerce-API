package vn.pph.oms_api.dto.request.cart;

import lombok.*;
import vn.pph.oms_api.dto.request.cart.CartItem;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class OrderShop {
    private Long shopId;
    public List<CartItem> items;
}
