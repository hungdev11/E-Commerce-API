package vn.pph.oms_api.service.Impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.pph.oms_api.dto.request.UserSignUpRequest;
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
import java.util.HashSet;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationServiceImp implements AuthenticationService {
    TokenRepository tokenRepository;
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    static final long DAYS_IN_MILISECONDS = 24 * 60 * 60 * 1000;
    @Override
    public ApiResponse<SignUpResponse> signUp(UserSignUpRequest request) {
        //check email user
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.USER_EMAIL_EXITED);
        }
        String pwHash = passwordEncoder.encode(request.getPassword());
        User user = User.builder().email(request.getEmail())
                .name(request.getName())
                .password(pwHash)
                .status(UserStatus.ACTIVE)
                .isVerify(true)
                .roles(new HashSet<>(Role.USER.ordinal()))
                .build();
        userRepository.save(user);

        try {
            // Generate RSA key pair
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            PrivateKey privateKey = keyPair.getPrivate();
            PublicKey publicKey = keyPair.getPublic();

            // Convert keys to strings
            String publicKeyString = Base64.getEncoder().encodeToString(publicKey.getEncoded());
            String privateKeyString = Base64.getEncoder().encodeToString(privateKey.getEncoded());

            // Store public key in database
            Token tokenModel = Token.builder().userId(user.getId()).publicKey(publicKeyString).build();
            tokenRepository.save(tokenModel);

            // Generate tokens
            String accessToken = generateToken(user, privateKey, "ACCESS", 2);
            String refreshToken = generateToken(user, privateKey, "REFRESH", 7);

            SignUpResponse signUpResponse = SignUpResponse.builder()
                    .userId(user.getId())
                    .privateKey(privateKeyString)
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
            return ApiResponse.<SignUpResponse>builder()
                    .code(200)
                    .data(signUpResponse)
                    .message("Register successfully")
                    .build();
        } catch (Exception e) {
            throw new AppException(ErrorCode.SOME_THING_WENT_WRONG);
        }
    }
    private String generateToken(User user, PrivateKey privateKey, String typeToken, int days) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        long now = System.currentTimeMillis();
        long expiry = now + days * DAYS_IN_MILISECONDS;

        String payload = String.format("{" +
                "\"sub\": \"%s\", " +
                "\"email\": \"%s\", " +
                "\"type\": \"%s\", " +
                "\"exp\": %d " +
                "}", user.getId(), user.getEmail(), typeToken, expiry);

        // Sign payload
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(payload.getBytes());
        byte[] signedPayload = signature.sign();

        // Encode payload and signature as token
        return Base64.getEncoder().encodeToString(payload.getBytes()) + "." +
                Base64.getEncoder().encodeToString(signedPayload);
    }
}
