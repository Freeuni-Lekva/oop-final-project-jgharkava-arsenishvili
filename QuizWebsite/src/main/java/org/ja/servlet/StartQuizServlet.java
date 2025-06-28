package org.ja.servlet;

import org.ja.dao.QuestionDao;
import org.ja.dao.QuizzesDao;
import org.ja.model.quiz.Quiz;
import org.ja.model.quiz.question.Question;
import org.ja.model.quiz.response.Response;
import org.ja.utils.Constants;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

@WebServlet("/start-quiz")
public class StartQuizServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long quizId = Long.parseLong(req.getParameter(Constants.RequestParameters.QUIZ_ID));
        Quiz quiz  = ((QuizzesDao) getServletContext().getAttribute(Constants.ContextAttributes.QUIZZES_DAO)).getQuizById(quizId);
        QuestionDao questionDao = (QuestionDao) getServletContext().getAttribute(Constants.ContextAttributes.QUESTIONS_DAO);
        ArrayList<Question> questions = questionDao.getQuizQuestions(quizId);

        HttpSession session = req.getSession();
        session.setAttribute(Constants.SessionAttributes.QUIZ, quiz);

        if("randomized".equals(quiz.getQuestionOrder()))
            Collections.shuffle(questions);

        session.setAttribute(Constants.SessionAttributes.QUESTIONS, questions);
        session.setAttribute(Constants.SessionAttributes.CURRENT_QUESTION_INDEX, 0);
        session.setAttribute(Constants.SessionAttributes.RESPONSES, new ArrayList<Response>());

        req.getRequestDispatcher("/single-question-page.jsp").forward(req, resp);
//        req.getRequestDispatcher("/all-questions-page.jsp").forward(req, resp);

/*        if("one-page".equals(quiz.getQuestionPlacement())) {
            req.getRequestDispatcher("/all-questions-page.jsp").forward(req, resp);
        } else {
            req.getRequestDispatcher("/single-question-page.jsp").forward(req, resp);
        }*/
    }
}
