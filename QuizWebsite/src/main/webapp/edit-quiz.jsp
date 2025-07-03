<%@ page import="org.ja.dao.QuizzesDao" %>
<%@ page import="org.ja.utils.Constants" %>
<%@ page import="org.ja.model.quiz.Quiz" %>
<%@ page import="org.ja.model.quiz.question.Question" %>
<%@ page import="java.util.List" %>
<%@ page import="org.ja.dao.QuestionDao" %>
<%@ page import="org.ja.dao.CategoriesDao" %>
<%@ page import="org.ja.model.CategoriesAndTags.Category" %><%--
  Created by IntelliJ IDEA.
  User: lizamarsagishvili
  Date: 01.07.25
  Time: 21:50
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    QuizzesDao quizzesDao = (QuizzesDao) application.getAttribute(Constants.ContextAttributes.QUIZZES_DAO);
    QuestionDao questionDao = (QuestionDao) application.getAttribute(Constants.ContextAttributes.QUESTIONS_DAO);
    CategoriesDao categoriesDao = (CategoriesDao) application.getAttribute(Constants.ContextAttributes.CATEGORIES_DAO);

//    long quizId = Long.parseLong(request.getParameter(Constants.RequestParameters.QUIZ_ID));
    long quizId = 2;
    Quiz quiz = quizzesDao.getQuizById(quizId);
    String categoryName = categoriesDao.getCategoryById(quiz.getCategoryId()).getCategoryName();
    List<Question> questions = questionDao.getQuizQuestions(quizId);
    List<Category> allCategories = categoriesDao.getAllCategories();
%>
<html>
<head>
    <title>Edit Quiz</title>
    <script src = "js/edit-quiz.js" defer></script>
</head>
<body>
    <h2>Edit Quiz</h2>
    <input type="hidden" id="quizId" value="<%=quizId%>">

    <label for="quizTitle">Edit Quiz Title:</label>
    <h2 contenteditable="true" id="quizTitle"><%=quiz.getName()%></h2>

    <label for="quizDescription">Edit Quiz Description:</label>
    <h2 contenteditable="true" id="quizDescription"><%=quiz.getDescription()%></h2>

    <label for="quizTimeLimit">Edit Quiz time limit:</label>
    <h2 contenteditable="true" id="quizTimeLimit"><%=quiz.getTimeInMinutes()%></h2>

    <label for="quizCategory">Edit Quiz Category:</label>
    <select id="quizCategory">
        <%
            for (Category category : allCategories) {
                boolean selected = category.getCategoryId() == quiz.getCategoryId();
        %>
        <option value="<%=category.getCategoryId()%>" <%= selected ? "selected" : "" %>>
            <%=category.getCategoryName()%>
        </option>
        <%
            }
        %>
    </select>



</body>
</html>
