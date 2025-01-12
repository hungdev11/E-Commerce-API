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
@Table(name = "spu_to_sku")
public class SpuToSku extends IdBaseEntity {

    @Column(name = "sku_no", nullable = false)
    private String skuNo;

    @Column(name = "spu_no", nullable = false)
    private String spuNo;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @Embedded
    private AuditBase audit;
}
