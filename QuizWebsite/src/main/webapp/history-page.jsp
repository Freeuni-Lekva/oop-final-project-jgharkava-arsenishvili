<%@ page import="org.ja.model.user.User" %>
<%@ page import="org.ja.utils.Constants" %>
<%@ page import="org.ja.dao.HistoriesDao" %>
<%@ page import="org.ja.model.OtherObjects.History" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="org.ja.dao.QuizzesDao" %>
<%@ page import="org.ja.dao.UsersDao" %><%--
  Created by IntelliJ IDEA.
  User: tober
  Date: 7/6/2025
  Time: 8:51 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    long userId = (long) request.getAttribute(Constants.RequestParameters.USER_ID);
    User user = ((UsersDao) application.getAttribute(Constants.ContextAttributes.USERS_DAO)).getUserById(userId);
    User sessionUser = (User) session.getAttribute(Constants.SessionAttributes.USER);
    HistoriesDao hd=(HistoriesDao) application.getAttribute(Constants.ContextAttributes.HISTORIES_DAO);
    ArrayList<History> arr=hd.getHistoriesByUserIdSortedByDate(user.getId());
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
</head>
<body>
    <%
        if(sessionUser.getId() == userId) {%>
            <h1>Your full history of quizzes:</h1><%
        } else { %>
            <h1>
<%--                TODO test this--%>
                <a href="visit-user.jsp?userId=<%=user.getId()%>"><%=user.getUsername()%></a>
            </h1><%
        }
    %>
<ul>
<%
    for(History h: arr){
        %>
    <li><%=quizDao.getQuizById(h.getQuizId()).getName()%></li>
    <p>Your Score: <%=h.getScore()%></p>
    <p>Your Time: <%=h.getCompletionTime()%></p>
    <p>Taken On: <%=h.getCompletionDate()%></p>
        <%
    }
%>
</ul>
</body>
</html>
