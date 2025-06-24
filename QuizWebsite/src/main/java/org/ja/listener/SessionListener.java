package org.ja.listener;

import org.ja.model.OtherObjects.Answer;
import org.ja.model.OtherObjects.Match;
import org.ja.model.quiz.question.Question;
import org.ja.utils.Constants;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@WebListener
public class SessionListener implements HttpSessionListener {
    private Map<Question, List<Answer>> questionAnswerMap;
    private Map<Question, List<Match>> questionMatchMap;


    @Override
    public void sessionCreated(HttpSessionEvent se) {
        questionAnswerMap = new HashMap<>();
        se.getSession().setAttribute(Constants.SessionAttributes.QUESTIONS, questionAnswerMap);

        questionMatchMap = new HashMap<>();
        se.getSession().setAttribute(Constants.SessionAttributes.MATCHES, questionMatchMap);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
    }
}
