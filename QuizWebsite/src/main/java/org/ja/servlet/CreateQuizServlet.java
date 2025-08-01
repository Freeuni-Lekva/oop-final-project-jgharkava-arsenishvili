package org.ja.servlet;

import org.ja.model.data.Tag;
import org.ja.model.data.Answer;
import org.ja.model.data.Match;
import org.ja.model.quiz.Quiz;
import org.ja.model.quiz.question.Question;
import org.ja.model.data.User;
import org.ja.utils.Constants;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Servlet that handles creation of a new quiz.
 *
 * <p>Processes quiz data submitted by a user from a form,
 * initializes the Quiz object, handles quiz tags and categories,
 * and sets up session attributes for subsequent question creation.</p>
 *
 * <p>After processing, redirects the user to the question creation page.</p>
 */
@WebServlet("/create-quiz")
public class CreateQuizServlet extends HttpServlet {


    /**
     * Handles POST request to create a new quiz.
     *
     * <p>Extracts quiz information such as title, description, category, tags,
     * time limit, and quiz behavior settings from the request parameters.
     * Creates a Quiz object and stores it in the session.</p>
     *
     * <p>If any new tag is specified, stores it in the session for later creation.</p>
     *
     * <p>Initializes session attributes to manage questions and matches
     * related to the quiz creation process.</p>
     *
     * @param request  the HttpServletRequest containing form data for quiz creation
     * @param response the HttpServletResponse used to redirect the client
     * @throws IOException if an input or output error occurs during redirection
     */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = (User) request.getSession().getAttribute(Constants.SessionAttributes.USER);

        String title = request.getParameter("quizTitle").trim();
        String description = request.getParameter("quizDescription").trim();
        String category = request.getParameter("category");
        String[] tags = request.getParameterValues("tags");
        String time = request.getParameter("quizDuration");
        String orderType = request.getParameter("orderType");
        String placementType = request.getParameter("placementType");
        String correctionType = request.getParameter("correctionType");

        long categoryId = Integer.parseInt(category);
        int timeLimit = Integer.parseInt(time);

        Quiz quiz = new Quiz(title, description, timeLimit, categoryId, user.getId(), orderType, placementType, correctionType);

        request.getSession().setAttribute(Constants.SessionAttributes.QUIZ, quiz);

        ArrayList<Long> tagIds = new ArrayList<>();

        if (tags != null){
            for (String tag: tags){
                tagIds.add(Long.parseLong(tag));
            }
        }

        request.getSession().setAttribute(Constants.SessionAttributes.TAGS_TO_ADD, tagIds);

        String otherTag = request.getParameter("otherTag");

        if (otherTag != null && !otherTag.trim().isEmpty()){
            Tag tag = new Tag(otherTag);

            request.getSession().setAttribute(Constants.SessionAttributes.TAG_TO_CREATE, tag);
        }

        // setting up
        request.getSession().setAttribute(Constants.SessionAttributes.HAS_QUESTIONS, false);

        Map<Question, List<Answer>> questionAnswerMap = new HashMap<>();
        request.getSession().setAttribute(Constants.SessionAttributes.QUESTIONS, questionAnswerMap);

        Map<Question, List<Match>> questionMatchMap = new HashMap<>();
        request.getSession().setAttribute(Constants.SessionAttributes.MATCHES, questionMatchMap);

        response.sendRedirect("create-question.jsp");
    }

}
