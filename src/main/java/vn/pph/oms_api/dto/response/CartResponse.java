package vn.pph.oms_api.dto.response;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CartResponse {
    private Long userId;
    private List<OrderByShop> orderShops;
}
