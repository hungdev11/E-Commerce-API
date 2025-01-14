package vn.pph.oms_api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import vn.pph.oms_api.model.Token;
import vn.pph.oms_api.repository.TokenRepository;
import vn.pph.oms_api.service.AuthenticationService;
import vn.pph.oms_api.utils.TokenUtils;

import java.security.Key;
import java.security.interfaces.RSAPublicKey;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private TokenRepository tokenRepository;

    private static final String[] PUBLIC_ENDPOINTS = {
            "/authentication/sign-up",
            "/authentication/sign-in",
            "/authentication/refresh-token", // Yêu cầu xác thực để làm mới token
            "/authentication/log-out",    // Yêu cầu xác thực để đăng xuất
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
        //prevent api end-point of attack cross-site
        http.csrf(CsrfConfigurer::disable);
//        http.formLogin(form -> form.loginProcessingUrl("/login"));
        http.authorizeHttpRequests(req -> req
                .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                .requestMatchers(AUTHENTICATED_ENDPOINTS).authenticated()
                .anyRequest().denyAll()
        );
        //http.oauth2ResourceServer(oauth -> oauth.jwt(jwtConfigurer -> jwtConfigurer.decoder(jwtDecoder(1L))));
        return http.build();
    }
//    @Bean
//    public JwtDecoder jwtDecoder (Long userId) throws Exception {
//        Token token= tokenRepository.findByUserId(userId).getFirst();
//        Key publicKey = TokenUtils.decodeStringToPublicKey(token.getPublicKey());
//        return NimbusJwtDecoder
//                .withPublicKey((RSAPublicKey) publicKey)
//                .signatureAlgorithm(SignatureAlgorithm.RS256)
//                .build();
//    }
}
