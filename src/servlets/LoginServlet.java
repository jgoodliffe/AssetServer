package servlets;

import LoggingSystem.LoggingSystem;
import authentication.PasswordGenerator;
import authentication.TokenStore;
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

    private LoggingSystem log;

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setStatus(HttpStatus.NOT_IMPLEMENTED_501);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
    }

    @Override
    public void init() throws ServletException {
        this.log = LoggingSystem.getInstance();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String requestURL = req.getRequestURI();
        resp.setStatus(HttpStatus.OK_200);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        //System.out.println(req);

        String requestType = req.getParameter("loginType");

        if(requestType.equals("changePassword")){
            String authHeader = req.getHeader("auth");
            String encodedAuth = authHeader.substring(authHeader.indexOf(' ')+1);

            String username = new String(Base64.getDecoder().decode(encodedAuth));

            //Check if Username exists
            boolean userExists = DataStore.getInstance().getSqlQueries().checkUserExists(username);
            if(userExists){
                //Generate new Password, Store and send as email.
                PasswordGenerator pw = new PasswordGenerator();
                String newPassword = pw.newPassword();
                //DataStore.getInstance().newConnection();

                int id = DataStore.getInstance().getSqlQueries().getUserID(username);

                DataStore.getInstance().getSqlQueries().changePassword(id, newPassword);

                log.infoMessage("Password for "+username+" updated.");
                JSONObject obj = new JSONObject();


                obj.put("request-received", "true");
                obj.put("error-type", "none");
                obj.put("response-code", HttpStatus.OK_200);
                resp.getWriter().write(obj.toString());
            }

        } else{
            String authHeader = req.getHeader("auth"); //u + p sent in request header.
            if(!isNullOrEmpty(authHeader)){
                //System.out.println(authHeader);
                String encodedAuth = authHeader.substring(authHeader.indexOf(' ')+1); //Isolate base64 encoded part of auth header

                String decodedAuth = new String(Base64.getDecoder().decode(encodedAuth));

                String username = decodedAuth.substring(0, decodedAuth.indexOf(':'));
                String password = decodedAuth.substring(decodedAuth.indexOf(':')+1);

                log.infoMessage("Received login request for user " + username);

                //Check against what is in the Database
                if(DataStore.getInstance().getSqlQueries().checkUserCredentials(username, password)){
                    //Login Successful
                    log.infoMessage("User: " + username + " has successfully logged in. Creating token.");
                    //TODO: Complete Tokenize - Check if user already has a token!!
                    resp.setStatus(HttpStatus.OK_200);
                    resp.setContentType("application/json");
                    resp.setCharacterEncoding("UTF-8");

                    //Check if token already exists, if not create it.
                    String authToken = "";
                    if(isNullOrEmpty(TokenStore.getInstance().getUsername(username))){
                        authToken = TokenStore.getInstance().putToken(username);
                        log.infoMessage("Issued token: " + authToken);
                    } else
                    {
                        //Retrieve existing token
                        log.infoMessage("A valid token already exists for this user: " + authToken);
                        authToken = "already exists";
                    }


                    String userLevel = DataStore.getInstance().getSqlQueries().getUserLevel(username);
                    JSONObject obj = new JSONObject();


                    obj.put("request-received", "true");
                    obj.put("error-type", "none");
                    obj.put("response-code", HttpStatus.OK_200);
                    obj.put("auth-token", authToken);
                    obj.put("user-level", userLevel);
                    resp.getWriter().write(obj.toString());

                } else {
                    //Login Unsuccessful.
                    log.infoMessage("Unsuccessful login for user: " + username +
                            "\n Reason: Bad login credentials.");
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
                log.infoMessage("Invalid request received (Bad auth header)");
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
}
