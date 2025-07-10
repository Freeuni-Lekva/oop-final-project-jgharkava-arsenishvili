<%
  String err = (String) request.getAttribute("error");
  if(err != null){ %>
<div class="error-message"><%=err%></div>
<%  } %>

<!DOCTYPE html>
<html>
<head>
  <title>Sign-Up</title>
  <link rel="stylesheet" type="text/css" href="css/sign-up.css">
</head>

<body>
<h1>Create a New Account</h1>

<div class="signup_field">
  <form action="signUp" method="post">
    <label>
      Username:
      <input type="text" name="username" required/>
    </label>
    <label>
      Password:
      <input type="password" name="password" required/>
    </label>
    <label>
      Photo URL:
      <input type="text" name="photo" required/>
    </label>
    <input type="submit" value="Sign Up" />
  </form>
  <p class="back-link"><a href="index.jsp">Back To Log In</a></p>
</div>

</body>
</html>
