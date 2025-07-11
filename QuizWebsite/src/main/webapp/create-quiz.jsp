<%@ page import="org.ja.utils.Constants" %>
<%@ page import="org.ja.model.data.Category" %>
<%@ page import="org.ja.dao.CategoriesDao" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="org.ja.dao.TagsDao" %>
<%@ page import="org.ja.model.data.Tag" %>
<%@ page import="java.util.List" %><%--
  Created by IntelliJ IDEA.
  User: lizamarsagishvili
  Date: 18.06.25
  Time: 22:37
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    CategoriesDao categoriesDao = (CategoriesDao) application.getAttribute(Constants.ContextAttributes.CATEGORIES_DAO);
    List<Category> categories = categoriesDao.getAllCategories();

    TagsDao tagsDao = (TagsDao) application.getAttribute(Constants.ContextAttributes.TAGS_DAO);
    List<Tag> tags = tagsDao.getAllTags();
%>


<html>
<head>
    <title>Create Quiz</title>
    <link rel = "stylesheet" href = "css/create-quiz.css" />
    <script src = "js/create-quiz.js" defer></script>
</head>

<%
    if (session.getAttribute(Constants.SessionAttributes.USER) == null) {
%>

<body>
    <div class = "access-denied">
        <h1>Access Denied</h1>
        <p>You must <a href = "index.jsp" style = "color: cornflowerblue;">log in</a> to create a quiz</p>
    </div>
</body>

<%
        } else {
%>

<body>
    <div class = "container">
        <h2>Create a Quiz</h2>
        <form action = "create-quiz" method = "post" id="quizForm">

            <label for = "quizTitle">Quiz Title</label>
            <input type = "text" name = "quizTitle" id = "quizTitle" required placeholder = "Enter quiz Title">

            <label for = "quizDescription">Quiz Description</label>
            <textarea name = "quizDescription" id = "quizDescription" rows = "4" required placeholder = "Enter quiz description"></textarea>

            <div class = "select-row">
                <div class = "select-group">
                    <label for = "category">Select Category</label>
                    <select name = "category" id = "category" class = "scrollable-select" size = "1">
                        <% for (Category category: categories){ %>
                            <option value = "<%= category.getCategoryId()%>"><%= category.getCategoryName()%></option>
                        <% } %>
                    </select>

                    <label>Select Tags</label>

                    <div class = "checkbox-scroll-pane">
                        <div class="checkbox-wrapper other-wrapper">
                            <input type="checkbox" id="tag_other" name="tag_editor" value="" onclick="toggleOtherInput(this)">
                            <label class="checkbox-button" for="tag_other">Other</label>
                            <input type="text" id="otherTagInput" name="otherTag" placeholder="Enter your tag" oninput="updateOtherValue()">
                        </div>

                        <% for (Tag tag: tags){ %>
                            <div class = "checkbox-label">
                                <input type = "checkbox" id = "tag_<%= tag.getTagId()%>" name = "tags" value = "<%= tag.getTagId()%>">
                                <label class = "checkbox-button" for = "tag_<%= tag.getTagId()%>"><%= tag.getTagName()%></label>
                            </div>
                        <% } %>
                    </div>

                </div>
            </div>

            <div class = "select-group">
                <label for = "quizDuration">Time limit</label>
                <input type = "text" name = "quizDuration" id = "quizDuration" placeholder = "10" required>
            </div>

            <label>Question Order:</label>
            <div class = "radio-group">
                <label class = "radio-option"><input type = "radio" name = "orderType" value = "ordered" required checked><span>Ordered</span></label>
                <label class = "radio-option"><input type = "radio" name = "orderType" value = "randomized"><span>Random</span></label>
            </div>

            <label>Questions Placement:</label>
            <div class = "radio-group">
                <label class = "radio-option"><input type = "radio" name = "placementType" value = "one-page" required checked>One Page</label>
                <label class = "radio-option"><input type = "radio" name = "placementType" value = "multiple-page">Multiple Page</label>
            </div>

            <label>Question Correction:</label>
            <div class = "radio-group">
                <label class = "radio-option"><input type = "radio" name = "correctionType" value = "final-correction" required checked>Final Feedback</label>
                <label class = "radio-option"><input type = "radio" name = "correctionType" value = "immediate-correction" disabled>Immediate Correction</label>
            </div>

            <button type = "submit" name = "action" value = "createQuiz" class = "btn">
                Add questions
            </button>

        </form>

        <form id = "discard-form" action = "discard-quiz" method = "post" onsubmit = "return confirmDiscard();">
            <button type = "submit" id = "discardChangesButton">Discard All Changes</button>
        </form>
    </div>
</body>

<%
    }
%>
</html>

