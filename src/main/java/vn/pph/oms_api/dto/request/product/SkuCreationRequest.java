package vn.pph.oms_api.dto.request.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;
import lombok.experimental.FieldDefaults;
import vn.pph.oms_api.model.sku.Product;

import java.math.BigDecimal;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SkuCreationRequest {
    String skuNo;
    String skuName;
    String skuDescription;
    Integer skuStock;
    @JsonProperty("isDefault")
    Boolean isDefault;
    BigDecimal skuPrice;
    Map<String, String> attributes;
}
