package vn.pph.oms_api.service.Impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import vn.pph.oms_api.dto.request.UserSignInRequest;
import vn.pph.oms_api.dto.request.UserSignUpRequest;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import vn.pph.oms_api.dto.response.SignInResponse;
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
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;
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
    public SignUpResponse signUp(UserSignUpRequest request) {
        log.info("Starting user sign-up process for email: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Email {} is already registered", request.getEmail());
            throw new AppException(ErrorCode.USER_EMAIL_EXITED);
        }
        if (userRepository.existsByName(request.getName())) {
            log.warn("Name {} is already existed in system", request.getName());
            throw new AppException(ErrorCode.USER_NAME_EXITED);
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

            return SignUpResponse.builder()
                    .userId(user.getId())
                    .privateKey(privateKeyString)
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        } catch (Exception e) {
            log.error("Error during token generation process", e);
            throw new AppException(ErrorCode.SOME_THING_WENT_WRONG);
        }
    }

    @Override
    public Object signIn(UserSignInRequest request, String privateKeyString) {
        List<User> users = userRepository.findByEmail(request.getEmail());
        if (users.isEmpty()) {
            log.error("Can not find user with email {}", request.getEmail());
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        User user = users.get(0);
        if (Objects.nonNull(user) && !checkLoginPassword(user, request.getPassword())) {
            log.error("User id {} got wrong password", user.getId());
            throw new AppException(ErrorCode.LOGIN_FAILED);
        }
        if (StringUtils.hasLength(privateKeyString)) {
            return signInWithPrivateKey(user, privateKeyString);
        } else {
            // generate new pair key and token
            try {
                KeyPair keyPair = generateKeyPair();
                String publicKeyString = encodeKeyToString(keyPair.getPublic());
                String newPrivateKeyString = encodeKeyToString(keyPair.getPrivate());

                //renew public key
                Token token = tokenRepository.findByUserId(user.getId()).get(0);
                token.setPublicKey(publicKeyString);
                tokenRepository.save(token);

                // Generate tokens
                String accessToken = generateToken(user, keyPair.getPrivate(), "ACCESS", 2);
                String refreshToken = generateToken(user, keyPair.getPrivate(), "REFRESH", 7);

                log.info("Successfully generated tokens for user ID: {}", user.getId());

                return SignUpResponse.builder()
                        .userId(user.getId())
                        .privateKey(newPrivateKeyString)
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
            } catch (Exception e) {
                log.error("Error during token generation process", e);
                throw new AppException(ErrorCode.SOME_THING_WENT_WRONG);
            }
        }
    }
    private boolean checkLoginPassword(User user, String loginPassword) {
        return passwordEncoder.matches(loginPassword, user.getPassword());
    }
    public SignInResponse signInWithPrivateKey(User user, String privateKeyString) {
        log.info("User log in with private key");
        // test private key
        if (!isValidPrivateKey(user, privateKeyString)) {
            log.error("User id {} got invalid private key", user.getId());
            throw new AppException(ErrorCode.INVALID_PRIVATE_KEY);
        }
        try {
            PrivateKey privateKey = decodeStringToPrivateKey(privateKeyString);
            log.info("Convert private key successfully!!");
            //generate new token with this valid private key
            String accessToken = generateToken(user, privateKey, "ACCESS", 2);
            String refreshToken = generateToken(user, privateKey, "REFRESH", 7);

            log.info("Successfully generated tokens for user ID: {}", user.getId());
            return SignInResponse.builder()
                    .authenticated(true)
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .userId(user.getId())
                    .build();
        } catch (Exception exception) {
            log.error("Error during token generation process ", exception);
            throw new AppException(ErrorCode.SOME_THING_WENT_WRONG);
        }
    }
    private boolean isValidPrivateKey(User user, String privateKeyString) {
        try {
            // Payload to sign (can be any unique string or timestamp)
            String testPayload = "test-payload-" + System.currentTimeMillis();

            // Convert private key string to PrivateKey object
            PrivateKey privateKey = decodeStringToPrivateKey(privateKeyString);

            // Sign the payload using the private key
            String signature = signPayload(privateKey, testPayload);

            // Retrieve the public key for the user from the database
            String userPublicKeyString = tokenRepository.findByUserId(user.getId()).get(0).getPublicKey();

            // Convert public key string to PublicKey object
            PublicKey publicKey = decodeStringToPublicKey(userPublicKeyString);

            // Verify the signature using the public key
            return verifySignature(publicKey, testPayload, signature);

        } catch (Exception e) {
            log.error("Error validating user's private key", e);
            return false; // Private key is invalid or an error occurred
        }
    }
    private String signPayload(PrivateKey privateKey, String payload) throws Exception {
        log.info("Sign test payload using private key");
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(payload.getBytes());
        return Base64.getEncoder().encodeToString(signature.sign());
    }
    private boolean verifySignature(PublicKey publicKey, String payload, String signature) throws Exception {
        log.info("Verify using public key");
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initVerify(publicKey);
        sig.update(payload.getBytes());
        return sig.verify(Base64.getDecoder().decode(signature));
    }
    private PublicKey decodeStringToPublicKey(String publicKeyString) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(publicKeyString);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(keyBytes));
        log.info("Decode public key string successfully");
        return publicKey;
    }

    private PrivateKey decodeStringToPrivateKey(String privateKeyString) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(privateKeyString.replace(" ", "+"));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(keyBytes));
        log.info("Decode private key string successfully");
        return privateKey;
    }

    private User createUser(UserSignUpRequest request, String hashedPassword) {
        Set<String> roles = new HashSet<>();
        roles.add(Role.USER.name());
        User user = User.builder()
                .email(request.getEmail())
                .name(request.getName())
                .password(hashedPassword)
                .status(UserStatus.ACTIVE)
                .isVerify(true)
                .roles(roles)
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
                .claim("role", user.getRoles())
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(expiry))
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }
}
