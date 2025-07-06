<%@ page import="org.ja.dao.QuizzesDao" %>
<%@ page import="org.ja.utils.Constants" %>
<%@ page import="org.ja.dao.QuestionDao" %>
<%@ page import="org.ja.model.quiz.Quiz" %>
<%@ page import="java.util.List" %>
<%@ page import="org.ja.model.quiz.question.Question" %>
<%@ page import="org.ja.model.quiz.question.PictureResponseQuestion" %>
<%@ page import="org.ja.dao.AnswersDao" %>
<%@ page import="org.ja.model.OtherObjects.Answer" %>
<%@ page import="java.lang.reflect.Array" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Arrays" %><%--
  Created by IntelliJ IDEA.
  User: lizamarsagishvili
  Date: 04.07.25
  Time: 22:37
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    QuizzesDao quizzesDao = (QuizzesDao) application.getAttribute(Constants.ContextAttributes.QUIZZES_DAO);
    QuestionDao questionDao = (QuestionDao) application.getAttribute(Constants.ContextAttributes.QUESTIONS_DAO);
    AnswersDao answersDao = (AnswersDao) application.getAttribute(Constants.ContextAttributes.ANSWERS_DAO);

//    long quizId = Long.parseLong(request.getParameter(Constants.RequestParameters.QUIZ_ID));
    long quizId = 2;
    Quiz quiz = quizzesDao.getQuizById(quizId);
    List<Question> allQuestions = questionDao.getQuizQuestions(quizId);

%>
<html>
<head>
    <title>Edit Question</title>
    <script src = "js/edit-question.js" defer></script>
</head>
<body>

<% for (Question question: allQuestions){
    String questionType = question.getQuestionType();
    long questionId = question.getQuestionId();
    String questionText = question.getQuestionText();
%>
<div class="question-block" data-question-id="<%=questionId%>">
    <h4>Type: <%=questionType%></h4>
    <% if (questionText != null && !questionType.equals(Constants.QuestionTypes.PICTURE_RESPONSE_QUESTION)
    && !questionType.equals(Constants.QuestionTypes.FILL_IN_THE_BLANK_QUESTION)){
    %>
    <label>
        <textarea class="question-text"><%=questionText%></textarea>
    </label>

    <button type="button" class="save-question-btn" data-id="<%=questionId%>">Save</button>
    <button type="button" class="delete-question-btn" data-id="<%=questionId%>">Delete</button>
    <%
        }
    %>


    <% if (questionType.equals(Constants.QuestionTypes.RESPONSE_QUESTION)) { %>

    <div class="answers">
        <h5>Possible Options:</h5>
        <%  List<Answer> answers = answersDao.getQuestionAnswers(questionId);
            long answerId = answers.getFirst().getAnswerId();
            String[] splitAnswers = answers.getFirst().getAnswerText().split("/");
            List<String> eachAnswer = new ArrayList<String>(Arrays.asList(splitAnswers));
            for (String answer: eachAnswer){
        %>
        <div class="answer-block" data-answer-id="<%=answerId%>" data-answer-text="<%=answer%>">
            <textarea class="answer-text"><%=answer%></textarea>
            <button class="save-answer-btn">Save</button>
            <button class="delete-answer-btn">Delete</button>
        </div>
        <%
            }
        %>
        <button type="button" class="add-answer-btn">Add Option</button>
    </div>

    <% } else if (questionType.equals(Constants.QuestionTypes.MULTIPLE_CHOICE_QUESTION)) { %>

    <% } else if (questionType.equals(Constants.QuestionTypes.FILL_IN_THE_BLANK_QUESTION)) {

        List<String> words = new ArrayList<String>(Arrays.asList(questionText.split("\\s+")));
        int blankIndex = words.indexOf("_");

        words.remove(blankIndex);

        String rawText = String.join(" ", words);
    %>

    <div class="fill-in-the-blank-block" data-question-id="<%=questionId%>">
        <label>Raw Question (without blank):</label>
        <textarea class="edit-raw-question-input"
                  data-question-id="<%=questionId%>"
                  rows="2"><%= rawText %></textarea>

        <div class="interactive-edit-preview" data-question-id="<%=questionId%>"></div>

        <input type="hidden" class="final-question-text" data-question-id="<%=questionId%>" value="<%=questionText%>">

        <button type="button" class="save-question-btn" data-id="<%=questionId%>">Save</button>
        <button type="button" class="delete-question-btn" data-id="<%=questionId%>">Delete</button>
    </div>


    <div class="answers">
        <h5>Possible Options:</h5>
        <%  List<Answer> answers = answersDao.getQuestionAnswers(questionId);
            long answerId = answers.getFirst().getAnswerId();
            String[] splitAnswers = answers.getFirst().getAnswerText().split("/");
            List<String> eachAnswer = new ArrayList<String>(Arrays.asList(splitAnswers));
            for (String answer: eachAnswer){
        %>
        <div class="answer-block" data-answer-id="<%=answerId%>" data-answer-text="<%=answer%>">
            <textarea class="answer-text"><%=answer%></textarea>
            <button class="save-answer-btn">Save</button>
            <button class="delete-answer-btn">Delete</button>
        </div>
        <%
            }
        %>
        <button type="button" class="add-answer-btn">Add Option</button>
    </div>

    <% } else if (questionType.equals(Constants.QuestionTypes.PICTURE_RESPONSE_QUESTION)) { %>

    <label>
        <img src = "<%=question.getImageUrl()%>" alt="Image url" width="100">
        <textarea class="image-url" placeholder="Image URL"><%=question.getImageUrl()%></textarea>
    </label>

    <button type="button" class="save-question-btn" data-id="<%=questionId%>">Save</button>
    <button type="button" class="delete-question-btn" data-id="<%=questionId%>">Delete</button>

    <% if (question.getQuestionText() != null){
    %>
    <label>
        <textarea class="question-picture-text"><%=questionText%></textarea>
    </label>

    <button type="button" class="save-question-picture-btn" data-id="<%=questionId%>">Save Text</button>
    <button type="button" class="delete-question-picture-btn" data-id="<%=questionId%>">Delete Text</button>
    <%
        } else {
    %>
    <button type="button" class="add-question-text-btn" data-id="<%=questionId%>">Add Text</button>
    <%
        }
    %>

    <div class="answers">
        <h5>Possible Options:</h5>
        <%  List<Answer> answers = answersDao.getQuestionAnswers(questionId);
            long answerId = answers.getFirst().getAnswerId();
            String[] splitAnswers = answers.getFirst().getAnswerText().split("/");
            List<String> eachAnswer = new ArrayList<String>(Arrays.asList(splitAnswers));
            for (String answer: eachAnswer){
        %>
        <div class="answer-block" data-answer-id="<%=answerId%>" data-answer-text="<%=answer%>">
            <textarea class="answer-text"><%=answer%></textarea>
            <button class="save-answer-btn">Save</button>
            <button class="delete-answer-btn">Delete</button>
        </div>
        <%
            }
        %>
        <button type="button" class="add-answer-btn">Add Option</button>
    </div>
    <% } else if (questionType.equals(Constants.QuestionTypes.MULTI_ANSWER_QUESTION)) { %>

    <% } else if (questionType.equals(Constants.QuestionTypes.MULTI_CHOICE_MULTI_ANSWER_QUESTION)) { %>

    <% } else if (questionType.equals(Constants.QuestionTypes.MATCHING_QUESTION)) { %>

    <% } %>
</div>

<%
    }
%>

</body>
</html>