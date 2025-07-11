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
import java.util.List;
import java.util.Map;

/**
 * Servlet to grade a single question in a quiz, supporting both practice mode and taking mode.
 *
 * <p>
 * In practice mode, it updates mastery levels per question and cycles through questions until
 * mastery is achieved. In taking mode, it records the user response, calculates grades, and
 * manages quiz completion including timing and final grading.
 * </p>
 *
 * <p>
 * This servlet expects the following session attributes:
 * <ul>
 *   <li>{@code QUIZ_MODE} - the current quiz mode (TAKING or PRACTICE)</li>
 *   <li>{@code RESPONSES} - the list of user responses collected so far</li>
 *   <li>{@code QUESTIONS} - the list of questions in the quiz</li>
 *   <li>{@code CURRENT_QUESTION_INDEX} - index of the current question being graded</li>
 *   <li>{@code grades} - list of integer grades per question</li>
 *   <li>{@code responseGrades} - list of detailed grades per response</li>
 *   <li>{@code PRACTICE_QUESTIONS_MASTERY_MAP} - mastery counts for questions in practice mode</li>
 *   <li>{@code QUIZ} - the quiz metadata, including time limit and correction settings</li>
 *   <li>{@code USER} - the user taking the quiz</li>
 *   <li>{@code start-time} - quiz start timestamp in milliseconds</li>
 * </ul>
 *
 *
 * <p>
 * The servlet forwards to appropriate JSP pages:
 * <ul>
 *   <li>{@code immediate-correction.jsp} for immediate feedback</li>
 *   <li>{@code single-question-page.jsp} for the next question page</li>
 *   <li>{@code quiz-result.jsp} to show final results</li>
 * </ul>
 *
 */
@WebServlet("/grade-single-question")
public class GradeSingleQuestionServlet extends HttpServlet {


    /**
     * Handles POST requests to grade the submitted answer for the current question.
     * Delegates grading logic depending on the current quiz mode (practice or taking).
     */
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Constants.QuizMode quizMode = (Constants.QuizMode) req.getSession().getAttribute(Constants.SessionAttributes.QUIZ_MODE);

        if(Constants.QuizMode.TAKING == quizMode)
            analyzeTakingMode(req, resp);
        else analyzePracticeMode(req, resp);
    }


    // --- Helper Methods ---


    /**
     * Handles grading logic in practice mode.
     * Updates mastery counts, grades the current question, cycles the question index,
     * and forwards to immediate correction feedback page.
     */
    private void analyzePracticeMode(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();

        List<Response> responses = (List<Response>) session.getAttribute(Constants.SessionAttributes.RESPONSES);
        List<Response> responseList = ResponseBuilder.buildResponse(req);
        Response response = responseList.isEmpty() ? new Response() : responseList.get(0);
        responses.remove(0);
        responses.add(response);

        Integer index = (Integer) session.getAttribute(Constants.SessionAttributes.CURRENT_QUESTION_INDEX);
        List<Question> questions = (List<Question>) session.getAttribute(Constants.SessionAttributes.QUESTIONS);
        Question question = questions.get(index);

        // always of size 1;
        List<Integer> grades = (List<Integer>) session.getAttribute("grades");
        List<List<Integer>> responseGrades = (List<List<Integer>>) session.getAttribute("responseGrades");

        grades.remove(0);
        responseGrades.remove(0);

        calculateGrade(response, question, grades, responseGrades);

        int grade = grades.get(0);

        Map<Question, Integer> masteryMap = (Map<Question, Integer>) session.getAttribute(Constants.SessionAttributes.PRACTICE_QUESTIONS_MASTERY_MAP);

        for(int i = 0; i < questions.size(); i++) {
            index = (index + 1) % questions.size();
            if(masteryMap.containsKey(questions.get(index))) break;
        }

        session.setAttribute(Constants.SessionAttributes.CURRENT_QUESTION_INDEX, index);

        ///  if answered correctly
        if(grade == question.getNumAnswers()) {
            masteryMap.computeIfPresent(question, (q, val) -> val - 1);
            if(masteryMap.get(question) == 0) masteryMap.remove(question);
        }

        req.setAttribute("question", question);
        req.getRequestDispatcher("/immediate-correction.jsp").forward(req, resp);
    }


    /**
     * Calculates the grade and detailed grades for a response against the question.
     * Retrieves correct answers or matches from the database depending on question type.
     *
     * @param response       The user's response to grade
     * @param question       The question to grade against
     * @param grades         The list of cumulative grades per question (to update)
     * @param responseGrades The list of detailed response grades per question (to update)
     */
    private void calculateGrade(Response response, Question question, List<Integer> grades, List<List<Integer>> responseGrades) {
        int grade = 0;
        List<Integer> respGrades;

        if(Constants.QuestionTypes.MATCHING_QUESTION.equals(question.getQuestionType())) {
            MatchesDao matchesDao = (MatchesDao) getServletContext().getAttribute(Constants.ContextAttributes.MATCHES_DAO);
            List<Match> matches = matchesDao.getQuestionMatches(question.getQuestionId());

            respGrades = question.gradeResponse(matches, response);
        } else {
            AnswersDao answersDao = (AnswersDao) getServletContext().getAttribute(Constants.ContextAttributes.ANSWERS_DAO);
            List<Answer> answers = answersDao.getQuestionAnswers(question.getQuestionId());

            respGrades = question.gradeResponse(answers, response);
        }

        grade = Math.max(0, respGrades.stream().mapToInt(Integer::intValue).sum());

        grades.add(grade);
        responseGrades.add(respGrades);
    }


    /**
     * Handles grading logic in quiz-taking mode.
     * Records the response, calculates the grade, checks time limits and quiz completion,
     * inserts history record if quiz ended, and forwards to appropriate JSP.
     */
    private void analyzeTakingMode(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();

        List<Response> responses = (List<Response>) session.getAttribute(Constants.SessionAttributes.RESPONSES);
        List<Response> responseList = ResponseBuilder.buildResponse(req);
        // if multi choice and not selected any
        Response response = responseList.isEmpty() ? new Response() : responseList.get(0);
        responses.add(response);

        Integer index = (Integer) session.getAttribute(Constants.SessionAttributes.CURRENT_QUESTION_INDEX);
        session.setAttribute(Constants.SessionAttributes.CURRENT_QUESTION_INDEX, index+1);
        List<Question> questions = (List<Question>) session.getAttribute(Constants.SessionAttributes.QUESTIONS);
        List<Integer> grades = (List<Integer>) session.getAttribute("grades");
        List<List<Integer>> responseGrades = (List<List<Integer>>) session.getAttribute("responseGrades");

        Question question = questions.get(index);

        calculateGrade(response, question, grades, responseGrades);

        Quiz quiz = (Quiz) session.getAttribute(Constants.SessionAttributes.QUIZ);
        long startTime = (long) session.getAttribute("start-time");
        long currTime = System.currentTimeMillis();

        boolean isTimedOut = quiz.getTimeInMinutes() * 60L <= (currTime - startTime) / 1000;
        boolean isLastQuestion = index + 1 == questions.size();
        boolean isImmediateCorrection = quiz.getQuestionCorrection().equals("immediate-correction");

        if (isTimedOut || isLastQuestion) {
            long quizId = ((Quiz) session.getAttribute(Constants.SessionAttributes.QUIZ)).getId();
            long userId = ((User) session.getAttribute(Constants.SessionAttributes.USER)).getId();

            double completionTime = (double) (currTime - startTime) / 60000;
            if (completionTime >= quiz.getTimeInMinutes()) {
                completionTime = quiz.getTimeInMinutes();
            }

            session.setAttribute("time-spent-in-minutes", completionTime);
            int totalScore = grades.stream().mapToInt(Integer::intValue).sum();
            Timestamp completionDate = new Timestamp(currTime);

            History newHistory = new History(userId, quizId, totalScore, completionTime, completionDate);
            HistoriesDao historiesDao = (HistoriesDao) getServletContext().getAttribute(Constants.ContextAttributes.HISTORIES_DAO);

            try {
                historiesDao.insertHistory(newHistory);
            } catch (SQLException e) {
                throw new RuntimeException("Error Inserting New History In Database");
            }

            if (isLastQuestion && isImmediateCorrection) {
                req.setAttribute("question", question);
                req.getRequestDispatcher("/immediate-correction.jsp").forward(req, resp);
            } else {
                req.getRequestDispatcher("/quiz-result.jsp").forward(req, resp);
            }

        } else if (isImmediateCorrection) {
            // Show immediate correction after each question
            req.setAttribute("question", question);
            req.getRequestDispatcher("/immediate-correction.jsp").forward(req, resp);

        } else {
            // Proceed to next question normally
            req.getRequestDispatcher("/single-question-page.jsp").forward(req, resp);
        }

    }
}
