package vn.pph.oms_api.model.sku;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.pph.oms_api.model.BaseEntity;

import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "attr")
public class Attribute extends BaseEntity {
    private String name;

    @Column(name = "attr_type")
    private String attrType;

    private String description;

    @OneToMany(mappedBy = "attribute", cascade = CascadeType.ALL)
    List<AttributeValue> valueList;
}
