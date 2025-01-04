package vn.pph.oms_api.service.Impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.pph.oms_api.dto.request.UserSignUpRequest;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import vn.pph.oms_api.dto.response.ApiResponse;
import vn.pph.oms_api.dto.response.SignUpResponse;
import vn.pph.oms_api.exception.AppException;
import vn.pph.oms_api.exception.ErrorCode;
import vn.pph.oms_api.model.Token;
import vn.pph.oms_api.model.User;
import vn.pph.oms_api.repository.TokenRepository;
import vn.pph.oms_api.repository.UserRepository;
import vn.pph.oms_api.service.AuthenticationService;
import vn.pph.oms_api.utils.Role;
import vn.pph.oms_api.utils.UserStatus;

import java.security.*;
import java.util.Base64;
import java.util.Date;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationServiceImp implements AuthenticationService {

    TokenRepository tokenRepository;
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    static final long DAYS_IN_MILLISECONDS = TimeUnit.DAYS.toMillis(1);

    @Override
    public ApiResponse<SignUpResponse> signUp(UserSignUpRequest request) {
        log.info("Starting user sign-up process for email: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Email {} is already registered", request.getEmail());
            throw new AppException(ErrorCode.USER_EMAIL_EXITED);
        }

        // Hash password and save user
        String hashedPassword = passwordEncoder.encode(request.getPassword());
        User user = createUser(request, hashedPassword);

        try {
            KeyPair keyPair = generateKeyPair();
            String publicKeyString = encodeKeyToString(keyPair.getPublic());
            String privateKeyString = encodeKeyToString(keyPair.getPrivate());

            storeToken(user.getId(), publicKeyString);

            // Generate tokens
            String accessToken = generateToken(user, keyPair.getPrivate(), "ACCESS", 2);
            String refreshToken = generateToken(user, keyPair.getPrivate(), "REFRESH", 7);

            log.info("Successfully generated tokens for user ID: {}", user.getId());

            SignUpResponse signUpResponse = SignUpResponse.builder()
                    .userId(user.getId())
                    .privateKey(privateKeyString)
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();

            return ApiResponse.<SignUpResponse>builder()
                    .code(200)
                    .data(signUpResponse)
                    .message("Registration successful")
                    .build();

        } catch (Exception e) {
            log.error("Error during token generation process", e);
            throw new AppException(ErrorCode.SOME_THING_WENT_WRONG);
        }
    }

    private User createUser(UserSignUpRequest request, String hashedPassword) {
        User user = User.builder()
                .email(request.getEmail())
                .name(request.getName())
                .password(hashedPassword)
                .status(UserStatus.ACTIVE)
                .isVerify(true)
                .roles(new HashSet<>(Role.USER.ordinal()))
                .build();

        userRepository.save(user);
        log.info("User with email {} saved successfully", request.getEmail());
        return user;
    }

    private KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        log.debug("RSA KeyPair generated");
        return keyPairGenerator.generateKeyPair();
    }

    private String encodeKeyToString(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    private void storeToken(Long userId, String publicKey) {
        Token token = Token.builder()
                .userId(userId)
                .publicKey(publicKey)
                .build();
        tokenRepository.save(token);
        log.info("Public key stored in database for user ID: {}", userId);
    }

    private String generateToken(User user, PrivateKey privateKey, String typeToken, int days) {
        long now = System.currentTimeMillis();
        long expiry = now + days * DAYS_IN_MILLISECONDS;

        log.info("Generating {} token for user ID: {} with expiry in {} days", typeToken, user.getId(), days);

        return Jwts.builder()
                .setSubject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("type", typeToken)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(expiry))
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }
}
