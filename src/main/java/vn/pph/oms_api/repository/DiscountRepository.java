package vn.pph.oms_api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.pph.oms_api.model.Discount;
import vn.pph.oms_api.utils.DiscountStatus;

import java.util.Optional;


@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {
    boolean existsByShopIdAndCode(Long shopId, String code);
    boolean existsByCode(String code);
    Optional<Discount> findByCode(String code);
    @Query("SELECT d FROM Discount d WHERE d.shopId = :shopId AND " +
            "(:status IS NOT NULL AND d.status = :status)")
    Page<Discount> findAllByShopId(@Param("shopId") Long shopId,
                                   @Param("status") DiscountStatus status,
                                   Pageable pageable);
}
