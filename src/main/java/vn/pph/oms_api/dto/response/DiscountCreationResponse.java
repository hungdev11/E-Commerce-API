package vn.pph.oms_api.dto.response;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import vn.pph.oms_api.utils.DiscountApplyTo;
import vn.pph.oms_api.utils.DiscountStatus;
import vn.pph.oms_api.utils.DiscountType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Slf4j
public class DiscountCreationResponse {
    private String name;
    private String description;
    private DiscountType type;
    private BigDecimal value;
    private String code;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private int maximumQuantity;
    private int maxPerUser;
    private DiscountStatus status;
    private Long shopId;
    private BigDecimal minOrderValue;
    private List<Long> productIds;
}
