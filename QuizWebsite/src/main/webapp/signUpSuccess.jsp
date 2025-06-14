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
</head>
<body>
<h1>You have signed up successfully!</h1>
<p><a href="userPage.jsp">Go To Your Page</a></p>
</body>
</html>
