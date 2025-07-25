<%@ page import="org.ja.utils.Constants" %>
<%@ page import="org.ja.model.quiz.Quiz" %>
<%@ page import="java.util.*" %>
<%@ page import="org.ja.model.data.History" %>
<%@ page import="org.ja.model.data.User" %>
<%@ page import="org.ja.dao.*" %>
<%@ page import="org.ja.model.data.QuizRating" %>
<%@ page import="org.ja.utils.NumUtils" %>
<%@ page import="org.ja.utils.TimeUtils" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    QuizzesDao quizzesDao = (QuizzesDao) application.getAttribute(Constants.ContextAttributes.QUIZZES_DAO);
    UsersDao usersDao = (UsersDao) application.getAttribute(Constants.ContextAttributes.USERS_DAO);
    HistoriesDao historiesDao = (HistoriesDao) application.getAttribute(Constants.ContextAttributes.HISTORIES_DAO);
    CategoriesDao categoriesDao = (CategoriesDao) application.getAttribute(Constants.ContextAttributes.CATEGORIES_DAO);
    QuizTagsDao quizTagsDao = (QuizTagsDao) application.getAttribute(Constants.ContextAttributes.QUIZ_TAG_DAO);
    TagsDao tagsDao = (TagsDao) application.getAttribute(Constants.ContextAttributes.TAGS_DAO);
    QuizRatingsDao quizRatingsDao = (QuizRatingsDao) application.getAttribute(Constants.ContextAttributes.QUIZ_RATING_DAO);

    User user = (User) session.getAttribute(Constants.SessionAttributes.USER);
    long quizId = Long.parseLong(request.getParameter(Constants.RequestParameters.QUIZ_ID));
    Quiz quiz = quizzesDao.getQuizById(quizId);

    String quizName = quiz.getName();
    String quizDescription = quiz.getDescription();
    int quizScore = quiz.getScore();
    String creatorName = usersDao.getUserById(quiz.getCreatorId()).getUsername();
    String categoryName = categoriesDao.getCategoryById(quiz.getCategoryId()).getCategoryName();
    List<Long> quizTagIds = quizTagsDao.getTagsByQuizId(quizId, Constants.FETCH_LIMIT);
    double averageRating = quiz.getAvgRating();
    List<QuizRating> quizRatings = quizRatingsDao.getQuizRatingsByQuizId(quizId, Constants.FETCH_LIMIT);
    long participantCount = quiz.getParticipantCount();

    List<String> quizTags = new ArrayList<String>();
    for (Long quizTag: quizTagIds)
        quizTags.add(tagsDao.getTagById(quizTag).getTagName());

    Map<User, String> userReviews = new HashMap<User, String>();
    for (QuizRating quizRating: quizRatings){
        if (quizRating.getReview() != null){
            User currUser = usersDao.getUserById(quizRating.getUserId());
            userReviews.put(currUser, quizRating.getReview());
        }
    }

    boolean isCreator = user.getId() == quiz.getCreatorId();

    List<History> histories = historiesDao.getUserHistoryByQuiz(user.getId(), quizId, Constants.FETCH_LIMIT);
    List<History> topPerformers = historiesDao.getTopNDistinctHistoriesByQuizId(quizId, 3);
    List<History> allPerformers = historiesDao.getDistinctTopHistoriesByQuizId(quizId, Constants.FETCH_LIMIT);
    List<History> recentPerformers = historiesDao.getHistoriesByQuizId(quizId, Constants.FETCH_LIMIT);

    long totalAttempts = historiesDao.getTotalAttempts(quizId);
    double averageScore = historiesDao.getAverageScore(quizId);
    long maxScore = historiesDao.getMaximumScore(quizId);
    long minScore = historiesDao.getMinimumScore(quizId);
    double averageTime = historiesDao.getAverageTime(quizId);

    Map<String, List<History>> topByRange = new HashMap<String, List<History>>();
    topByRange.put("last_day", historiesDao.getTopPerformersByQuizIdAndRange(quizId, "last_day", Constants.FETCH_LIMIT));
    topByRange.put("last_week", historiesDao.getTopPerformersByQuizIdAndRange(quizId, "last_week", Constants.FETCH_LIMIT));
    topByRange.put("last_month", historiesDao.getTopPerformersByQuizIdAndRange(quizId, "last_month", Constants.FETCH_LIMIT));
    topByRange.put("last_year", historiesDao.getTopPerformersByQuizIdAndRange(quizId, "last_year", Constants.FETCH_LIMIT));
%>

<html>
<head>
    <title>Quiz Overview</title>
    <link rel="stylesheet" href="css/quiz-overview.css" />
    <link rel="stylesheet" href="css/hotlink.css">
    <script src="js/quiz-overview.js" defer></script>
</head>

<body>

<%--Basic Info--%>
<div class="information-container">
    <h1 class="quiz-title"><%= quizName %></h1>

    <h2 class="creator-name">Creator: <a class="hotlink" href="visit-user.jsp?<%=Constants.RequestParameters.USER_ID%>=<%=quiz.getCreatorId()%>"><%= creatorName %></a></h2>
    <h2 class="creation-date">Created on: <%=quiz.getCreationDate().toLocalDateTime().format(TimeUtils.DATE_TIME_FORMATTER)%></h2>

    <p class="quiz-description"><%= quizDescription %></p>

    <div class="quiz-meta">
        <p><strong>Max Score:</strong> <%= quizScore %></p>
        <p><strong>Average Rating:</strong> <%= NumUtils.formatOneDecimal(averageRating) %></p>
        <p><strong>Participant Count:</strong> <%=participantCount%></p>
        <p><strong>Category:</strong> <%= categoryName %></p>
        <p><strong>Tags:</strong> <%= String.join(", ", quizTags) %></p>
    </div>

    <div class="quiz-reviews">
        <%
            if(!userReviews.keySet().isEmpty()){%>
                <p><Strong>Reviews:</Strong></p>

                <% for (User currUser : userReviews.keySet()) {
                    String review = userReviews.get(currUser); %>

                <div class="review-block">
                    <h4><a class="hotlink"
                           href="visit-user.jsp?<%=Constants.RequestParameters.USER_ID%>=<%=currUser.getId()%>"><%= currUser.getUsername() %>
                    </a></h4>
                    <%=review%>
                </div>
        <% }}
        %>
    </div>
</div>


<%--Upper Row--%>
<div class="first-row">
    <div class="section-container">
        <h3>Your Attempts on this Quiz</h3>
        <% if (histories.isEmpty()){
        %>
        No activity to show
        <%
        } else {
        %>
        <label for="sortBy">Sort by:</label>
        <select id="sortBy" onchange="sortTable()">
            <option value="date">Date</option>
            <option value="time">Time taken</option>
            <option value="percentage">Percent Correct</option>
        </select>
        <button id="sortDirectionBtn" onclick="toggleSortDirection()">Sort Ascending</button>

        <%--user's performance on quiz--%>
        <div class="scrollable-pane">
            <table id="historyTable" class="styled-table">
                <thead>
                <tr>
                    <th>Date</th>
                    <th>Time</th>
                    <th>Percent Correct</th>
                </tr>
                </thead>
                <tbody>
                <% for (History h : histories) {
                    String dateInfo = h.getCompletionDate().toLocalDateTime().format(TimeUtils.DATE_TIME_FORMATTER);
                    String timeInfo = TimeUtils.formatDuration(h.getCompletionTime());
                    String percentInfo = NumUtils.formatPercentage((double) h.getScore() / quizScore);
                %>

                <tr data-date="<%=dateInfo%>"
                    data-time="<%=timeInfo%>"
                    data-percentage="<%=percentInfo%>">

                    <td><%=dateInfo%></td>
                    <td><%=timeInfo%></td>
                    <td><%=percentInfo%></td>
                </tr>
                <% } %>
                </tbody>
            </table>
        </div>

        <%
            }
        %>
    </div>

    <div class="section-container">
        <%--top performers--%>
        <h3>Top Performers</h3>
        <% if (topPerformers.isEmpty()){
        %>
        No activity to show
        <%
        } else {
        %>
        <div class="buttons-section">
            <button id="showTopBtn" onclick="showTopPerformers()" style="display: none;">Show Top 3</button>
            <button id="showAllBtn" onclick="showAllPerformers()">Show All</button>
        </div>

        <div id="topPerformersContainer" class="scrollable-pane">
            <table id="topPerformersTable" class="styled-table">
                <thead>
                <tr>
                    <th>User</th>
                    <th>Score</th>
                    <th>Time</th>
                    <th>Date</th>
                </tr>
                </thead>
                <tbody>
                <% for (History h : topPerformers) {
                    User performer = usersDao.getUserById(h.getUserId());
                %>
                <tr>
                    <td><a class="hotlink" href="visit-user.jsp?<%=Constants.RequestParameters.USER_ID%>=<%=h.getUserId()%>">
                        <%= performer.getUsername() %></a></td>
                    <td><%= h.getScore() %></td>
                    <td><%= TimeUtils.formatDuration(h.getCompletionTime()) %></td>
                    <td><%= h.getCompletionDate().toLocalDateTime().format(TimeUtils.DATE_TIME_FORMATTER) %></td>
                </tr>
                <% } %>
                </tbody>
            </table>
        </div>

        <div class="scrollable-pane" id="allPerformersContainer">
            <table id="allPerformersTable" class="styled-table">
                <thead>
                <tr>
                    <th>User</th>
                    <th>Score</th>
                    <th>Time</th>
                    <th>Date</th>
                </tr>
                </thead>
                <tbody>
                <% for (History h : allPerformers) {
                    User performer = usersDao.getUserById(h.getUserId());
                %>
                <tr>
                    <td><a class="hotlink" href="visit-user.jsp?<%=Constants.RequestParameters.USER_ID%>=<%=h.getUserId()%>"><%= performer.getUsername() %></a></td>
                    <td><%= h.getScore() %></td>
                    <td><%= TimeUtils.formatDuration(h.getCompletionTime()) %></td>
                    <td><%= h.getCompletionDate().toLocalDateTime().format(TimeUtils.DATE_TIME_FORMATTER) %></td>
                </tr>
                <% } %>
                </tbody>
            </table>
        </div>
        <%
            }
        %>
    </div>

    <div class="section-container">
        <%--range--%>
        <h3>Top Performers by Range</h3>
        <% if (topByRange.isEmpty()){
        %>
        No activity to show
        <%
        } else {
        %>
        <select id="timeFilter" onchange = "filterByRange()">
            <option value="last_day">Last Day</option>
            <option value="last_week">Last Week</option>
            <option value="last_month">Last Month</option>
            <option value="last_year">Last Year</option>
        </select>

        <div id = "rangeContainer">
            <% for (String range: Arrays.asList("last_day", "last_week", "last_month", "last_year")){
                List<History> list = topByRange.get(range);
            %>

            <div class="range-table scrollable-pane" id = "range-<%=range%>" style = "<%="last_day".equals(range) ? "" : "display:none;"%>">
                <table class="styled-table">
                    <thead>
                    <tr>
                        <th>User</th>
                        <th>Score</th>
                        <th>Time</th>
                        <th>Date</th>
                    </tr>
                    </thead>

                    <tbody>
                    <% for (History history: list){
                        User performer = usersDao.getUserById(history.getUserId());
                    %>
                    <tr>
                        <td><a class="hotlink" href="visit-user.jsp?<%=Constants.RequestParameters.USER_ID%>=<%=performer.getId()%>"><%= performer.getUsername() %></a></td>
                        <td><%=history.getScore()%></td>
                        <td><%= TimeUtils.formatDuration(history.getCompletionTime()) %></td>
                        <td><%= history.getCompletionDate().toLocalDateTime().format(TimeUtils.DATE_TIME_FORMATTER) %></td>

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
        <%
            }
        %>
    </div>
</div>


<%--Lower Row--%>
<div class="second-row">
    <div class="section-container">
        <%--recent performers--%>
        <h3>Recent Performers</h3>
        <% if (recentPerformers.isEmpty()){
        %>
        No activity to show
        <%
        } else {
        %>
        <div>
            <div class="scrollable-pane">
                <table class="styled-table">
                    <thead>
                    <tr>
                        <th>User</th>
                        <th>Score</th>
                        <th>Time</th>
                        <th>Date</th>
                    </tr>
                    </thead>

                    <tbody>
                    <% for (History history: recentPerformers){
                        User performer = usersDao.getUserById(history.getUserId());
                    %>
                    <tr>
                        <td><a class="hotlink" href="visit-user.jsp?<%=Constants.RequestParameters.USER_ID%>=<%=performer.getId()%>"><%= performer.getUsername() %></a></td>
                        <td><%=history.getScore()%></td>
                        <td><%= TimeUtils.formatDuration(history.getCompletionTime()) %></td>
                        <td><%= history.getCompletionDate().toLocalDateTime().format(TimeUtils.DATE_TIME_FORMATTER) %></td>

                    </tr>
                    <%
                        }
                    %>
                    </tbody>
                </table>
            </div>
        </div>
        <%
            }
        %>
    </div>
    <div class="section-container">
        <%--statistics --%>
        <h3>Summary Statistics</h3>
        <% if (recentPerformers.isEmpty()){
        %>
        No activity to show
        <%
        } else {
        %>
        <div class="summary-statistics">
            <ul>
                <li>Total attempts: <%=totalAttempts%></li>
                <li>Average Score: <%=NumUtils.formatOneDecimal(averageScore)%></li>
                <li>Highest Score: <%=maxScore%></li>
                <li>Lowest Score: <%=minScore%></li>
                <li>Average Time: <%=TimeUtils.formatDuration(averageTime)%></li>
            </ul>
        </div>
        <%
            }
        %>
    </div>
    <div class="buttons-section">
        <%--buttons--%>
        <form action="start-quiz" method="get">
            <input type="hidden" name="<%=Constants.RequestParameters.QUIZ_ID%>" value="<%=quizId%>">
            <button type="submit">Start Quiz</button>
        </form>
        <form action="practice-quiz" method="post">
            <input type="hidden" name="<%=Constants.RequestParameters.QUIZ_ID%>" value="<%=quizId%>">
            <button type="submit">Start Quiz in Practice Mode</button>
        </form>
        <form action="edit-quiz.jsp" method="post">
            <input type="hidden" name="<%= Constants.RequestParameters.QUIZ_ID %>" value="<%=quizId%>">
            <button type="submit" <%= isCreator ? "" : "disabled" %>>Edit quiz</button>
        </form>
        <form action="quiz-search.jsp" method="get" >
            <button type="submit">Go to Quiz Search </button>
        </form>
        <form action="user-page.jsp" method="get">
            <button type="submit">Go Home</button>
        </form>
    </div>
</div>
</body>
</html>

