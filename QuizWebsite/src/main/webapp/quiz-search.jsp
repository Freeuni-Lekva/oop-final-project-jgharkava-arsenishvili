<%@ page import="org.ja.utils.Constants" %>
<%@ page import="org.ja.dao.CategoriesDao" %>
<%@ page import="org.ja.dao.TagsDao" %>
<%@ page import="org.ja.model.CategoriesAndTags.Category" %>
<%@ page import="java.util.List" %>
<%@ page import="org.ja.model.CategoriesAndTags.Tag" %>
<%@ page import="java.util.concurrent.CompletionService" %>
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

    <form action="search-quiz" method="get">
      <input type="text" name="<%=Constants.FilterFields.QUIZ_NAME%>">

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
        <select>
          <option name="<%=Constants.FilterFields.ORDER%>" value="average_rating">Rating</option>
          <option name="<%=Constants.FilterFields.ORDER%>" value="creation_date">Creation Date</option>
        </select>
      </label>

      <input type="submit" value="search">
    </form>

    <%
      List<Quiz> quizzes = ((QuizzesDao) application.getAttribute(Constants.ContextAttributes.QUIZZES_DAO)).getQuizzesSortedByCreationDate();

      for(Quiz quiz : quizzes) {%>
        <h5>Name - <%=quiz.getName()%> Rating - <%=quiz.getAvgRating()%></h5>
    <%

      }
    %>

  </body>
</html>
