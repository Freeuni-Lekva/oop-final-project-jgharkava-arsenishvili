package org.ja.servlet;

import org.ja.utils.Constants;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@WebServlet("/messages-to-delete")
public class MessagesToDeleteServlet extends HttpServlet {
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        Long messageId = Long.parseLong(req.getParameter("messageId"));

        List<Long> messagesToDelete = (List<Long>) req.getSession().getAttribute(Constants.SessionAttributes.MESSAGES_TO_DELETE);

        messagesToDelete.add(messageId);
    }
}
