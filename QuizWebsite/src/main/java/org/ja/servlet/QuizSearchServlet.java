package org.ja.servlet;

import org.ja.dao.QuizzesDao;
import org.ja.model.filters.Filter;
import org.ja.model.filters.FilterBuilder;
import org.ja.model.quiz.Quiz;
import org.ja.utils.Constants;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Servlet responsible for handling quiz search requests.
 * <p>
 * Supports GET requests that take filter parameters from the query string,
 * build a Filter object, retrieve matching quizzes from the database,
 * and forward the results to the quiz-search.jsp page.
 * </p>
 * <p>
 * POST requests are currently not handled and ignored.
 * </p>
 */
@WebServlet("/quiz-search")
public class QuizSearchServlet extends HttpServlet {

    /**
     * Handles HTTP GET requests for searching quizzes based on filters.
     * Builds a Filter object from the request parameters, queries the database
     * using the QuizzesDao, and forwards the results to the JSP page.
     *
     * @param req  the HttpServletRequest containing filter parameters
     * @param resp the HttpServletResponse used to forward to the results page
     * @throws ServletException if a servlet error occurs
     * @throws IOException      if an I/O error occurs during forwarding
     */
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Filter filter = FilterBuilder.build(req);
        QuizzesDao quizzesDao = (QuizzesDao) getServletContext().getAttribute(Constants.ContextAttributes.QUIZZES_DAO);

        List<Quiz> quizzes = quizzesDao.filterQuizzes(filter, Constants.FETCH_LIMIT);

        req.setAttribute("quizzes", quizzes);
        req.getRequestDispatcher("/quiz-search.jsp").forward(req, resp);
    }
}
