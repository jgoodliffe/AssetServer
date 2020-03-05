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

/**
 * UserServlet - Handles all account creation/Modification Commands.
 */
public class EventServlet extends HttpServlet {

    private LoggingSystem log;

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

        try{
            //Parse for a specific event
            int id = Integer.parseInt(requestUrl.substring("/person/".length())); //Get Event ID to look up
            JSONObject json = DataStore.getInstance().getSqlQueries().getEvent(id);
            if(json!= null){
                response.getOutputStream().println(json.toString());
            } else{
                response.getOutputStream().println("{}");
            }
        } catch(NumberFormatException e){
            //Upcoming Events
            if(requestUrl.substring("/person/".length()).equals("upcoming")){
                log.infoMessage("Upcoming Events requested...");
                JSONObject json = new JSONObject();
                json.put("events",DataStore.getInstance().getSqlQueries().getUpcomingEvents());
                if(json!= null){
                    System.out.println(json);
                    response.getOutputStream().println(json.toString());
                } else{
                    response.getOutputStream().println("{}");
                }
            } else if(requestUrl.substring("/person/".length()).equals("all")){
                log.infoMessage("All Events requested...");
                JSONObject json = new JSONObject();
                json.put("events",DataStore.getInstance().getSqlQueries().getAllEvents());
                if(json!= null){
                    System.out.println(json);
                    response.getOutputStream().println(json.toString());
                } else{
                    response.getOutputStream().println("{}");
                }
            } else{
                //Invalid search criteria
                response.getOutputStream().println("{}");
            }
        }
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setStatus(HttpStatus.NOT_IMPLEMENTED_501);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
    }
}

