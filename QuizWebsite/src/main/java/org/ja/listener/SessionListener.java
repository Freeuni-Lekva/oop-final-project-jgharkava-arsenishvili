package org.ja.listener;

import org.ja.dao.MessageDao;
import org.ja.utils.Constants;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.ArrayList;
import java.util.List;


/**
 * Session listener that initializes and cleans up session-specific resources.
 */
@WebListener
public class SessionListener implements HttpSessionListener {;

    /**
     * Called when a new HTTP session is created.
     * Initializes a list to track message IDs that should be deleted when the session ends.
     *
     * @param se the HttpSessionEvent containing the newly created session
     */
    @Override
    public void sessionCreated(HttpSessionEvent se) {
        List<Long> messagesToDelete = new ArrayList<>();
        se.getSession().setAttribute(Constants.SessionAttributes.MESSAGES_TO_DELETE, messagesToDelete);
    }


    /**
     * Called when an HTTP session is destroyed.
     * Retrieves and deletes any messages that were marked for deletion during the session.
     *
     * @param se the HttpSessionEvent containing the session being destroyed
     */
    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        HttpSession session = se.getSession();

        MessageDao messageDao = (MessageDao) session.getServletContext().getAttribute(Constants.ContextAttributes.MESSAGE_DAO);
        List<Long> messagesToDelete = (List<Long>) session.getAttribute(Constants.SessionAttributes.MESSAGES_TO_DELETE);

        for(Long messageId : messagesToDelete)
            messageDao.removeMessage(messageId);
    }
}
