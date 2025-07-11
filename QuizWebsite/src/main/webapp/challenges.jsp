<%@ page import="org.ja.utils.Constants" %>
<%@ page import="org.ja.dao.ChallengesDao" %>
<%@ page import="org.ja.model.data.Challenge" %>
<%@ page import="org.ja.dao.QuizzesDao" %>
<%@ page import="org.ja.dao.UsersDao" %>
<%@ page import="java.util.List" %><%--
  Created by IntelliJ IDEA.
  User: user
  Date: 7/9/2025
  Time: 11:11 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  long userId = Long.parseLong(request.getParameter(Constants.RequestParameters.USER_ID));
  QuizzesDao quizDao = (QuizzesDao)application.getAttribute(Constants.ContextAttributes.QUIZZES_DAO);
  UsersDao usersDao = (UsersDao)application.getAttribute(Constants.ContextAttributes.USERS_DAO);
  ChallengesDao challengeDao = (ChallengesDao)application.getAttribute(Constants.ContextAttributes.CHALLENGES_DAO);
%>
<html>
<head>
    <title>Challenges</title>
  <link rel="stylesheet" type="text/css" href="css/challenges.css">
  <script src="js/user-page.js" defer></script>
</head>
<body>
<div class="center-wrapper">
  <%
    List<Challenge> challenges = challengeDao.challengesAsReceiver(userId, Constants.FETCH_LIMIT);
    if(challenges != null && !challenges.isEmpty()){
      int num = challenges.size();
  %>
  <h2 class="mb-10" id="challenge-count-wrapper">
    You have <span id="challenge-count"><%=num%></span> new challenge(s).
  </h2>
  <%
    for(Challenge c: challenges){
  %>
  <div class="challenge-item" id="challenge-<%=c.getQuizId()%>">
    <strong><%=quizDao.getQuizById(c.getQuizId()).getName()%></strong>
    <p class="text-small">From: <%=usersDao.getUserById(c.getSenderUserId()).getUsername()%></p>
    <div class="challenge-actions">
      <button class="accept-button" data-challenge-id="<%=c.getQuizId()%>" data-quiz-id="<%=c.getQuizId()%>">Accept</button>
      <button class="delete-button" data-challenge-id="<%=c.getQuizId()%>">Delete</button>
    </div>
  </div>
  <%
    }
  } else {
  %>
  <p class="text-small">No new challenges.</p>
  <% } %>
</div>
</body>
</html>
