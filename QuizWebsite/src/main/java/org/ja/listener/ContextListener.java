package org.ja.listener;

import org.apache.commons.dbcp2.BasicDataSource;
import org.ja.dao.CategoriesDao;
import org.ja.dao.TagsDao;
import org.ja.dao.UsersDao;
import org.ja.utils.Constants;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.sql.SQLException;

@WebListener
public class ContextListener implements ServletContextListener {
    private BasicDataSource ds;

    public void contextInitialized(ServletContextEvent sce) {
        ds = new BasicDataSource();
        ds.setUrl("jdbc:mysql://localhost:3306/ja_project_db");
        ds.setUsername("root");
        ds.setPassword("ja1234");
        sce.getServletContext().setAttribute(Constants.ContextAttributes.USERS_DAO, new UsersDao(ds));
        sce.getServletContext().setAttribute(Constants.ContextAttributes.CATEGORIES_DAO, new CategoriesDao(ds));
        sce.getServletContext().setAttribute(Constants.ContextAttributes.TAGS_DAO, new TagsDao(ds));

    }

    public void contextDestroyed(ServletContextEvent sce) {
        try {
            ds.close();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to close the DataSource in ContextListener" ,e);
        }
    }
}