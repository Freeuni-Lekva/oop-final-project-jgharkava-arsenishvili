package org.ja.servlet;

import org.apache.commons.dbcp2.BasicDataSource;
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

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
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
