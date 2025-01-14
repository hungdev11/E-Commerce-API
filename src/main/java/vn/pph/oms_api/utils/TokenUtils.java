package vn.pph.oms_api.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import vn.pph.oms_api.model.User;

import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Slf4j
public class TokenUtils {
    static final long DAYS_IN_MILLISECONDS = TimeUnit.DAYS.toMillis(1);

    public static Claims decodeJWT(String token, PublicKey publicKey) {
        return Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    public static String signPayload(PrivateKey privateKey, String payload) throws Exception {
        log.info("Sign test payload using private key");
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(payload.getBytes());
        return Base64.getEncoder().encodeToString(signature.sign());
    }
    public static boolean verifySignature(PublicKey publicKey, String payload, String signature) throws Exception {
        log.info("Verify using public key");
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initVerify(publicKey);
        sig.update(payload.getBytes());
        return sig.verify(Base64.getDecoder().decode(signature));
    }
    public static PublicKey decodeStringToPublicKey(String publicKeyString) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(publicKeyString);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(keyBytes));
        log.info("Decode public key string successfully");
        return publicKey;
    }

    public static PrivateKey decodeStringToPrivateKey(String privateKeyString) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(privateKeyString.replace(" ", "+"));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(keyBytes));
        log.info("Decode private key string successfully");
        return privateKey;
    }
    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        log.debug("RSA KeyPair generated");
        return keyPairGenerator.generateKeyPair();
    }

    public static String encodeKeyToString(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public static String generateToken(User user, PrivateKey privateKey, String typeToken, int days) {
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
