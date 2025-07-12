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
import java.sql.SQLException;


/**
 * Servlet responsible for handling user sign-up functionality.
 * It validates whether the username is already taken, hashes the password with salt,
 * stores the new user in the database, and redirects to a success or error page accordingly.
 */
@WebServlet("/signUp")
public class SignUpServlet extends HttpServlet {

    /**
     * Handles the HTTP POST request for user registration.
     * <p>
     * Steps:
     * <ul>
     *     <li>Retrieves username, password, and photo from request parameters.</li>
     *     <li>Checks if the username already exists in the database.</li>
     *     <li>If not, hashes the password with a generated salt.</li>
     *     <li>Creates a new user and inserts it into the database.</li>
     *     <li>Sets the new user in session and forwards to the success page.</li>
     *     <li>If the username exists, forwards back to sign-up page with an error message.</li>
     * </ul>
     *
     * @param request  HttpServletRequest containing user input parameters
     * @param response HttpServletResponse used to forward to the appropriate JSP
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username").trim();
        String password = request.getParameter("password").trim();
        String photo = request.getParameter("photo").trim();

        UsersDao dao = (UsersDao)getServletContext().getAttribute(Constants.ContextAttributes.USERS_DAO);
        User check = dao.getUserByUsername(username);
        if(check != null){
            request.setAttribute("error", "Account with that username already exists");
            request.getRequestDispatcher("/sign-up.jsp").forward(request, response);
        }else{
            User user = null;
            try {
                String salt = PasswordHasher.getSalt();
                String hashedPassword = PasswordHasher.hashPassword(password, salt);
                user = new User(0, username, hashedPassword, salt, null, photo, "user");
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
            try {
                dao.insertUser(user);
                request.getSession().setAttribute(Constants.SessionAttributes.USER, user);
                request.getRequestDispatcher("/sign-up-success.jsp").forward(request, response);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
