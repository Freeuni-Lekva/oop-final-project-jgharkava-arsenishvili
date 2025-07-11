package org.ja.servlet;

import org.ja.dao.QuizRatingsDao;
import org.ja.model.data.QuizRating;
import org.ja.model.data.User;
import org.ja.utils.Constants;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Servlet to handle submitting a review and rating for a quiz.
 * Accepts POST requests with quiz ID, user rating, and optional review text.
 * Inserts the review into the database using QuizRatingsDao.
 */
@WebServlet("/review-quiz")
public class QuizReviewServlet extends HttpServlet {


    /**
     * Handles POST requests to add a quiz rating and review from the current user.
     *
     * @param req  the HttpServletRequest containing parameters:
     *             - quizId (long)
     *             - rating (int)
     *             - review (String, optional)
     * @param resp the HttpServletResponse (not used here)
     */
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        QuizRatingsDao quizRatingsDao = (QuizRatingsDao) getServletContext().getAttribute(Constants.ContextAttributes.QUIZ_RATING_DAO);

        User user = (User) req.getSession().getAttribute(Constants.SessionAttributes.USER);
        long quizId = Long.parseLong(req.getParameter(Constants.RequestParameters.QUIZ_ID));
        int rating = Integer.parseInt(req.getParameter("rating"));
        String review = req.getParameter("review");

        quizRatingsDao.insertQuizRating(new QuizRating(quizId, user.getId(), rating, review.isEmpty() ? null : review));
    }
}
