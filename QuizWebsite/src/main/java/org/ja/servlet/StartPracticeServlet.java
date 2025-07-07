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
import java.util.*;

@WebServlet("/practice-quiz")
public class StartPracticeServlet extends HttpServlet {
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        QuizzesDao quizzesDao = (QuizzesDao) getServletContext().getAttribute(Constants.ContextAttributes.QUIZZES_DAO);
        QuestionDao questionDao = (QuestionDao) getServletContext().getAttribute(Constants.ContextAttributes.QUESTIONS_DAO);

        Quiz quiz = quizzesDao.getQuizById(Long.parseLong(req.getParameter(Constants.RequestParameters.QUIZ_ID)));

        List<Question> questions = questionDao.getQuizQuestions(quiz.getId());
        if("randomized".equals(quiz.getQuestionOrder()))
            Collections.shuffle(questions);

        Map<Question, Integer> masteryMap = new HashMap<>();
        for(Question question : questions) masteryMap.put(question, 1);

        HttpSession session = req.getSession();

        /// dummy first elements
        List<Integer> grades = new ArrayList<>();
        grades.add(-1);
        session.setAttribute("grades", grades);

        List<List<Integer>> responseGrades = new ArrayList<>();
        responseGrades.add(new ArrayList<>());
        session.setAttribute("responseGrades", responseGrades);

        List<Response> responses = new ArrayList<>();
        responses.add(new Response());
        session.setAttribute(Constants.SessionAttributes.RESPONSES, responses);

        session.setAttribute(Constants.SessionAttributes.QUESTIONS, questions);
        session.setAttribute(Constants.SessionAttributes.PRACTICE_QUESTIONS_MASTERY_MAP, masteryMap);
        session.setAttribute(Constants.SessionAttributes.QUIZ_MODE, Constants.QuizMode.PRACTICE);
        session.setAttribute(Constants.SessionAttributes.CURRENT_QUESTION_INDEX, 0);

        req.getRequestDispatcher("/single-question-page.jsp").forward(req, resp);
    }
}
