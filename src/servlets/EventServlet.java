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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * UserServlet - Handles all account creation/Modification Commands.
 */
public class EventServlet extends HttpServlet {

    private LoggingSystem log;

    @Override
    public void init() throws ServletException {
        this.log = LoggingSystem.getInstance();
    }
    public static boolean isNullOrEmpty(String str) {
        if(str != null && !str.trim().isEmpty())
            return false;
        return true;
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

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        if(!request.getParameter("request-type").isEmpty()){
            switch(request.getParameter("request-type")){
                case "delete":
                    if(!isNullOrEmpty(request.getParameter("eventID"))){
                        try{
                            int eventID = Integer.parseInt(request.getParameter("eventID"));
                            if(DataStore.getInstance().getSqlQueries().deleteEvent(eventID)){
                                JSONObject obj = new JSONObject();
                                obj.put("request-received", "true");
                                obj.put("error-type", "none");
                                obj.put("response-code", HttpStatus.OK_200);
                                response.getWriter().write(obj.toString());
                                log.infoMessage("Successfully deleted event.");
                                break;
                            } else{
                                JSONObject obj = new JSONObject();
                                obj.put("request-received", "true");
                                obj.put("error-type", "Error Deleting Event - Server Error.");
                                obj.put("response-code", HttpStatus.INTERNAL_SERVER_ERROR_500);
                                response.getWriter().write(obj.toString());
                                log.infoMessage("Error deleting event - Server");
                            }
                        } catch(NumberFormatException e){
                            JSONObject obj = new JSONObject();
                            obj.put("request-received", "true");
                            obj.put("error-type", "Error Deleting Event - Invalid ID");
                            obj.put("response-code", HttpStatus.BAD_REQUEST_400);
                            response.getWriter().write(obj.toString());
                            log.infoMessage("Error deleting event - invalid ID given.");
                            break;
                        }
                    }
                    break;

                case "create":
                    String eventName = request.getParameter("eventName");
                    String startTime = request.getParameter("startTime");
                    String finishTime = request.getParameter("finishTime");
                    String notes = request.getParameter("notes");
                    String projectManager = request.getParameter("projectManager");

                    if(DataStore.getInstance().getSqlQueries().addEvent(eventName, startTime, finishTime, notes, projectManager)) {
                        JSONObject obj = new JSONObject();
                        obj.put("request-received", "true");
                        obj.put("error-type", "none");
                        obj.put("response-code", HttpStatus.OK_200);
                        response.getWriter().write(obj.toString());
                        log.infoMessage("Successfully added event.");
                    } else{
                        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR_500);
                        JSONObject obj = new JSONObject();
                        obj.put("request-received", "true");
                        obj.put("error-type", "Error Adding Event - Server Error");
                        obj.put("response-code", HttpStatus.INTERNAL_SERVER_ERROR_500);
                        response.getWriter().write(obj.toString());
                        log.infoMessage("Error Adding event - Server Error");
                    }
                    break;

                case "edit":
                    log.infoMessage("EDIT EVENT not yet implemented");
                    response.setStatus(HttpStatus.NOT_IMPLEMENTED_501);
                    response.getOutputStream().println("{}");
                    break;

                default:
                    break;
            }
        }
    }
}

