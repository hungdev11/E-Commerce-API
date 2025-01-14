package vn.pph.oms_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.pph.oms_api.model.sku.Attribute;

@Repository
public interface AttributeRepository extends JpaRepository<Attribute, Long> {
    boolean existsByName(String name);
}
