package org.ja.servlet;

import org.ja.dao.ChallengesDao;
import org.ja.model.data.Challenge;
import org.ja.model.data.User;
import org.ja.utils.Constants;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet to handle sending quiz challenge requests from one user to another.
 *
 * It listens for POST requests at the "/challenge-friend" endpoint, and creates
 * a new challenge record linking the sender, receiver, and quiz.
 */
@WebServlet("/challenge-friend")
public class ChallengeServlet extends HttpServlet {

    /**
     * Handles POST requests to create a new challenge from the current user (sender)
     * to another user (receiver) for a specified quiz.
     *
     * Expects parameters:
     * <ul>
     *   <li>"challenged-id": the user ID of the challenge receiver</li>
     *   <li>"quiz_id": the ID of the quiz to challenge</li>
     * </ul>
     *
     * The sender is determined from the session attribute {@code Constants.SessionAttributes.USER}.
     * The method inserts a new Challenge into the database using ChallengesDao.
     *
     * @param req  the HttpServletRequest containing parameters and session data
     * @param resp the HttpServletResponse for sending the response (currently unused)
     */
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        ChallengesDao challengesDao = (ChallengesDao) getServletContext().getAttribute(Constants.ContextAttributes.CHALLENGES_DAO);

        User sender = (User) req.getSession().getAttribute(Constants.SessionAttributes.USER);
        long receiverId = Long.parseLong(req.getParameter("challenged-id"));
        long quizId = Long.parseLong(req.getParameter(Constants.RequestParameters.QUIZ_ID));

        challengesDao.insertChallenge(new Challenge(sender.getId(), receiverId, quizId));
    }
}