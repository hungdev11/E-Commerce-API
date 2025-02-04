package vn.pph.oms_api.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import vn.pph.oms_api.model.sku.Sku;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "inventory")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Inventory extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "sku_id", nullable = false)
    Sku sku;

    @Builder.Default
    String location = "Unknown";

    int stock;

    @Column(name = "shop_id", nullable = false)
    Long shopId;

    @OneToMany(mappedBy = "inventory", cascade = CascadeType.ALL, orphanRemoval = true)
    @Column(nullable = false)
    List<ReservationItem> reservations = new ArrayList<>();
}
