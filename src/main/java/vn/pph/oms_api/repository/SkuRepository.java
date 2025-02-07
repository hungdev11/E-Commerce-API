package vn.pph.oms_api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.pph.oms_api.model.sku.Product;
import vn.pph.oms_api.model.sku.Sku;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface SkuRepository extends JpaRepository<Sku, Long> {
    Page<Sku> findAllByProduct(Product product, Pageable pageable);
    List<Sku> findAllByProduct(Product product);
    List<Sku> findAllBySkuNoIn(Collection<String> skuNos);
    Optional<Sku> findBySkuNo(String skuNo);
}
