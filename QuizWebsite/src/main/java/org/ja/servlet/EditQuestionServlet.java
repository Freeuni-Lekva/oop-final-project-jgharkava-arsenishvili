package org.ja.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.ja.dao.AnswersDao;
import org.ja.dao.MatchesDao;
import org.ja.dao.QuestionDao;
import org.ja.model.data.Match;
import org.ja.utils.Constants;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;


/**
 * Servlet to handle editing quiz questions and their related data via AJAX JSON requests.
 *
 * <p>The GET method forwards to the question editing JSP page, passing quizId as a parameter.</p>
 *
 * <p>The POST method accepts JSON input specifying various actions such as:
 * updating question text or image,
 * deleting questions,
 * updating or removing answer options,
 * setting correct choices,
 * manipulating match pairs, etc.
 *
 * After processing the requested action, the servlet responds with JSON indicating success or failure.</p>
 */
@WebServlet("/edit-question")
public class EditQuestionServlet extends HttpServlet {


    /**
     * Handles GET requests by forwarding to the question editing page.
     *
     * @param request  HttpServletRequest containing the quiz ID parameter.
     * @param response HttpServletResponse used to forward the request.
     * @throws ServletException if a servlet error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Long quizId = Long.valueOf(request.getParameter(Constants.RequestParameters.QUIZ_ID));

        request.setAttribute(Constants.RequestParameters.QUIZ_ID, quizId);
        request.getRequestDispatcher("/edit-question.jsp").forward(request, response);
    }


    /**
     * Handles POST requests with JSON payloads specifying actions to edit questions,
     * answers, and matches.
     *
     * @param request  HttpServletRequest containing JSON data specifying the edit action.
     * @param response HttpServletResponse used to send back JSON success/failure status.
     * @throws IOException if an input or output error occurs during request processing.
     */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        QuestionDao questionDao = (QuestionDao) getServletContext().getAttribute(Constants.ContextAttributes.QUESTIONS_DAO);
        AnswersDao answersDao = (AnswersDao) getServletContext().getAttribute(Constants.ContextAttributes.ANSWERS_DAO);
        MatchesDao matchesDao = (MatchesDao) getServletContext().getAttribute(Constants.ContextAttributes.MATCHES_DAO);

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
                
                String oldAnswerOptionText = jsonObject.get("oldText").getAsString();

                if (isNew) {
                    long answerId = jsonObject.get("answerId").getAsLong();

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


                answersDao.setChoiceValidity(setChoiceValidityId, isChoiceCorrect);

                break;
            case "updateLeftMatch":
                String newLeftText = jsonObject.get("newLeftText").getAsString();
                boolean isNewMatch = jsonObject.get("isNew").getAsBoolean();

                if (isNewMatch){
                    long matchQuestionId = jsonObject.get("questionId").getAsLong();
                    String newRightText = jsonObject.get("rightText").getAsString();
                    Match newMatch = new Match(-1, matchQuestionId, newLeftText, newRightText);
                    matchesDao.insertMatch(newMatch);

                    JsonObject jsonResponse = new JsonObject();
                    jsonResponse.addProperty("success", true);
                    jsonResponse.addProperty("matchId", newMatch.getMatchId());

                    response.setContentType("application/json");
                    response.getWriter().write(new Gson().toJson(jsonResponse));
                    return;
                } else {
                    long updateLeftMatchId = jsonObject.get("matchId").getAsLong();
                    matchesDao.updateLeftMatch(updateLeftMatchId, newLeftText);
                }

                break;
            case "updateRightMatchText":
                long rightMatchId = jsonObject.get("matchId").getAsLong();
                String newRightText = jsonObject.get("newRightText").getAsString();

                matchesDao.updateRightMatch(rightMatchId, newRightText);

                break;
            case "deleteMatch":
                long deleteMatchId = jsonObject.get("matchId").getAsLong();

                matchesDao.removeMatch(deleteMatchId);

                break;
        }
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.getWriter().write("{\"success\": true}");
    }
}
