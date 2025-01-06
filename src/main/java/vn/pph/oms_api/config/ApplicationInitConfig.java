package vn.pph.oms_api.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import vn.pph.oms_api.model.User;
import vn.pph.oms_api.repository.UserRepository;
import vn.pph.oms_api.utils.Role;

import java.util.HashSet;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ApplicationInitConfig {

    private final PasswordEncoder passwordEncoder;
    @Value("${admin.name}")
    private String adminName;

    @Value("${admin.password}")
    private String adminPassword;
    @Bean
    ApplicationRunner applicationRunner(UserRepository repository) {
        return args -> {
            if (repository.findByName(adminName).isEmpty()) {
                Set<String> roles = new HashSet<>();
                roles.add(Role.ADMIN.name());
                roles.add(Role.USER.name());
                User admin = User.builder()
                        .name("admin")
                        .roles(roles)
                        .password(passwordEncoder.encode(adminPassword))
                        .build();
                repository.save(admin);
                log.warn("Admin user has been created!!!");
            }
        };
    }
}
