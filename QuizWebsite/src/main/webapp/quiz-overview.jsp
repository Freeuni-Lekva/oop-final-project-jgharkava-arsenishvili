<%@ page import="org.ja.utils.Constants" %>
<%@ page import="org.ja.model.quiz.Quiz" %><%--
  Created by IntelliJ IDEA.
  User: tober
  Date: 6/19/2025
  Time: 1:26 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Quiz Overview</title>
</head>
<body>
    <h1><%=((Quiz)session.getAttribute(Constants.SessionAttributes.QUIZ)).getName()%></h1>
</body>
</html>
