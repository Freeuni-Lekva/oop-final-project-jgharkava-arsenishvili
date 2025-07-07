<%--
  Created by IntelliJ IDEA.
  User: user
  Date: 6/12/2025
  Time: 10:53 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  String username = (String) request.getAttribute("username");
%>
<html>
<head>
  <title>Welcome <%=username%>!</title>
  <link rel="stylesheet" type="text/css" href="css/sign-up-success.css">
</head>
<body>
<div class="success-container">
  <h1>You have signed up successfully!</h1>
  <p class="welcome-message">Welcome to the Quiz Website</p>
  <a href="user-page.jsp" class="success-link">go to your page</a>
</div>
</body>
</html>
