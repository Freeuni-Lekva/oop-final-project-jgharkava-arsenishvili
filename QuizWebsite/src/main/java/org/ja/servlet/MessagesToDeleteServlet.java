package org.ja.servlet;

import org.ja.utils.Constants;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


/**
 * Servlet that handles adding message IDs to a session list for deletion.
 * Receives a POST request with a message ID, then adds it to the list
 * of message IDs marked for deletion in the user's session.
 */
@WebServlet("/messages-to-delete")
public class MessagesToDeleteServlet extends HttpServlet {

    /**
     * Handles POST requests to add a message ID to the deletion list stored in the session.
     *
     * @param req  the HttpServletRequest object containing the request parameters and session
     * @param resp the HttpServletResponse object (not used here)
     */
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        Long messageId = Long.parseLong(req.getParameter("messageId"));

        List<Long> messagesToDelete = (List<Long>) req.getSession().getAttribute(Constants.SessionAttributes.MESSAGES_TO_DELETE);

        messagesToDelete.add(messageId);
    }
}
