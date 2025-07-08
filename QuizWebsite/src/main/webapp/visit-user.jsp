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
    User currentUser = (User)session.getAttribute(Constants.SessionAttributes.USER);
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
    <!-- user info-->
    <div class="user-info">
        <h2><%=visitedUser.getUsername()%></h2>
        <img src="<%=visitedUser.getPhoto()%>" alt="Profile Picture" width="100" height="100">
    </div>

    <div class="action-row">
        <%Friendship friendship = friendsDao.getFriendshipByIds(currentUser.getId(), visitedUser.getId());
            if(friendship == null){%>
            <!--no relationship yet -->
            <form action="communication" method="post">
                <input type="hidden" name="action" value="add-friend">
                <input type="hidden" name="friendId" value="<%=visitedUser.getId()%>">
                <button type="submit">Add friend</button>
            </form>
        <%}else if("friends".equals(friendship.getFriendshipStatus())){%>
            <!--friends -->
            <form action="communication" method="post">
                <input type="hidden" name="action" value="remove-friend">
                <input type="hidden" name="friendId" value="<%=visitedUser.getId()%>">
                <button type="submit">Remove friend</button>
            </form>
            <!-- send challenge -->
            <form action="communication" method="post">
                Quiz name: <label>
                <input type="text" name="quiz-name" required/></label>
                <input type="hidden" name="action" value="send-challenge">
                <input type="hidden" name="friendId" value="<%=visitedUser.getId()%>" />
                <button type="submit">Challenge</button>
            </form>
             <!-- send message -->
            <form action="communication" method="post">
                <input type="hidden" name="action" value="send-message">
                <input type="hidden" name="recipient" value="<%=visitedUser.getUsername()%>">
                <label>Message:</label><br>
                <textarea name="message" rows="3" cols="30" placeholder="Write your message" required></textarea><br><br>
                <button type="submit">Send</button>
            </form>
        <%}else if(("pending".equals(friendship.getFriendshipStatus()))){
            if(friendship.getFirstUserId() == currentUser.getId()){%>
            <!--curUser has sent the request -->
                <form action="communication" method="post">
                    <input type="hidden" name="action" value="remove-request">
                    <input type="hidden" name="friendId" value="<%=visitedUser.getId()%>">
                    <button type="submit">Remove request</button>
                </form>
            <%}else if(friendship.getSecondUserId() == currentUser.getId()){%>
            <!--curUser has received request  -->
                <form action="communication" method="post">
                    <input type="hidden" name="friendId" value="<%=visitedUser.getId()%>">
                    <button type="submit" name="action" value="accept">Accept</button>
                    <button type="submit" name="action" value="delete">Delete</button>
                </form>
            <%}%>
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
        <img src="<%=a.getAchievementPhoto()%>" alt="Description of image" width="200"/>
        <% }
        } else { %>
        <p><%= name %> has no achievements yet.</p>
        <% } %>
    </div>

</body>
</html>
