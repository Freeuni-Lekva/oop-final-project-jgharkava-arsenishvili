package org.ja.servlet;

import org.ja.dao.AnswersDao;
import org.ja.dao.HistoriesDao;
import org.ja.dao.MatchesDao;
import org.ja.model.OtherObjects.Answer;
import org.ja.model.OtherObjects.History;
import org.ja.model.OtherObjects.Match;
import org.ja.model.quiz.Quiz;
import org.ja.model.quiz.question.Question;
import org.ja.model.quiz.response.Response;
import org.ja.model.quiz.response.ResponseBuilder;
import org.ja.model.user.User;
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

@WebServlet("/grade-single-question")
public class GradeSingleQuestionServlet extends HttpServlet {
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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

        int grade = 0;
        List<Integer> respGrades;

        // TODO response size is less than questions.size at the end is multi choice questions and not been selected any

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

        Quiz quiz = (Quiz) session.getAttribute(Constants.SessionAttributes.QUIZ);
        long startTime = (long) session.getAttribute("start-time");
        long currTime = System.currentTimeMillis();

        if(quiz.getTimeInMinutes() * 60L <= (currTime - startTime) / 1000 || index + 1 == questions.size()) {
            long quizId = ((Quiz) session.getAttribute(Constants.SessionAttributes.QUIZ)).getId();
            long userId = ((User) session.getAttribute(Constants.SessionAttributes.USER)).getId();
            double completionTime = (double) (currTime - startTime) / 60000;
            if(quiz.getTimeInMinutes() != 0 && completionTime >= quiz.getTimeInMinutes())
                completionTime = quiz.getTimeInMinutes();

            session.setAttribute("time-spent-in-minutes", completionTime);
            long totalScore = grades.stream().mapToInt(Integer::intValue).sum();

            Timestamp completionDate = new Timestamp(currTime);

            History newHistory = new History(userId, quizId, totalScore, completionTime, completionDate);
            HistoriesDao historiesDao = (HistoriesDao) getServletContext().getAttribute(Constants.ContextAttributes.HISTORIES_DAO);

            try {
                historiesDao.insertHistory(newHistory);
            } catch (SQLException e) {
                throw new RuntimeException("Error Inserting New History In Database");
            }

            req.getRequestDispatcher("/quiz-result.jsp").forward(req, resp);

        } else if(quiz.getQuestionCorrection().equals("immediate-correction")) {
            req.getRequestDispatcher("/immediate-correction.jsp").forward(req, resp);
        } else
            req.getRequestDispatcher("/single-question-page.jsp").forward(req, resp);
    }
}
