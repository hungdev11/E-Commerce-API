package vn.pph.oms_api.dto.response.product;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductCreationResponse {
    String name;
    BigDecimal price;
    Integer stock;
    int countSku;
}
