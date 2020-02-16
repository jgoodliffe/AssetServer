package authentication;


import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Primarily for hashing passwords within the Database
 */
public class PasswordHasher {

    static SecureRandom random = new SecureRandom();
    static byte[] salt = new byte[16];
    byte[] encryptedPasswordByteArray;
    String hashedPassword;

    public static byte[] getSalt() {
        try {
            random = SecureRandom.getInstance("SHA1PRNG");
            random.nextBytes(salt);
            return salt;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String hashPassword(String rawPassword, byte[] saltValue) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(saltValue);

            encryptedPasswordByteArray = md.digest(rawPassword.getBytes(StandardCharsets.UTF_8));

            hashedPassword = new String(encryptedPasswordByteArray, StandardCharsets.UTF_8);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hashedPassword;
    }
}
