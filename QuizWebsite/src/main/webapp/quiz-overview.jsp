<%@ page import="org.ja.utils.Constants" %>
<%@ page import="org.ja.model.quiz.Quiz" %>
<%@ page import="org.ja.dao.QuizzesDao" %><%--
  Created by IntelliJ IDEA.
  User: tober
  Date: 6/19/2025
  Time: 1:26 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    long quizId = Long.parseLong(request.getParameter(Constants.RequestParameters.QUIZ_ID));
    Quiz quiz = ((QuizzesDao) application.getAttribute(Constants.ContextAttributes.QUIZZES_DAO)).getQuizById(quizId);
%>

<html>
<head>
    <title>Quiz Overview</title>
</head>
<body>
    <h1><%=quiz.getName()%></h1>

    <form action="start-quiz" method="get">
        <input type="hidden" name="<%=Constants.RequestParameters.QUIZ_ID%>" value="<%=quiz.getId()%>">
        <button type="submit">Start Quiz</button>
    </form>
</body>
</html>
