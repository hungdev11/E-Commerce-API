package vn.pph.oms_api.model;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "cart-product")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class CartProduct extends BaseEntity{
    private Long productId;
    private Long shopId;
    private int quantity;

    @ManyToOne(cascade = CascadeType.ALL)
    private Cart cart; // Linking CartProduct to Cart entity
}
