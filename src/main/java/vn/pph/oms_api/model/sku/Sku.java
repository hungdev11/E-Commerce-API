package vn.pph.oms_api.model.sku;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.pph.oms_api.model.BaseEntity;

import java.math.BigDecimal;

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

    @Column(name = "status", nullable = false) // 0: in stock/ 1: out of stock
    private Boolean status;

    @Column(name = "sort")
    private Integer sort;

    @Column(name = "sku_stock", nullable = false)
    private Integer skuStock;

    @Column(name = "sku_price", nullable = false, precision = 8, scale = 2)
    private BigDecimal skuPrice;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "spu_id")
    private Product product;
}