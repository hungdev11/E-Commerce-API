package vn.pph.oms_api.dto.request.order;

import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewOrderRequest {
    private Long cartId;
    private Long userId;
    private List<ShopOrder> shopOrders;
}



