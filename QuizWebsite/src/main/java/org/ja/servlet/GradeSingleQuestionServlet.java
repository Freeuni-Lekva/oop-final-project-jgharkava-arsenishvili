package org.ja.servlet;

import org.ja.dao.AnswersDao;
import org.ja.dao.MatchesDao;
import org.ja.model.OtherObjects.Answer;
import org.ja.model.OtherObjects.Match;
import org.ja.model.quiz.Quiz;
import org.ja.model.quiz.question.Question;
import org.ja.model.quiz.response.Response;
import org.ja.model.quiz.response.ResponseBuilder;
import org.ja.utils.Constants;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
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

        if(quiz.getQuestionCorrection().equals("immediate-correction")) {
            req.getRequestDispatcher("/immediate-correction.jsp").forward(req, resp);
        } else if(index+1 != questions.size())
            req.getRequestDispatcher("/single-question-page.jsp").forward(req, resp);
        else req.getRequestDispatcher("/quiz-result.jsp").forward(req, resp);
    }
}
