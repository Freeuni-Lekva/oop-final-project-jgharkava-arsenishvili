package org.ja.listener;

import org.ja.model.quiz.question.Question;
import org.ja.utils.Constants;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.ArrayList;
import java.util.List;

@WebListener
public class SessionListener implements HttpSessionListener {
    private List<Question> questionsToAdd;

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        questionsToAdd = new ArrayList<>();

        se.getSession().setAttribute(Constants.SessionAttributes.QUESTIONS, questionsToAdd);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
    }
}
