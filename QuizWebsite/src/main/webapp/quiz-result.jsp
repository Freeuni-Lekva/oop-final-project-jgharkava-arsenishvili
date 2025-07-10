<%@ page import="java.util.List" %>
<%@ page import="org.ja.model.quiz.response.Response" %>
<%@ page import="org.ja.utils.Constants" %>
<%@ page import="org.ja.model.quiz.question.Question" %>
<%@ page import="org.ja.model.quiz.Quiz" %>
<%@ page import="org.ja.model.OtherObjects.Match" %>
<%@ page import="org.ja.model.OtherObjects.Answer" %>
<%@ page import="org.ja.model.user.User" %>
<%@ page import="org.ja.model.OtherObjects.Friendship" %>
<%@ page import="org.ja.dao.*" %>
<%@ page import="org.ja.model.OtherObjects.History" %>
<%@ page import="org.ja.utils.NumUtils" %>
<%@ page import="org.ja.utils.TimeUtils" %><%--
  Created by IntelliJ IDEA.
  User: tober
  Date: 7/1/2025
  Time: 10:14 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    MatchesDao matchesDao = (MatchesDao) application.getAttribute(Constants.ContextAttributes.MATCHES_DAO);
    AnswersDao answersDao = (AnswersDao) application.getAttribute(Constants.ContextAttributes.ANSWERS_DAO);
    HistoriesDao historyDao = (HistoriesDao) application.getAttribute(Constants.ContextAttributes.HISTORIES_DAO);
    UsersDao usersDao = (UsersDao) application.getAttribute(Constants.ContextAttributes.USERS_DAO);
    FriendShipsDao friendShipsDao = (FriendShipsDao) application.getAttribute(Constants.ContextAttributes.FRIENDSHIPS_DAO);

    User user = (User) session.getAttribute(Constants.SessionAttributes.USER);

    List<Friendship> friends = friendShipsDao.getFriends(user.getId());

    Quiz quiz = (Quiz) session.getAttribute(Constants.SessionAttributes.QUIZ);
    List<Integer> grades = (List<Integer>) session.getAttribute("grades");
    List<List<Integer>> responseGrades = (List<List<Integer>>) session.getAttribute("responseGrades");
    List<Response> responses = (List<Response>) session.getAttribute(Constants.SessionAttributes.RESPONSES);
    List<Question> questions = (List<Question>) session.getAttribute(Constants.SessionAttributes.QUESTIONS);

    int totalScore = 0;
    for (Integer g : grades) {
        totalScore += g;
    }

    double timeSpent = (Double) session.getAttribute("time-spent-in-minutes");

%>

<html>
<head>
    <title>Quiz Result</title>
    <link rel="stylesheet" type="text/css" href="css/quiz-result.css">
    <link rel="stylesheet" type="text/css" href="css/hotlink.css">
    <script src="js/quiz-result.js" defer></script>
</head>
<body>

<input type="hidden" id="quiz-id-hidden" value="<%=quiz.getId()%>">

<div class="score-box">
    <h2>Quiz Result</h2>
    <p><span class="label">Your Total Score:</span> <%= totalScore %> / <%= quiz.getScore() %></p>
    <p><span class="label">Your Time Spent: <%= TimeUtils.formatDuration(timeSpent) %> </span> </p>
</div>

<h2 class="section-title">Review Your Answers</h2>

<%
    for(int j = 0; j < questions.size(); j++) {
        Question question = questions.get(j);

        Response resp;
        int grade;
        List<Integer> respGrades = null;

        if(responses.size() <= j) {
            resp = null;
            grade = 0;
        } else {
            resp = responses.get(j);
            grade = grades.get(j);
            respGrades = responseGrades.get(j);
        }

        String type = question.getQuestionType();
%>
<div class="question-block">
    <div class="question-text">
        Question <%= j + 1 %>:
        <%if(Constants.QuestionTypes.FILL_IN_THE_BLANK_QUESTION.equals(type)) {%>
            <%=question.getQuestionText().replace("_", "_____")%><%
        } else {%>
            <%=question.getQuestionText() != null ? question.getQuestionText() : ""%><%
        }%>
    </div>

    <% if (question.getImageUrl() != null) { %>
    <img src="<%= question.getImageUrl() %>" alt="Question Image" width="300" height="200"><br><br>
    <% } %>

    <% if(Constants.QuestionTypes.MATCHING_QUESTION.equals(type)) {
        List<Match> matches = matchesDao.getQuestionMatches(question.getQuestionId());
    %>
    <div class="answer-block">
        <div class="label">Your Matches:</div>
        <%
            if(resp == null) {%>
                <h6>Haven't Chosen In Time</h6><%
            } else {
                for(int i = 0; i < resp.size(); i++) {
                    Match match = resp.getMatch(i);%>

                <div class="<%=respGrades.get(i) > 0 ? "correct" : "incorrect"%>"><%= match.getLeftMatch() %> → <%= match.getRightMatch() %></div><%
                }
            }%>
    </div>

    <div class="answer-block">
        <div class="label">Correct Matches:</div>
        <% for(Match match : matches) { %>
        <div><%= match.getLeftMatch() %> → <%= match.getRightMatch() %></div>
        <% } %>
    </div>

    <% }

    /// MULTI CHOICE QUESTIONS
    else if(Constants.QuestionTypes.MULTI_CHOICE_MULTI_ANSWER_QUESTION.equals(type) || Constants.QuestionTypes.MULTIPLE_CHOICE_QUESTION.equals(type)) {
        List<Answer> answers = answersDao.getQuestionAnswers(question.getQuestionId());
        int responseIndex = 0;
    %>
    <div class="answer-block">
        <div class="label">Your Answer(s):</div>
        <%
            if(resp == null) {%>
                <h6>Haven't Chosen In Time</h6>
        <%
            } else {
                for (Answer answer : answers) {
                    boolean checked = responseIndex < resp.size() && answer.containsAnswer(resp.getAnswer(responseIndex));
                    String answerClass = "";
                    if (checked) {
                        int gr = respGrades.get(responseIndex);
                        answerClass = gr > 0 ? "correct" : "incorrect";
                        responseIndex++;
                    }%>

                    <div class="<%= answerClass %>">
                        <input type="radio" <%=checked ? "checked" : ""%> disabled>
                        <%--TODO allign next to radio--%>
                        <%= answer.getAnswerText() %>
                    </div><%
                }
            }%>
    </div>

    <div class="answer-block">
        <div class="label">Correct Answer(s):</div>
        <% for (Answer answer : answers) { %>
        <div>
            <input type="radio" <%= answer.getAnswerValidity() ? "checked" : "" %> disabled>
            <%= answer.getAnswerText() %>
        </div>
        <% } %>
    </div>

    <% }

    /// OTHER TYPE OF QUESTIONS
    else {
        List<Answer> answers = answersDao.getQuestionAnswers(question.getQuestionId());
    %>
    <div class="answer-block">
        <div class="label">Your Answer(s):</div>
        <%
            if(resp == null) {%>
                <h6>Haven't Chosen In Time</h6><%
            } else {
                for(int i = 0; i < resp.size(); i++) { %>
                    <div class="<%=respGrades.get(i) > 0 ? "correct" : "incorrect"%>"><%= resp.getAnswer(i).trim().isEmpty() ? "Left Empty" : resp.getAnswer(i) %></div><%
                }

                for(int i = resp.size(); i < question.getNumAnswers(); i++) { %>
                    <div class="incorrect">Left Empty</div><%
                }
            } %>
    </div>

    <div class="answer-block">
        <div class="label">Correct Answer(s):</div>
        <% for(Answer answer : answers) {
            String answerText = answer.getAnswerText();%>
        <div><%= answerText.contains("¶") ? answerText.substring(0, answerText.indexOf('¶')) : answerText %></div>
        <% } %>
    </div>
    <% } %>
</div>

    <div class="score-display">
        Score: <%=grade%> out of <%=question.getNumAnswers()%>
    </div>
<% } %>


<%--challenge friend--%>
<%--TODO make pretty--%>
<div id="user-list-panel"><%
    if(!friends.isEmpty()) {
            for(Friendship friendship : friends) {
                User friend = usersDao.getUserById(friendship.getFirstUserId() == user.getId() ? friendship.getSecondUserId() : friendship.getFirstUserId());%>

    <div class="user-entry">
        <span>
            <a class="hotlink" href="visit-user.jsp?<%=Constants.RequestParameters.USER_ID%>=<%=friend.getId()%>">
                <%= friend.getUsername() %>
            </a>
        </span>

        <button onclick="sendChallenge(this, <%=friend.getId()%>)">Challenge</button>
    </div><%
            }
    } else {%>
    <p>No users available to challenge</p><%
    }%>
</div>


<!-- Quiz Review & Rating -->
<div class="quiz-feedback-section">
    <h3>Rate This Quiz</h3>

    <div id="rating-stars" class="star-rating">
        <span data-value="5">&#9733;</span>
        <span data-value="4">&#9733;</span>
        <span data-value="3">&#9733;</span>
        <span data-value="2">&#9733;</span>
        <span data-value="1">&#9733;</span>
    </div>

    <textarea id="quiz-review" rows="4" cols="50" placeholder="Leave a comment about the quiz..." style="margin-top: 10px; width: 100%; max-width: 600px;"></textarea>

    <br><br>
    <button onclick="submitReview()">Submit Rating & Review</button>
    <p id="rating-status" style="margin-top: 10px;"></p>
</div>


<%-- User's past performance on this quiz--%>

<%
    List<History> usersPastPerformance = historyDao.getUserHistoryByQuiz(user.getId(), quiz.getId());
%>

<div class="scrollable-history-table-title">
    <h3>Your Past attempts on this quiz:</h3>
</div>

<div class="scrollable-history-table">
    <table>
        <thead>
        <tr>
            <th>Attempt #</th>
            <th>Date</th>
            <th>Score</th>
            <th>Time taken</th>
        </tr>
        </thead>
        <tbody>
        <%
            int attempt = 1;
            for (History h : usersPastPerformance) {
        %>
        <tr>
            <td><%= attempt++ %></td>
            <td><%= h.getCompletionDate().toLocalDateTime().format(TimeUtils.DATE_TIME_FORMATTER) %></td>
            <td><%= h.getScore() %></td>
            <td><%= TimeUtils.formatDuration(h.getCompletionTime()) %></td>
        </tr>
        <%
            }
            if (usersPastPerformance.isEmpty()) {
        %>
        <tr>
            <td colspan="4">No attempts yet.</td>
        </tr>
        <%
            }
        %>
        </tbody>
    </table>
</div>

<%-- Friends' history on this quiz--%>

<%
    List<History> friendsHistory = historyDao.getUserFriendsHistoryByQuiz(user.getId(), quiz.getId());
%>

<div class="scrollable-history-table-title">
    <h3>Your Friends' attempts on this quiz:</h3>
</div>

<div class="scrollable-history-table">
    <table>
        <thead>
        <tr>
            <th>Friend</th>
            <th>Date</th>
            <th>Score</th>
            <th>Time taken</th>
        </tr>
        </thead>
        <tbody>
        <%
            for (History h : friendsHistory) {
                long friendId = h.getUserId();
                String friendName = usersDao.getUserById(friendId).getUsername();
        %>
        <tr>
            <td>
                <a class="hotlink" href="visit-user.jsp?<%=Constants.RequestParameters.USER_ID%>=<%=friendId%>"><%=friendName%></a>
            </td>
            <td><%= h.getCompletionDate() %></td>
            <td><%= h.getScore() %></td>
            <td><%= TimeUtils.formatDuration(h.getCompletionTime()) %></td>
        </tr>
        <%
            }
            if (friendsHistory.isEmpty()) {
        %>
        <tr><td colspan="5">No friends have taken this quiz yet.</td></tr>
        <%
            }
        %>
        </tbody>
    </table>
</div>

<%--return to homepage--%>

<form action="user-page.jsp" method="get">
    <button type="submit" class="back-btn">Back To Homepage</button>
</form>

</body>
</html>
