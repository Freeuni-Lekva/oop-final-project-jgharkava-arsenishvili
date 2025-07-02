package org.ja.servlet;

import org.ja.dao.AnswersDao;
import org.ja.dao.MatchesDao;
import org.ja.model.OtherObjects.Answer;
import org.ja.model.OtherObjects.Match;
import org.ja.model.quiz.question.Question;
import org.ja.model.quiz.response.Response;
import org.ja.model.quiz.response.ResponseBuilder;
import org.ja.utils.Constants;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@WebServlet("/grade-single-page-quiz")
public class GradeAllQuestionsServlet extends HttpServlet {
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        HttpSession session = req.getSession();

        MatchesDao matchesDao = (MatchesDao) getServletContext().getAttribute(Constants.ContextAttributes.MATCHES_DAO);
        AnswersDao answersDao = (AnswersDao) getServletContext().getAttribute(Constants.ContextAttributes.ANSWERS_DAO);

        List<Question> questions = (List<Question>) session.getAttribute(Constants.SessionAttributes.QUESTIONS);
        List<Response> responses = ResponseBuilder.buildResponse(req);


        List<Integer> grades = new ArrayList<>();

        System.out.println(questions.size());
        System.out.println(responses.size());
        System.out.println(Collections.list(req.getParameterNames()));

        // TODO response size is less than questions.size if some field if left unused same may be in single question grader
        for(int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            Response response = responses.get(i);
            int grade = 0;

            if(Constants.QuestionTypes.MATCHING_QUESTION.equals(question.getQuestionType())) {
                ArrayList<Match> matches = matchesDao.getQuestionMatches(question.getQuestionId());
                grade = question.gradeResponse(matches, response);
            } else {
                ArrayList<Answer> answers = answersDao.getQuestionAnswers(question.getQuestionId());
                grade = question.gradeResponse(answers, response);
            }

            grades.add(grade);
        }

        session.setAttribute("grades", grades);

        System.out.println(grades);
    }
}
