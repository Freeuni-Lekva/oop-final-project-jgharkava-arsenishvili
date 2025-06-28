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
import java.util.List;

@WebServlet("/grade-single-page-quiz")
public class GradeAllQuestionsServlet extends HttpServlet {
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        List<Response> responseList = ResponseBuilder.buildResponse(req);

        HttpSession session = req.getSession();
        ArrayList<Question> questions = (ArrayList<Question>) session.getAttribute(Constants.SessionAttributes.QUESTIONS);
        MatchesDao matchesDao = (MatchesDao) getServletContext().getAttribute(Constants.ContextAttributes.MATCHES_DAO);
        AnswersDao answersDao = (AnswersDao) getServletContext().getAttribute(Constants.ContextAttributes.ANSWERS_DAO);

        ArrayList<ArrayList<Integer>> grades = new ArrayList<>();

        for(int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            Response response = responseList.get(i);
            int grade = 0;
            int maxGrade = question.getNumAnswers();

            if(Constants.QuestionTypes.MATCHING_QUESTION.equals(question.getQuestionType())) {
                ArrayList<Match> matches = matchesDao.getQuestionMatches(question.getQuestionId());
                grade = question.gradeResponse(matches, response);
            } else {
                ArrayList<Answer> answers = answersDao.getQuestionAnswers(question.getQuestionId());
                grade = question.gradeResponse(answers, response);
            }

            grades.add(new ArrayList<>(Arrays.asList(grade, maxGrade)));
        }

        System.out.println(grades);
    }
}
