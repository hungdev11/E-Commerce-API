package vn.pph.oms_api.model;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "cart_product")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class CartProduct extends BaseEntity{
    private Long productId;
    private String skuNo;
    private Long shopId;
    private int quantity;

    @ManyToOne()
    private Cart cart; // Linking CartProduct to Cart entity
}
