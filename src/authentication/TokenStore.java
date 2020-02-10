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
     * getUserName
     * @param token - token used to access server
     * @return username - the username that the token is registered to.
     */
    public String getUsername(String token){
        if(tokenMap.containsKey(token)){
            if(tokenMap.get(token).expirationTime > System.currentTimeMillis()){
                return tokenMap.get(token).username;
            }
            else{
                //the token has expired, delete it
                tokenMap.remove(token);
            }
        }
        return null;
    }
}
