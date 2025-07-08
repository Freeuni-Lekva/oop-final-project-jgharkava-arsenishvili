package org.ja.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import netscape.javascript.JSObject;
import org.ja.dao.AnswersDao;
import org.ja.dao.QuestionDao;
import org.ja.dao.QuizzesDao;
import org.ja.utils.Constants;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

@WebServlet("/edit-question")
public class EditQuestionServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Long quizId = Long.valueOf(request.getParameter(Constants.RequestParameters.QUIZ_ID));

        request.setAttribute(Constants.RequestParameters.QUIZ_ID, quizId);
        request.getRequestDispatcher("/edit-question.jsp").forward(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        QuestionDao questionDao = (QuestionDao) getServletContext().getAttribute(Constants.ContextAttributes.QUESTIONS_DAO);
        AnswersDao answersDao = (AnswersDao) getServletContext().getAttribute(Constants.ContextAttributes.ANSWERS_DAO);

        BufferedReader bufferedReader = request.getReader();
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(bufferedReader, JsonObject.class);

        String action = jsonObject.get("action").getAsString();

        switch(action){
            case "updateQuestionText":
                long questionId = jsonObject.get("questionId").getAsLong();

                JsonElement textElement = jsonObject.get("questionText");
                String newText = (textElement != null && !textElement.isJsonNull()) ? textElement.getAsString() : null;

                if (newText != null)
                    questionDao.updateQuestionText(questionId, newText);

                // PICTURE-RESPONSE QUESTION
                JsonElement imageElement = jsonObject.get("imageUrl");
                String newImageUrl = (imageElement != null && !imageElement.isJsonNull()) ? imageElement.getAsString() : null;

                if (newImageUrl != null)
                    questionDao.updateQuestionImage(questionId, newImageUrl);

                break;
            case "deleteQuestionText": //for picture response
                long questionIdText = jsonObject.get("questionId").getAsLong();

                questionDao.updateQuestionText(questionIdText, null);

                break;
            case "deleteQuestion":
                long deleteId = jsonObject.get("questionId").getAsLong();

                questionDao.removeQuestion(deleteId);

                break;
            case "updateOption":
                boolean isNew = jsonObject.has("isNew") && jsonObject.get("isNew").getAsBoolean();
                String newAnswerOptionText = jsonObject.get("newText").getAsString();

                System.out.println(newAnswerOptionText);

                String oldAnswerOptionText = jsonObject.get("oldText").getAsString();

                System.out.println(oldAnswerOptionText);

                if (isNew) {
                    long answerId = jsonObject.get("answerId").getAsLong();

                    System.out.println(answerId);

                    answersDao.insertNewAnswerOption(answerId, newAnswerOptionText);
                } else {
                    long answerId = jsonObject.get("answerId").getAsLong();
                    answersDao.updateAnswerOptionText(answerId, oldAnswerOptionText, newAnswerOptionText);
                }
                break;
            case "removeOption":
                long answerRemoveId = jsonObject.get("answerId").getAsLong();
                String answerOptionText = jsonObject.get("optionText").getAsString();

                answersDao.removeAnswerOption(answerRemoveId, answerOptionText);

                break;
            case "setCorrectChoice":
                long choiceId = jsonObject.get("answerId").getAsLong();
                long questionChoiceId = jsonObject.get("questionId").getAsLong();

                answersDao.setOneCorrectChoice(questionChoiceId, choiceId);

                break;
            case "updateAnswerText":
                long updateAnswerId = jsonObject.get("answerId").getAsLong();
                String updateAnswerText = jsonObject.get("newText").getAsString();

                answersDao.updateAnswer(updateAnswerId, updateAnswerText);

                break;
            case "deleteAnswer":
                long deleteAnswerId = jsonObject.get("answerId").getAsLong();

                answersDao.removeAnswer(deleteAnswerId);

                break;
            case "setChoiceValidity":
                long setChoiceValidityId = jsonObject.get("answerId").getAsLong();
                boolean isChoiceCorrect = jsonObject.get("isCorrect").getAsBoolean();

                System.out.println(isChoiceCorrect);

                answersDao.setChoiceValidity(setChoiceValidityId, isChoiceCorrect);

                break;
        }
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.getWriter().write("{\"success\": true}");
    }
}
