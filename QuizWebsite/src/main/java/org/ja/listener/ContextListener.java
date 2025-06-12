package org.ja.listener;

import org.apache.commons.dbcp2.BasicDataSource;
import org.ja.dao.UsersDao;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class ContextListener implements ServletContextListener {
    public void contextInitialized(ServletContextEvent sce) {
        BasicDataSource ds = new BasicDataSource();
        ds.setUrl("jdbc:mysql://localhost:3306/ja_project_db");
        ds.setUsername("root");
        ds.setPassword("ja1234");
        UsersDao dao = new UsersDao(ds);
        sce.getServletContext().setAttribute("dao", dao);
    }
    public void contextDestroyed(ServletContextEvent sce) {}
}