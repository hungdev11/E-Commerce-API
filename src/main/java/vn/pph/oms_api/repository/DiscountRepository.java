package vn.pph.oms_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.pph.oms_api.model.Discount;


@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {
    boolean existsByShopIdAndCode(Long shopId, String code);
}
