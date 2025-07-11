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

@WebServlet("/upload")
public class UserPageServlet extends HttpServlet {
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String newPhoto = req.getParameter("newPhoto").trim();
        UsersDao dao = (UsersDao)getServletContext().getAttribute(Constants.ContextAttributes.USERS_DAO);
        User curUser = (User) req.getSession().getAttribute(Constants.SessionAttributes.USER);
        if(newPhoto != null){
            dao.updatePhoto(newPhoto, curUser.getId());
            curUser.setPhoto(newPhoto);
            resp.sendRedirect("/user-page.jsp");
            return;
        }
        resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid update");
    }
}
