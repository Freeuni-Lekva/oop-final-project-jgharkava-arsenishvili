package org.ja.servlet;

import org.ja.model.CategoriesAndTags.Tag;
import org.ja.model.OtherObjects.Answer;
import org.ja.model.OtherObjects.Match;
import org.ja.model.quiz.Quiz;
import org.ja.model.quiz.question.Question;
import org.ja.model.user.User;
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

@WebServlet("/create-quiz")
public class CreateQuizServlet extends HttpServlet {

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = (User) request.getSession().getAttribute(Constants.SessionAttributes.USER);

        String title = request.getParameter("quizTitle");
        String description = request.getParameter("quizDescription");
        String category = request.getParameter("category");
        String[] tags = request.getParameterValues("tags");
        String time = request.getParameter("quizDuration");
        String orderType = request.getParameter("orderType");
        String placementType = request.getParameter("placementType");
        String correctionType = request.getParameter("correctionType");

        long categoryId = Integer.parseInt(category);
        // TODO not required, if not written default 0
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
