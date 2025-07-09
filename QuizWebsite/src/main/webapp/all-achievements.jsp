<%@ page import="org.ja.dao.AchievementsDao" %>
<%@ page import="org.ja.utils.Constants" %>
<%@ page import="org.ja.model.OtherObjects.Achievement" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="org.ja.dao.UserAchievementsDao" %>
<%@ page import="org.ja.model.OtherObjects.UserAchievement" %>
<%@ page import="java.util.List" %><%--
  Created by IntelliJ IDEA.
  User: user
  Date: 7/9/2025
  Time: 2:04 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  long userId = Long.parseLong(request.getParameter(Constants.RequestParameters.USER_ID));
  UserAchievementsDao userAchievementsDao = (UserAchievementsDao) application.getAttribute(Constants.ContextAttributes.USER_ACHIEVEMENTS_DAO);
  AchievementsDao achievementsDao = (AchievementsDao) application.getAttribute(Constants.ContextAttributes.ACHIEVEMENTS_DAO);
%>
<html>
<head>
  <title>Your Achievements</title>
  <link rel="stylesheet" type="text/css" href="css/all-achievements.css">
</head>
<body>
<div class="center-wrapper">
  <%
    List<UserAchievement> achievements = userAchievementsDao.getUserAchievements(userId);
    if(achievements != null && !achievements.isEmpty()){
      int num = achievements.size();
  %>
  <h2>
    You have <%=num%> achievement(s).
  </h2>
  <%
    for(UserAchievement a: achievements){
  %>
  <div class="achievement-item" id="achievement-<%=a.getAchievementId()%>">
    <strong><%=achievementsDao.getAchievement(a.getAchievementId()).getAchievementName()%></strong>
    <img src="<%=achievementsDao.getAchievement(a.getAchievementId()).getAchievementPhoto()%>" alt="Achievement Badge">
    <p><%=achievementsDao.getAchievement(a.getAchievementId()).getAchievementDescription()%></p>
  </div>
  <%
    }
  }
  %>
  <button onclick="window.history.back()" class="back-btn">
    ← Go Back
  </button>
</div>
</body>
</html>
