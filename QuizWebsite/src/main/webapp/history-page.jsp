<%@ page import="org.ja.model.data.User" %>
<%@ page import="org.ja.utils.Constants" %>
<%@ page import="org.ja.dao.HistoriesDao" %>
<%@ page import="org.ja.model.data.History" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="org.ja.dao.QuizzesDao" %>
<%@ page import="org.ja.dao.UsersDao" %>
<%@ page import="java.util.List" %>
<%@ page import="static org.ja.utils.TimeUtils.DATE_TIME_FORMATTER" %>
<%@ page import="static org.ja.utils.TimeUtils.formatDuration" %><%--
  Created by IntelliJ IDEA.
  User: tober
  Date: 7/6/2025
  Time: 8:51 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    long userId = Long.parseLong(request.getParameter(Constants.RequestParameters.USER_ID));
    User user = ((UsersDao)application.getAttribute(Constants.ContextAttributes.USERS_DAO)).getUserById(userId);
    User sessionUser = (User)session.getAttribute(Constants.SessionAttributes.USER);
    HistoriesDao hd=(HistoriesDao) application.getAttribute(Constants.ContextAttributes.HISTORIES_DAO);
    List<History> arr=hd.getHistoriesByUserId(user.getId());
    QuizzesDao quizDao = (QuizzesDao)application.getAttribute(Constants.ContextAttributes.QUIZZES_DAO);
%>
<html>
<head>
    <%
        if(sessionUser.getId() == userId) {%>
    <title>Your History</title><%
} else { %>
    <title><%=user.getUsername()%>'s History</title><%
    }
%>
    <link rel="stylesheet" type="text/css" href="css/history-page.css">
    <link rel="stylesheet" type="text/css" href="css/hotlink.css">
</head>
<body>
<div class="history-container">
    <div class="history-header">
        <%
            if(sessionUser.getId() == userId) {%>
        <h1>Your full history of quizzes:</h1><%
    } else { %>
        <h1>
            <a href="visit-user.jsp?<%=Constants.RequestParameters.USER_ID%>=<%=user.getId()%>"
               class="user-link">
                <%=user.getUsername()%>
            </a>'s full history of quizzes:
        </h1><%
        }
    %>
    </div>

    <% if (arr != null && !arr.isEmpty()) { %>

    <ul class="history-list">
        <%
            for(History h: arr){
        %>
        <li class="history-item">
            <a href="quiz-overview.jsp?<%=Constants.RequestParameters.QUIZ_ID%>=<%=h.getQuizId()%>"
               class="quiz-link">
                <%=quizDao.getQuizById(h.getQuizId()).getName()%>
            </a>

            <div class="quiz-details">
                <div class="detail-item score">
                    <span class="detail-label">Score</span>
                    <span class="detail-value"><%=h.getScore()%></span>
                </div>
                <div class="detail-item time">
                    <span class="detail-label">Time</span>
                    <span class="detail-value"><%=formatDuration(h.getCompletionTime())%></span>
                </div>
                <div class="detail-item date">
                    <span class="detail-label">Taken On</span>
                    <span class="detail-value"><%=h.getCompletionDate().toLocalDateTime().format(DATE_TIME_FORMATTER)%></span>
                </div>
            </div>
        </li>
        <%
            }
        %>
    </ul>
    <% } else { %>
    <div class="empty-history">
        <p>No quiz history found.</p>
    </div>
    <% } %><br>

    <form action="user-page.jsp" method="get" >
        <button type="submit" class="back-btn">Go Home</button>
    </form>
</div>
</body>
</html>