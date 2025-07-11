<%@ page import="org.ja.model.data.User" %>
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
        if(msg.contains("Successfully")){
%>
    <div class="message-success"><%=msg%></div>
<%
    }else{
%>
    <div class="message-error"><%=msg%></div>
<%
    }}
%>
<!DOCTYPE html>
<html>
<head>
    <title>Administrator Page</title>
    <link rel="stylesheet" type="text/css" href="css/administrator.css">
</head>
<body>
    <div class="header">
        <h1>Welcome, <%=user.getUsername()%></h1>
    </div>


    <div class="admin-container">
        <!-- create announcement -->
        <div class="admin-section">
            <h2>Create an Announcement</h2>
            <form action="administrator" method="post" class="admin-form">
                <label>Enter your Announcement: <input type="text" name="announcementText"/></label>
                <button type="submit" name="action" value="create">Post</button>
            </form>
        </div>

        <!-- promote user -->
        <div class="admin-section">
            <h2>Promote a User</h2>
            <form action="administrator" method="post" class="admin-form">
                <label>Enter Username: <input type="text" name="promoteUsername"/></label>
                <button type="submit" name="action" value="promote">Promote</button>
            </form>
        </div>

        <!-- remove user -->
        <div class="admin-section">
            <h2>Remove a User</h2>
            <form action="administrator" method="post" class="admin-form">
                <label>Enter Username: <input type="text" name="removeUsername" /></label>
                <button type="submit" name="action" value="removeUser">Remove</button>
            </form>
        </div>

        <!-- remove quiz-->
        <div class="admin-section">
            <h2>Remove a Quiz</h2>
            <form action="administrator" method="post" class="admin-form">
                <label>Enter Quiz Name: <input type="text" name="removeQuizName" /></label>
                <button type="submit" name="action" value="removeQuiz">Remove</button>
            </form>
        </div>

        <!-- clear History -->
        <div class="admin-section">
            <h2>Clear Quiz History</h2>
            <form action="administrator" method="post" class="admin-form">
                <label>Enter Quiz Name: <input type="text" name="clearQuizHistoryName" /></label>
                <button type="submit" name="action" value="clearHistory">Clear</button>
            </form>
        </div>

        <!-- add Category -->
        <div class="admin-section">
            <h2>Add Quiz Category</h2>
            <form action="administrator" method="post" class="admin-form">
                <label>Enter Category Name: <input type="text" name="addCategoryName" /></label>
                <button type="submit" name="action" value="addCategory">Add</button>
            </form>
        </div>

        <!-- statistics-->
        <div class="admin-section">
            <h2>Statistics</h2>
            <%
                int user_cnt = adminsDao.getUserCount();
                int quiz_cnt = adminsDao.getTakenQuizzesCount();
            %>
            <div class="stat-item">Number of users in total: <span class="stat-number"><%=user_cnt%></span></div>
            <div class="stat-item">Number of quizzes taken in total: <span class="stat-number"><%=quiz_cnt%></span></div>
        </div>
        <form action="user-page.jsp" method="get" class="admin-form">
            <button type="submit" >Go Home</button>
        </form>
    </div>
</body>
</html>

