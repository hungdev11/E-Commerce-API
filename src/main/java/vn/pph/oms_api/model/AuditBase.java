package vn.pph.oms_api.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@MappedSuperclass
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public abstract class AuditBase {
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;

    @Column(name = "update_time")
    private LocalDateTime updateTime;
}
