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

    @Column(name = "product_shopId")
    private Long productShopId;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @Column(name = "sort")
    private Integer sort;

    @Column(name = "slug")
    private String slug;

    @Column(name = "is_draft")
    private boolean isDraft;

    @Column(name = "is_publish")
    private boolean isPublish;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<Sku> skuList;
}
