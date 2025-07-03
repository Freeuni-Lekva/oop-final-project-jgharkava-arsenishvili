package org.ja.servlet;

import org.ja.dao.QuizzesDao;
import org.ja.utils.Constants;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/edit-quiz")
public class EditQuizServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        QuizzesDao quizzesDao = (QuizzesDao) getServletContext().getAttribute(Constants.ContextAttributes.QUIZZES_DAO);

        long quizId = Long.parseLong(request.getParameter(Constants.RequestParameters.QUIZ_ID));
        String field = request.getParameter("field");

        switch (field) {
            case "title":
                String newTitle = request.getParameter("title");
                quizzesDao.updateQuizTitle(quizId, newTitle);
                response.setStatus(HttpServletResponse.SC_OK);
                break;
            case "description":
                String newDesc = request.getParameter("description");
                quizzesDao.updateQuizDescription(quizId, newDesc);
                response.setStatus(HttpServletResponse.SC_OK);
                break;
            case "limit":
                int newLimit = Integer.parseInt(request.getParameter("limit"));
                System.out.println(newLimit);
                quizzesDao.updateQuizTimeLimit(quizId, newLimit);
                response.setStatus(HttpServletResponse.SC_OK);
                break;
            case "category":
                long newCategory = Long.parseLong(request.getParameter("category"));
                System.out.println(newCategory);
                quizzesDao.updateQuizCategory(quizId, newCategory);
                response.setStatus(HttpServletResponse.SC_OK);
                break;
            default:
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown field");
        }
    }

}
