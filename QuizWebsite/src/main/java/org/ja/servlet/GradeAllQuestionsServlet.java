package org.ja.servlet;

import org.ja.dao.AnswersDao;
import org.ja.dao.HistoriesDao;
import org.ja.dao.MatchesDao;
import org.ja.model.data.Answer;
import org.ja.model.data.History;
import org.ja.model.data.Match;
import org.ja.model.quiz.Quiz;
import org.ja.model.quiz.question.Question;
import org.ja.model.quiz.response.Response;
import org.ja.model.quiz.response.ResponseBuilder;
import org.ja.model.data.User;
import org.ja.utils.Constants;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


/**
 * Servlet that handles grading of all questions in a single-page quiz submission.
 *
 * <p>
 * This servlet retrieves the user's responses from the request, compares them
 * against correct answers or matches from the database, computes the grades per question,
 * and stores grading results in the session.
 * </p>
 *
 * <p>
 * Additionally, it records the quiz completion history, including total score
 * and time spent, in the database, then redirects the user to the quiz result page.
 * </p>
 */
@WebServlet("/grade-single-page-quiz")
public class GradeAllQuestionsServlet extends HttpServlet {


    /**
     * Processes POST request to grade the entire quiz at once.
     *
     * <p>
     * Steps:
     * <ol>
     *   <li>Retrieve quiz questions and user responses from session and request</li>
     *   <li>For each question, fetch correct answers/matches from the database</li>
     *   <li>Compute grade for each question and store detailed grades</li>
     *   <li>Calculate total score and time spent, update quiz history in the database</li>
     *   <li>Store grading results in session attributes</li>
     *   <li>Redirect to the quiz results page</li>
     * </ol>
     * </p>
     *
     * @param req  HttpServletRequest containing user responses and session data
     * @param resp HttpServletResponse used to redirect to the quiz result page
     * @throws ServletException if servlet-specific error occurs
     * @throws IOException      if I/O error occurs during request processing
     */
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();

        MatchesDao matchesDao = (MatchesDao) getServletContext().getAttribute(Constants.ContextAttributes.MATCHES_DAO);
        AnswersDao answersDao = (AnswersDao) getServletContext().getAttribute(Constants.ContextAttributes.ANSWERS_DAO);

        List<Question> questions = (List<Question>) session.getAttribute(Constants.SessionAttributes.QUESTIONS);
        List<Response> responses = ResponseBuilder.buildResponse(req);

        List<Integer> grades = new ArrayList<>();
        List<List<Integer>> responseGrades = new ArrayList<>();

        for(int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            Response response = responses.size() > i ? responses.get(i) : new Response();
            int grade = 0;
            List<Integer> respGrades;

            if(Constants.QuestionTypes.MATCHING_QUESTION.equals(question.getQuestionType())) {
                List<Match> matches = matchesDao.getQuestionMatches(question.getQuestionId());
                respGrades = question.gradeResponse(matches, response);
            } else {
                List<Answer> answers = answersDao.getQuestionAnswers(question.getQuestionId());
                respGrades = question.gradeResponse(answers, response);
            }

            grade = Math.max(0, respGrades.stream().mapToInt(Integer::intValue).sum());
            grades.add(grade);
            responseGrades.add(respGrades);
        }

        session.setAttribute("grades", grades);
        session.setAttribute("responseGrades", responseGrades);
        session.setAttribute(Constants.SessionAttributes.RESPONSES, responses);

        // updating database
        Quiz quiz =  (Quiz) session.getAttribute(Constants.SessionAttributes.QUIZ);
        long userId = ((User) session.getAttribute(Constants.SessionAttributes.USER)).getId();
        long endTime = System.currentTimeMillis();
        long startTime = (long) session.getAttribute("start-time");
        double completionTime = (double) (endTime - startTime) / 60000;
        if(completionTime >= quiz.getTimeInMinutes())
            completionTime = quiz.getTimeInMinutes();

        session.setAttribute("time-spent-in-minutes", completionTime);
        int totalScore = grades.stream().mapToInt(Integer::intValue).sum();

        Timestamp completionDate = new Timestamp(endTime);

        History newHistory = new History(userId, quiz.getId(), totalScore, completionTime, completionDate);
        HistoriesDao historiesDao = (HistoriesDao) getServletContext().getAttribute(Constants.ContextAttributes.HISTORIES_DAO);
        try {
            historiesDao.insertHistory(newHistory);
        } catch (SQLException e) {
            throw new RuntimeException("Error Inserting New History In Database");
        }

        resp.sendRedirect("quiz-result.jsp");
    }
}
