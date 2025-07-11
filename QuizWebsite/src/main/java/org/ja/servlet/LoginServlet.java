package org.ja.servlet;

import org.ja.dao.UsersDao;
import org.ja.model.data.User;
import org.ja.utils.Constants;
import org.ja.utils.PasswordHasher;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

/**
 * Servlet implementation class LoginServlet.
 * Handles user login requests by validating username and password.
 * On successful login, stores user information in session and redirects to user page.
 * On failure, forwards back to login page with error message.
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    /**
     * Handles HTTP POST requests for login.
     * Reads username and password parameters, trims them,
     * validates credentials using {@link PasswordHasher},
     * and manages session attributes accordingly.
     *
     * @param request  the HttpServletRequest object containing client request data
     * @param resp     the HttpServletResponse object for sending response data
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        UsersDao dao = (UsersDao)getServletContext().getAttribute(Constants.ContextAttributes.USERS_DAO);
        User user = dao.getUserByUsername(username);
        try {
            if(user == null || !PasswordHasher.verifyPassword(password, user.getPasswordHashed(), user.getSalt())) {
                request.setAttribute("error", "Invalid username or password");
                request.getRequestDispatcher("/index.jsp").forward(request, resp);
            }else{
                request.getSession().setAttribute(Constants.SessionAttributes.USER, user);
                request.getRequestDispatcher("/user-page.jsp").forward(request, resp);
            }
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
