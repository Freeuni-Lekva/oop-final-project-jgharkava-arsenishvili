package org.ja.servlet;

import org.ja.dao.ChallengesDao;
import org.ja.model.OtherObjects.Challenge;
import org.ja.model.user.User;
import org.ja.utils.Constants;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/challenge-friend")
public class ChallengeServlet extends HttpServlet {
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        ChallengesDao challengesDao = (ChallengesDao) getServletContext().getAttribute(Constants.ContextAttributes.CHALLENGES_DAO);

        User sender = (User) req.getSession().getAttribute(Constants.SessionAttributes.USER);
        long receiverId = Long.parseLong(req.getParameter("challenged-id"));
        long quizId = Long.parseLong(req.getParameter(Constants.RequestParameters.QUIZ_ID));

        // TODO will be boolean in the near future
        challengesDao.insertChallenge(new Challenge(sender.getId(), receiverId, quizId));
    }
}