package vn.pph.oms_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.pph.oms_api.model.Token;

import java.util.List;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    boolean existsByPublicKey(String publicKey);
    List<Token> findByUserId(Long userId);
}
