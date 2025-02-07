package vn.pph.oms_api.dto.response.order;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.pph.oms_api.model.BaseEntity;
import vn.pph.oms_api.model.Order.Order;

import java.math.BigDecimal;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class OrderProductResponse {
    private Long productId;
    private String skuNo;
    private int quantity;
    private BigDecimal price;
}
