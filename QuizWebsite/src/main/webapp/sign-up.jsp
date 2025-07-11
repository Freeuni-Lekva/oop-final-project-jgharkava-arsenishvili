<%@ page import="org.ja.utils.Constants" %><%
  if(session.getAttribute(Constants.SessionAttributes.USER) != null) {
    response.sendRedirect("/user-page.jsp");
    return;
  }

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
  <form action="signUp" method="post" id="registration-form">
    <label>
      Username:
      <input type="text" name="username" autocomplete="off" id="username-field" required/>
    </label>
    <label>
      Password:
      <input type="password" name="password" id="password-field" autocomplete="off" required/>
    </label>
    <label>
      Photo URL:
      <input type="text" name="photo" id="photo-field" autocomplete="off" required/>
    </label>
    <input type="submit" value="Sign Up" />
  </form>
  <p class="back-link"><a href="index.jsp">Back To Log In</a></p>
</div>

<script>
  document.getElementById("registration-form").addEventListener("submit", function(e) {
    const username = document.getElementById("username-field");
    const password = document.getElementById("password-field");
    const image = document.getElementById("photo-field");

    if(username.value.trim() === "") {
      e.preventDefault();

      username.setCustomValidity("Please fill out this field.");
      username.reportValidity();

      setTimeout(() => username.setCustomValidity(""), 2000);
      return;
    }

    if(username.value.trim().length >= 60) {
      e.preventDefault();

      username.setCustomValidity("This field is too long.");
      username.reportValidity();

      setTimeout(() => username.setCustomValidity(""), 2000);
      return;
    }

    if(password.value.trim() === "") {
      e.preventDefault();

      password.setCustomValidity("Please fill out this field.");
      password.reportValidity();

      setTimeout(() => password.setCustomValidity(""), 2000);
      return;
    }

    if(password.value.trim().length >= 200) {
      e.preventDefault();

      password.setCustomValidity("This field is too long.");
      password.reportValidity();

      setTimeout(() => password.setCustomValidity(""), 2000);
      return;
    }

    if(image.value.trim() === "") {
      e.preventDefault();

      image.setCustomValidity("Please fill out this field.");
      image.reportValidity();

      setTimeout(() => image.setCustomValidity(""), 2000);
      return;
    }

    if(image.value.trim().length >= 2040) {
      e.preventDefault();

      image.setCustomValidity("This field is too long.");
      image.reportValidity();

      setTimeout(() => image.setCustomValidity(""), 2000);
    }
  });

</script>
</body>
</html>
