package vn.pph.oms_api.model.sku;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.pph.oms_api.model.BaseEntity;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "product")
public class Product extends BaseEntity {

    @Column(name = "product_name", nullable = false, unique = true)
    private String productName;

    @Column(name = "thumb")
    private String productThumb;

    @Column(name = "product_desc")
    private String productDesc;

    @Column(name = "product_price")
    private BigDecimal productPrice;

    @Column(name = "product_status")
    private Boolean productStatus; // 0: out of stock, 1: in stock

    @Column(name = "product_shopId", nullable = false)
    private Long productShopId;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;

    @Column(name = "sort")
    @Builder.Default
    private Integer sort = 0;

    @Column(name = "slug")
    private String slug;

    // Giữ nguyên kiểu boolean, JPA sẽ tự động ánh xạ thành TINYINT(1)
    @Column(name = "is_draft", nullable = false)
    @Builder.Default
    private boolean isDraft = true; // Default value is true

    @Column(name = "is_publish", nullable = false)
    @Builder.Default
    private boolean isPublish = false; // Default value is false

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<Sku> skuList;
}


