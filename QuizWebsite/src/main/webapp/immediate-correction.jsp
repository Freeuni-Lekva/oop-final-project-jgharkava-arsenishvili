<%@ page import="org.ja.utils.Constants" %>
<%@ page import="org.ja.model.quiz.question.Question" %>
<%@ page import="java.util.List" %>
<%@ page import="org.ja.model.quiz.response.Response" %>
<%@ page import="org.ja.dao.AnswersDao" %>
<%@ page import="org.ja.model.OtherObjects.Answer" %>
<%@ page import="org.ja.dao.MatchesDao" %>
<%@ page import="org.ja.model.OtherObjects.Match" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
  Integer index = (Integer) session.getAttribute(Constants.SessionAttributes.CURRENT_QUESTION_INDEX);
  List<Question> questions = (List<Question>) session.getAttribute(Constants.SessionAttributes.QUESTIONS);
  Question question = questions.get(index - 1);
  List<Integer> grades = (List<Integer>) session.getAttribute("grades");
  Response resp = ((List<Response>) session.getAttribute(Constants.SessionAttributes.RESPONSES)).get(index - 1);
%>

<html>
<head>
  <title>Question Feedback</title>
</head>
<body>

<div class="question-block">
  <div class="question-text">
    Question <%=index%>: <%=question.getQuestionText()%>
  </div>

  <% if(question.getImageUrl() != null) { %>
  <img src="<%=question.getImageUrl()%>" alt="Question Image" width="300" height="200">
  <% } %>

  <%
    String type = question.getQuestionType();

    if(Constants.QuestionTypes.MATCHING_QUESTION.equals(type)) {
      MatchesDao matchesDao = (MatchesDao) application.getAttribute(Constants.ContextAttributes.MATCHES_DAO);
      List<Match> matches = matchesDao.getQuestionMatches(question.getQuestionId());
  %>
  <div class="answer-block">
    <div class="label">Your Response:</div>
    <% for(int i = 0; i < resp.size(); i++) {
      Match match = resp.getMatch(i); %>
    <div><%=match.getLeftMatch()%> → <%=match.getRightMatch()%></div>
    <% } %>
  </div>

  <div class="answer-block">
    <div class="label">Correct Response:</div>
    <% for(Match match : matches) { %>
    <div><%=match.getLeftMatch()%> → <%=match.getRightMatch()%></div>
    <% } %>
  </div>

  <% } else if(Constants.QuestionTypes.MULTI_CHOICE_MULTI_ANSWER_QUESTION.equals(type) || Constants.QuestionTypes.MULTIPLE_CHOICE_QUESTION.equals(type)) {
    AnswersDao answersDao = (AnswersDao) application.getAttribute(Constants.ContextAttributes.ANSWERS_DAO);
    List<Answer> answers = answersDao.getQuestionAnswers(question.getQuestionId());
    int responseIndex = 0;
  %>
  <div class="answer-block">
    <div class="label">Your Response:</div>
    <% for (Answer answer : answers) {
      boolean checked = responseIndex < resp.size() && answer.containsAnswer(resp.getAnswer(responseIndex));
      if (checked) responseIndex++;
    %>
    <div class="<%= checked ? "correct" : "" %>">
      <input type="radio" <%= checked ? "checked" : "" %> disabled>
      <%= answer.getAnswerText() %>
    </div>
    <% } %>
  </div>

  <div class="answer-block">
    <div class="label">Correct Answer(s):</div>
    <% for (Answer answer : answers) { %>
    <div class="<%= answer.getAnswerValidity() ? "correct" : "" %>">
      <input type="radio" <%= answer.getAnswerValidity() ? "checked" : "" %> disabled>
      <%= answer.getAnswerText() %>
    </div>
    <% } %>
  </div>

  <% } else { // Default type (open response)
    AnswersDao answersDao = (AnswersDao) application.getAttribute(Constants.ContextAttributes.ANSWERS_DAO);
    List<Answer> answers = answersDao.getQuestionAnswers(question.getQuestionId());
  %>
  <div class="answer-block">
    <div class="label">Your Response:</div>
    <% for(int i = 0; i < resp.size(); i++) { %>
    <div><%= resp.getAnswer(i) %></div>
    <% } %>
  </div>

  <div class="answer-block">
    <div class="label">Correct Answer(s):</div>
    <% for(Answer answer : answers) { %>
    <div class="correct"><%= answer.getAnswerText() %></div>
    <% } %>
  </div>
  <% } %>

  <div class="score-display">
    Score: <%=grades.get(index - 1)%> out of <%=question.getNumAnswers()%>
  </div>
</div>

<% if(index == questions.size()) { %>
<form method="get" action="quiz-result.jsp">
  <input type="submit" value="View Quiz Result">
</form>
<% } else { %>
<form method="get" action="single-question-page.jsp">
  <input type="submit" value="Next Question">
</form>
<% } %>

</body>
</html>
