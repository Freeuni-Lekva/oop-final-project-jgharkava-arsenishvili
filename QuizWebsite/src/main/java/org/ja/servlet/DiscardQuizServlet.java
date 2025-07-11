package org.ja.servlet;

import org.ja.model.data.Answer;
import org.ja.model.data.Match;
import org.ja.model.quiz.question.Question;
import org.ja.utils.Constants;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;



/**
 * Servlet that handles discarding a quiz creation in progress.
 *
 * <p>Clears all quiz-related session attributes, including questions,
 * matches, tags, and the quiz object itself.</p>
 *
 * <p>After clearing the session data, redirects the user to their user page.</p>
 */
@WebServlet("/discard-quiz")
public class DiscardQuizServlet extends HttpServlet {


    /**
     * Handles POST requests to discard the current quiz creation process.
     *
     * <p>Removes all quiz data stored in the user's session to reset
     * the quiz creation state.</p>
     *
     * @param request  the HttpServletRequest object
     * @param response the HttpServletResponse object used to redirect the user
     * @throws IOException if an input or output error is detected
     *                     when sending the redirect
     */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();

        session.setAttribute(Constants.SessionAttributes.QUESTIONS, new HashMap<Question, List<Answer>>());
        session.setAttribute(Constants.SessionAttributes.MATCHES, new HashMap<Question, List<Match>>());
        session.setAttribute(Constants.SessionAttributes.QUIZ, null);
        session.setAttribute(Constants.SessionAttributes.TAGS_TO_ADD, null);
        session.setAttribute(Constants.SessionAttributes.TAG_TO_CREATE, null);
        session.setAttribute(Constants.SessionAttributes.HAS_QUESTIONS, false);

        response.sendRedirect("user-page.jsp");
    }
}
