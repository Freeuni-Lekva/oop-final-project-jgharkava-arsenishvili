<%@ page import="java.util.ArrayList" %>
<%@ page import="org.ja.model.user.User" %>
<%@ page import="org.ja.utils.Constants" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="org.ja.model.quiz.Quiz" %>
<%@ page import="org.ja.dao.*" %>
<%@ page import="org.ja.model.OtherObjects.*" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    UsersDao usersDao = (UsersDao)application.getAttribute(Constants.ContextAttributes.USERS_DAO);
    String name = request.getParameter("query");
    User visitedUser = usersDao.getUserByUsername(name);
    User currentUser = (User) session.getAttribute(Constants.SessionAttributes.USER);
    FriendShipsDao friendsDao = (FriendShipsDao)application.getAttribute(Constants.ContextAttributes.FRIENDSHIPS_DAO);
    UserAchievementsDao userAchievementsDao = (UserAchievementsDao)application.getAttribute(Constants.ContextAttributes.USER_ACHIEVEMENTS_DAO);
    AchievementsDao achievementDao = (AchievementsDao)application.getAttribute(Constants.ContextAttributes.ACHIEVEMENTS_DAO);
    ArrayList<UserAchievement> achievements = userAchievementsDao.getUserAchievements(visitedUser.getId());
%>
<!DOCTYPE html>
<html>
<head>
    <title><%=visitedUser.getUsername()%></title>
    <link rel="stylesheet" type="text/css" href="css/visit-user.css">
    <script src = "js/visit-user.js" defer></script>
</head>
<body>
<div class="container">
    <!-- user info-->
    <div class="user-info">
        <h2><%=visitedUser.getUsername()%></h2>
        <img src="<%=visitedUser.getPhoto()%>" alt="Profile Picture" width="100" height="100">
    </div>

    <div class="action-row">
        <%if(!friendsDao.contains(currentUser.getId(), visitedUser.getId())){%>
            <form action="add-friend.jsp" method="post">
                <button type="submit">Add friend</button>
            </form>
        <%}else{%>
        <form action="remove-friend.jsp" method="post">
            <button type="submit">Remove friend</button>
        </form>
        <form action="challenge.jsp" method="get">
            <button type="submit">Challenge</button>
        </form>
        <form action="send-message.jsp" method="get">
        </form>
        <%}%>
    </div>


    <div class="action-row">
        <form action="user-history.jsp" method="get">
            <input type="hidden" name="userId" value="<%= visitedUser.getId() %>">
            <button type="submit">See <%=name%>'s History</button>
        </form>

        <button type="button" onclick="toggleAchievements()">View <%= name %>'s Achievements</button>
    </div>

    <div id="achievements-section" style="display: none; margin-top: 20px;">
        <h3>Achievements</h3>
        <% if (achievements != null && !achievements.isEmpty()) {
            for (UserAchievement ua : achievements) {
                Achievement a = achievementDao.getAchievement(ua.getAchievementId()); %>
        <p><strong><%= a.getAchievementName() %>
        </strong> – <%= a.getAchievementDescription() %>
        </p>
        <% }
        } else { %>
        <p><%= name %> has no achievements yet.</p>
        <% } %>
    </div>

</div>

</body>
</html>