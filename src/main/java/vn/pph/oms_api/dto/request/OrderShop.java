package vn.pph.oms_api.dto.request;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class OrderShop {
    private Long shopId;
    public List<CartItem> items;
}
