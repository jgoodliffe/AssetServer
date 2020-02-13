package authentication;


import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Primarily for hashing passwords within the Database
 */
public class PasswordHasher {

    SecureRandom random = new SecureRandom();
    byte[] salt = new byte[16];
    byte[] encryptedPasswordByteArray;
    String hashedPassword;

    public String hashPassword(String rawPassword) {
        random.nextBytes(salt);
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt);

            encryptedPasswordByteArray = md.digest(rawPassword.getBytes(StandardCharsets.UTF_8));

            hashedPassword = new String(encryptedPasswordByteArray, StandardCharsets.UTF_8);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hashedPassword;
    }
}
