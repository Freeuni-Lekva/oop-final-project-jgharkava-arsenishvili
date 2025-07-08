package org.ja.servlet;

import org.ja.dao.AdministratorsDao;
import org.ja.dao.AnnouncementsDao;
import org.ja.dao.QuizzesDao;
import org.ja.dao.UsersDao;
import org.ja.model.OtherObjects.Announcement;
import org.ja.model.quiz.Quiz;
import org.ja.model.user.User;
import org.ja.utils.Constants;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;

@WebServlet("/administrator")
public class AdministratorServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        AdministratorsDao adminsDao = (AdministratorsDao) getServletContext().getAttribute(Constants.ContextAttributes.ADMINISTRATORS_DAO);
        QuizzesDao quizzesDao = (QuizzesDao) getServletContext().getAttribute(Constants.ContextAttributes.QUIZZES_DAO);
        UsersDao usersDao = (UsersDao) getServletContext().getAttribute(Constants.ContextAttributes.USERS_DAO);
        AnnouncementsDao announcementsDao = (AnnouncementsDao) getServletContext().getAttribute(Constants.ContextAttributes.ANNOUNCEMENTS_DAO);
        String action = request.getParameter("action");
        if("create".equals(action)) {
            String text = request.getParameter("announcementText");
            Announcement announcement = new Announcement();
            User curUser = (User) request.getSession().getAttribute(Constants.SessionAttributes.USER);
            announcementsDao.insertAnnouncement(new Announcement(0, curUser.getId(), text, new Timestamp(System.currentTimeMillis())));
            request.setAttribute("message", "Successfully posted announcement");
            request.getRequestDispatcher("/administrator.jsp").forward(request, response);
        }else if("promote".equals(action)) {
            String name = request.getParameter("promoteUsername");
            User user = usersDao.getUserByUsername(name);
            if(!user.getStatus().equals("administrator")){
                adminsDao.promoteToAdministrator(user.getId());
                request.setAttribute("message", "Successfully promoted user " + name + " to an administrator");
                request.getRequestDispatcher("/administrator.jsp").forward(request, response);
            }else{
                request.setAttribute("message", name + "is already an administrator");
                request.getRequestDispatcher("/administrator.jsp").forward(request, response);
            }
        }else if("removeUser".equals(action)) {
            String name = request.getParameter("removeUsername");
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
            Quiz quiz = quizzesDao.getQuizByName(name);
            if(quiz != null){
                quizzesDao.removeQuizByName(name);
                request.setAttribute("message", "Successfully removed quiz " + name);
                request.getRequestDispatcher("/administrator.jsp").forward(request, response);
            }else{
                request.setAttribute("message", "Quiz " + name + " was not found");
                request.getRequestDispatcher("/administrator.jsp").forward(request, response);
            }
        }else if("clearHistory".equals(action)) {
            String name = request.getParameter("clearQuizHistoryName");
            Quiz quiz = quizzesDao.getQuizByName(name);
            if(quiz != null && quiz.getParticipantCount() != 0){
                adminsDao.clearQuizHistory(quizzesDao.getQuizByName(name).getId());
                request.setAttribute("message", "Successfully cleared history of quiz " + name);
                request.getRequestDispatcher("/administrator.jsp").forward(request, response);
            }else {
                request.setAttribute("message", "Quiz " + name + " has no history");
                request.getRequestDispatcher("/administrator.jsp").forward(request, response);
            }
        }
    }
}
