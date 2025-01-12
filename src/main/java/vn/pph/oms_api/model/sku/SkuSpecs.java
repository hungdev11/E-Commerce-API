package vn.pph.oms_api.model.sku;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.pph.oms_api.model.IdBaseEntity;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "sku_specs")
public class SkuSpecs extends IdBaseEntity {
    @Column(name = "spu_specs", columnDefinition = "json")
    private String spuSpecs;
}

