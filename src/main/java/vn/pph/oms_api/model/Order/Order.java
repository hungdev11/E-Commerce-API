package vn.pph.oms_api.model.Order;

import jakarta.persistence.*;
import lombok.*;
import vn.pph.oms_api.model.BaseEntity;
import vn.pph.oms_api.model.User;
import vn.pph.oms_api.utils.OrderStatus;
import vn.pph.oms_api.utils.PaymentMethod;

import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order extends BaseEntity {
    @ManyToOne()
    private User user;

    @Embedded
    private Checkout checkout;

    @Embedded
    private ShippingAddress shipping;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderProduct> orderProducts;

    private String trackingOrder;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING;
}
