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
import java.util.List;

/**
 * Servlet responsible for initializing a quiz-taking session.
 * <p>
 * It retrieves the quiz and its associated questions from the database,
 * applies question ordering (randomized or default),
 * initializes the user session attributes needed for taking the quiz,
 * including responses, grades, timing, and current question index,
 * and finally forwards the user to the appropriate quiz page (single question or all questions).
 * </p>
 */
@WebServlet("/start-quiz")
public class StartQuizServlet extends HttpServlet {


    /**
     * Handles GET requests to start a quiz.
     * <p>
     * The method performs the following steps:
     * <ul>
     *   <li>Fetches the quiz by ID from the request parameter.</li>
     *   <li>Retrieves the list of questions for the quiz.</li>
     *   <li>Randomizes question order if specified by the quiz settings.</li>
     *   <li>Initializes session attributes for:
     *       <ul>
     *          <li>The quiz object</li>
     *          <li>The list of questions</li>
     *          <li>Current question index (starting at 0)</li>
     *          <li>User responses (empty list)</li>
     *          <li>Grades and detailed response grades (empty lists)</li>
     *          <li>Quiz mode (set to TAKING)</li>
     *          <li>Time limit in seconds and start time (if applicable)</li>
     *       </ul>
     *   </li>
     *   <li>Forwards to either the single-question page or the all-questions page
     *       depending on the quiz's question placement setting.</li>
     * </ul>
     *
     *
     * @param req  the HttpServletRequest containing the quiz ID parameter
     * @param resp the HttpServletResponse used to forward to the quiz page
     * @throws ServletException if an error occurs during forwarding
     * @throws IOException      if an I/O error occurs during forwarding
     */
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long quizId = Long.parseLong(req.getParameter(Constants.RequestParameters.QUIZ_ID));
        Quiz quiz  = ((QuizzesDao) getServletContext().getAttribute(Constants.ContextAttributes.QUIZZES_DAO)).getQuizById(quizId);
        QuestionDao questionDao = (QuestionDao) getServletContext().getAttribute(Constants.ContextAttributes.QUESTIONS_DAO);
        List<Question> questions = questionDao.getQuizQuestions(quizId);

        HttpSession session = req.getSession();

        if("randomized".equals(quiz.getQuestionOrder()))
            Collections.shuffle(questions);

        session.setAttribute(Constants.SessionAttributes.QUIZ, quiz);
        session.setAttribute(Constants.SessionAttributes.QUESTIONS, questions);
        session.setAttribute(Constants.SessionAttributes.CURRENT_QUESTION_INDEX, 0);
        session.setAttribute(Constants.SessionAttributes.RESPONSES, new ArrayList<Response>());
        session.setAttribute("grades", new ArrayList<Integer>());
        session.setAttribute("responseGrades", new ArrayList<ArrayList<Integer>>());
        session.setAttribute(Constants.SessionAttributes.QUIZ_MODE, Constants.QuizMode.TAKING);

        int timeLimitInMinutes = quiz.getTimeInMinutes();

        if(timeLimitInMinutes != 0) {
            session.setAttribute("time-limit-in-seconds", timeLimitInMinutes * 60);
            session.setAttribute("start-time", System.currentTimeMillis());
        }

        if(("one-page").equals(quiz.getQuestionPlacement())) {
            req.getRequestDispatcher("/all-questions-page.jsp").forward(req, resp);
        } else {
            req.getRequestDispatcher("/single-question-page.jsp").forward(req, resp);
        }
    }
}
