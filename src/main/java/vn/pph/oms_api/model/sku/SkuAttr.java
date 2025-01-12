package vn.pph.oms_api.model.sku;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.pph.oms_api.model.AuditBase;
import vn.pph.oms_api.model.BaseEntity;
import vn.pph.oms_api.model.IdBaseEntity;

import java.math.BigDecimal;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "sku_attr")
public class SkuAttr extends IdBaseEntity {
    @Column(name = "sku_no", unique = true)
    private String skuNo;

    @Column(name = "sku_stock", nullable = false)
    private Integer skuStock;

    @Column(name = "sku_price", nullable = false, precision = 8, scale = 2)
    private BigDecimal skuPrice;

    @Column(name = "sku_attrs", columnDefinition = "json")
    private String skuAttrs;

    @Embedded
    private AuditBase audit;
}
