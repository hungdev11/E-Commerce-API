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

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "sd_product")
public class Product extends IdBaseEntity {
    @Column(name = "product_name")
    private String productName;

    @Column(name = "product_desc")
    private String productDesc;

    @Column(name = "product_status")
    private Byte productStatus;

    @Column(name = "product_attrs", columnDefinition = "json")
    private String productAttrs; // Consider using `@Convert` if working with a Map

    @Column(name = "product_shopId")
    private Long productShopId;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @Column(name = "sort")
    private Integer sort;

    @Embedded
    private AuditBase audit;
}
