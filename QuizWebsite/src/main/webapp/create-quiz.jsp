<%@ page import="org.ja.utils.Constants" %>
<%@ page import="org.ja.model.CategoriesAndTags.Category" %>
<%@ page import="org.ja.dao.CategoriesDao" %>
<%@ page import="java.util.ArrayList" %><%--
  Created by IntelliJ IDEA.
  User: lizamarsagishvili
  Date: 18.06.25
  Time: 22:37
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Create a new Quiz</title>
    <link rel = "stylesheet" href = "css/quizCreate.css" />
</head>

<%
    if (session.getAttribute(Constants.SessionAttributes.USER) == null) {
%>

<body style = "text-align : center; padding-top: 100px">
    <h1>Access Denied</h1>
    <p>You must <a href = "sign-up.jsp" style = "color: cornflowerblue;">log in</a> to create a quiz</p>
</body>

<%
    } else {
        // clear previous session data
        session.setAttribute(Constants.SessionAttributes.QUIZ, null);
%>

<body>
    <form action = "CreateQuizServlet" method = "post">
        <h2>Create a new Quiz</h2>
        <br>
        <label>Quiz Title</label>
            <input type = "text" name = "quizTitle" required placeholder="Enter quiz title">
        <br>

        <label>Quiz Description</label>
        <textarea name = "quizDescription" placeholder = "Enter a description ..." rows = "4"></textarea>
        <br>

        <label>Quiz Category:</label>
        <div class = "scrollable-box">
            <ul>
                <%
                    CategoriesDao categoriesDao = (CategoriesDao) request.getAttribute(Constants.ContextAttributes.CATEGORIES_DAO);
                    ArrayList<Category> categories = categoriesDao.getAllCategories();

                    if (categories != null){
                        for (Category category: categories){
                %>
                    <li><
                        <input type = "radio"
                               id = "category_<%=category.getCategoryId()%>"
                               name = "selectedCategory"
                               value = "<%=category.getCategoryId()%>">
                        <label for = "category_<%=category.getCategoryId()%>"><%=category.getCategoryName()%></label>
                    </li>
                <%
                        }
                    } else {
                %>
                    <li>No Categories Available</li>
                <%
                    }
                %>
            </ul>
        </div>
        <br>

        <label>Display Mode:</label>
        <input type = "radio" name = "displayMode" value = "single" checked> Single Page
        <input type = "radio" name = "displayMode" value = "multiple" > Multiple Page
        <br>

        <label>Question Order:</label>
        <input type = "radio" name = "order" value = "ordered" checked> Ordered
        <input type = "radio" name = "order" value = "random"> Randomized
        <br>

        <input type = "checkbox" name = "feedback"> Enable Immediate Feedback
        <br>
        <input type = "checkbox" name = "practiceMode"> Enable Practice Mode
        <br>

        <button type = "submit"> Continue to add Questions</button>

    </form>
</body>


<%
    }
%>
</html>
