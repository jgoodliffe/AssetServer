package authentication;

import java.util.Random;

/**
 * Generates a new Password.
 */
public class PasswordGenerator {
    int length = 22;
    Random rnd = new Random();
    int min = 33;
    int max = 126;
    int[] exclude = {92, 96};

    /**
     * generateRandom - Generates a random number excluding certain ASCII Values.
     *
     * @return
     */
    private int generateRandom() {
        int random = min + rnd.nextInt(max - min + 1 - exclude.length);
        for (int ex : exclude) {
            if (random < ex) {
                break;
            }
            random++;
        }
        return random;
    }

    /**
     * newPassword - Generates a random password.
     *
     * @return - A password.
     */
    public String newPassword() {
        char[] password = new char[length];
        for (int i = 0; i < length; i++) {
            password[i] = ((char) generateRandom());
        }
        return new String(password);
    }
}
