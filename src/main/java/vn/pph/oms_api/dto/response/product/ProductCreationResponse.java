package vn.pph.oms_api.dto.response.product;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Product info")
public class ProductCreationResponse {
    @Schema(description = "Product name", examples = "Iphone 16 Pro Max 1Tb")
    String name;
    BigDecimal price;
    Integer stock;
    int countSku;
}
