package authentication;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TokenStore {
    private Map<String, TokenData> tokenMap = new HashMap<>();

    private static TokenStore instance = new TokenStore();
    public static TokenStore getInstance(){
        return instance;
    }

    private TokenStore(){}

    public String putToken(String username){
        String token = UUID.randomUUID().toString();
        tokenMap.put(token, new TokenData(username));
        return token;
    }

    /**
     * getUserName - Converts a Token to a Username...
     *
     * @param token - token used to access server
     * @return username - the username that the token is registered to.
     */
    public String getUsername(String token){
        if(tokenMap.containsKey(token)) {
            if (tokenMap.get(token).expirationTime > System.currentTimeMillis()) {
                return tokenMap.get(token).username;
            } else {
                //the token has expired, delete it
                tokenMap.remove(token);
            }
        }
        return null;
    }

    public long getExpiryTime(String token) {
        if (tokenMap.containsKey(token)) {
            if (tokenMap.get(token).expirationTime > System.currentTimeMillis()) {
                return tokenMap.get(token).expirationTime;
            } else {
                //the token has expired, delete it
                tokenMap.remove(token);
            }
        }
        return 0;
    }

    public String getMacAddress(String token) {
        if (tokenMap.containsKey(token)) {
            if (tokenMap.get(token).expirationTime > System.currentTimeMillis()) {
                return tokenMap.get(token).macAddress;
            } else {
                //the token has expired, delete it
                tokenMap.remove(token);
            }
        }
        return null;
    }

    public <String, TokenData> String getToken(Map<String, TokenData> map, TokenData value) {
        return map.entrySet()
                .stream()
                .filter(entry -> value.equals(entry.getValue()))
                .map(Map.Entry::getKey)
                .findFirst().get();
    }

    public Boolean containsToken(TokenData td) {
        return tokenMap.containsValue(td);
    }

    public String getTokenString(Map<String, TokenData> map, TokenData value) {
        if (containsToken(value)) {
            return getToken(map, value);
        }
        return null;
    }
}
