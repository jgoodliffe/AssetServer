package authentication;

public class TokenData {
    String username;
    String macAddress;
    long expirationTime;

    TokenData(String username){
        this.username = username;
        //15 minutes from now
        expirationTime = System.currentTimeMillis() + 15 * 60 * 1000;
    }
}