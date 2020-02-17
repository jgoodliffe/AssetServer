package servlets;

import dbSystem.DataStore;
import org.eclipse.jetty.http.HttpStatus;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * DashboardServlet - Serves the Dashboard GUI requests
 */
public class DashboardServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String requestUrl = req.getRequestURI();
        resp.setStatus(HttpStatus.OK_200);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        try {
            int id = Integer.parseInt(requestUrl.substring("/person/".length())); //Get User ID to look up
            JSONObject json = DataStore.getInstance().getSqlQueries().getPerson(id);
            if (json != null) {
                resp.getOutputStream().println(json.toString());
            } else {
                resp.getOutputStream().println("{}");
            }
        } catch (NumberFormatException e) {
            //All people
            if (requestUrl.substring("/person/".length()).equals("all")) {
                JSONObject json = new JSONObject();
                json.put("people", DataStore.getInstance().getSqlQueries().getAllPeople());
                if (json != null) {
                    resp.getOutputStream().println(json.toString());
                } else {
                    resp.getOutputStream().println("{}");
                }
            } else {
                //Invalid search criteria
                resp.getOutputStream().println("{}");
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }
}
