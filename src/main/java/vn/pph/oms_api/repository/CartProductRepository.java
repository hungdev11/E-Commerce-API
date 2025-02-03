package vn.pph.oms_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.pph.oms_api.model.Cart;
import vn.pph.oms_api.model.CartProduct;

import java.util.Optional;

@Repository
public interface CartProductRepository extends JpaRepository<CartProduct, Long> {
}
