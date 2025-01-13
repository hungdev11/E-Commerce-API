package vn.pph.oms_api.model.sku;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.pph.oms_api.model.BaseEntity;


@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "attr_val", uniqueConstraints = @UniqueConstraint(name = "uk_sku_attr", columnNames = {"sku_id", "attr_id"}))
public class AttributeValue extends BaseEntity {
    @ManyToOne()
    @JoinColumn(name = "sku_id", nullable = false)
    private Sku sku;

    @ManyToOne()
    @JoinColumn(name = "attr_id", nullable = false)
    private Attribute attribute;

    @Column(name = "value", nullable = false)
    private String value;
}
