package vn.pph.oms_api.model.Order;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.math.BigDecimal;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Checkout {
    private BigDecimal totalPrice;
    private BigDecimal feeShip;
    private BigDecimal totalDiscount;
    private BigDecimal totalCheckout;
}
