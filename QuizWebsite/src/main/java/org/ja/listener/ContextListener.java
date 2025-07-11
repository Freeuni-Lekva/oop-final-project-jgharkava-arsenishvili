package org.ja.listener;

import org.apache.commons.dbcp2.BasicDataSource;
import org.ja.dao.*;
import org.ja.utils.Constants;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.sql.SQLException;

/**
 * Application context listener that initializes and destroys shared resources for the web application.
 *
 * <p>On application startup, it sets up a shared {@link BasicDataSource} and initializes all DAO instances,
 * storing them in the servlet context under the keys defined in {@link org.ja.utils.Constants.ContextAttributes}.</p>
 *
 * <p>On application shutdown, it properly closes the {@link BasicDataSource} to release database connections.</p>
 */
@WebListener
public class ContextListener implements ServletContextListener {
    private BasicDataSource ds;


    /**
     * Initializes the application context by setting up the database connection pool and
     * registering all DAO instances as servlet context attributes.
     *
     * @param sce the ServletContextEvent containing the ServletContext to be initialized
     */
    public void contextInitialized(ServletContextEvent sce) {
        ds = new BasicDataSource();
        ds.setUrl("jdbc:mysql://localhost:3306/ja_project_db");
        ds.setUsername("root");
        ds.setPassword("ja1234");

        sce.getServletContext().setAttribute(Constants.ContextAttributes.USERS_DAO, new UsersDao(ds));
        sce.getServletContext().setAttribute(Constants.ContextAttributes.CATEGORIES_DAO, new CategoriesDao(ds));
        sce.getServletContext().setAttribute(Constants.ContextAttributes.TAGS_DAO, new TagsDao(ds));
        sce.getServletContext().setAttribute(Constants.ContextAttributes.ACHIEVEMENTS_DAO, new AchievementsDao(ds));
        sce.getServletContext().setAttribute(Constants.ContextAttributes.ANSWERS_DAO, new AnswersDao(ds));
        sce.getServletContext().setAttribute(Constants.ContextAttributes.ANNOUNCEMENTS_DAO, new AnnouncementsDao(ds));
        sce.getServletContext().setAttribute(Constants.ContextAttributes.CHALLENGES_DAO, new ChallengesDao(ds));
        sce.getServletContext().setAttribute(Constants.ContextAttributes.HISTORIES_DAO, new HistoriesDao(ds));
        sce.getServletContext().setAttribute(Constants.ContextAttributes.FRIENDSHIPS_DAO, new FriendShipsDao(ds));
        sce.getServletContext().setAttribute(Constants.ContextAttributes.ADMINISTRATORS_DAO, new AdministratorsDao(ds));
        sce.getServletContext().setAttribute(Constants.ContextAttributes.MATCHES_DAO, new MatchesDao(ds));
        sce.getServletContext().setAttribute(Constants.ContextAttributes.QUIZZES_DAO, new QuizzesDao(ds));
        sce.getServletContext().setAttribute(Constants.ContextAttributes.USER_ACHIEVEMENTS_DAO, new UserAchievementsDao(ds));
        sce.getServletContext().setAttribute(Constants.ContextAttributes.QUIZ_TAG_DAO, new QuizTagsDao(ds));
        sce.getServletContext().setAttribute(Constants.ContextAttributes.QUIZ_RATING_DAO, new QuizRatingsDao(ds));
        sce.getServletContext().setAttribute(Constants.ContextAttributes.MESSAGE_DAO, new MessageDao(ds));
        sce.getServletContext().setAttribute(Constants.ContextAttributes.QUESTIONS_DAO, new QuestionDao(ds));
    }


    /**
     * Cleans up the application context by closing the database connection pool.
     *
     * @param sce the ServletContextEvent containing the ServletContext to be destroyed
     */
    public void contextDestroyed(ServletContextEvent sce) {
        try {
            ds.close();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to close the DataSource in ContextListener" ,e);
        }
    }
}