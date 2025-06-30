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
  AnnouncementsDao announcementsDao = (AnnouncementsDao)application.getAttribute(Constants.ContextAttributes.ANNOUNCEMENTS_DAO);
  UsersDao usersDao = (UsersDao)application.getAttribute(Constants.ContextAttributes.USERS_DAO);
  QuizzesDao quizDao = (QuizzesDao)application.getAttribute(Constants.ContextAttributes.QUIZZES_DAO);
  HistoriesDao historiesDao = (HistoriesDao)application.getAttribute(Constants.ContextAttributes.HISTORIES_DAO);
  MessageDao messageDao = (MessageDao)application.getAttribute(Constants.ContextAttributes.MESSAGE_DAO);
  ChallengesDao challengeDao = (ChallengesDao)application.getAttribute(Constants.ContextAttributes.CHALLENGES_DAO);
  UserAchievementsDao userAchievementDao = (UserAchievementsDao)application.getAttribute(Constants.ContextAttributes.USER_ACHIEVEMENTS_DAO);
  AchievementsDao achievementDao = (AchievementsDao)application.getAttribute(Constants.ContextAttributes.ACHIEVEMENTS_DAO);
  FriendShipsDao friendsDao = (FriendShipsDao)application.getAttribute(Constants.ContextAttributes.FRIENDSHIPS_DAO);
  User user = (User)session.getAttribute(Constants.SessionAttributes.USER);
  String query = request.getParameter("query");
  String errorMessage = null;

  if (query != null && !query.trim().isEmpty()) {
    User foundUser = usersDao.getUserByUsername(query.trim());

    if (foundUser != null) {
      response.sendRedirect("visit-user.jsp?query=" + foundUser.getUsername());
      return;
    } else {
      errorMessage = "User \"" + query + "\" not found.";
    }
  }
%>
<!DOCTYPE html>
<html>
<head>
  <title><%=user.getUsername() %></title>
  <link rel="stylesheet" type="text/css" href="css/user-page.css">
  <script src = "js/visit-user.js" defer></script>
</head>

<body>


<div class="main-grid">
  <!--name and a picture-->
  <div class="card-section">
    <h1><%=user.getUsername()%>
    </h1>
    <p><%=user.getPhoto()%>
    </p>
  </div>

  <div class="card-section">
    <!-- Popular Quizzes-->
    <h3>The Most Popular Quizzes: </h3>
    <%
      ArrayList<Quiz> quizzes = quizDao.getQuizzesSortedByParticipantCount();
      int cnt1 = 0;
      for (Quiz quiz : quizzes) {
        if (cnt1 == 3) break;
    %>
    <div class="quiz-card">
      <h2><%=quiz.getName()%>
      </h2>
      <p>Taken by <strong><%=quiz.getParticipantCount()%>
      </strong> users.</p>
    </div>
    <%
        cnt1++;
      }
    %>
  </div>

  <div class="card-section">
    <!--Recently Created Quizzes-->
    <h3>Recently Created Quizzes:</h3>
    <%
      ArrayList<Quiz> recentlyCreated = quizDao.getQuizzesSortedByCreationDate();
      int cnt2 = 0;
      for (Quiz quiz : recentlyCreated) {
        if (cnt2 == 5) break;
    %>
    <div class="quiz-card">
      <h2><%=quiz.getName()%>
      </h2>
      <p>Created By: <%=usersDao.getUserById(quiz.getCreatorId()).getUsername()%>
      </p>
      <p>Date: <%=quiz.getCreationDate()%>
      </p>
    </div>
    <%
        cnt2++;
      }
    %>
  </div>

  <!--History of quizzes taken by user -->
  <div class="card-section">
    <%
      ArrayList<History> recentHistory = historiesDao.getHistoriesByUserIdSortedByDate(user.getId());
      if(!recentHistory.isEmpty()){%>
    <h3>The last Quizzes taken by you:</h3>
    <%
      int cnt3 = 0;
      for(History h: recentHistory){
        if(cnt3 == 5) break;%>
    <div class="quiz-card">
      <h2><%=quizDao.getQuizById(h.getQuizId()).getName()%></h2>
      <p>Your Score: <%=h.getScore()%></p>
      <p>Your Time: <%=h.getCompletionTime()%></p>
      <p>Taken On: <%=h.getCompletionDate()%></p>
    </div>
    <%cnt3++;}
    }else{%>
    <p>You have not taken any Quizzes yet.</p>
    <%}%>
  </div>


  <!--History of user created quizzes-->

    <%
      ArrayList<Quiz> creationHistory = quizDao.getQuizzesByCreatorId(user.getId());
      int cnt4 = 0;
      if(!creationHistory.isEmpty()){ %>
  <div class="card-section">
    <h3>The Last Quizzes created by You:</h3>
    <%for(Quiz q: creationHistory){
      if(cnt4 == 5) break;%>
    <div class="quiz-card">
      <h2><%=q.getName()%></h2>
      <p> <%=q.getDescription()%></p>
      <p>Average Rating: <%=q.getAvgRating()%></p>
      <p>Taken By: <%=q.getParticipantCount()%> Users.</p>
    </div>
    <%
        cnt4++;}%>
    <%}%>
  </div>


  <div class="card-section">
    <h2>Your Latest Achievement</h2>
    <%ArrayList<UserAchievement> achievements = userAchievementDao.getUserAchievements(user.getId());
      if (achievements != null && !achievements.isEmpty()) {
        Achievement latest = achievementDao.getAchievement(achievements.get(0).getAchievementId());
    %>
    <p><strong><%=latest.getAchievementName()%></strong></p>

    <div id="achievements-section" class="all-achievements" style="display: none;">
      <%achievements.remove(0);
        if(!achievements.isEmpty()) {%>
      <button type="button" onclick="toggleAchievements()">View All Achievements</button>
      <%for(UserAchievement a: achievements){%>
      <p><%=achievementDao.getAchievement(a.getAchievementId()).getAchievementName()%></p>
      <%}
      }}else{ %>
      <p>You have no achievements yet</p>
      <%  }
      %>
    </div>
  </div>


  <!--friends activity (taken quizzes) -->
  <div class="card-section">
    <h2>Recent quizzes taken by your friends:</h2>
    <%
      ArrayList<History> friendsQuizzes = historiesDao.getUserFriendsHistorySortedByCompletionDate(user.getId());
      if(friendsQuizzes != null && !friendsQuizzes.isEmpty()) {
        int cnt8 = 0;
        for(History h: friendsQuizzes){
          if(cnt8 == 3) break; %>
    <div class="quiz-card">
      <h3><%=quizDao.getQuizById(h.getQuizId()).getName()%></h3>
      <p>By: <%=usersDao.getUserById(h.getUserId()).getUsername()%></p>
      <p>On: <%=h.getCompletionDate()%></p>
    </div>
    <% cnt8++;
    }
    }else{%>
    <p>None of your friends have taken quizzes yet.</p>
    <%}%>
  </div>

  <!--friends activity (created quizzes) -->
  <div class="card-section">
    <h2>Recent quizzes created by your friends:</h2>
    <%
      ArrayList<Quiz> quizzesByFriends = quizDao.getFriendsQuizzesSortedByCreationDate(user.getId());
      if(quizzesByFriends != null && !quizzesByFriends.isEmpty()) {
        int cnt9 = 0;
        for(Quiz q: quizzesByFriends){
          if(cnt9 == 3) break; %>
    <div class="quiz-card">
      <h3><%=q.getName()%></h3>
      <p>Created by: <%=usersDao.getUserById(q.getCreatorId()).getUsername()%></p>
    </div>

    <% cnt9++;
    }
    }else{%>
    <p>None of your friends have created quizzes yet.</p>
    <%}%>
  </div>


  <!-- messages TODO link to see more-->
  <div class="card-section">
    <h2>Your Messages</h2>
    <%
      ArrayList<Message> messages = messageDao.getMessagesForUserSorted(user.getId());
      if(messages != null && !messages.isEmpty()){ %>
    <p>You have <%=messages.size()%> new message(s).</p>
    <ul>
      <%
        int cnt5 = 0;
        for(Message m: messages){
          if(cnt5 == 3) break;%>
      <li>From <%=usersDao.getUserById(m.getSenderUserId()).getUsername()%></li>
      <%cnt5++;
      }%>
    </ul>
    <a href="messages.jsp">See all messages</a>
    <% }else{%>
    <p>No new messages.</p>
    <%}%>
  </div>
  <!-- challenges  TODO link to see more -->
  <div class="card-section">
    <h2>Your Challenges</h2>
    <%
      ArrayList<Challenge> challenges = challengeDao.challengesAsReceiver(user.getId());
      if(challenges != null && !challenges.isEmpty()){ %>
    <p>You have <%=challenges.size()%> new challenge(s).</p>
    <ul>
      <%
        int cnt6 = 0;
        for(Challenge c: challenges){
          if(cnt6 == 3) break;%>
      <li>From <%=usersDao.getUserById(c.getSenderUserId()).getUsername()%></li>
      <%cnt6++;
      }%>
    </ul>
    <a href="challenges.jsp">See all challenges</a>
    <%}else{%>
    <p>No new challenges.</p>
    <%}%>
  </div>
</div>




  <div class="right-corner-group">
    <!--lookup -->
    <div class="card-section">
      <h2>User Lookup</h2>
      <form action="user-page.jsp" method="get">
        <label>
          <input type="text" name="query" value="<%= query != null ? query : "" %>">
        </label>
        <button type="submit">Search</button>
      </form> <br>
      <% if (errorMessage != null) { %>
      <p style="color: red; margin-top: 10px;"><%= errorMessage %></p>
      <%}%>
    </div>

    <!--friend requests  TODO link to see more -->
    <div class="card-section">
      <h2>Friend Requests</h2>
      <%
        ArrayList<Friendship> requests = friendsDao.getFriendRequests(user.getId());
        if(requests != null && !requests.isEmpty()){ %>
      <p>You have <%=requests.size()%> friend request(s).</p>
      <%
        int cnt10 = 0;
        for(Friendship f: requests){
          if(cnt10 == 3) break;%>
      <p>Sent From <%=usersDao.getUserById(f.getFirstUserId()).getUsername()%></p>
      <%cnt10++;
      } %> <a href="friend-requests.jsp">See all requests</a>
      <%}else{%>
      <p>No new friend requests.</p>
      <%}%>
    </div>
    <!-- administrator-->
      <%
        if(user.getStatus().equals("administrator")){ %>
      <div class="card-section">
        <a href="administrator.jsp">Switch to Your Administrator Page</a>
      </div>
      <%}%>
</div>







<!--Announcements -->
<div class="announcements-section">
  <h2>Recent Announcements by Administrators:</h2>
  <%
    ArrayList<Announcement> ann = announcementsDao.getAllAnnouncements();
    Map<Announcement, String> announcements = new HashMap<Announcement, String>();
    for (Announcement a : ann) {
      User administrator = usersDao.getUserById(a.getAdministratorId());
      announcements.put(a, administrator.getUsername());
      if(announcements.size() == 3) break;
    }
    if(!announcements.isEmpty()){
      for(Announcement an : announcements.keySet()){ %>
  <div class="announcement-card">
    <h3><%=an.getAnnouncementText() %></h3>
    <p>Posted By: <%=announcements.get(an) %></p>
    <p>Date: <%=an.getCreationDate() %></p>
  </div>
  <%}
  } else { %>
  <p>No recent announcements found.</p>
  <%}%>
</div>







  <!-- Create/take quiz -->
  <div class="quiz-actions">
    <form action="create-quiz.jsp" method="get">
      <button class="quiz-button">Create Quiz</button>
    </form>
    <form action="take-quiz.jsp" method="get">
      <button class="quiz-button">Take Quiz</button>
    </form>
  </div>
</div>

<!-- administrator-->
<div>
  <%
    if(user.getStatus().equals("administrator")){ %>
    <a href="administrator.jsp">Switch to Your Administrator Page</a>
  <%}%>
</div>
</body>
</html>

