package vn.pph.oms_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.pph.oms_api.model.ReservationItem;

@Repository
public interface ReservationItemRepository extends JpaRepository<ReservationItem, Long> {
}
