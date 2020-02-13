package servlets;

import org.eclipse.jetty.http.HttpStatus;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * UserServlet - Handles all account creation/Modification Commands.
 */
public class UserServlet extends HttpServlet {

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setStatus(HttpStatus.NOT_IMPLEMENTED_501);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String requestURL = req.getRequestURI();
        String authHeader = req.getHeader("auth"); //Token sent in request header.
        if (!isNullOrEmpty(authHeader)) {
            //Check for Token.


        }
        resp.setStatus(HttpStatus.BAD_REQUEST_400);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
    }
}

