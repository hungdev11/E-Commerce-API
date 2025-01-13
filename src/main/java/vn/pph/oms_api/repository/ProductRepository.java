package vn.pph.oms_api.repository;

import vn.pph.oms_api.model.sku.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    //boolean existsByName(String name);
}
