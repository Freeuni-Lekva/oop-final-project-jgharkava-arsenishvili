<%@ page import="org.ja.utils.Constants" %>
<%@ page import="org.ja.model.quiz.Quiz" %>
<%@ page import="org.ja.dao.QuizzesDao" %>
<%@ page import="org.ja.dao.UsersDao" %>
<%@ page import="org.ja.dao.HistoriesDao" %>
<%@ page import="java.util.*" %>
<%@ page import="org.ja.model.OtherObjects.History" %>
<%@ page import="org.ja.model.user.User" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    QuizzesDao quizzesDao = (QuizzesDao) application.getAttribute(Constants.ContextAttributes.QUIZZES_DAO);
    UsersDao usersDao = (UsersDao) application.getAttribute(Constants.ContextAttributes.USERS_DAO);
    HistoriesDao historiesDao = (HistoriesDao) application.getAttribute(Constants.ContextAttributes.HISTORIES_DAO);

    User user = usersDao.getUserById(1);
    long quizId = 2;
    Quiz quiz = quizzesDao.getQuizById(quizId);

    String quizName = quiz.getName();
    String quizDescription = quiz.getDescription();
    int quizScore = quizzesDao.getQuizScore(quizId);
    String creatorName = usersDao.getUserById(quiz.getCreatorId()).getUsername();

    List<History> histories = historiesDao.getUserHistoryByQuiz(user.getId(), quizId);
    List<History> topPerformers = historiesDao.getTopNDistinctHistoriesByQuizId(quizId, 3);
    List<History> allPerformers = historiesDao.getDistinctTopHistoriesByQuizId(quizId);

    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm");

    Map<String, List<History>> topByRange = new HashMap<String, List<History>>();
    topByRange.put("last_day", historiesDao.getTopPerformersByQuizIdAndRange(quizId, "last_day"));
    topByRange.put("last_week", historiesDao.getTopPerformersByQuizIdAndRange(quizId, "last_week"));
    topByRange.put("last_month", historiesDao.getTopPerformersByQuizIdAndRange(quizId, "last_month"));
    topByRange.put("last_year", historiesDao.getTopPerformersByQuizIdAndRange(quizId, "last_year"));

%>

<html>
<head>
    <title>Quiz Overview</title>
    <link rel="stylesheet" href="css/quiz-overview.css" />
    <script src="js/quiz-overview.js" defer></script>
</head>
<body>
<h1><%= quizName %></h1>
<h2><%= creatorName %></h2>
<p><%= quizDescription%></p>
<h2>Max Score: <%= quizScore %></h2>

<h3>Your Attempts on this Quiz</h3>
<label for="sortBy">Sort by:</label>
<select id="sortBy" onchange="sortTable()">
    <option value="date">Date</option>
    <option value="time">Time taken</option>
    <option value="percentage">Percent Correct</option>
</select>
<button id="sortDirectionBtn" onclick="toggleSortDirection()">Sort Ascending</button>

<%--user's performance on quiz--%>
<table id="historyTable" class="styled-table">
    <thead>
    <tr>
        <th>Date</th>
        <th>Time (min)</th>
        <th>Percent Correct</th>
    </tr>
    </thead>
    <tbody>
    <% for (History h : histories) {
        double percentage = (double) (100 * h.getScore()) / quizScore;
    %>
    <tr data-date="<%= sdf.format(h.getCompletionDate()) %>"
        data-time="<%= String.format(Locale.US, "%.2f", h.getCompletionTime()) %>"
        data-percentage="<%= String.format(Locale.US, "%.2f", percentage) %>">
        <td><%= sdf.format(h.getCompletionDate()) %></td>
        <td><%= String.format(Locale.US, "%.2f", h.getCompletionTime()) %></td>
        <td><%= String.format(Locale.US, "%.2f", percentage) %></td>
    </tr>
    <% } %>
    </tbody>
</table>

<%--top performers--%>
<h3>Top Performers</h3>
<div>
    <button id="showTopBtn" onclick="showTopPerformers()" style="display: none;">Show Top 3</button>
    <button id="showAllBtn" onclick="showAllPerformers()">Show All</button>
</div>

<div id="topPerformersContainer">
    <table id="topPerformersTable" class="styled-table">
        <thead>
        <tr>
            <th>User</th>
            <th>Score</th>
            <th>Time (min)</th>
            <th>Date</th>
        </tr>
        </thead>
        <tbody>
        <% for (History h : topPerformers) {
            User performer = usersDao.getUserById(h.getUserId());
        %>
        <tr>
            <td><%= performer.getUsername() %></td>
            <td><%= h.getScore() %></td>
            <td><%= String.format(Locale.US, "%.2f", h.getCompletionTime()) %></td>
            <td><%= sdf.format(h.getCompletionDate()) %></td>
        </tr>
        <% } %>
        </tbody>
    </table>
</div>

<div id="allPerformersContainer">
    <table id="allPerformersTable" class="styled-table">
        <thead>
        <tr>
            <th>User</th>
            <th>Score</th>
            <th>Time (min)</th>
            <th>Date</th>
        </tr>
        </thead>
        <tbody>
        <% for (History h : allPerformers) {
            User performer = usersDao.getUserById(h.getUserId());
        %>
        <tr>
            <td><%= performer.getUsername() %></td>
            <td><%= h.getScore() %></td>
            <td><%= String.format(Locale.US, "%.2f", h.getCompletionTime()) %></td>
            <td><%= sdf.format(h.getCompletionDate()) %></td>
        </tr>
        <% } %>
        </tbody>
    </table>
</div>

<%--range--%>
<h3>Top Performers by Range:</h3>
<select id = "timeFilter" onchange = "filterByRange()">
    <option value = "last_day">Last Day</option>
    <option value = "last_week">Last Week</option>
    <option value = "last_month">Last Month</option>
    <option value = "last_year">Last Year</option>
</select>

<div id = "rangeContainer">
    <% for (String range: Arrays.asList("last_day", "last_week", "last_month", "last_year")){
        List<History> list = topByRange.get(range);
    %>


    <div class = "range-table scrollable-pane" id = "range-<%=range%>" style = "<%="last_day".equals(range) ? "" : "display:none;"%>">
        <table class = "styled-table">
            <thead>
            <tr>
                <th>User</th>
                <th>Score</th>
                <th>Time (min)</th>
                <th>Date</th>
            </tr>
            </thead>

            <tbody>
            <% for (History history: list){
                User performer = usersDao.getUserById(history.getUserId());
            %>
            <tr>
                <td><%=performer.getUsername()%></td>
                <td><%=history.getScore()%></td>
                <td><%=String.format(Locale.US, "%.2f", history.getCompletionTime())%></td>
                <td><%=sdf.format(history.getCompletionDate())%></td>

            </tr>
            <%
                }
            %>
            </tbody>
        </table>
    </div>
    <%
        }
    %>
</div>

</body>
</html>

