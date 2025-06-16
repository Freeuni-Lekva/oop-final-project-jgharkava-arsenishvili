<%
    String err = (String) request.getAttribute("error");
    if(err != null){ %>
<div style="color:red;"><%=err%></div>
<%  } %>

<!DOCTYPE html>
<html>
<head>
    <title>Quiz Website</title>
</head>

<body>
<h1>Welcome to the Quiz Website!</h1>
<h3>log in to your account or sign up if you don't have one</h3>

<form action="login" method="post">
    Username: <label>
    <input type="text" name="username" required/>
</label><br /><br />
    Password: <label>
    <input type="password" name="password" required/>
</label>
    <input type="submit" value="Login" />
</form>
<p><a href="sign-up.jsp">Sign Up Here</a></p>

</body>
</html>