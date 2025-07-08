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

    List<Category> categories = categoriesDao.getAllCategories();
    List<Tag> tags = tagsDao.getAllTags();
%>

<html>
<head>
    <title>Quiz Search</title>
</head>
<body>

<form action="quiz-search" method="get">
    <label>
        <input type="text" name="<%=Constants.FilterFields.QUIZ_NAME%>">
    </label>

    <%
        for(Category category : categories) {%>
    <input type="checkbox" id="category<%=category.getCategoryId()%>" name="<%=Constants.FilterFields.CATEGORY%>" value="<%=category.getCategoryName()%>">
    <label for="category<%=category.getCategoryId()%>"><%=category.getCategoryName()%></label>
    <%
        }
    %>

    <%
        for(Tag tag : tags) {%>
    <input type="checkbox" id="tag<%=tag.getTagId()%>" name="<%=Constants.FilterFields.TAG%>" value="<%=tag.getTagName()%>">
    <label for="tag<%=tag.getTagId()%>"><%=tag.getTagName()%></label>
    <%
        }
    %>

    <label>
        <select name="<%=Constants.FilterFields.ORDER%>">
            <option value="<%=Constants.FilterFields.ORDER_PLACEHOLDER%>">Select</option>
            <option value="average_rating">Rating</option>
            <option value="creation_date">Creation Date</option>
            <option value="participant_count">Participant Count</option>
        </select>
    </label>

    <input type="submit" value="search">
</form>

<%

    if(request.getAttribute("quizzes") == null) {
        List<Quiz> quizzes = ((QuizzesDao) application.getAttribute(Constants.ContextAttributes.QUIZZES_DAO)).getQuizzesSortedByCreationDate();

        for(Quiz quiz : quizzes) {%>
<form action="quiz-overview.jsp" method="get">
    <input type="hidden" name="<%=Constants.RequestParameters.QUIZ_ID%>" value="<%=quiz.getId()%>">
    <button type="submit" style="background:none; border:none; color:blue; text-decoration:underline; cursor:pointer;"><%=quiz.getName()%></button>
</form>
<%
    }
} else {
    List<Quiz> quizzes = (List<Quiz>) request.getAttribute("quizzes");


//    TODO may be better with <a href="">

    for(Quiz quiz : quizzes) {%>
<form action="quiz-overview.jsp" method="get">
    <input type="hidden" name="<%=Constants.RequestParameters.QUIZ_ID%>" value="<%=quiz.getId()%>">
    <button type="submit" style="background:none; border:none; color:blue; text-decoration:underline; cursor:pointer;"><%=quiz.getName()%></button>
</form>
<%
        }
    }
%>

</body>
</html>
