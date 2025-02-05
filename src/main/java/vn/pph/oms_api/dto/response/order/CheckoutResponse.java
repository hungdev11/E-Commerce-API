package vn.pph.oms_api.dto.response.order;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CheckoutResponse {
    private BigDecimal totalPrice;
    private BigDecimal feeShip;
    private BigDecimal totalDiscount;
    private BigDecimal totalCheckout;
    private List<ShopOrderRes> shopOrders;
}
