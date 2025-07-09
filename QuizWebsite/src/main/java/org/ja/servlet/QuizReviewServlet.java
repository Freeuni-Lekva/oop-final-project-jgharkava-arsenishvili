package org.ja.servlet;

import org.ja.dao.QuizRatingsDao;
import org.ja.model.OtherObjects.QuizRating;
import org.ja.model.user.User;
import org.ja.utils.Constants;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/review-quiz")
public class QuizReviewServlet extends HttpServlet {
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        QuizRatingsDao quizRatingsDao = (QuizRatingsDao) getServletContext().getAttribute(Constants.ContextAttributes.QUIZ_RATING_DAO);

        User user = (User) req.getSession().getAttribute(Constants.SessionAttributes.USER);
        long quizId = Long.parseLong(req.getParameter(Constants.RequestParameters.QUIZ_ID));
        int rating = Integer.parseInt(req.getParameter("rating"));
        String review = req.getParameter("review");

        // TODO will be boolean in the near future
        quizRatingsDao.insertQuizRating(new QuizRating(quizId, user.getId(), rating, review.isEmpty() ? null : review));
    }
}
