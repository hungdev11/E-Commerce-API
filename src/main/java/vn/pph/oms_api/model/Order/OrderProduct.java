package vn.pph.oms_api.model.Order;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;
import vn.pph.oms_api.model.BaseEntity;

import java.math.BigDecimal;

@Entity
@Table(name = "order_products")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderProduct extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    private Long productId;

    private String skuNo;

    private int quantity;

    private BigDecimal price;
}
