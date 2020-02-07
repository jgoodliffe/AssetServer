package servlets;

import org.eclipse.jetty.http.HttpStatus;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;

public class LoginServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setStatus(HttpStatus.NOT_IMPLEMENTED_501);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String requestURL = req.getRequestURI();
        resp.setStatus(HttpStatus.OK_200);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        String authHeader = req.getHeader("auth"); //u + p sent in request header.

        String encodedAuth = authHeader.substring(authHeader.indexOf(' ')+1); //Isolate base64 encoded part of auth header

        String decodedAuth = new String(Base64.getDecoder().decode(encodedAuth));

        String username = decodedAuth.substring(0, decodedAuth.indexOf(':'));
        String password = decodedAuth.substring(decodedAuth.indexOf(':')+1);

        System.out.println("Received uName = "+username+" and pWord = "+password);
    }
}
