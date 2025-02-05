package vn.pph.oms_api.model;

import jakarta.persistence.*;
import lombok.*;
import vn.pph.oms_api.model.Order.Order;
import vn.pph.oms_api.utils.Role;
import vn.pph.oms_api.utils.UserStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String name;
    String email;
    String password;
    UserStatus status;
    boolean isVerify;
    Set<String> roles;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @Column(nullable = false)
    @Builder.Default
    private List<Order> orders = new ArrayList<>();
}
