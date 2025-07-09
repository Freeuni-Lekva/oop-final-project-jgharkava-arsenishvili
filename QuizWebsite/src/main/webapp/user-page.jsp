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

    if (foundUser != null && foundUser.getId() != user.getId()) {
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
  <script src="js/user-page.js" defer></script>
</head>

<body>
<div class="dashboard-container">

  <!-- Profile Section -->
  <div class="card profile-card">
    <h1><%=user.getUsername()%></h1>
    <img src="<%=user.getPhoto()%>" alt="Profile Picture">
  </div>

  <!-- Quiz Highlights Section -->
  <div class="card quiz-highlights">
    <h3>Quiz Highlights</h3>

    <!-- Popular Quizzes -->
    <div class="quiz-section popular">
      <h4>Most Popular</h4>
      <%
        List<Quiz> quizzes = quizDao.getQuizzesSortedByParticipantCount();
        int cnt1 = 0;
        for (Quiz quiz : quizzes) {
          if (cnt1 == 3) break;
      %>
      <div class="quiz-item">
        <strong><%=quiz.getName()%></strong>
        <p class="text-small">Taken by <%=quiz.getParticipantCount()%> users</p>
      </div>
      <%
          cnt1++;
        }
      %>
    </div>

    <!-- Recently Created -->
    <div class="quiz-section recent">
      <h4>Recently Created</h4>
      <%
        List<Quiz> recentlyCreated = quizDao.getQuizzesSortedByCreationDate();
        int cnt2 = 0;
        for (Quiz quiz : recentlyCreated) {
          if (cnt2 == 3) break;
      %>
      <div class="quiz-item">
        <strong><%=quiz.getName()%></strong>
        <p class="text-small">By: <%=usersDao.getUserById(quiz.getCreatorId()).getUsername()%></p>
        <p class="text-small"><%=quiz.getCreationDate()%></p>
      </div>
      <%
          cnt2++;
        }
      %>
    </div>
  </div>

  <!-- Lookup Section -->
  <div class="card lookup">
    <h3>Lookup People</h3>
    <form action="user-page.jsp" method="get">
      <input type="text" name="query" placeholder="Search for users..." value="<%= query != null ? query : "" %>">
      <button type="submit">Search</button>
    </form>
    <% if (errorMessage != null) { %>
    <p style="color: red; margin-top: 10px;"><%= errorMessage %></p>
    <% } %>
  </div>

  <!-- My History Section -->
  <div class="card my-history">
    <h3>My History</h3>

    <!-- Last Taken -->
    <div class="history-section">
      <h4>Last Taken</h4>
      <%
        List<History> recentHistory = historiesDao.getHistoriesByUserId(user.getId());
        if(!recentHistory.isEmpty()){
          int cnt3 = 0;
          for(History h: recentHistory){
            if(cnt3 == 3) break;
      %>
      <div class="history-item">
        <strong><%=quizDao.getQuizById(h.getQuizId()).getName()%></strong>
        <p class="text-small">Score: <%=h.getScore()%> | Time: <%=h.getCompletionTime()%></p>
        <p class="text-small"><%=h.getCompletionDate()%></p>
      </div>
      <%
          cnt3++;
        }
      } else {
      %>
      <p class="text-small">No quizzes taken yet.</p>
      <% } %>
    </div>

    <!-- Last Created -->
    <div class="history-section">
      <h4>Last Created</h4>
      <%
        List<Quiz> creationHistory = quizDao.getQuizzesByCreatorId(user.getId());
        int cnt4 = 0;
        if(!creationHistory.isEmpty()){
          for(Quiz q: creationHistory){
            if(cnt4 == 3) break;
      %>
      <div class="history-item">
        <strong><%=q.getName()%></strong>
        <p class="text-small">Rating: <%=q.getAvgRating()%> | Taken by: <%=q.getParticipantCount()%> users</p>
      </div>
      <%
          cnt4++;
        }
      } else {
      %>
      <p class="text-small">No quizzes created yet.</p>
      <% } %>
    </div>
  </div>

  <!-- Friends History Section -->
  <div class="card friends-history">
    <h3>Friends History</h3>

    <!-- Last Taken by Friends -->
    <div class="history-section">
      <h4>Last Taken</h4>
      <%
        List<History> friendsQuizzes = historiesDao.getUserFriendsHistory(user.getId());
        if(friendsQuizzes != null && !friendsQuizzes.isEmpty()) {
          int cnt8 = 0;
          for(History h: friendsQuizzes){
            if(cnt8 == 3) break;
      %>
      <div class="history-item">
        <strong><%=quizDao.getQuizById(h.getQuizId()).getName()%></strong>
        <p class="text-small">By: <%=usersDao.getUserById(h.getUserId()).getUsername()%></p>
        <p class="text-small"><%=h.getCompletionDate()%></p>
      </div>
      <%
          cnt8++;
        }
      } else {
      %>
      <p class="text-small">No friend activity yet.</p>
      <% } %>
    </div>

    <!-- Last Created by Friends -->
    <div class="history-section">
      <h4>Last Created</h4>
      <%
        List<Quiz> quizzesByFriends = quizDao.getFriendsQuizzesSortedByCreationDate(user.getId());
        if(quizzesByFriends != null && !quizzesByFriends.isEmpty()) {
          int cnt9 = 0;
          for(Quiz q: quizzesByFriends){
            if(cnt9 == 3) break;
      %>
      <div class="history-item">
        <strong><%=q.getName()%></strong>
        <p class="text-small">By: <%=usersDao.getUserById(q.getCreatorId()).getUsername()%></p>
      </div>
      <%
          cnt9++;
        }
      } else {
      %>
      <p class="text-small">No friend creations yet.</p>
      <% } %>
    </div>
  </div>

  <div class="full-history">
    <h4>Full history</h4>
    <a href="history.jsp">view your full history</a>
  </div>

  <!-- Achievements Section -->
  <div class="card achievements">
    <h3>Latest Achievement</h3>
    <%
      List<UserAchievement> achievements = userAchievementDao.getUserAchievements(user.getId());
      if (achievements != null && !achievements.isEmpty()) {
        Achievement latest = achievementDao.getAchievement(achievements.get(0).getAchievementId());
    %>
    <div class="achievement-badge">
      <strong><%=latest.getAchievementName()%></strong>
    </div>
    <%}else{%>
      <p class="text-small">No achievements yet</p>
    <%}%>
  </div>

  <!-- Take Quiz Button -->
  <div class="card take-quiz">
    <form action="quiz-search.jsp" method="get">
      <button class="quiz-button" type="submit">Take Quiz</button>
    </form>
  </div>

  <!-- Create Quiz Button -->
  <div class="card create-quiz">
    <form action="create-quiz.jsp" method="get">
      <button class="quiz-button" type="submit">Create Quiz</button>
    </form>
  </div>

  <!-- Messages Section -->
  <div class="card messages">
    <h3>Messages</h3>
    <%
      List<Message> messages = messageDao.getMessagesForUser(user.getId());
      if(messages != null && !messages.isEmpty()){
    %>
    <p class="mb-10">You have <%=messages.size()%> new message(s).</p>
    <%
      int cnt5 = 0;
      for(Message m: messages){
        if(cnt5 == 3) break;
    %>
    <div class="message-item">
      From: <%=usersDao.getUserById(m.getSenderUserId()).getUsername()%>
    </div>
    <%
        cnt5++;
      }
    %>
    <a href="messages.jsp">See all messages</a> <br></br>
    <div class="mb-15">
      <label>Send a message:</label>
      <input type="text" id="recipient" placeholder="Recipient username" required>
      <textarea id="message" rows="2" placeholder="Write your message" required></textarea>
      <button onclick="sendMessage()">Send</button>
      <p id="message-status"></p>
    </div>

    <%
    } else {
    %>
    <p class="text-small">No new messages.</p>
    <% } %>
  </div>

  <!-- Challenges Section -->
  <div class="card challenges">
    <h3>Challenges</h3>
    <%
      List<Challenge> challenges = challengeDao.challengesAsReceiver(user.getId());
      if(challenges != null && !challenges.isEmpty()){
    %>
    <p class="mb-10">You have <%=challenges.size()%> new challenge(s).</p>
    <%
      int cnt6 = 0;
      for(Challenge c: challenges){
        if(cnt6 == 3) break;
    %>
    <div class="challenge-item">
      <strong><%=quizDao.getQuizById(c.getQuizId()).getName()%></strong>
      <p class="text-small">From: <%=usersDao.getUserById(c.getSenderUserId()).getUsername()%></p>
    </div>
    <%
        cnt6++;
      }
    %>
    <a href="challenges.jsp">See all challenges</a>
    <%
    } else {
    %>
    <p class="text-small">No new challenges.</p>
    <% } %>
  </div>

  <!-- Announcements Section -->
  <div class="card announcements">
    <h3>Announcements by Administrators</h3>
    <%
      List<Announcement> ann = announcementsDao.getAllAnnouncements();
      Map<Announcement, String> announcements = new HashMap<Announcement, String>();
      for (Announcement a : ann) {
        User administrator = usersDao.getUserById(a.getAdministratorId());
        announcements.put(a, administrator.getUsername());
        if(announcements.size() == 3) break;
      }
      if(!announcements.isEmpty()){
        for(Announcement an : announcements.keySet()){
    %>
    <div class="announcement-item">
      <strong><%=an.getAnnouncementText() %></strong>
      <p class="text-small">By: <%=announcements.get(an) %> | <%=an.getCreationDate() %></p>
    </div>
    <%
      }
    } else {
    %>
    <p class="text-small">No recent announcements found.</p>
    <% } %>
  </div>

  <!-- Friend Requests Section || Show All friend requests-->
  <div class="card friend-requests">
    <h3>Friend Requests</h3>
    <%
      List<Friendship> requests = friendsDao.getFriendRequests(user.getId());
      if(requests != null && !requests.isEmpty()){
        int num = requests.size();
    %>
    <p class="mb-10" id="request-count">You have <%=num%> friend request(s).</p>
    <%
      int cnt10 = 0;
      for(Friendship f: requests){
        if(cnt10 == 3) break;
    %>
    <div class="friend-request" data-user-id="<%=f.getFirstUserId()%>">
      <span><%=usersDao.getUserById(f.getFirstUserId()).getUsername()%></span>
      <div>
        <button class="accept" data-user-id="<%=f.getFirstUserId()%>">Accept</button>
        <button class="delete" data-user-id="<%=f.getFirstUserId()%>">Delete</button>
      </div>
    </div>

    <%
        cnt10++;
      }
    } else {
    %>
    <p class="text-small">No new friend requests.</p>
    <% } %>
  </div>

  <!-- Administrator Switch Section -->
  <%
    if(user.getStatus().equals("administrator")){
  %>
  <div class="card admin-switch">
    <a href="administrator.jsp">Switch to Administrator Mode</a>
  </div>
  <%
    }
  %>

</div>
</body>
</html>
