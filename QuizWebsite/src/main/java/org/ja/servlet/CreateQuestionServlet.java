package org.ja.servlet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ja.model.quiz.question.PictureResponseQuestion;
import org.ja.model.quiz.question.Question;
import org.ja.model.quiz.question.ResponseQuestion;
import org.ja.utils.Constants;

import java.io.IOException;
import java.util.List;

@WebServlet("/create-question")
public class CreateQuestionServlet extends HttpServlet {
    private String questionType;
    private String questionText;
    private String imageUrl;
    private Question question;

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        return;
//        questionType = request.getParameter("questionType");
//        questionText = request.getParameter("questionText");
//        imageUrl = request.getParameter("imageUrl");
//
//        String[] answers = request.getParameterValues("answer");
//
//        switch (questionType){
//            case Constants.QuestionTypes.RESPONSE_QUESTION:
//                handleResponseQuestion();
//            case Constants.QuestionTypes.PICTURE_RESPONSE_QUESTION:
//                handlePictureQuestion();
//
//        }
//
//        List<Question> questions = (List<Question>) request.getSession().getAttribute(Constants.SessionAttributes.QUESTIONS);
//        questions.add(question);
//        request.getSession().setAttribute(Constants.SessionAttributes.QUESTIONS, questions);
//
//        response.sendRedirect("create-question.jsp");
    }

    private void handleResponseQuestion(){
        question = new ResponseQuestion(questionText);
    }

    private void handlePictureQuestion(){
        question = new PictureResponseQuestion(imageUrl, questionText);
    }
}
