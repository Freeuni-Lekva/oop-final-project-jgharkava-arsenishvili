<%@ page import="org.ja.model.user.User" %>
<%@ page import="org.ja.utils.Constants" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="org.ja.model.quiz.Quiz" %>
<%@ page import="org.ja.dao.*" %>
<%@ page import="org.ja.model.OtherObjects.*" %>
<%@ page import="java.util.List" %>
<%@ page import="org.ja.utils.TimeUtils" %>
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
      response.sendRedirect("visit-user.jsp?" + Constants.RequestParameters.USER_ID + "=" + foundUser.getId());
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
  <link rel="stylesheet" href="css/hotlink.css">
  <script src="js/user-page.js" defer></script>
</head>

<body>
<div class="dashboard-container">

  <!-- Profile Section -->
  <div class="card profile-card">
    <h1><%=user.getUsername()%></h1>
    <img src="<%=user.getPhoto()%>" alt="Profile Picture">
    <form action="upload" method="post">
      <label for="newPhoto">Change Picture URL:</label>
      <input type="text" id="newPhoto" name="newPhoto" placeholder="Enter image URL">
      <button type="submit">Submit</button>
    </form>
  </div>

  <!-- Quiz Highlights Section -->
  <div class="card quiz-highlights">
    <h3>Quiz Highlights</h3>

    <!-- Popular Quizzes -->
    <div class="quiz-section popular">
      <h3>Most Popular</h3>
      <%
        List<Quiz> quizzes = quizDao.getQuizzesSortedByParticipantCount();
        int cnt1 = 0;
        for (Quiz quiz : quizzes) {
          if (cnt1 == 3) break;
      %>
      <div class="quiz-item">
        <strong><a class="hotlink" href="quiz-overview.jsp?<%=Constants.RequestParameters.QUIZ_ID%>=<%=quiz.getId()%>"><%=quiz.getName()%></a></strong>
        <p class="text-small">Taken by <%=quiz.getParticipantCount()%> users</p>
      </div>
      <%
          cnt1++;
        }
      %>
    </div>

    <!-- Recently Created -->
    <div class="quiz-section recent">
      <h3>Recently Created</h3>
      <%
        List<Quiz> recentlyCreated = quizDao.getQuizzesSortedByCreationDate();
        int cnt2 = 0;
        for (Quiz quiz : recentlyCreated) {
          User currUser = usersDao.getUserById(quiz.getCreatorId());
          if (cnt2 == 3) break;
      %>
      <div class="quiz-item">
        <strong><a class="hotlink" href="quiz-overview.jsp?<%=Constants.RequestParameters.QUIZ_ID%>=<%=quiz.getId()%>"><%=quiz.getName()%></a></strong>
        <p class="text-small">By: <a class="hotlink" href="visit-user.jsp?<%=Constants.RequestParameters.USER_ID%>=<%=currUser.getId()%>"><%=currUser.getUsername()%></a></p>
        <p class="text-small"><%=quiz.getCreationDate().toLocalDateTime().format(TimeUtils.DATE_TIME_FORMATTER)%></p>
      </div>
      <%
          cnt2++;
        }
      %>
    </div>
  </div>

  <!-- Lookup Section -->
  <div class="card lookup">
    <h3>Find People</h3>
    <form action="user-page.jsp" method="get">
      <input type="text" name="query" placeholder="Search for users..." value="<%= query != null ? query : "" %>">
      <button type="submit">Search</button>
    </form>
    <% if (errorMessage != null) { %>
    <p id="error-message" style="color: red; margin-top: 10px;"><%= errorMessage %></p>
    <% } %>
  </div>

  <!-- My History Section -->
  <div class="card my-history">
    <h3>My History</h3>

    <!-- Last Taken -->
    <div class="history-section">
      <h3>Last Taken</h3>
      <%
        List<History> recentHistory = historiesDao.getHistoriesByUserId(user.getId());
        if(!recentHistory.isEmpty()){
          int cnt3 = 0;
          for(History h: recentHistory){
            Quiz quiz = quizDao.getQuizById(h.getQuizId());
            if(cnt3 == 3) break;
      %>
      <div class="history-item">
        <strong><a class="hotlink" href="quiz-overview.jsp?<%=Constants.RequestParameters.QUIZ_ID%>=<%=quiz.getId()%>"><%=quiz.getName()%></a></strong>
        <p class="text-small">Score: <%=h.getScore()%> | Time: <%=TimeUtils.formatDuration(h.getCompletionTime())%></p>
        <p class="text-small"><%=h.getCompletionDate().toLocalDateTime().format(TimeUtils.DATE_TIME_FORMATTER)%></p>
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
      <h3>Last Created</h3>
      <%
        List<Quiz> creationHistory = quizDao.getQuizzesByCreatorId(user.getId());
        int cnt4 = 0;
        if(!creationHistory.isEmpty()){
          for(Quiz q: creationHistory){
            if(cnt4 == 3) break;
      %>
      <div class="history-item">
        <strong><a class="hotlink" href="quiz-overview.jsp?<%=Constants.RequestParameters.QUIZ_ID%>=<%=q.getId()%>"><%=q.getName()%></a></strong>
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
      <h3>Last Taken</h3>
      <%
        List<History> friendsQuizzes = historiesDao.getUserFriendsHistory(user.getId());
        if(friendsQuizzes != null && !friendsQuizzes.isEmpty()) {
          int cnt8 = 0;
          for(History h: friendsQuizzes){
            Quiz quiz = quizDao.getQuizById(h.getQuizId());
            User currUser = usersDao.getUserById(h.getUserId());
            if(cnt8 == 3) break;
      %>
      <div class="history-item">
        <strong><a class="hotlink" href="quiz-overview.jsp?<%=Constants.RequestParameters.QUIZ_ID%>=<%=quiz.getId()%>"><%=quiz.getName()%></a></strong>
        <p class="text-small">By: <a class="hotlink" href="visit-user.jsp?<%=Constants.RequestParameters.USER_ID%>=<%=currUser.getId()%>"><%=currUser.getUsername()%></a></p>
        <p class="text-small"><%=h.getCompletionDate().toLocalDateTime().format(TimeUtils.DATE_TIME_FORMATTER)%></p>
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
      <h3>Last Created</h3>
      <%
        List<Quiz> quizzesByFriends = quizDao.getFriendsQuizzesSortedByCreationDate(user.getId());
        if(quizzesByFriends != null && !quizzesByFriends.isEmpty()) {
          int cnt9 = 0;
          for(Quiz quiz: quizzesByFriends){
            if(cnt9 == 3) break;
            User creator = usersDao.getUserById(quiz.getCreatorId());
      %>
      <div class="history-item">
        <strong><a class="hotlink" href="quiz-overview.jsp?<%=Constants.RequestParameters.QUIZ_ID%>=<%=quiz.getId()%>"><%=quiz.getName()%></a></strong>
        <p class="text-small">By: <a class="hotlink" href="visit-user.jsp?<%=Constants.RequestParameters.USER_ID%>=<%=creator.getId()%>"><%=creator.getUsername()%></a></p>
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
    <h4>Full Quiz history</h4>
    <a href="history-page.jsp?<%=Constants.RequestParameters.USER_ID%>=<%= user.getId() %>">view your full quiz history</a>
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
    <a href="all-achievements.jsp?<%=Constants.RequestParameters.USER_ID%>=<%=user.getId()%>" class="view-all-link">View all your achievements →</a>
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
        List<Long> alreadyViewedMessages = (List<Long>) session.getAttribute(Constants.SessionAttributes.MESSAGES_TO_DELETE);
        int newMessagesNum = 0;

        for(Message m : messages)
          if(!alreadyViewedMessages.contains(m.getMessageId())) newMessagesNum++;
    %>
    <p class="mb-10" id="message-count-wrapper">
      You have <span id="message-count"><%= newMessagesNum %></span> unread message(s).
    </p>
    <div class="scrollable-pane">
      <%
        for(Message m: messages){
          User sender = usersDao.getUserById(m.getSenderUserId());
          String content = m.getMessageText().replace("\"", "&quot;").replace("\n", "\\n");
      %>
      <div class="message-item <%=alreadyViewedMessages.contains(m.getMessageId()) ? "viewed" : ""%>">
        <p>From: <strong><a class="hotlink" href="visit-user.jsp?<%=Constants.RequestParameters.USER_ID%>=<%=sender.getId()%>"><%=sender.getUsername()%></a></strong></p>
        <button class="view-message-button" data-message="<%= content %>" data-message-id="<%=m.getMessageId()%>">View</button>
      </div>

    <%
      }%>
    </div>
    <%} else {
    %>
    <p class="text-small">No new messages.</p>

    <% } %>
    <div class="message-item message-form">
      <label for="recipient">Send a message:</label>
      <input type="text" id="recipient" class="message-input" placeholder="Recipient username" required>
      <textarea id="message" class="message-textarea" rows="2" placeholder="Write your message" required></textarea>
      <button class="send-button" onclick="sendMessage()">Send</button>
      <p id="message-status" class="message-status"></p>
    </div>
  </div>

  <!-- Message Modal -->
  <div id="messageModal" class="modal" style="display:none;">
    <div class="modal-content">
      <span class="close-button">&times;</span>
      <p id="modal-message-text"></p>
    </div>
  </div>

  <!-- Challenges Section -->
  <div class="card challenges">
    <h3>Challenges</h3>
    <%
      List<Challenge> challenges = challengeDao.challengesAsReceiver(user.getId());
      if(challenges != null && !challenges.isEmpty()){
        int num = challenges.size();
    %>
    <p class="mb-10" id="challenge-count-wrapper" data-total-count="<%=num%>">
      You have <span id="challenge-count"><%=num%></span> new challenge(s).
    </p>
      <div class="scrollable-pane">
      <%
        for(Challenge c: challenges){
        Quiz currQuiz = quizDao.getQuizById(c.getQuizId());
        User sender = usersDao.getUserById(c.getSenderUserId());
      %>
      <div class="challenge-item" id="challenge-<%=c.getChallengeId()%>">
        <strong><a class="hotlink" href="quiz-overview.jsp?<%=Constants.RequestParameters.QUIZ_ID%>=<%=currQuiz.getId()%>"><%=currQuiz.getName()%></a></strong>
        <p class="text-small">From: <a class="hotlink" href="visit-user.jsp?<%=Constants.RequestParameters.USER_ID%>=<%=sender.getId()%>"><%=sender.getUsername()%></a></p>
        <div class="challenge-actions">
          <button class="accept-button" data-challenge-id="<%=c.getChallengeId()%>" data-quiz-id="<%=c.getQuizId()%>">Accept</button>
          <button class="delete-button" data-challenge-id="<%=c.getChallengeId()%>">Delete</button>
        </div>
      </div>
      <%
        }%>
      </div>
    <%} else {
    %>
    <p class="text-small">No new challenges.</p>
    <% } %>
  </div>


  <!-- Announcements Section -->
  <div class="card announcements">
    <h3>Announcements by Administrators</h3>
    <%

      List<Announcement> ann = announcementsDao.getAllAnnouncements();
      Map<Announcement, User> announcements = new HashMap<Announcement, User>();
      for (Announcement a : ann) {
        User administrator = usersDao.getUserById(a.getAdministratorId());
        announcements.put(a, administrator);
        if(announcements.size() == 3) break;
      }
      if(!announcements.isEmpty()){
        for(Announcement an : announcements.keySet()){
          User currUser = announcements.get(an);
    %>
    <div class="announcement-item">
      <strong><%=an.getAnnouncementText() %></strong>
      <p class="text-small">By: <a class="hotlink" href="visit-user.jsp?<%=Constants.RequestParameters.USER_ID%>=<%=currUser.getId()%>"><%=currUser.getUsername()%></a> | <%=an.getCreationDate().toLocalDateTime().format(TimeUtils.DATE_TIME_FORMATTER) %></p>
    </div>
    <%
      }
    } else {
    %>
    <p class="text-small">No recent announcements found.</p>
    <% } %>
  </div>

  <!-- Friend Requests Section-->
  <div class="card friend-requests">
    <h3>Friend Requests</h3>
    <%
      List<Friendship> requests = friendsDao.getFriendRequests(user.getId());
      if(requests != null && !requests.isEmpty()){
        int num = requests.size();
    %>
    <p class="mb-10" id="request-count-wrapper" data-total-count="<%=num%>">
      You have <span id="request-count"><%=num%></span> friend request(s).
    </p>
    <div class="scrollable-pane">
    <%
      for(Friendship f: requests){
        User currUser = usersDao.getUserById(f.getFirstUserId());
    %>
    <div class="friend-request" data-user-id="<%=f.getFirstUserId()%>">
      <span><a class="hotlink" href="visit-user.jsp?<%=Constants.RequestParameters.USER_ID%>=<%=currUser.getId()%>"><%=currUser.getUsername()%></a></span>
      <div class="friend-request-actions">
        <button class="accept-request-button" data-user-id="<%=f.getFirstUserId()%>">Accept</button>
        <button class="delete-request-button" data-user-id="<%=f.getFirstUserId()%>">Delete</button>
      </div>
    </div>
    <%
      }%>
    </div>
    <%} else {
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

  <form action="logout" method="post" style="position: absolute; top: 20px; right: 20px;">
    <button type="submit">Log Out</button>
  </form>

</div>
</body>
</html>
