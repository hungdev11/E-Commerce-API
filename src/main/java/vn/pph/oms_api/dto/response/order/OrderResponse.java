package vn.pph.oms_api.dto.response.order;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.pph.oms_api.model.Order.Checkout;
import vn.pph.oms_api.model.Order.OrderProduct;
import vn.pph.oms_api.model.Order.ShippingAddress;
import vn.pph.oms_api.utils.PaymentMethod;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class OrderResponse {
    private Long userId;

    private Checkout checkout;

    private ShippingAddress shipping;

    private PaymentMethod paymentMethod;

    private List<OrderProductResponse> orderProducts;
}
