package vn.pph.oms_api.dto.request.order;

import lombok.*;
import vn.pph.oms_api.model.Order.ShippingAddress;
import vn.pph.oms_api.utils.PaymentMethod;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompleteOrderRequest {
    private ShippingAddress address;
    private PaymentMethod paymentMethod;
    private ReviewOrderRequest checkoutRequest;
}
