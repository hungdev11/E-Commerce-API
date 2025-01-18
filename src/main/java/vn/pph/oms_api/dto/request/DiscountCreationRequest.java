package vn.pph.oms_api.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import vn.pph.oms_api.utils.DiscountApplyTo;
import vn.pph.oms_api.utils.DiscountStatus;
import vn.pph.oms_api.utils.DiscountType;
import vn.pph.oms_api.validation.ValidDateRange;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ValidDateRange // Custom annotation to validate startDate < endDate
public class DiscountCreationRequest {
    @NotBlank(message = "Name must not be blank")
    private String name;

    private String description;

    @NotNull(message = "Discount type must not be null")
    private DiscountType type;

    @NotNull(message = "Value must not be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Value must be greater than 0")
    private BigDecimal value;

    @NotBlank(message = "Code must not be blank")
    private String code;

    @NotNull(message = "Start date must not be null")
    @FutureOrPresent(message = "Start date must be in the present or future")
    private LocalDateTime startDate;

    @NotNull(message = "End date must not be null")
    @FutureOrPresent(message = "End date must be in the present or future")
    private LocalDateTime endDate;

    @Min(value = 0, message = "Maximum discount quantity must be at least 0")
    private int maximumQuantity;

    @Min(value = 0, message = "Max discount per user must be at least 0")
    private int maxPerUser;

    private DiscountStatus status;

    @NotNull(message = "Shop ID must not be null")
    private Long shopId;

    @NotNull(message = "Minimum order value must not be null")
    @DecimalMin(value = "0.0", inclusive = true, message = "Minimum order value must be at least 0")
    private BigDecimal minOrderValue;
    private DiscountApplyTo applyTo;
    private List<Long> productIds;
}
