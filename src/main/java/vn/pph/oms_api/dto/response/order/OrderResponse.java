package vn.pph.oms_api.dto.response.order;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.pph.oms_api.model.Order.Checkout;
import vn.pph.oms_api.model.Order.OrderProduct;
import vn.pph.oms_api.model.Order.ShippingAddress;
import vn.pph.oms_api.utils.OrderStatus;
import vn.pph.oms_api.utils.PaymentMethod;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class OrderResponse {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long shopId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private OrderStatus status;

    private Long userId;

    private Checkout checkout;

    private ShippingAddress shipping;

    private PaymentMethod paymentMethod;

    private List<OrderProductResponse> orderProducts;
}
