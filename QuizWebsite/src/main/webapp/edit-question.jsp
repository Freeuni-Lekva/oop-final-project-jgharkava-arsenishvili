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
<%@ page import="java.util.Arrays" %>
<%@ page import="org.ja.dao.MatchesDao" %>
<%@ page import="org.ja.model.OtherObjects.Match" %><%--
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
    MatchesDao matchesDao = (MatchesDao) application.getAttribute(Constants.ContextAttributes.MATCHES_DAO);

    long quizId = Long.parseLong(request.getParameter(Constants.RequestParameters.QUIZ_ID));
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
<div class="question-block" data-question-id="<%=questionId%>" data-question-type="<%=questionType%>">

<%--    Question-text block--%>
    <h4>Type: <%=questionType%></h4>
    <% if (questionText != null && !questionType.equals(Constants.QuestionTypes.PICTURE_RESPONSE_QUESTION)
    && !questionType.equals(Constants.QuestionTypes.FILL_IN_THE_BLANK_QUESTION)){
    %>
    <label>
        <textarea class="question-text"><%=questionText%></textarea>
    </label>

    <button type="button" class="save-question-btn" data-id="<%=questionId%>">Save Question Text</button>
    <button type="button" class="delete-question-btn" data-id="<%=questionId%>">Delete Question</button>
    <%
        }
    %>

<%--    individual questions--%>
    <% if (questionType.equals(Constants.QuestionTypes.RESPONSE_QUESTION)) { %>

    <div class="options">
        <h5>Possible Options:</h5>
        <%  List<Answer> answers = answersDao.getQuestionAnswers(questionId);
            long answerId = answers.getFirst().getAnswerId();
            String[] splitOptions = answers.getFirst().getAnswerText().split("¶");
            List<String> options = new ArrayList<String>(Arrays.asList(splitOptions));
            for (String option: options){
        %>
        <div class="option-block" data-answer-id="<%=answerId%>" data-option-text="<%=option%>">
            <textarea class="option-text"><%=option%></textarea>
            <button class="save-option-btn">Save Option Text</button>
            <button class="delete-option-btn">Delete Option</button>
        </div>
        <%
            }
        %>
        <button type="button" class="add-option-btn">Add Option</button>
    </div>

    <% } else if (questionType.equals(Constants.QuestionTypes.MULTIPLE_CHOICE_QUESTION)) { %>
    <div class="choices">
        <h5>Choices:</h5>
        <%  List<Answer> choices = answersDao.getQuestionAnswers(questionId);

            for (Answer choice: choices){
                long answerId = choice.getAnswerId();
                String choiceText = choice.getAnswerText();
                boolean isCorrect = choice.getAnswerValidity();
        %>
            <div class="choice-block" data-answer-id="<%=answerId%>">
                <textarea class="choice-text"><%=choiceText%></textarea>

                <button class="mark-as-true-btn">
                    <%=isCorrect ? "Marked as true" : "Mark as true"%>
                </button>

                <button class="save-choice-btn">Save Choice Text</button>

                <button class="delete-choice-btn">
                    Delete Choice
                </button>
            </div>
        <%
            }
        %>
    </div>

    <% }
    else if (questionType.equals(Constants.QuestionTypes.FILL_IN_THE_BLANK_QUESTION)) {

        List<String> words = new ArrayList<String>(Arrays.asList(questionText.split("\\s+")));
        int blankIndex = words.indexOf("_");

        words.remove(blankIndex);

        String rawText = String.join(" ", words);
    %>

<%--    Fill-in-the-blank question text block--%>
    <div class="fill-in-the-blank-block" data-question-id="<%=questionId%>">
        <label>Raw Question (without blank):</label>
        <textarea class="edit-raw-question-input"
                  data-question-id="<%=questionId%>"
                  rows="2"><%= rawText %></textarea>

        <div class="interactive-edit-preview" data-question-id="<%=questionId%>"></div>

        <input type="hidden" class="final-question-text" data-question-id="<%=questionId%>" value="<%=questionText%>">

        <button type="button" class="save-question-btn" data-id="<%=questionId%>">Save Question Text</button>
        <button type="button" class="delete-question-btn" data-id="<%=questionId%>">Delete Question</button>
    </div>

    <div class="options">
        <h5>Possible Options:</h5>
        <%  List<Answer> answers = answersDao.getQuestionAnswers(questionId);
            long answerId = answers.getFirst().getAnswerId();
            String[] splitOptions = answers.getFirst().getAnswerText().split("¶");
            List<String> eachOption = new ArrayList<String>(Arrays.asList(splitOptions));
            for (String option: eachOption){
        %>
        <div class="option-block" data-answer-id="<%=answerId%>" data-option-text="<%=option%>">
            <textarea class="option-text"><%=option%></textarea>
            <button class="save-option-btn">Save Option Text</button>
            <button class="delete-option-btn">Delete Option</button>
        </div>
        <%
            }
        %>
        <button type="button" class="add-option-btn">Add Option</button>
    </div>

    <% } else if (questionType.equals(Constants.QuestionTypes.PICTURE_RESPONSE_QUESTION)) { %>

<%--    Image block--%>
    <label>
        <img src = "<%=question.getImageUrl()%>" alt="Image url" width="100">
        <textarea class="image-url" placeholder="Image URL"><%=question.getImageUrl()%></textarea>
    </label>

    <button type="button" class="save-question-btn" data-id="<%=questionId%>">Save Image URL</button>
    <button type="button" class="delete-question-btn" data-id="<%=questionId%>">Delete Question</button>

<%--    Options question text block--%>
    <% if (question.getQuestionText() != null){
    %>
    <label>
        <textarea class="question-picture-text"><%=questionText%></textarea>
    </label>

    <button type="button" class="save-question-picture-btn" data-id="<%=questionId%>">Save Question Text</button>
    <button type="button" class="delete-question-picture-btn" data-id="<%=questionId%>">Delete Question Text</button>
    <%
        } else {
    %>
    <button type="button" class="add-question-text-btn" data-id="<%=questionId%>">Add Question Text</button>
    <%
        }
    %>

    <div class="options">
        <h5>Possible Options:</h5>
        <%  List<Answer> answers = answersDao.getQuestionAnswers(questionId);
            long answerId = answers.getFirst().getAnswerId();
            String[] splitOptions = answers.getFirst().getAnswerText().split("¶");
            List<String> eachOption = new ArrayList<String>(Arrays.asList(splitOptions));
            for (String option: eachOption){
        %>
        <div class="option-block" data-answer-id="<%=answerId%>" data-option-text="<%=option%>">
            <textarea class="option-text"><%=option%></textarea>
            <button class="save-option-btn">Save Option Text</button>
            <button class="delete-option-btn">Delete Option</button>
        </div>
        <%
            }
        %>
        <button type="button" class="add-option-btn">Add Option</button>
    </div>
    <% } else if (questionType.equals(Constants.QuestionTypes.MULTI_ANSWER_QUESTION)) { %>

    <div class="multi-answer-block" data-question-id="<%=questionId%>">
        <%
            List<Answer> multiAnswers = answersDao.getQuestionAnswers(questionId);
            for (Answer answer : multiAnswers) {
                long answerId = answer.getAnswerId();
                String[] splitOptions = answer.getAnswerText().split("¶");
                List<String> options = new ArrayList<String>(Arrays.asList(splitOptions));
        %>

        <div class="answer-block" data-answer-id="<%=answerId%>" data-main-answer-text="<%=options.getFirst().trim()%>">
            <label>Main Answer:</label>
            <textarea class="main-answer-text"><%=options.getFirst().trim()%></textarea>
            <button type="button" class="answer-save-text-btn">Save Answer Text</button>

            <div class="answer-options" data-answer-id="<%=answerId%>">
                <% for (int i = 1; i < options.size(); i++) {
                    String optionText = options.get(i).trim();
                %>
                <div class="answer-option-block" data-option-text="<%=optionText%>" data-answer-id="<%=answerId%>">
                    <textarea class="answer-option-text"><%=optionText%></textarea>
                    <button type="button" class="answer-save-option-btn">Save Option Text</button>
                    <button type="button" class="answer-delete-option-btn">Delete Option</button>
                </div>
                <% } %>
            </div>

            <button type="button" class="answer-add-option-btn">Add Option</button>
        </div>

        <% } %>
    </div>


    <% } else if (questionType.equals(Constants.QuestionTypes.MULTI_CHOICE_MULTI_ANSWER_QUESTION)) { %>

    <div class="multiple-choices">
        <h5>Choices:</h5>
        <%  List<Answer> choices = answersDao.getQuestionAnswers(questionId);

            for (Answer choice: choices){
                long answerId = choice.getAnswerId();
                String choiceText = choice.getAnswerText();
                boolean isCorrect = choice.getAnswerValidity();
        %>
        <div class="multiple-choice-block" data-answer-id="<%=answerId%>">
            <textarea class="multiple-choice-text"><%=choiceText%></textarea>

            <button class="multiple-mark-as-true-btn">
                <%=isCorrect ? "Marked as true" : "Mark as true"%>
            </button>

            <button class="save-multiple-choice-btn">Save Choice Text</button>

            <button class="delete-multiple-choice-btn">
                Delete Choice
            </button>
        </div>
        <%
            }
        %>
    </div>

    <% } else if (questionType.equals(Constants.QuestionTypes.MATCHING_QUESTION)) {

        List<Match> matches = matchesDao.getQuestionMatches(questionId);

        ArrayList<String> leftMatches = new ArrayList<String>();

        for (Match match: matches){
            leftMatches.add(match.getLeftMatch());
        }

        ArrayList<String> rightMatches = new ArrayList<String>();

        for (Match match: matches){
            String right = match.getRightMatch();
            if (!rightMatches.contains(right))
                rightMatches.add(right);
        }
    %>

    <div class="matching-question-block" data-question-id="<%=questionId%>">

        <div class="matching-columns">
            <div class="left-column">
                <h4>Left Options</h4>
                <div class="left-options">
                    <%
                        for (int i = 0; i < leftMatches.size(); i++) {
                            String left = leftMatches.get(i);
                            String matchedRight = matches.get(i).getRightMatch();
                            long matchId = matches.get(i).getMatchId();
                    %>
                    <div class="left-group" data-match-id="<%=matchId%>">
                        <input type="text" class="left-match" value="<%=left%>" required>
                        <select class="right-select" required>
                            <% for (String right : rightMatches) { %>
                            <option value="<%=right%>" <%=matchedRight.equals(right) ? "selected" : ""%>><%=right%></option>
                            <% } %>
                        </select>
                        <button class="save-left-option-btn">Save Text</button>
                        <button class="delete-left-option-btn">Delete</button>
                    </div>
                    <% } %>
                </div>
                <button type="button" class="add-left-option-btn">Add Left Option</button>
            </div>

            <div class="right-column">
                <h4>Right Options</h4>
                <div class="right-options">
                    <% for (String right : rightMatches) { %>
                    <div class="right-option-wrapper">
                        <input type="text" class="right-match" value="<%=right%>" required>
                        <button class="save-right-option-btn">Save Text</button>
                        <button class="delete-right-option-btn">Delete</button>
                    </div>
                    <% } %>
                </div>
                <button type="button" class="add-right-option-btn">Add Right Option</button>
            </div>
        </div>

    </div>

    <% } %>
</div>

<%
    }
%>

<button class="finish-editing-button"  data-quiz-id="<%=quiz.getId()%>">Finish editing questions</button>

</body>
</html>