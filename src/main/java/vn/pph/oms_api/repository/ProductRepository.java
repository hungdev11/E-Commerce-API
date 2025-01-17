package vn.pph.oms_api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.pph.oms_api.model.sku.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByProductName(String name);
    Page<Product> findAllByProductShopId (Long shopId, Pageable pageable);
    @Query("SELECT p FROM Product p WHERE p.productShopId = :shopId AND " +
            "(:isDraft IS NOT NULL AND p.isDraft = :isDraft) AND " +
            "(:isPublish IS NOT NULL AND p.isPublish = :isPublish)")
    Page<Product> findProductsByShopAndStatus(@Param("shopId") Long shopId,
                                              @Param("isDraft") Boolean isDraft,
                                              @Param("isPublish") Boolean isPublish,
                                              Pageable pageable);



}
