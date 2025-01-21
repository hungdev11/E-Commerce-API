package vn.pph.oms_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.pph.oms_api.model.Cart;


import java.util.Optional;
@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
   Optional<Cart> findByUserId(Long userId);
   boolean existsByUserId(Long userId);
}
