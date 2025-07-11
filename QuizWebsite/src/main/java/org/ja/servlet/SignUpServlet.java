package org.ja.servlet;

import org.ja.dao.UsersDao;
import org.ja.model.user.User;
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

@WebServlet("/signUp")
public class SignUpServlet extends HttpServlet {
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
