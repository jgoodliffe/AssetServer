package servlets;

import LoggingSystem.LoggingSystem;
import authentication.TokenStore;
import dbSystem.DataStore;
import org.eclipse.jetty.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import javax.management.openmbean.TabularDataSupport;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

    public class PersonServlet extends HttpServlet {

        private LoggingSystem log;

        @Override
        public void init() throws ServletException {
            this.log = LoggingSystem.getInstance();
        }

        @Override
        public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
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
                int id = Integer.parseInt(requestUrl.substring("/person/".length())); //Get User ID to look up
                JSONObject json = DataStore.getInstance().getSqlQueries().getPerson(id);
                json.put("response-code", HttpStatus.OK_200);
                if(json!= null){
                    response.getOutputStream().println(json.toString());
                } else{
                    response.getOutputStream().println("{}");
                }
            } catch(NumberFormatException e){
                //All people
                if(requestUrl.substring("/person/".length()).equals("all")){
                    JSONObject json = new JSONObject();
                    json.put("people",DataStore.getInstance().getSqlQueries().getAllPeople());
                    json.put("response-code", HttpStatus.OK_200);
                    if(json!= null){
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



        @Override
        public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            int uid = Integer.parseInt(request.getParameter("uid"));
            String firstName = request.getParameter("firstName");
            String lastName = request.getParameter("lastName");
            String email = request.getParameter("email");
            String firstLine = request.getParameter("firstLine");
            String postCode = request.getParameter("postCode");
            String tel = request.getParameter("tel");
            String company = request.getParameter("company");
            String role = request.getParameter("role");
            String notes = request.getParameter("notes");

            DataStore.getInstance().getSqlQueries().editPeopleTable(true, 0, uid, firstName, lastName, firstLine,
                    postCode, email, tel, company, role, notes);
        }


    }
