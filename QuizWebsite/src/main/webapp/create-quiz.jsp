<%@ page import="org.ja.utils.Constants" %>
<%@ page import="org.ja.model.CategoriesAndTags.Category" %>
<%@ page import="org.ja.dao.CategoriesDao" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="org.ja.dao.TagsDao" %>
<%@ page import="org.ja.model.CategoriesAndTags.Tag" %><%--
  Created by IntelliJ IDEA.
  User: lizamarsagishvili
  Date: 18.06.25
  Time: 22:37
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    CategoriesDao categoriesDao = (CategoriesDao) application.getAttribute(Constants.ContextAttributes.CATEGORIES_DAO);
    ArrayList<Category> categories = categoriesDao.getAllCategories();

    TagsDao tagsDao = (TagsDao) application.getAttribute(Constants.ContextAttributes.TAGS_DAO);
    ArrayList<Tag> tags = tagsDao.getAllTags();
%>


<html>
<head>
    <title>Create Quiz</title>
    <link rel = "stylesheet" href = "css/quizCreate.css" />
</head>

<%
    //if (session.getAttribute(Constants.SessionAttributes.USER) == null) {
%>

<%--<body>--%>
<%--    <div class = "access-denied">--%>
<%--        <h1>Access Denied</h1>--%>
<%--        <p>You must <a href = "index.jsp" style = "color: cornflowerblue;">log in</a> to create a quiz</p>--%>
<%--    </div>--%>
<%--</body>--%>

<%
    //} else {
        // clear previous session data
        session.setAttribute(Constants.SessionAttributes.QUIZ, null);
%>

<body>
    <div class = "container">
        <h2>Create a Quiz</h2>
        <form action = "CreateQuizServlet" method = "post">

            <label for = "quizTitle">Quiz Title</label>
            <input type = "text" name = "quizTitle" id = "quizTitle" required placeholder = "Enter quiz Title">

            <label for = "quizDescription">Quiz Description</label>
            <textarea name = "quizDescription" id = "quizDescription" rows = "4" required placeholder = "Enter quiz description"></textarea>

            <div class = "select-row">
                <div class = "select-group">
                    <label for = "category">Select Category</label>
                    <select name = "category" id = "category" class = "scrollable-select" size = 1>
                        <% for (Category category: categories){ %>
                        <option value = "<%= category.getCategoryId()%>"><%= category.getCategoryName()%></option>
                        <% } %>
                    </select>
                </div>


                <div class = "select-group">
                    <label for = "tag">Select Tag</label>
                    <select name = "tag" id = "tag" class = "scrollable-select" size = 1>
                        <% for (Tag tag: tags){ %>
                        <option value = "<%= tag.getTagId()%>"><%= tag.getTagName()%></option>
                        <% } %>
                    </select>
                </div>
            </div>

            <label for = "quizDuration">Time limit</label>
            <input type = "text" name = "quizDuration" id = "quizDuration" placeholder = "10">

            <label>Question Order:</label>
            <div class = "radio-group">
                <label class = "radio-option"><input type = "radio" name = "orderType" value = "ordered" required checked><span>Ordered</span></label>
                <label class = "radio-option"><input type = "radio" name = "orderType" value = "random"><span>Random</span></label>
            </div>

            <label>Questions Placement:</label>
            <div class = "radio-group">
                <label class = "radio-option"><input type = "radio" name = "placementType" value = "onePage" required checked>One Page</label>
                <label class = "radio-option"><input type = "radio" name = "placementType" value = "multiplePage">Multiple Page</label>
            </div>

            <label>Question Correction:</label>
            <div class = "radio-group">
                <label class = "radio-option"><input type = "radio" name = "correctionType" value = "finalFeedback" required checked>Final Feedback</label>
                <label class = "radio-option"><input type = "radio" name = "correctionType" value = "immediateCorrection">Immediate Correction</label>
            </div>


            <button type = "submit" name = "action" value = "createQuiz" class = "btn">
                Add questions
            </button>

        </form>
    </div>
</body>

<%
    //}
%>
</html>
