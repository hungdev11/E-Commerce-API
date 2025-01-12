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
import vn.pph.oms_api.model.IdBaseEntity;

import java.math.BigDecimal;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "sku")
public class Sku extends IdBaseEntity {
    @Column(name = "sku_no", unique = true, nullable = false)
    private String skuNo;

    @Column(name = "sku_name")
    private String skuName;

    @Column(name = "sku_description")
    private String skuDescription;

    @Column(name = "sku_type")
    private Byte skuType;

    @Column(name = "status", nullable = false)
    private Byte status;

    @Column(name = "sort")
    private Integer sort;

    @Column(name = "sku_stock", nullable = false)
    private Integer skuStock;

    @Column(name = "sku_price", nullable = false, precision = 8, scale = 2)
    private BigDecimal skuPrice;

    @Embedded
    private AuditBase audit;

}