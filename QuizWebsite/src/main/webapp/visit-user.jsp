<%@ page import="java.util.ArrayList" %>
<%@ page import="org.ja.model.user.User" %>
<%@ page import="org.ja.utils.Constants" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="org.ja.model.quiz.Quiz" %>
<%@ page import="org.ja.dao.*" %>
<%@ page import="org.ja.model.OtherObjects.*" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    UsersDao usersDao = (UsersDao)application.getAttribute(Constants.ContextAttributes.USERS_DAO);
    long id = Long.parseLong(request.getParameter(Constants.RequestParameters.USER_ID));
    User visitedUser = usersDao.getUserById(id);
    User currentUser = (User)session.getAttribute(Constants.SessionAttributes.USER);

    if(visitedUser.getId() == currentUser.getId()) response.sendRedirect("/user-page.jsp");

    FriendShipsDao friendsDao = (FriendShipsDao)application.getAttribute(Constants.ContextAttributes.FRIENDSHIPS_DAO);
    UserAchievementsDao userAchievementsDao = (UserAchievementsDao)application.getAttribute(Constants.ContextAttributes.USER_ACHIEVEMENTS_DAO);
    AchievementsDao achievementDao = (AchievementsDao)application.getAttribute(Constants.ContextAttributes.ACHIEVEMENTS_DAO);
    List<UserAchievement> achievements = userAchievementsDao.getUserAchievements(visitedUser.getId());
%>
<!DOCTYPE html>
<html>
<head>
    <title><%=visitedUser.getUsername()%></title>
    <link rel="stylesheet" type="text/css" href="css/visit-user.css">
    <link rel="stylesheet" type="text/css" href="css/hotlink.css">
    <script src = "js/visit-user.js" defer></script>
</head>
<body>
<div class="visit-user-container">
    <!-- user info-->
    <div class="user-info">
        <h2><%=visitedUser.getUsername()%></h2>
        <img src="<%=visitedUser.getPhoto()%>" alt="Profile Picture" width="100" height="100">
    </div>

    <!-- feedback message -->
    <div class="action-row">
        <%Friendship friendship = friendsDao.getFriendshipByIds(currentUser.getId(), visitedUser.getId());
            if(friendship == null){%>
        <!--no relationship yet -->
        <div class="action-section friend-action">
            <button type="button" onclick="handleFriendAction('add-friend', <%=visitedUser.getId()%>)">Add friend</button>
        </div>
        <%}else if("friends".equals(friendship.getFriendshipStatus())){%>
        <!--friends -->
        <div class="action-section friend-action">
            <button type="button" onclick="handleFriendAction('remove-friend', <%=visitedUser.getId()%>)">Remove friend</button>
        </div>
        <!-- send challenge -->
        <div class="action-section challenge-action">
            <label for="quizName">Quiz name:</label>
            <input type="text" id="quizName" placeholder="Enter quiz name" required>
            <button type="button" onclick="handleMessageAction('send-challenge', <%=visitedUser.getId()%>)">Challenge</button>
        </div>
        <!-- send message -->
        <div class="action-section message-action">
            <label for="message">Message:</label>
            <textarea id="message" name="message" rows="3" placeholder="Write your message" required></textarea>
            <button type="button" onclick="handleMessageAction('send-message', <%=visitedUser.getId()%>)">Send</button>
        </div>
        <%}else if(("pending".equals(friendship.getFriendshipStatus()))){
            if(friendship.getFirstUserId() == currentUser.getId()){%>
        <!--curUser has sent the request -->
        <div class="action-section friend-action">
            <button type="button" onclick="handleFriendAction('remove-request', <%=visitedUser.getId()%>)">Remove request</button>
        </div>
        <%}else if(friendship.getSecondUserId() == currentUser.getId()){%>
        <!--curUser has received request  -->
        <div class="action-section friend-action">
            <button onclick="handleFriendAction('accept', <%=visitedUser.getId()%>)">Accept</button>
            <button onclick="handleFriendAction('delete', <%=visitedUser.getId()%>)">Delete</button>
        </div>
        <%}%>
        <%}%>
    </div>
</div>
    <div class="bottom-actions">
        <form action="history-page.jsp" method="get">
            <input type="hidden" name="<%= Constants.RequestParameters.USER_ID %>" value="<%= visitedUser.getId() %>">
            <button type="submit">See <%=visitedUser.getUsername()%>'s Quiz History</button>

        </form>

        <button type="button" onclick="toggleAchievements()">View <%= visitedUser.getUsername() %>'s Achievements</button>
    </div>

    <div id="achievements-section" style="display: none; margin-top: 20px;">
        <h3>Achievements</h3>
        <% if (achievements != null && !achievements.isEmpty()) {
            for (UserAchievement ua : achievements) {
                Achievement a = achievementDao.getAchievement(ua.getAchievementId()); %>
        <p><strong><%= a.getAchievementName() %>
        </strong> – <%= a.getAchievementDescription() %>
        </p>
        <img src="<%=a.getAchievementPhoto()%>" alt="Description of image" width="200"/>
        <% }
        } else { %>
        <p><%= visitedUser.getUsername() %> has no achievements yet.</p>
        <% } %>
    </div>

    <button onclick="window.history.back()" class="back-btn">
        ← Go Back
    </button>
</body>
</html>
