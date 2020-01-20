package main;
import org.json.*;
import com.google.gson.Gson;

public class JSONParse {

    private static final Gson gson = new Gson();

    public static boolean isJSONValid(String jsonInString) {
        try {
            gson.fromJson(jsonInString, Object.class);
            return true;
        } catch(com.google.gson.JsonSyntaxException ex) {
            return false;
        }
    }

    public JSONArray stringToArray(String s) {
        if (isJSONValid(s)) {
            JSONArray ja = new JSONArray(s);
            System.out.println(ja.toString());
            return ja;
        } else {
            System.out.println("Null JSON Object found.");
            return null;
        }
    }
}
