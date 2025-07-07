<%
    String err = (String) request.getAttribute("error");
    if(err != null){ %>
        <div class="error-message"><%=err%></div>
<%  } %>

<!DOCTYPE html>
<html>
<head>
    <title>Quiz Website</title>
    <link rel="stylesheet" type="text/css" href="css/index.css">
</head>

<body>
<h1>Welcome to the Quiz Website!</h1>
<h3>log in to your account or sign up if you don't have one</h3>

<div class="login_field">
    <form action="login" method="post">
        <label>
            Username:
            <input type="text" name="username" required/>
        </label>
         <label>
             Password:
             <input type="password" name="password" required/>
         </label>
        <input type="submit" value="Login" />
    </form>
    <p class="sign-up-link"><a href="sign-up.jsp">Sign Up Here</a></p>
</div>
</body>
</html>
