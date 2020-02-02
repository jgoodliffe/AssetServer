package servlets;

import dbSystem.DataStore;
import org.eclipse.jetty.http.HttpStatus;
import org.json.JSONArray;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

    public class PersonServlet extends HttpServlet {

        @Override
        public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            String requestUrl = request.getRequestURI();
            response.setStatus(HttpStatus.OK_200);
            try{
                int id = Integer.parseInt(requestUrl.substring("/people/".length())); //Get User ID to look up
                JSONArray json = DataStore.getInstance().getSqlQueries().getPerson(id);
                if(json!= null){
                    response.getOutputStream().println(json.toString());
                } else{
                    response.getOutputStream().println("{}");
                }
            } catch(NumberFormatException e){
                //Invalid search criteria
                response.getOutputStream().println("{}");
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
