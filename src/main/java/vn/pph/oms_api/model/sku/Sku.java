package vn.pph.oms_api.model.sku;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.pph.oms_api.model.BaseEntity;
import vn.pph.oms_api.model.Inventory;
import vn.pph.oms_api.model.ReservationItem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "sku")
public class Sku extends BaseEntity {

    @Column(name = "sku_no", unique = true, nullable = false)
    private String skuNo;

    @Column(name = "sku_name", nullable = false)
    private String skuName;

    @Column(name = "sku_description")
    private String skuDescription;

    // Sử dụng kiểu boolean, sẽ được ánh xạ thành TINYINT(1) trong MySQL
    @Column(name = "status", nullable = false)
    private boolean status; // 0: in stock / 1: out of stock

    @Column(name = "sort")
    private int sort;

    @Column(name = "is_default", nullable = false) // 0: not default, 1: default
    private boolean isDefault;

    @Column(name = "sku_stock", nullable = false)
    private Integer skuStock;

    @Column(name = "sku_price", nullable = false, precision = 8, scale = 2)
    private BigDecimal skuPrice;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "spu_id")
    private Product product;

    @OneToMany(mappedBy = "sku", cascade = CascadeType.ALL)
    private List<AttributeValue> valueList;

    @OneToMany(mappedBy = "sku", cascade = CascadeType.ALL, orphanRemoval = true)
    @Column(nullable = false)
    List<Inventory> inventories = new ArrayList<>();
}
