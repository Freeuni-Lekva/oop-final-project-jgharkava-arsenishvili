package org.ja.listener;

import org.apache.commons.dbcp2.BasicDataSource;
import org.ja.dao.*;
import org.ja.utils.Constants;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.sql.SQLException;

@WebListener
public class ContextListener implements ServletContextListener {
    BasicDataSource ds;

    public void contextInitialized(ServletContextEvent sce) {
        ds = new BasicDataSource();
        ds.setUrl("jdbc:mysql://localhost:3306/ja_project_db");
        ds.setUsername("root");
        ds.setPassword("ja1234");
        sce.getServletContext().setAttribute(Constants.ContextAttributes.ANNOUNCEMENTS_DAO, new AnnouncementsDao(ds));
        sce.getServletContext().setAttribute(Constants.ContextAttributes.USERS_DAO, new UsersDao(ds));
        sce.getServletContext().setAttribute(Constants.ContextAttributes.QUIZZES_DAO, new QuizzesDao(ds));
        sce.getServletContext().setAttribute(Constants.ContextAttributes.HISTORIES_DAO, new HistoriesDao(ds));
        sce.getServletContext().setAttribute(Constants.ContextAttributes.MESSAGE_DAO, new MessageDao(ds));
        sce.getServletContext().setAttribute(Constants.ContextAttributes.CHALLENGES_DAO, new ChallengesDao(ds));
        sce.getServletContext().setAttribute(Constants.ContextAttributes.USER_ACHIEVEMENTS_DAO, new UserAchievementsDao(ds));
        sce.getServletContext().setAttribute(Constants.ContextAttributes.ACHIEVEMENTS_DAO, new AchievementsDao(ds));
        sce.getServletContext().setAttribute(Constants.ContextAttributes.FRIENDSHIPS_DAO, new FriendShipsDao(ds));
    }

    public void contextDestroyed(ServletContextEvent sce) {
        try {
            ds.close();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to close the DataSource in ContextListener" ,e);
        }
    }
}