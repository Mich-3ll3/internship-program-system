package mx.uv.internshipprogramsystem.logic.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import org.mindrot.jbcrypt.BCrypt;

import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;

public class SecurityManager {
    private static final int TOKEN_LENGTH = 32;
    private static final String HASH_ALGORITHM = "SHA-256";

    public String generateActivationToken() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] tokenBytes = new byte[TOKEN_LENGTH];

        secureRandom.nextBytes(tokenBytes);

        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }

    public String hashToken(String token) throws BusinessException {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(HASH_ALGORITHM);

            byte[] hashBytes = messageDigest.digest(token.getBytes(StandardCharsets.UTF_8));

            StringBuilder hashedToken = new StringBuilder();

            for (byte hashByte : hashBytes) {
                hashedToken.append(String.format("%02x", hashByte));
            }

            return hashedToken.toString();
        } catch (NoSuchAlgorithmException algorithmException) {
            throw new BusinessException("No se pudo generar el hash del token.",algorithmException);
        }
    }

    public String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword,BCrypt.gensalt());
    }

    public boolean verifyPassword( String plainPassword, String passwordHash) {
        return BCrypt.checkpw(plainPassword, passwordHash);
    }
}