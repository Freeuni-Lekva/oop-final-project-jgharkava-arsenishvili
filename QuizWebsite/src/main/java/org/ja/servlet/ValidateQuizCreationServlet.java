package org.ja.servlet;

import org.ja.dao.QuizzesDao;
import org.ja.dao.TagsDao;
import org.ja.utils.Constants;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet to validate uniqueness of quiz names and tag names during quiz creation.
 * <p>
 * Processes GET requests with a parameter "action" that determines
 * whether to validate a quiz title or a tag name.
 * Returns "true" if the provided name is available (does not exist),
 * otherwise returns "false".
 * </p>
 * <p>
 * Expected query parameters:
 * <ul>
 *   <li>action: "validate-quiz-name" or anything else (to validate tag name)</li>
 *   <li>title: the quiz name to validate (if action is "validate-quiz-name")</li>
 *   <li>tag-name: the tag name to validate (if action is not "validate-quiz-name")</li>
 * </ul>
 *
 */
@WebServlet("/validate-creation")
public class ValidateQuizCreationServlet extends HttpServlet {

    /**
     * Handles GET requests for validation of quiz or tag names.
     *
     * @param req  the HttpServletRequest containing the parameters "action", and either "title" or "tag-name"
     * @param resp the HttpServletResponse used to write the validation result ("true" or "false")
     * @throws IOException if an I/O error occurs while writing the response
     */
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
