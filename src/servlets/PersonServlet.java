package servlets;

import dbSystem.DataStore;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PersonServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        String requestUrl = request.getRequestURI();
        String name = requestUrl.substring("/people/".length());

        //Handle request...
        response.getOutputStream().println("{}");
    }



    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String name = request.getParameter("name");
        String firstLine = request.getParameter("about");
        String phone = request.getParameter("phone");
        String postCode = request.getParameter("postcode");
        String email = request.getParameter("email");
        String company = request.getParameter("company");
        String role = request.getParameter("role");
        String notes = request.getParameter("notes");
        int userLevel = 0;

        DataStore.getInstance().editUsersTable(true,0,name,firstLine,phone,postCode,email,company,role,notes);
    }


}
