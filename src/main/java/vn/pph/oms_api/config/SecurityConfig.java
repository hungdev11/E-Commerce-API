package vn.pph.oms_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String[] PUBLIC_ENDPOINTS = {
            "/authentication/sign-up",
            "/authentication/sign-in",
            "/authentication/log-out",    // Yêu cầu xác thực để đăng xuất
            "/authentication/refresh-token", // Yêu cầu xác thực để làm mới token
            "/products",
    };
    private static final String[] AUTHENTICATED_ENDPOINTS = {

    };

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(CsrfConfigurer::disable);
//        http.formLogin(form -> form.loginProcessingUrl("/login"));
        http.authorizeHttpRequests(req -> req
                .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                .requestMatchers(AUTHENTICATED_ENDPOINTS).authenticated()
                .anyRequest().denyAll()
        );
        return http.build();
    }
}
