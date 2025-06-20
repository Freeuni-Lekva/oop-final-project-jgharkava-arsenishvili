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
%>
<!DOCTYPE html>
<html>
<head>
  <title><%=user.getUsername() %></title>
</head>
<body>

<!--Announcements -->
<div>
  <h2>Recent Announcements by Administrators</h2>
  <%
    ArrayList<Announcement> ann = announcementsDao.getAnnouncementsSortedByCreationDate();
    Map<Announcement, String> announcements = new HashMap<Announcement, String>();
    for (Announcement a : ann) {
      User administrator = usersDao.getUserById(a.getAdministratorId());
      announcements.put(a, administrator.getUsername());
      if(announcements.size() == 3) break;
    }
    if(!announcements.isEmpty()){
      for(Announcement an : announcements.keySet()){ %>
          <div>
              <h3>Posted By: <%=announcements.get(an) %></h3>
              <p>Date: <%=an.getCreationDate() %></p>
              <p><%=an.getAnnouncementText() %></p>
          </div>
  <%  }
  } else { %>
  <p>No recent announcements found.</p>
  <% } %>
</div>


<!-- Popular Quizzes-->
<div>
  <h3>The Most Popular Quizzes: </h3>
  <%
    ArrayList<Quiz> quizzes = quizDao.getQuizzesSortedByParticipantCount();
    int cnt1 = 0;
    for(Quiz quiz : quizzes){
      if(cnt1 == 3) break;%>
      <p><%=quiz.getName()%></p>
      <p>Taken by <%=quiz.getParticipantCount()%> users.</p>
  <%
    cnt1++;
    } %>
</div>


<!--Recently Created Quizzes-->
<div>
  <h3>Recently Created Quizzes:</h3>
  <%
    ArrayList<Quiz> recentlyCreated = quizDao.getQuizzesSortedByCreationDate();
    int cnt2 = 0;
    for(Quiz quiz: recentlyCreated){
      if(cnt2 == 5) break;%>
      <p><%=quiz.getName()%></p>
      <p>Created By: <%=usersDao.getUserById(quiz.getCreatorId()).getUsername()%></p>
      <p>On: <%=quiz.getCreationDate()%></p>
  <%
    cnt2++;
    }%>
</div>

<!--History if quizzes taken by user -->
<div>
  <h2>History of Your Quizzes</h2>
  <%
    ArrayList<History> recentHistory = historiesDao.getHistoriesByUserIdSortedByDate(user.getId());
    if(!recentHistory.isEmpty()){ %>
      <h3>The Last <%=recentHistory.size()%> Quizzes taken by You:</h3>
      <% int cnt3 = 0;
      for(History h: recentHistory){
        if(cnt3 == 5) break;%>
        <p><%=quizDao.getQuizById(h.getQuizId()).getName()%></p>
        <p>Your Score: <%=h.getScore()%></p>
        <p>Your Time: <%=h.getCompletionTime()%></p>
        <p>Taken On: <%=h.getCompletionDate()%></p>
      <%
          cnt3++;}
    }else{%>
      <p>You have not taken any Quizzes yet.</p>
  <%}%>
</div>


<!--History of user created quizzes-->
<div>
  <%
    ArrayList<Quiz> creationHistory = quizDao.getQuizzesByCreatorId(user.getId());
    int cnt4 = 0;
    if(!creationHistory.isEmpty()){ %>
      <h2>History of Your Quizzes</h2>
      <h3>The Last Quizzes created by You:</h3>
      <%for(Quiz q: creationHistory){
        if(cnt4 == 5) break;%>
        <p><%=q.getName()%></p>
        <p> <%=q.getDescription()%></p>
        <p>Average Rating: <%=q.getAvgRating()%></p>
        <p>Taken By: <%=q.getParticipantCount()%> Users.</p>
      <%
      cnt4++;}%>
  <%}%>
</div>

<!--lookup -->
<div>
  <h2>Lookup</h2>
  <form action="friends.jsp" method="get">
    <label>
      <input type="text" name="query">
    </label>
    <button type="submit">Search</button>
  </form> <br>
</div>

<!-- Create/take quiz -->
<div>
  <form action="create-quiz.jsp" method="get">
    <button type="submit">Create Quiz</button>
  </form> <br>

  <form action="take-quiz.jsp" method="get">
    <button type="submit">Take Quiz</button>
  </form>
</div>

<!-- messages TODO link to see more-->
<div>
  <h2>Your Messages</h2>
  <%
    ArrayList<Message> messages = messageDao.getMessagesForUserSorted(user.getId());
    if(messages != null && !messages.isEmpty()){ %>
      <p>You have <%=messages.size()%> new message(s).</p>
    <%
      int cnt5 = 0;
      for(Message m: messages){
        if(cnt5 == 3) break;%>
        <p>Sent From <%=usersDao.getUserById(m.getSenderUserId()).getUsername()%></p>
      <%cnt5++;
      }%>
      <a href="messages.jsp">See all messages</a>
  <% }else{%>
      <p>No new messages.</p>
     <%}%>
</div>

<!-- challenges  TODO link to see more -->
<div>
  <h2>Your Challenges</h2>
  <%
    ArrayList<Challenge> challenges = challengeDao.challengesAsReceiver(user.getId());
    if(challenges != null && !challenges.isEmpty()){ %>
      <p>You have <%=challenges.size()%> new challenge(s).</p>
      <%
      int cnt6 = 0;
      for(Challenge c: challenges){
        if(cnt6 == 3) break;%>
        <p>Sent From <%=usersDao.getUserById(c.getSenderUserId()).getUsername()%></p>
      <%cnt6++;
      }%>
      <a href="challenges.jsp">See all challenges</a>
  <%}else{%>
    <p>No new challenges.</p>
  <%}%>
</div>

<!-- achievements TODO link to see more -->
<div>
  <h2>Your Achievements</h2>
  <%
    ArrayList<UserAchievement> achievements = userAchievementDao.getUserAchievements(user.getId());
    if(achievements != null && !achievements.isEmpty()) {
      int cnt7 = 0;
      for(UserAchievement a: achievements){
        if(cnt7 == 3) break; %>
        <p><%=achievementDao.getAchievement(a.getAchievementId()).getAchievementName()%></p>
     <%   cnt7++;
      } %>
        <a href="achievements.jsp">See all achievements</a>
   <% }else{ %>
      <p>You have no achievements yet</p>
   <%  }
   %>
</div>

<!--friends activity (taken quizzes) -->
<div>
  <h2>Recent quizzes taken by your friends.</h2>
  <%
    ArrayList<History> friendsQuizzes = historiesDao.getUserFriendsHistorySortedByCompletionDate(user.getId());
    if(friendsQuizzes != null && !friendsQuizzes.isEmpty()) {
      int cnt8 = 0;
      for(History h: friendsQuizzes){
        if(cnt8 == 3) break; %>
        <p><%=quizDao.getQuizById(h.getQuizId()).getName()%></p>
        <p>Taken by: <%=usersDao.getUserById(h.getUserId()).getUsername()%></p>
        <p>Taken on: <%=h.getCompletionDate()%></p>
      <% cnt8++;
      }
    }else{%>
      <p>None of your friends have taken quizzes yet.</p>
    <%}%>
</div>

<!--friends activity (created quizzes) -->
<div>
  <h2>Recent quizzes created by your friends.</h2>
  <%
    ArrayList<Quiz> quizzesByFriends = quizDao.getFriendsQuizzesSortedByCreationDate(user.getId());
    if(quizzesByFriends != null && !quizzesByFriends.isEmpty()) {
      int cnt9 = 0;
      for(Quiz q: quizzesByFriends){
        if(cnt9 == 3) break; %>
        <p><%=q.getName()%></p>
        <p>Created by: <%=usersDao.getUserById(q.getCreatorId()).getUsername()%></p>
        <% cnt9++;
      }
    }else{%>
    <p>None of your friends have created quizzes yet.</p>
  <%}%>
</div>

<!-- requests  TODO link to see more -->
<div>
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
</body>
</html>

