package vn.pph.oms_api.dto.request;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.OneToMany;
import lombok.*;
import lombok.experimental.FieldDefaults;
import vn.pph.oms_api.model.sku.Sku;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class ProductCreationRequest {
    private Long shopId;
    private String productName;
    private String productThumb;
    private String productDesc;
    private List<SkuCreationRequest> skuList;
}
