<%@ page import="org.ja.model.user.User" %>
<%@ page import="org.ja.utils.Constants" %>
<%@ page import="org.ja.dao.AdministratorsDao" %><%--
  Created by IntelliJ IDEA.
  User: user
  Date: 6/25/2025
  Time: 3:18 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    User user = (User)session.getAttribute(Constants.SessionAttributes.USER);
    AdministratorsDao adminsDao = (AdministratorsDao)application.getAttribute(Constants.ContextAttributes.ADMINISTRATORS_DAO);
    String msg = (String) request.getAttribute("message");
    if (msg != null && !msg.isEmpty()) {
%>
    <div style="color: green;"><strong><%= msg %></strong></div>
<%
    }
%>
<!DOCTYPE html>
<html>
<head>
    <title>Administrator Page</title>
</head>
<body>
    <h1>Welcome, <%=user.getUsername()%></h1>

<!-- create announcement -->
<div>
    <h2>Create an Announcement</h2>
    <form action="administrator" method="post">
        <label>Enter your Announcement: <input type="text" name="announcementText"/></label>
        <button type="submit" name="action" value="create">Post</button>
    </form>
</div>

<!-- promote user -->
<div>
    <h2>Promote a User</h2>
    <form action="administrator" method="post">
        <label>Enter Username: <input type="text" name="promoteUsername" /></label>
        <button type="submit" name="action" value="promote">Promote</button>
    </form>
</div>

<!-- remove user -->
<div>
    <h2>Remove a User</h2>
    <form action="administrator" method="post">
        <label>Enter Username: <input type="text" name="removeUsername" /></label>
        <button type="submit" name="action" value="removeUser">Remove</button>
    </form>
</div>

<!-- remove quiz-->
<div>
    <h2>Remove a Quiz</h2>
    <form action="administrator" method="post">
        <label>Enter Quiz Name: <input type="text" name="removeQuizName" /></label>
        <button type="submit" name="action" value="removeQuiz">Remove</button>
    </form>
</div>

<!-- clear History -->
<div>
    <h2>Clear Quiz History</h2>
    <form action="administrator" method="post">
        <label>Enter Quiz Name: <input type="text" name="clearQuizHistoryName" /></label>
        <button type="submit" name="action" value="clearHistory">Clear</button>
    </form>
</div>

<!-- statistics-->
<div>
    <h2>Statistics</h2>
    <%
        int user_cnt = adminsDao.getUserCount();
        int quiz_cnt = adminsDao.getTakenQuizzesCount();
    %>
    <p>Number of users in total: <%=user_cnt%></p>
    <p>Number of quizzes taken in total: <%=quiz_cnt%></p>
</div>
</body>
</html>
