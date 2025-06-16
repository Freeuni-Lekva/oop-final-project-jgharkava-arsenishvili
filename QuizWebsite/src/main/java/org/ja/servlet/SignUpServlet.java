package org.ja.servlet;

import org.ja.dao.UsersDao;
import org.ja.model.user.User;
import org.ja.utils.Constants;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/signUp")
public class SignUpServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        UsersDao dao = (UsersDao)getServletContext().getAttribute(Constants.ContextAttributes.USERS_DAO);
        User check = dao.getUserByUsername(username);
        if(check != null){
            request.setAttribute("error", "Account with that username already exists");
            request.getRequestDispatcher("/sign-up.jsp").forward(request, response);
        }else{
            User user = new User(0, username, password, "", "", "");
            try {
                dao.insertUser(user);
                request.getSession().setAttribute("username", username);
                request.getRequestDispatcher("/sign-up-success.jsp").forward(request, response);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
