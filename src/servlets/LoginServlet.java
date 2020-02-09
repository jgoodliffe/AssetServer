package servlets;

import dbSystem.DataStore;
import org.eclipse.jetty.http.HttpStatus;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;

public class LoginServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setStatus(HttpStatus.NOT_IMPLEMENTED_501);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
    }

    public static boolean isNullOrEmpty(String str) {
        if(str != null && !str.isEmpty())
            return false;
        return true;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String requestURL = req.getRequestURI();
        resp.setStatus(HttpStatus.OK_200);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        System.out.println(req);
        String authHeader = req.getHeader("auth"); //u + p sent in request header.
        if(!isNullOrEmpty(authHeader)){
            System.out.println(authHeader);
            String encodedAuth = authHeader.substring(authHeader.indexOf(' ')+1); //Isolate base64 encoded part of auth header

            String decodedAuth = new String(Base64.getDecoder().decode(encodedAuth));

            String username = decodedAuth.substring(0, decodedAuth.indexOf(':'));
            String password = decodedAuth.substring(decodedAuth.indexOf(':')+1);

            System.out.println("Received uName= "+username+" and pWord= "+password);

            //Check against what is in the Database
            if(DataStore.getInstance().getSqlQueries().checkUserCredentials(username, password)){
                //Login Successful
                //TODO: Tokenize - Generate token - Check user levels etc
                resp.setStatus(HttpStatus.OK_200);
                resp.setContentType("application/json");
                resp.setCharacterEncoding("UTF-8");
                String authToken = "123abc";
                JSONObject obj = new JSONObject();
                obj.put("request-received", "true");
                obj.put("error-type", "none");
                obj.put("response-code", HttpStatus.OK_200);
                obj.put("auth-token", authToken);
                resp.getWriter().write(obj.toString());

            } else{
                //Login Unsuccessful.
                System.out.println("Login unsuccessful!");
                resp.setStatus(HttpStatus.BAD_REQUEST_400);
                resp.setContentType("application/json");
                resp.setCharacterEncoding("UTF-8");

                JSONObject err = new JSONObject();
                err.put("request-received", "true");
                err.put("error-type", "Incorrect login credentials.");
                err.put("response-code", HttpStatus.BAD_REQUEST_400);
                resp.getWriter().write(err.toString());
            }
        } else{
            System.out.println("Invalid auth header.");
            resp.setStatus(HttpStatus.BAD_REQUEST_400);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");

            JSONObject err = new JSONObject();
            err.put("request-received", "true");
            err.put("error-type", "Invalid request");
            err.put("response-code", HttpStatus.BAD_REQUEST_400);
            resp.getWriter().write(err.toString());

        }
    }
}
