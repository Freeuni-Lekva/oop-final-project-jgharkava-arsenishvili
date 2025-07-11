package org.ja.servlet;

import org.ja.dao.UsersDao;
import org.ja.model.data.User;
import org.ja.utils.Constants;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;



/**
 * Servlet to handle user profile photo updates.
 * <p>
 * Processes POST requests that include a new photo URL parameter,
 * updates the photo for the currently logged-in user in the database,
 * updates the user object in the session,
 * and redirects the user back to the user page.
 * If the new photo parameter is missing or null, sends a 400 Bad Request response.
 * </p>
 */
@WebServlet("/upload")
public class UserPageServlet extends HttpServlet {


    /**
     * Handles POST requests to update the user's profile photo.
     *
     * @param req  the HttpServletRequest containing the "newPhoto" parameter
     * @param resp the HttpServletResponse used to send redirects or errors
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs during request processing
     */
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String newPhoto = req.getParameter("newPhoto");
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
