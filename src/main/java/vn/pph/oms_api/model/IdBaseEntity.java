package vn.pph.oms_api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@MappedSuperclass
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public abstract class IdBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
