package org.ja.servlet;

import org.ja.dao.QuestionDao;
import org.ja.dao.QuizzesDao;
import org.ja.model.quiz.Quiz;
import org.ja.model.quiz.question.Question;
import org.ja.utils.Constants;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/practice-quiz")
public class StartPracticeServlet extends HttpServlet {
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        QuizzesDao quizzesDao = (QuizzesDao) getServletContext().getAttribute(Constants.ContextAttributes.QUIZZES_DAO);
        QuestionDao questionDao = (QuestionDao) getServletContext().getAttribute(Constants.ContextAttributes.QUESTIONS_DAO);

        Quiz quiz = quizzesDao.getQuizById(Long.parseLong(req.getParameter(Constants.RequestParameters.QUIZ_ID)));
        List<Question> questions = questionDao.getQuizQuestions(quiz.getId());
        if("randomized".equals(quiz.getQuestionOrder()))
            Collections.shuffle(questions);

        Map<Question, Integer> questionNumAnsweredMap = new HashMap<>();
        for(Question question : questions) questionNumAnsweredMap.put(question, 0);


    }
}
