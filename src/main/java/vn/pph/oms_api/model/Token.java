package vn.pph.oms_api.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tokens")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "user_id", nullable = false, unique = true)
    Long userId;

    @Lob
    @Column(name = "public_key", columnDefinition = "TEXT", nullable = false, unique = true)
    String publicKey;

    @Lob
    @Column(name = "ref_used", columnDefinition = "BLOB")
    Set<String> refreshTokensUsed;
}
