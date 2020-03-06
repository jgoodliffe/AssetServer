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
public class AssetServlet extends HttpServlet {
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

        log.infoMessage("Received a Request - ASSETS");
        response.setStatus(HttpStatus.OK_200);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        if(requestUrl.substring("/assets/".length()).equals("categories")) {
            log.infoMessage("Returning ASSET Categories.");
            JSONObject json = new JSONObject();
            json.put("response-code", HttpStatus.OK_200);
            json.put("categories", DataStore.getInstance().getSqlQueries().getAssetCategories());
            if(json!= null){
                System.out.println(json);
                response.getOutputStream().println(json.toString());
                return;
            } else{
                response.getOutputStream().println("{}");
                return;
            }
        } else if (requestUrl.substring("/assets/".length()).equals("edit")){
            return;
        }

        //Return all Assets
        log.infoMessage("Returning All ASSETS");
        if(!request.getParameter("category").isEmpty()){
            //Return Assets by Category
            log.infoMessage("Returning ASSETS by Category.");
            JSONObject json = new JSONObject();
            json.put("response-code", HttpStatus.OK_200);
            json.put("assets", DataStore.getInstance().getSqlQueries().getAssetsByCategory(request.getParameter("category")));
            if(json!= null){
                System.out.println(json);
                response.getOutputStream().println(json.toString());
            } else{
                response.getOutputStream().println("{}");
            }

        } else{
            //Return All Assets
            log.infoMessage("Returning ALL ASSETS");
            JSONObject json = new JSONObject();
            json.put("response-code", HttpStatus.OK_200);
            json.put("assets", DataStore.getInstance().getSqlQueries().getAllAssets());
            if(json!= null){
                System.out.println(json);
                response.getOutputStream().println(json.toString());
            } else{
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

