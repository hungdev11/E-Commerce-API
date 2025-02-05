package vn.pph.oms_api.dto.response.discount;

import lombok.*;
import vn.pph.oms_api.utils.DiscountStatus;
import vn.pph.oms_api.utils.DiscountType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class DiscountResponse {
    private String name;
    private String description;
    private DiscountType type;
    private BigDecimal value;
    private String code;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private int maximumQuantity;
    private int maxPerUser;
    private BigDecimal minOrderValue;
    private List<Long> productIds;
}
