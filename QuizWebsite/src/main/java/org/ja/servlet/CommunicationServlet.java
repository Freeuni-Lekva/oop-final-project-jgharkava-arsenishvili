package org.ja.servlet;

import org.ja.dao.*;
import org.ja.model.data.Challenge;
import org.ja.model.data.Friendship;
import org.ja.model.data.Message;
import org.ja.model.quiz.Quiz;
import org.ja.model.data.User;
import org.ja.utils.Constants;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * Servlet handling communication-related actions between users such as
 * managing friendships, sending challenges, and sending messages.
 *
 * Listens for POST requests at "/communication" endpoint and processes
 * various actions based on the "action" request parameter.
 *
 * Supported actions include:
 * <ul>
 *   <li>add-friend: Send a friend request to another user.</li>
 *   <li>remove-friend / remove-request / delete: Remove an existing friendship or friend request.</li>
 *   <li>send-challenge: Send a quiz challenge to a friend.</li>
 *   <li>accept: Accept a friend request.</li>
 *   <li>send-message: Send a private message to a friend.</li>
 *   <li>delete-challenge: Remove a previously sent challenge.</li>
 * </ul>
 *
 * Responses are returned as plain text, usually "OK" for success or error messages/status codes for failures.
 */
@WebServlet("/communication")
public class CommunicationServlet extends HttpServlet {

    /**
     * Handles HTTP POST requests for various communication actions.
     * The action to perform is specified by the "action" parameter.
     * Request parameters vary by action (e.g., friendId, quizName, message, recipient).
     *
     * @param request  the HttpServletRequest containing parameters and session info
     * @param response the HttpServletResponse to send plain text responses
     * @throws ServletException if a servlet error occurs
     * @throws IOException if an I/O error occurs while writing the response
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        FriendShipsDao friendShipsDao = (FriendShipsDao) getServletContext().getAttribute(Constants.ContextAttributes.FRIENDSHIPS_DAO);
        ChallengesDao challengesDao = (ChallengesDao) getServletContext().getAttribute(Constants.ContextAttributes.CHALLENGES_DAO);
        MessageDao messageDao = (MessageDao) getServletContext().getAttribute(Constants.ContextAttributes.MESSAGE_DAO);
        QuizzesDao quizzesDao = (QuizzesDao) getServletContext().getAttribute(Constants.ContextAttributes.QUIZZES_DAO);
        UsersDao usersDao = (UsersDao) getServletContext().getAttribute(Constants.ContextAttributes.USERS_DAO);
        User curUser = (User) request.getSession().getAttribute(Constants.SessionAttributes.USER);
        String action = request.getParameter("action");
        String id = request.getParameter("friendId");
        long friendId = 0;
        Friendship fr = null;
        if (id != null) {
            friendId = Long.parseLong(id);
            fr = friendShipsDao.getFriendshipByIds(curUser.getId(), friendId);
        }
        if("add-friend".equals(action)) {
            friendShipsDao.insertFriendRequest(new Friendship(curUser.getId(), friendId));
            response.setContentType("text/plain");
            response.getWriter().write("OK");
        } else if ("remove-friend".equals(action)) {
            friendShipsDao.removeFriendShip(fr);
            response.setContentType("text/plain");
            response.getWriter().write("OK");
        } else if("send-challenge".equals(action)) {
            String quizName = request.getParameter("quizName");
            Quiz quiz = quizzesDao.getQuizByName(quizName);
            response.setContentType("text/plain");
            if(quiz != null) {
                Challenge challenge = new Challenge(curUser.getId(), friendId, quiz.getId());
                challengesDao.insertChallenge(challenge);
                response.getWriter().write("OK");
            }else{
                response.getWriter().write("NOT_FOUND");
            }
        }else if("remove-request".equals(action)) {
            friendShipsDao.removeFriendShip(fr);
            response.setContentType("text/plain");
            response.getWriter().write("OK");
        }else if("accept".equals(action)) {
            friendShipsDao.acceptFriendRequest(fr);
            response.setContentType("text/plain");
            response.getWriter().write("OK");
        }else if("delete".equals(action)) {
            friendShipsDao.removeFriendShip(fr);
            response.setContentType("text/plain");
            response.getWriter().write("OK");
        } else if("send-message".equals(action)) {
            String friendName = request.getParameter("recipient");
            String message = request.getParameter("message");
            User recipient = usersDao.getUserByUsername(friendName);
            if (recipient == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.setContentType("text/plain");
                response.getWriter().write("Recipient not found.");
                return;
            }
            Friendship friendship = friendShipsDao.getFriendshipByIds(curUser.getId(), recipient.getId());
            if(friendship == null || "pending".equals(friendship.getFriendshipStatus())){
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.setContentType("text/plain");
                response.getWriter().write("You are not friends with " + recipient.getUsername() + ".");
            } else {
                Message m = new Message(curUser.getId(), recipient.getId(), message);
                messageDao.insertMessage(m);
                response.setContentType("text/plain");
                response.getWriter().write("OK");
            }
        }else if("delete-challenge".equals(action)) {
            long challengeId = Long.parseLong(request.getParameter("challengeId"));
            challengesDao.removeChallenge(challengeId);
            response.setContentType("text/plain");
            response.getWriter().write("OK");
        }
    }
}

