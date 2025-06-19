package org.ja.servlet;

import org.ja.dao.QuizzesDao;
import org.ja.model.Filters.Filter;
import org.ja.model.Filters.FilterBuilder;
import org.ja.model.quiz.Quiz;
import org.ja.utils.Constants;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/quiz-search")
public class QuizSearchServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Filter filter = FilterBuilder.build(req);
        QuizzesDao quizzesDao = (QuizzesDao) getServletContext().getAttribute(Constants.ContextAttributes.QUIZZES_DAO);

        System.out.println(filter.buildOrderByClause());
        System.out.println(filter.buildWhereClause());
        System.out.println(filter.getParameters());


        List<Quiz> quizzes = quizzesDao.filterQuizzes(filter);

        req.setAttribute("quizzes", quizzes);
        req.getRequestDispatcher("/quiz-search.jsp").forward(req, resp);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        return;
    }
}
