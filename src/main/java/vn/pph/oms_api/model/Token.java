package vn.pph.oms_api.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

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
    @Column(name = "user_id", nullable = false)
    Long userId;

    @Lob
    @Column(name = "public_key", columnDefinition = "TEXT", nullable = false)
    String publicKey;
}
