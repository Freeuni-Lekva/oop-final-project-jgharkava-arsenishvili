package org.ja.servlet;

import org.ja.model.CategoriesAndTags.Tag;
import org.ja.model.quiz.Quiz;
import org.ja.utils.Constants;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

@WebServlet("/create-quiz")
public class CreateQuizServlet extends HttpServlet {

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String title = request.getParameter("quizTitle");
        String description = request.getParameter("quizDescription");
        String category = request.getParameter("category");
        String[] tags = request.getParameterValues("tags[]");
        String time = request.getParameter("quizDuration");
        String orderType = request.getParameter("orderType");
        String placementType = request.getParameter("placementType");
        String correctionType = request.getParameter("correctionType");

        long categoryId = Integer.parseInt(category);
        int timeLimit = Integer.parseInt(time);

        Quiz quiz = new Quiz();
        quiz.setName(title);
        quiz.setDescription(description);
        quiz.setCategoryId(categoryId);
        quiz.setTimeInMinutes(timeLimit);
        quiz.setQuestionOrder(orderType);
        quiz.setQuestionPlacement(placementType);
        quiz.setQuestionCorrection(correctionType);

        request.getSession().setAttribute(Constants.SessionAttributes.QUIZ, quiz);

        ArrayList<Long> tagIds = new ArrayList<>();

        if (tags != null){
            for (String tag: tags){
                tagIds.add(Long.parseLong(tag));
            }
        }

        request.getSession().setAttribute(Constants.SessionAttributes.TAGS_TO_ADD, tagIds);

        String otherTag = request.getParameter("otherTag");
        System.out.println(otherTag);

        if (otherTag != null && !otherTag.trim().isEmpty()){
            Tag tag = new Tag();
            tag.setTagName(otherTag);

            request.getSession().setAttribute(Constants.SessionAttributes.TAG_TO_CREATE, tag);
        }

        response.sendRedirect("create-question.jsp");
    }

}
