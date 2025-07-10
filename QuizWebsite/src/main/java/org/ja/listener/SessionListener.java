package org.ja.listener;

import org.ja.dao.MessageDao;
import org.ja.model.OtherObjects.Answer;
import org.ja.model.OtherObjects.Match;
import org.ja.model.quiz.question.Question;
import org.ja.model.user.User;
import org.ja.utils.Constants;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebListener
public class SessionListener implements HttpSessionListener {;
    @Override
    public void sessionCreated(HttpSessionEvent se) {
        System.out.println("session initialized");

        List<Long> messagesToDelete = new ArrayList<>();
        se.getSession().setAttribute(Constants.SessionAttributes.MESSAGES_TO_DELETE, messagesToDelete);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        HttpSession session = se.getSession();

        MessageDao messageDao = (MessageDao) session.getServletContext().getAttribute(Constants.ContextAttributes.MESSAGE_DAO);
        List<Long> messagesToDelete = (List<Long>) session.getAttribute(Constants.SessionAttributes.MESSAGES_TO_DELETE);

        for(Long messageId : messagesToDelete)
            messageDao.removeMessage(messageId);
    }
}
