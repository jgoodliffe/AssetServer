package authentication;

import java.util.Arrays;
import java.util.Random;

/**
 * Generates + Hashes password
 */
public class PasswordGenerator {
    int length = 22;
    Random rnd = new Random();
    int min = 1;
    int max = 128;

    public String newPassword(){
        char[] password = new char[length];
        for (int i=0; i<length; i++){
            password[i] = ((char) (rnd.nextInt((max - min) + 1) + min));
        }
        return new String(password);
    }
}
