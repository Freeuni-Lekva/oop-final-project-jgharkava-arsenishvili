package org.ja.servlet;

import org.ja.dao.QuizTagsDao;
import org.ja.dao.QuizzesDao;
import org.ja.model.data.QuizTag;
import org.ja.model.quiz.Quiz;
import org.ja.utils.Constants;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * Servlet to handle editing properties of a Quiz, such as title, description,
 * time limit, category, tags, and question ordering/placement/correction statuses.
 *
 * <p>Receives POST requests with parameters identifying which quiz and which field
 * to update. Responds with appropriate HTTP status codes and messages.</p>
 */
@WebServlet("/edit-quiz")
public class EditQuizServlet extends HttpServlet {


    /**
     * Handles POST requests to update quiz properties.
     *
     * Supported fields include:
     * <ul>
     *     <li>title - updates quiz title (checks for duplicate title)</li>
     *     <li>description - updates quiz description</li>
     *     <li>limit - updates quiz time limit</li>
     *     <li>category - updates quiz category ID</li>
     *     <li>removeTag - removes a tag association</li>
     *     <li>addTag - adds a tag association</li>
     *     <li>orderStatus - updates question order status</li>
     *     <li>placementStatus - updates question placement status</li>
     *     <li>correctionStatus - updates question correction status</li>
     * </ul>
     *
     * @param request  HttpServletRequest containing parameters: quizId, field, and relevant values
     * @param response HttpServletResponse used to return success or error status
     * @throws IOException if an I/O error occurs during response writing
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        QuizzesDao quizzesDao = (QuizzesDao) getServletContext().getAttribute(Constants.ContextAttributes.QUIZZES_DAO);
        QuizTagsDao quizTagsDao = (QuizTagsDao) getServletContext().getAttribute(Constants.ContextAttributes.QUIZ_TAG_DAO);

        long quizId = Long.parseLong(request.getParameter(Constants.RequestParameters.QUIZ_ID));
        String field = request.getParameter("field");

        switch (field) {
            case "title":
                String newTitle = request.getParameter("title");
                Quiz withSameName = quizzesDao.getQuizByName(newTitle);
                if (withSameName != null) {
                    response.setStatus(HttpServletResponse.SC_CONFLICT);
                    response.setContentType("text/plain");
                    response.getWriter().write("A quiz with this title already exists.");
                    return;
                }
                quizzesDao.updateQuizTitle(quizId, newTitle);
                response.setStatus(HttpServletResponse.SC_OK);
                break;
            case "description":
                String newDesc = request.getParameter("description");
                quizzesDao.updateQuizDescription(quizId, newDesc);
                response.setStatus(HttpServletResponse.SC_OK);
                break;
            case "limit":
                int newLimit = Integer.parseInt(request.getParameter("limit"));
                quizzesDao.updateQuizTimeLimit(quizId, newLimit);
                response.setStatus(HttpServletResponse.SC_OK);
                break;
            case "category":
                long newCategory = Long.parseLong(request.getParameter("category"));
                quizzesDao.updateQuizCategory(quizId, newCategory);
                response.setStatus(HttpServletResponse.SC_OK);
                break;
            case "removeTag":
                long tagToRemove = Long.parseLong(request.getParameter("tagId"));
                quizTagsDao.removeQuizTag(quizId, tagToRemove);
                break;
            case "addTag":
                long tagToAdd = Long.parseLong(request.getParameter("tagId"));
                quizTagsDao.insertQuizTag(new QuizTag(quizId, tagToAdd));
                break;
            case "orderStatus":
                String newOrderStatus = request.getParameter("questionOrderStatus");
                quizzesDao.updateQuizQuestionOrderStatus(quizId, newOrderStatus);
                break;
            case "placementStatus":
                String newPlacementStatus = request.getParameter("questionPlacementStatus");
                if(Constants.QuizQuestionPlacementTypes.ONE_PAGE.equals(newPlacementStatus))
                    quizzesDao.updateQuizQuestionCorrectionStatus(quizId, Constants.QuizQuestionCorrectionTypes.FINAL_CORRECTION);
                quizzesDao.updateQuizQuestionPlacementStatus(quizId, newPlacementStatus);
                break;
            case "correctionStatus":
                String newCorrectionStatus = request.getParameter("questionCorrectionStatus");
                if(Constants.QuizQuestionCorrectionTypes.IMMEDIATE_CORRECTION.equals(newCorrectionStatus))
                    quizzesDao.updateQuizQuestionPlacementStatus(quizId, Constants.QuizQuestionPlacementTypes.MULTIPLE_PAGE);
                quizzesDao.updateQuizQuestionCorrectionStatus(quizId, newCorrectionStatus);
                break;
            default:
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown field");
        }
    }

}
