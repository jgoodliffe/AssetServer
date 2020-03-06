package servlets;

import LoggingSystem.LoggingSystem;
import authentication.TokenStore;
import dbSystem.DataStore;
import org.eclipse.jetty.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.List;

public class UtilityServlet extends HttpServlet {
    private LoggingSystem log;

    @Override
    public void init() throws ServletException {
        this.log = LoggingSystem.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //Not implemented
        resp.setStatus(HttpStatus.NOT_IMPLEMENTED_501);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        log.infoMessage("Received an invalid Request - Get not implemented");
        return;
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        //Check if the token is valid.
        if (!TokenStore.getInstance().checkToken(request.getHeader("token"))) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            JSONObject obj = new JSONObject();
            obj.put("request-received", "true");
            obj.put("error-type", "Invalid token supplied. Please try to log in again!");
            obj.put("response-code", HttpStatus.BAD_REQUEST_400);
            response.getWriter().write(obj.toString());
            return;
        }

        //Get substring and direct to relevant utility
        String utility = request.getRequestURI().substring("/utilities/".length());
        if(utility.isEmpty()) {
            response.setStatus(HttpStatus.BAD_REQUEST_400);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            log.infoMessage("Received an invalid Request - No Utility Specified.");
            return;
        }
        log.infoMessage("Received a Valid Request - "+utility);
        if(utility.equals("changePassword")){
            changePasswordHandler(request.getHeader("currentPassword"), request.getHeader("newPassword"), request.getHeader("token"), response);
        } else if(utility.equals("eventTypes")){
            getEventTypesHandler(request, response);
        }else{
            response.setStatus(HttpStatus.NOT_IMPLEMENTED_501);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            log.infoMessage("Received an invalid Request - Unimplemented Utility");
            return;
        }
    }

    /**
     * getEventTypes - Returns a list of event types.
     * @param request
     * @param response
     */
    private void getEventTypesHandler(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JSONObject json = new JSONObject();
        JSONArray eventTypesArray = new JSONArray();
        List<String> eventTypesList = DataStore.getEventTypes();
        for (int i = 0; i < eventTypesList.size(); i++) {
            eventTypesArray.put(eventTypesList.get(i));
        }
        try {
            log.infoMessage("Received a Valid Request.");
            response.setStatus(HttpStatus.OK_200);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            json.put("event-types", eventTypesArray);
            json.put("response-code", HttpStatus.OK_200);
            response.getOutputStream().println(json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * Changes a password
     * @param currentPassword - a user's current password
     * @param newPassword - a user's new password
     */
    private void changePasswordHandler(String currentPassword, String newPassword, String token, HttpServletResponse response) throws IOException{
        currentPassword = new String(Base64.getDecoder().decode(currentPassword));
        newPassword = new String(Base64.getDecoder().decode(newPassword));
        String username = TokenStore.getInstance().getUsername(token);
        System.out.println(username+ " "+currentPassword);
        if(DataStore.getInstance().getSqlQueries().checkUserCredentials(username, currentPassword)){
            int userID = DataStore.getInstance().getSqlQueries().getUserID(username);
            Boolean changedPassword = DataStore.getInstance().getSqlQueries().changePassword(userID, newPassword);
            if(changedPassword){
                response.setStatus(HttpStatus.OK_200);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                log.infoMessage("Successfully changed user's password.");

                JSONObject obj = new JSONObject();
                obj.put("request-received", "true");
                obj.put("error-type", "none");
                obj.put("response-code", HttpStatus.OK_200);
                response.getWriter().write(obj.toString());
                return;
            }
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            JSONObject obj = new JSONObject();
            obj.put("request-received", "true");
            obj.put("error-type", "Error Changing Password - Server side.");
            obj.put("response-code", HttpStatus.INTERNAL_SERVER_ERROR_500);
            response.getWriter().write(obj.toString());
            log.infoMessage("Error changing user's password.");
            return;
        } else{
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            JSONObject obj = new JSONObject();
            obj.put("request-received", "true");
            obj.put("error-type", "Incorrect Login Credentials!");
            obj.put("response-code", HttpStatus.BAD_REQUEST_400);
            response.getWriter().write(obj.toString());
        }
        return;
    }
}
