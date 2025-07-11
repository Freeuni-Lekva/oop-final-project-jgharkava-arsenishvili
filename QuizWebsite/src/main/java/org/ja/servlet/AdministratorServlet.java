package org.ja.servlet;

import org.ja.dao.*;
import org.ja.model.data.Category;
import org.ja.model.data.Announcement;
import org.ja.model.quiz.Quiz;
import org.ja.model.data.User;
import org.ja.utils.Constants;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet that handles administrative actions such as posting announcements,
 * promoting users to administrators, removing users or quizzes,
 * clearing quiz histories, and managing quiz categories.
 *
 * This servlet processes POST requests to the "/administrator" endpoint
 * and performs different operations based on the "action" parameter in the request.
 */
@WebServlet("/administrator")
public class AdministratorServlet extends HttpServlet {


    /**
     * Handles HTTP POST requests to perform various administrative operations.
     * The supported actions include:
     * <ul>
     *     <li>create - Create a new announcement.</li>
     *     <li>promote - Promote a user to administrator.</li>
     *     <li>removeUser - Remove a user by username.</li>
     *     <li>removeQuiz - Remove a quiz by name.</li>
     *     <li>clearHistory - Clear the history of a quiz.</li>
     *     <li>addCategory - Add a new quiz category.</li>
     * </ul>
     * For each action, appropriate request parameters are expected. After performing
     * the operation, the servlet forwards the request back to the administrator JSP
     * with a status message indicating success or failure.
     *
     * @param request  the HttpServletRequest containing request parameters
     * @param response the HttpServletResponse to write the response
     * @throws ServletException if an input or output error is detected when the servlet handles the POST request
     * @throws IOException      if the request for the POST could not be handled
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        AdministratorsDao adminsDao = (AdministratorsDao) getServletContext().getAttribute(Constants.ContextAttributes.ADMINISTRATORS_DAO);
        QuizzesDao quizzesDao = (QuizzesDao) getServletContext().getAttribute(Constants.ContextAttributes.QUIZZES_DAO);
        UsersDao usersDao = (UsersDao) getServletContext().getAttribute(Constants.ContextAttributes.USERS_DAO);
        AnnouncementsDao announcementsDao = (AnnouncementsDao) getServletContext().getAttribute(Constants.ContextAttributes.ANNOUNCEMENTS_DAO);
        CategoriesDao categoriesDao = (CategoriesDao) getServletContext().getAttribute(Constants.ContextAttributes.CATEGORIES_DAO);
        String action = request.getParameter("action");
        if("create".equals(action)) {
            String text = request.getParameter("announcementText");
            if(text.trim().isEmpty()) {
                request.setAttribute("message", "Cannot post empty announcement");
                request.getRequestDispatcher("/administrator.jsp").forward(request, response);
                return;
            }
            User curUser = (User) request.getSession().getAttribute(Constants.SessionAttributes.USER);
            announcementsDao.insertAnnouncement(new Announcement(curUser.getId(), text));
            request.setAttribute("message", "Successfully posted announcement");
            request.getRequestDispatcher("/administrator.jsp").forward(request, response);
        }else if("promote".equals(action)) {
            String name = request.getParameter("promoteUsername");
            if(name.trim().isEmpty()) {
                request.setAttribute("message", "Username cannot be empty");
                request.getRequestDispatcher("/administrator.jsp").forward(request, response);
                return;
            }
            User user = usersDao.getUserByUsername(name);
            if(user == null) {
                request.setAttribute("message", "User " + name + " was not found");
                request.getRequestDispatcher("/administrator.jsp").forward(request, response);
            }else if(!user.getStatus().equals("administrator")){
                adminsDao.promoteToAdministrator(user.getId());
                request.setAttribute("message", "Successfully promoted user " + name + " to an administrator");
                request.getRequestDispatcher("/administrator.jsp").forward(request, response);
            }else{
                request.setAttribute("message", name + "is already an administrator");
                request.getRequestDispatcher("/administrator.jsp").forward(request, response);
            }
        }else if("removeUser".equals(action)) {
            String name = request.getParameter("removeUsername");
            if(name.trim().isEmpty()) {
                request.setAttribute("message", "Username cannot be empty");
                request.getRequestDispatcher("/administrator.jsp").forward(request, response);
                return;
            }
            User user = usersDao.getUserByUsername(name);
            if(user != null){
                adminsDao.removeUserById(user.getId());
                request.setAttribute("message", "Successfully removed user " + name);
                request.getRequestDispatcher("/administrator.jsp").forward(request, response);
            }else{
                request.setAttribute("message", "User " + name + " was not found");
                request.getRequestDispatcher("/administrator.jsp").forward(request, response);
            }
        }else if("removeQuiz".equals(action)) {
            String name = request.getParameter("removeQuizName");
            if(name.trim().isEmpty()) {
                request.setAttribute("message", "Quiz name cannot be empty");
                request.getRequestDispatcher("/administrator.jsp").forward(request, response);
                return;
            }
            Quiz quiz = quizzesDao.getQuizByName(name);
            if(quiz != null){
                quizzesDao.removeQuizById(quiz.getId());
                request.setAttribute("message", "Successfully removed quiz " + name);
                request.getRequestDispatcher("/administrator.jsp").forward(request, response);
            }else{
                request.setAttribute("message", "Quiz " + name + " was not found");
                request.getRequestDispatcher("/administrator.jsp").forward(request, response);
            }
        }else if("clearHistory".equals(action)) {
            String name = request.getParameter("clearQuizHistoryName");
            if(name.trim().isEmpty()) {
                request.setAttribute("message", "Quiz name cannot be empty");
                request.getRequestDispatcher("/administrator.jsp").forward(request, response);
                return;
            }
            Quiz quiz = quizzesDao.getQuizByName(name);
            if(quiz == null) {
                request.setAttribute("message", "Quiz " + name + " was not found");
                request.getRequestDispatcher("/administrator.jsp").forward(request, response);
            } else {
                adminsDao.clearQuizHistory(quizzesDao.getQuizByName(name).getId());
                request.setAttribute("message", "Successfully cleared history of quiz " + name);
                request.getRequestDispatcher("/administrator.jsp").forward(request, response);
            }
        }else if("addCategory".equals(action)) {
            String name = request.getParameter("addCategoryName");
            if(name.trim().isEmpty()) {
                request.setAttribute("message", "Category name cannot be empty");
                request.getRequestDispatcher("/administrator.jsp").forward(request, response);
                return;
            }
            Category category = categoriesDao.getCategoryByName(name);
            if(category == null){
                categoriesDao.insertCategory(new Category(name));
                request.setAttribute("message", "Successfully added quiz category: " + name);
                request.getRequestDispatcher("/administrator.jsp").forward(request, response);
            }else{
                request.setAttribute("message", "Category " + name + " already exists");
                request.getRequestDispatcher("/administrator.jsp").forward(request, response);
            }
        }
    }
}
