<%@ page import="org.ja.utils.Constants" %>
<%@ page import="org.ja.dao.CategoriesDao" %>
<%@ page import="org.ja.dao.TagsDao" %>
<%@ page import="org.ja.model.CategoriesAndTags.Category" %>
<%@ page import="java.util.List" %>
<%@ page import="org.ja.model.CategoriesAndTags.Tag" %>
<%@ page import="org.ja.model.quiz.Quiz" %>
<%@ page import="org.ja.dao.QuizzesDao" %><%--
  Created by IntelliJ IDEA.
  User: tober
  Date: 6/19/2025
  Time: 12:39 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    CategoriesDao categoriesDao = (CategoriesDao) application.getAttribute(Constants.ContextAttributes.CATEGORIES_DAO);
    TagsDao tagsDao = (TagsDao) application.getAttribute(Constants.ContextAttributes.TAGS_DAO);

    List<Category> categories = categoriesDao.getAllCategories(Constants.FETCH_LIMIT);
    List<Tag> tags = tagsDao.getAllTags(Constants.FETCH_LIMIT);
%>

<html>
<head>
    <title>Quiz Search</title>
    <link rel="stylesheet" type="text/css" href="css/quiz-search.css">
    <link rel="stylesheet" type="text/css" href="css/hotlink.css">
</head>
<body>

<form action="quiz-search" method="get">
    <!-- Quiz name input -->
    <div class="quiz-name-section">
        <label for="quiz-name">Quiz name:</label>
        <input type="text" id="quiz-name" name="<%=Constants.FilterFields.QUIZ_NAME%>" placeholder="Enter quiz name...">
    </div>

    <!-- Categories section -->
    <div class="categories-section">
        <h3>Select categories:</h3>
        <div class="scrollable-container">
            <div class="checkbox-group">
                <%
                    for(Category category : categories) {%>
                <div class="checkbox-item">
                    <input type="checkbox" id="category<%=category.getCategoryId()%>" name="<%=Constants.FilterFields.CATEGORY%>" value="<%=category.getCategoryName()%>">
                    <label for="category<%=category.getCategoryId()%>"><%=category.getCategoryName()%></label>
                </div>
                <%
                    }
                %>
            </div>
        </div>
    </div>

    <!-- Tags section -->
    <div class="tags-section">
        <h3>Select tags:</h3>
        <div class="scrollable-container">
            <div class="checkbox-group">
                <%
                    for(Tag tag : tags) {%>
                <div class="checkbox-item">
                    <input type="checkbox" id="tag<%=tag.getTagId()%>" name="<%=Constants.FilterFields.TAG%>" value="<%=tag.getTagName()%>">
                    <label for="tag<%=tag.getTagId()%>"><%=tag.getTagName()%></label>
                </div>
                <%
                    }
                %>
            </div>
        </div>
    </div>

    <!-- Order section -->
    <div class="order-section">
        <label for="order-select">Sort by:</label>
        <select id="order-select" name="<%=Constants.FilterFields.ORDER%>" >
            <option value="<%=Constants.FilterFields.ORDER_PLACEHOLDER%>">Select sorting option</option>
            <option value="average_rating">Rating</option>
            <option value="creation_date">Creation Date</option>
            <option value="participant_count">Participant Count</option>
        </select>
    </div>

    <!-- Search button -->
    <div class="search-button">
        <input type="submit" value="Search">
    </div>
</form>

<!-- Quiz results -->
<div class="quiz-results">
    <h2>Quizzes:</h2>
    <%
        if(request.getAttribute("quizzes") == null) {
            List<Quiz> quizzes = ((QuizzesDao) application.getAttribute(Constants.ContextAttributes.QUIZZES_DAO)).getQuizzesSortedByCreationDate(Constants.FETCH_LIMIT);

            for(Quiz quiz : quizzes) {%>
    <a class="hotlink" href="quiz-overview.jsp?<%=Constants.RequestParameters.QUIZ_ID%>=<%=quiz.getId()%>"><%=quiz.getName()%></a>
    <%
        }
    } else {
        List<Quiz> quizzes = (List<Quiz>) request.getAttribute("quizzes");

        for(Quiz quiz : quizzes) {%>
    <a class="hotlink" href="quiz-overview.jsp?<%=Constants.RequestParameters.QUIZ_ID%>=<%=quiz.getId()%>"><%=quiz.getName()%></a>
    <%
            }
        }
    %>
</div>
</body>
</html>