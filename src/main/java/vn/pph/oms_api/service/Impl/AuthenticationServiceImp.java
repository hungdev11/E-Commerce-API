package vn.pph.oms_api.service.Impl;

import io.jsonwebtoken.Claims;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import vn.pph.oms_api.dto.request.token.RefreshTokenRequest;
import vn.pph.oms_api.dto.request.user.UserLogOutRequest;
import vn.pph.oms_api.dto.request.user.UserSignInRequest;
import vn.pph.oms_api.dto.request.user.UserSignUpRequest;
import vn.pph.oms_api.dto.response.token.RefreshTokenResponse;
import vn.pph.oms_api.dto.response.user.SignInResponse;
import vn.pph.oms_api.dto.response.user.SignUpResponse;
import vn.pph.oms_api.exception.AppException;
import vn.pph.oms_api.exception.ErrorCode;
import vn.pph.oms_api.model.Token;
import vn.pph.oms_api.model.User;
import vn.pph.oms_api.repository.TokenRepository;
import vn.pph.oms_api.repository.UserRepository;
import vn.pph.oms_api.service.AuthenticationService;
import vn.pph.oms_api.service.CartService;
import vn.pph.oms_api.utils.Role;
import vn.pph.oms_api.utils.TokenUtils;
import vn.pph.oms_api.utils.UserStatus;

import java.security.*;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationServiceImp implements AuthenticationService {

    TokenRepository tokenRepository;
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    CartService cartService;

    @Override
    @Transactional
    public SignUpResponse signUp(UserSignUpRequest request) {
        log.info("Starting user sign-up process for email: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Email {} is already registered", request.getEmail());
            throw new AppException(ErrorCode.USER_EMAIL_EXISTED);
        }
        if (userRepository.existsByName(request.getName())) {
            log.warn("Name {} is already existed in system", request.getName());
            throw new AppException(ErrorCode.USER_NAME_EXISTED);
        }

        // Hash password and save user
        String hashedPassword = passwordEncoder.encode(request.getPassword());
        User user = createUser(request, hashedPassword);

        try {
            KeyPair keyPair = TokenUtils.generateKeyPair();
            String publicKeyString = TokenUtils.encodeKeyToString(keyPair.getPublic());
            String privateKeyString = TokenUtils.encodeKeyToString(keyPair.getPrivate());

            storeToken(user.getId(), publicKeyString);

            // Generate tokens
            String accessToken = TokenUtils.generateToken(user, keyPair.getPrivate(), "ACCESS", 2);
            String refreshToken = TokenUtils.generateToken(user, keyPair.getPrivate(), "REFRESH", 7);

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
                KeyPair keyPair = TokenUtils.generateKeyPair();
                String publicKeyString = TokenUtils.encodeKeyToString(keyPair.getPublic());
                String newPrivateKeyString = TokenUtils.encodeKeyToString(keyPair.getPrivate());

                //renew public key
                List<Token> tokens = tokenRepository.findByUserId(user.getId());
                Token token;
                if (!tokens.isEmpty()) {
                    token = tokens.get(0);
                    token.setPublicKey(publicKeyString);
                } else {
                    token = Token.builder()
                            .id(user.getId())
                            .publicKey(publicKeyString)
                            .refreshTokensUsed(new HashSet<>())
                            .build();
                }
                tokenRepository.save(token);
                // Generate tokens
                String accessToken = TokenUtils.generateToken(user, keyPair.getPrivate(), "ACCESS", 2);
                String refreshToken = TokenUtils.generateToken(user, keyPair.getPrivate(), "REFRESH", 7);

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

    @Override
    public void logOut(UserLogOutRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        List<Token> tokens = tokenRepository.findByUserId(user.getId());
        if (tokens.isEmpty()) {
            log.error("Can not found token with user id {}", user.getId());
            throw new AppException(ErrorCode.TOKEN_NOT_FOUND);
        }
        try {
            Token token = tokens.get(0);
            PublicKey publicKey = TokenUtils.decodeStringToPublicKey(token.getPublicKey());
            Claims claims = TokenUtils.decodeJWT(request.getAccessToken(), publicKey);
            if (user.getId().compareTo(Long.valueOf(claims.getSubject())) != 0) {
                log.error("User id {} with id in token are different id in token {}", user.getId(), claims.getSubject());
                throw new AppException(ErrorCode.USER_ID_DIFF_ID_IN_TOKEN);
            }
            if (claims.getExpiration().before(new Date())) {
                log.error("User access token expired");
                throw new AppException(ErrorCode.TOKEN_EXPIRED);
            }
            token.setPublicKey("");
            tokenRepository.save(token);
        } catch (Exception e) {
            log.error("Error during token generation process", e);
            throw new AppException(ErrorCode.SOME_THING_WENT_WRONG);
        }
    }
    @Override
    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        List<Token> tokens = tokenRepository.findByUserId(user.getId());
        if (tokens.isEmpty()) {
            log.error("Can not found token with user id {}", user.getId());
            throw new AppException(ErrorCode.TOKEN_NOT_FOUND);
        }
        if (!isValidPrivateKey(user, request.getPrivateKey())) {
            log.error("User id {} got invalid private key", user.getId());
            throw new AppException(ErrorCode.INVALID_PRIVATE_KEY);
        }
        try {
            Token token = tokens.get(0);
            if (token.getRefreshTokensUsed().contains(request.getRefreshToken())) {
                log.error("Suspect token");
                // Next step can do is : delete all user's token to protect user and notification to user through email
                throw new AppException(ErrorCode.SOME_THING_WENT_WRONG);
            }
            PublicKey publicKey = TokenUtils.decodeStringToPublicKey(token.getPublicKey());
            Claims claims = TokenUtils.decodeJWT(request.getRefreshToken(), publicKey);
            if (user.getId().compareTo(Long.valueOf(claims.getSubject())) != 0) {
                log.error("User id {} with id in token are different id in token {}", user.getId(), claims.getSubject());
                throw new AppException(ErrorCode.USER_ID_DIFF_ID_IN_TOKEN);
            }
            if (claims.getExpiration().before(new Date())) {
                log.error("User access token expired");
                throw new AppException(ErrorCode.TOKEN_EXPIRED);
            }
            token.getRefreshTokensUsed().add(request.getRefreshToken());
            tokenRepository.save(token);
            PrivateKey privateKey = TokenUtils.decodeStringToPrivateKey(request.getPrivateKey());
            // Generate tokens
            String accessToken = TokenUtils.generateToken(user, privateKey, "ACCESS", 2);
            String refreshToken = TokenUtils.generateToken(user, privateKey, "REFRESH", 7);
            log.info("Successfully generated tokens for user ID: {}", user.getId());
            return RefreshTokenResponse.builder()
                    .userId(request.getUserId())
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        } catch (Exception e) {
            log.error("Error during token generation process", e);
            throw new AppException(ErrorCode.SOME_THING_WENT_WRONG);
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
            PrivateKey privateKey = TokenUtils.decodeStringToPrivateKey(privateKeyString);
            log.info("Convert private key successfully!!");
            //generate new token with this valid private key
            String accessToken = TokenUtils.generateToken(user, privateKey, "ACCESS", 2);
            String refreshToken = TokenUtils.generateToken(user, privateKey, "REFRESH", 7);

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
            PrivateKey privateKey = TokenUtils.decodeStringToPrivateKey(privateKeyString);

            // Sign the payload using the private key
            String signature = TokenUtils.signPayload(privateKey, testPayload);

            // Retrieve the public key for the user from the database
            String userPublicKeyString = tokenRepository.findByUserId(user.getId()).get(0).getPublicKey();

            // Convert public key string to PublicKey object
            PublicKey publicKey = TokenUtils.decodeStringToPublicKey(userPublicKeyString);

            // Verify the signature using the public key
            return TokenUtils.verifySignature(publicKey, testPayload, signature);

        } catch (Exception e) {
            log.error("Error validating user's private key", e);
            return false; // Private key is invalid or an error occurred
        }
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
        cartService.createCart(userRepository.save(user).getId());
        log.info("User with email {} saved successfully", request.getEmail());
        return user;
    }

    private void storeToken(Long userId, String publicKey) {
        Token token = Token.builder()
                .userId(userId)
                .refreshTokensUsed(new HashSet<>())
                .publicKey(publicKey)
                .build();
        tokenRepository.save(token);
        log.info("Public key stored in database for user ID: {}", userId);
    }
}
