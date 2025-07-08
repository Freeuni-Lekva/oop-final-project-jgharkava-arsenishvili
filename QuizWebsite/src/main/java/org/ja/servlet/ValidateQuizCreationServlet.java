package org.ja.servlet;

import org.ja.dao.QuizzesDao;
import org.ja.dao.TagsDao;
import org.ja.utils.Constants;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/validate-creation")
public class ValidateQuizCreationServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String action = req.getParameter("action");

        if("validate-quiz-name".equals(action)) {
            String quizName = req.getParameter("title");
            QuizzesDao quizzesDao = (QuizzesDao) getServletContext().getAttribute(Constants.ContextAttributes.QUIZZES_DAO);
            boolean exists = quizzesDao.getQuizByName(quizName) != null;
            resp.getWriter().write(Boolean.toString(!exists));
        } else {
            String tagName = req.getParameter("tag-name");
            TagsDao tagsDao = (TagsDao) getServletContext().getAttribute(Constants.ContextAttributes.TAGS_DAO);
            boolean exists = tagsDao.getTagByName(tagName) != null;
            resp.getWriter().write(Boolean.toString(!exists));
        }

    }
}
