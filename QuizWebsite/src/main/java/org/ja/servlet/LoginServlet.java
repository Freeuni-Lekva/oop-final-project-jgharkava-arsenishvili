package org.ja.servlet;

import org.apache.commons.dbcp2.BasicDataSource;
import org.ja.dao.UsersDao;
import org.ja.model.user.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        UsersDao dao = (UsersDao)getServletContext().getAttribute("dao");
        User user = dao.getUserByUsername(username);  //made getUserByUsername public
        if(user == null || !user.getPasswordHashed().equals(password)) {
            request.setAttribute("error", "Invalid username or password");
            request.getRequestDispatcher("/index.jsp").forward(request, resp);
        }else{
            request.getSession().setAttribute("user", user);
            request.getRequestDispatcher("/userPage.jsp").forward(request, resp);
        }
    }
}
