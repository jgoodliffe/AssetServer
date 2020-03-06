package servlets;

import LoggingSystem.LoggingSystem;
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

/**
 * UserServlet - Handles all account creation/Modification Commands.
 */
public class UserServlet extends HttpServlet {

    private LoggingSystem log;

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }

    @Override
    public void init() throws ServletException {
        this.log = LoggingSystem.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String requestUrl = request.getRequestURI();


        //Check if the token is valid.
        if(!TokenStore.getInstance().checkToken(request.getHeader("token"))){
            response.setStatus(HttpStatus.BAD_REQUEST_400);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            log.infoMessage("Received an invalid Request - Invalid Token.");
            JSONObject json = new JSONObject();
            json.put("auth", "false");
            response.getOutputStream().println(json.toString());
            return;
        }

        log.infoMessage("Received a Valid Request.");
        response.setStatus(HttpStatus.OK_200);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");


        //String username = DataStore.getInstance().getSqlQueries().getUsernameFromID(int id);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String requestURL = req.getRequestURI();
        String authHeader = req.getHeader("auth"); //Token sent in request header.
        if (!isNullOrEmpty(authHeader)) {
            //Check for Token in database
            String token = authHeader.substring(authHeader.indexOf(' ')+1);
            if(!isNullOrEmpty(TokenStore.getInstance().getUsername(token))){
                String username = req.getParameter("username");
                String password = new String(Base64.getDecoder().decode(req.getParameter("password"))); //Decode password sent over Base64
                try{
                    int userLevel = Integer.parseInt(req.getParameter("userLevel"));

                    //Check all fields not null before sto*red in DB
                    if(!isNullOrEmpty(username) && !isNullOrEmpty(password)){
                        DataStore.getInstance().getSqlQueries().addUser(username, password, userLevel);
                        resp.setStatus(HttpStatus.OK_200);
                        resp.setContentType("application/json");
                        resp.setCharacterEncoding("UTF-8");
                    }
                    resp.setStatus(HttpStatus.BAD_REQUEST_400);
                    resp.setContentType("application/json");
                    resp.setCharacterEncoding("UTF-8");
                } catch (NumberFormatException e){
                    resp.setStatus(HttpStatus.BAD_REQUEST_400);
                    resp.setContentType("application/json");
                    resp.setCharacterEncoding("UTF-8");
                }
            }
        }
        resp.setStatus(HttpStatus.BAD_REQUEST_400);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
    }
}

