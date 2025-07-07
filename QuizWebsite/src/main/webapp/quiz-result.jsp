<%@ page import="java.util.List" %>
<%@ page import="org.ja.model.quiz.response.Response" %>
<%@ page import="org.ja.utils.Constants" %>
<%@ page import="org.ja.model.quiz.question.Question" %>
<%@ page import="org.ja.model.quiz.Quiz" %>
<%@ page import="org.ja.model.OtherObjects.Match" %>
<%@ page import="org.ja.dao.MatchesDao" %>
<%@ page import="org.ja.model.OtherObjects.Answer" %>
<%@ page import="org.ja.dao.AnswersDao" %><%--
  Created by IntelliJ IDEA.
  User: tober
  Date: 7/1/2025
  Time: 10:14 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    MatchesDao matchesDao = (MatchesDao) application.getAttribute(Constants.ContextAttributes.MATCHES_DAO);
    AnswersDao answersDao = (AnswersDao) application.getAttribute(Constants.ContextAttributes.ANSWERS_DAO);

    Quiz quiz = (Quiz) session.getAttribute(Constants.SessionAttributes.QUIZ);
    List<Integer> grades = (List<Integer>) session.getAttribute("grades");
    List<List<Integer>> responseGrades = (List<List<Integer>>) session.getAttribute("responseGrades");
    List<Response> responses = (List<Response>) session.getAttribute(Constants.SessionAttributes.RESPONSES);
    List<Question> questions = (List<Question>) session.getAttribute(Constants.SessionAttributes.QUESTIONS);
    int totalScore = 0;
    for (Integer g : grades) {
        totalScore += g;
    }

    double timeSpent = (Double) session.getAttribute("time-spent-in-minutes");
    int totalSeconds = (int) (timeSpent * 60);
    int minutes = totalSeconds / 60;
    int seconds = totalSeconds % 60;
%>

<style>
    .incorrect { color: red; }
    .correct { color: green; }
</style>

<html>
<head>
    <title>Quiz Result</title>
</head>
<body>

<div class="score-box">
    <h2>Quiz Result</h2>
    <p><span class="label">Your Total Score:</span> <%= totalScore %> / <%= quiz.getScore() %></p>
    <p><span class="label">Your Time Spent: <%= minutes %> minutes <%= seconds %> seconds </span> </p>
</div>

<h2 class="section-title">Review Your Answers</h2>

<%
    for(int j = 0; j < questions.size(); j++) {
        Question question = questions.get(j);

        Response resp;
        int grade;
        List<Integer> respGrades = null;

        if(responses.size() <= j) {
            resp = null;
            grade = 0;
        } else {
            resp = responses.get(j);
            grade = grades.get(j);
            respGrades = responseGrades.get(j);
        }

        String type = question.getQuestionType();
%>
<div class="question-block">
    <div class="question-text">
        Question <%= j + 1 %>: <%= question.getQuestionText() != null ? question.getQuestionText() : ""%>
    </div>

    <% if (question.getImageUrl() != null) { %>
    <img src="<%= question.getImageUrl() %>" alt="Question Image" width="300" height="200"><br><br>
    <% } %>

    <% if(Constants.QuestionTypes.MATCHING_QUESTION.equals(type)) {
        List<Match> matches = matchesDao.getQuestionMatches(question.getQuestionId());
    %>
    <div class="answer-block">
        <div class="label">Your Matches:</div>
        <%
            if(resp == null) {%>
                <h6>Haven't Chosen In Time</h6><%
            } else {
                for(int i = 0; i < resp.size(); i++) {
                    Match match = resp.getMatch(i);%>

                <div class="<%=respGrades.get(i) > 0 ? "correct" : "incorrect"%>"><%= match.getLeftMatch() %> → <%= match.getRightMatch() %></div><%
                }
            }%>

    </div>

    <div class="answer-block">
        <div class="label">Correct Matches:</div>
        <% for(Match match : matches) { %>
        <div><%= match.getLeftMatch() %> → <%= match.getRightMatch() %></div>
        <% } %>
    </div>

    <% } else if(Constants.QuestionTypes.MULTI_CHOICE_MULTI_ANSWER_QUESTION.equals(type) || Constants.QuestionTypes.MULTIPLE_CHOICE_QUESTION.equals(type)) {
        List<Answer> answers = answersDao.getQuestionAnswers(question.getQuestionId());
        int responseIndex = 0;
    %>
    <div class="answer-block">
        <div class="label">Your Answer(s):</div>
        <%
            if(resp == null) {%>
                <h6>Haven't Chosen In Time</h6>
        <%
            } else {
                for (Answer answer : answers) {
                    boolean checked = responseIndex < resp.size() && answer.containsAnswer(resp.getAnswer(responseIndex));
                    if (checked) responseIndex++;%>

                    <div class="<%=checked ? "correct" : ""%>">
                        <input type="radio" <%=checked ? "checked" : ""%> disabled>
                        <%--TODO allign next to radio--%>
                        <div class="<%=checked ? (respGrades.get(responseIndex-1) > 0 ? "correct" : "incorrect") : ""%>"><%= answer.getAnswerText() %></div>
                    </div><%
                }
            }%>
    </div>

    <div class="answer-block">
        <div class="label">Correct Answer(s):</div>
        <% for (Answer answer : answers) { %>
        <div>
            <input type="radio" <%= answer.getAnswerValidity() ? "checked" : "" %> disabled>
            <%= answer.getAnswerText() %>
        </div>
        <% } %>
    </div>

    <% } else { // Default case (e.g., open questions)
        List<Answer> answers = answersDao.getQuestionAnswers(question.getQuestionId());
    %>
    <div class="answer-block">
        <div class="label">Your Answer(s):</div>
        <%
            if(resp == null) {%>
                <h6>Haven't Chosen In Time</h6><%
            } else {
                for(int i = 0; i < resp.size(); i++) { %>
        <div class="<%=respGrades.get(i) > 0 ? "correct" : "incorrect"%>"><%= resp.getAnswer(i) %></div>
        <% }
            } %>

    </div>

    <div class="answer-block">
        <div class="label">Correct Answer(s):</div>
        <% for(Answer answer : answers) {
            String answerText = answer.getAnswerText();%>
        <div><%= answerText.contains("/") ? answerText.substring(0, answerText.indexOf('/')) : answerText %></div>
        <% } %>
    </div>
    <% } %>
</div>

    <div class="score-display">
        Score: <%=grade%> out of <%=question.getNumAnswers()%>
    </div>
<% } %>




<%--Some other things--%>
<form action="user-page.jsp" method="get">
    <button type="submit" >Home</button>
</form>
</body>
</html>
