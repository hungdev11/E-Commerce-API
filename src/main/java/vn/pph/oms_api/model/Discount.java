package vn.pph.oms_api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.pph.oms_api.utils.DiscountApplyTo;
import vn.pph.oms_api.utils.DiscountStatus;
import vn.pph.oms_api.utils.DiscountType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "discount")
public class Discount extends BaseEntity{
    private String name;
    private String description;
    private DiscountType type;
    private BigDecimal value;

    @Column(unique = true)
    private String code;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "maximum_quantity", nullable = false)
    @Builder.Default
    private int maximumQuantity = 0;

    @Column(name = "used_quantity", nullable = false)
    @Builder.Default
    private int usedQuantity = 0;

    @Column(name = "user_used", nullable = false)
    @Builder.Default
    @ManyToMany
    @JoinTable(
            name = "discount_user",
            joinColumns = @JoinColumn(name = "discount_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> usersUsed = new ArrayList<>();

    @Column(name = "max_per_user", nullable = false)
    private int maxPerUser;

    @Column(name = "min_order_value", nullable = false)
    private BigDecimal minOrderValue;

    @Column(name = "shop_id", nullable = false)
    private Long shopId;

    @Builder.Default
    private DiscountStatus status = DiscountStatus.INACTIVE;

    @Column(name = "apply_to", nullable = false)
    @Builder.Default
    private DiscountApplyTo applyTo = DiscountApplyTo.ALL;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private boolean isDiscountDeleted = false;

    @Column(name = "products_apply", nullable = false)
    @Builder.Default
    List<Long> productIds = new ArrayList<>();
}
