<%
  String err = (String) request.getAttribute("error");
  if(err != null){ %>
<div style="color:red;"><%=err%></div>
<%  } %>

<!DOCTYPE html>
<html>
<head>
  <title>Sign-Up</title>
</head>

<body>
<h1>Create a New Account</h1>

<form action="signUp" method="post">
  Username: <label>
  <input type="text" name="username" required/>
</label><br /><br />
  Password: <label>
  <input type="password" name="password" required/>
</label>
  <input type="submit" value="Sign Up" />
</form>
<p><a href="index.jsp">Back To Log In</a></p>
</body>
</html>