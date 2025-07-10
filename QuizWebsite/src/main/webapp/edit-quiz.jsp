<%@ page import="org.ja.utils.Constants" %>
<%@ page import="org.ja.model.quiz.Quiz" %>
<%@ page import="org.ja.model.quiz.question.Question" %>
<%@ page import="java.util.List" %>
<%@ page import="org.ja.model.CategoriesAndTags.Category" %>
<%@ page import="org.ja.model.CategoriesAndTags.Tag" %>
<%@ page import="org.ja.dao.*" %>
<%@ page import="java.util.ArrayList" %><%--
  Created by IntelliJ IDEA.
  User: lizamarsagishvili
  Date: 01.07.25
  Time: 21:50
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    QuizzesDao quizzesDao = (QuizzesDao) application.getAttribute(Constants.ContextAttributes.QUIZZES_DAO);
    CategoriesDao categoriesDao = (CategoriesDao) application.getAttribute(Constants.ContextAttributes.CATEGORIES_DAO);
    TagsDao tagsDao = (TagsDao) application.getAttribute(Constants.ContextAttributes.TAGS_DAO);
    QuizTagsDao quizTagsDao = (QuizTagsDao) application.getAttribute(Constants.ContextAttributes.QUIZ_TAG_DAO);

    long quizId = Long.parseLong(request.getParameter(Constants.RequestParameters.QUIZ_ID));
    Quiz quiz = quizzesDao.getQuizById(quizId);
    List<Category> allCategories = categoriesDao.getAllCategories();
    List<Tag> allTags = tagsDao.getAllTags();
    List<Long> quizTags = quizTagsDao.getTagsByQuizId(quizId);
    String questionOrder = quiz.getQuestionOrder();
    String questionPlacement = quiz.getQuestionPlacement();
    String questionCorrection = quiz.getQuestionCorrection();
%>
<html>
<head>
    <title>Edit Quiz</title>
    <script src = "js/edit-quiz.js" defer></script>
</head>
<body>
    <h2>Edit Quiz</h2>

<%--    Title--%>
    <label for="quizTitle">Edit Quiz Title:</label>
    <h3 contenteditable="true" id="quizTitle"><%=quiz.getName()%></h3>

<%--    Description--%>
    <label for="quizDescription">Edit Quiz Description:</label>
    <h3 contenteditable="true" id="quizDescription"><%=quiz.getDescription()%></h3>

<%--    Time limit--%>
    <label for="quizTimeLimit">Edit Quiz time limit:</label>
    <h3 contenteditable="true" id="quizTimeLimit"><%=quiz.getTimeInMinutes()%></h3>

<%--    Category--%>
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

<%--    current tags--%>
    <div>
        <h3>Remove Tags for this quiz:</h3>
        <div id="quizTags">
            <% for (Long tagId : quizTags) { %>
            <span class="tag" data-id="<%=tagId%>">
                <%=tagsDao.getTagById(tagId).getTagName()%>
                <button class="removeTagBtn" data-id="<%=tagId%>">&times;</button>
            </span>
            <% } %>
        </div>
    </div>

<%--    available tags--%>
    <div>
        <h3>Add Tags for this quiz:</h3>
        <div id="availableTags">
            <% for (Tag tag: allTags){
                Long tagId = tag.getTagId();
                if (!quizTags.contains(tagId)){
            %>

            <span class="tag" data-id="<%=tagId%>">
                <%=tag.getTagName()%>
                <button class = "addTagBtn" data-id="<%=tagId%>">+</button>
            </span>

            <%
                    }
            }
            %>

        </div>
    </div>

    <div id="orderStatusContainer">
        <label for="orderStatus">Question Order:</label>
        <select id="orderStatus" name="orderStatus" data-quiz-id="<%=quizId%>">
            <option value="<%=Constants.QuizQuestionOrderTypes.QUESTIONS_ORDERED%>"
                    <%=questionOrder.equals(Constants.QuizQuestionOrderTypes.QUESTIONS_ORDERED) ? "selected" : "" %>>
                <%=Constants.QuizQuestionOrderTypes.QUESTIONS_ORDERED%>
            </option>
            <option value="<%=Constants.QuizQuestionOrderTypes.QUESTIONS_UNORDERED%>"
                    <%=questionOrder.equals(Constants.QuizQuestionOrderTypes.QUESTIONS_UNORDERED) ? "selected" : "" %>>
                <%=Constants.QuizQuestionOrderTypes.QUESTIONS_UNORDERED%>
            </option>
        </select>
    </div>

    <div id="placementStatusContainer">
        <label for="placementStatus">Question Placement:</label>
        <select id="placementStatus" name="placementStatus" data-quiz-id="<%=quizId%>">
            <option value="<%=Constants.QuizQuestionPlacementTypes.ONE_PAGE%>" <%=questionPlacement.equals(Constants.QuizQuestionPlacementTypes.ONE_PAGE) ? "selected" : "" %>>
                <%=Constants.QuizQuestionPlacementTypes.ONE_PAGE%>
            </option>
            <option value="<%=Constants.QuizQuestionPlacementTypes.MULTIPLE_PAGE%>" <%=questionPlacement.equals(Constants.QuizQuestionPlacementTypes.MULTIPLE_PAGE) ? "selected" : "" %>>
                <%=Constants.QuizQuestionPlacementTypes.MULTIPLE_PAGE%>
            </option>
        </select>
    </div>

    <div id="correctionStatusContainer">
        <label for="correctionStatus">Question Correction:</label>
        <select id="correctionStatus" name="correctionStatus" data-quiz-id="<%=quizId%>">
            <option value="<%=Constants.QuizQuestionCorrectionTypes.FINAL_CORRECTION%>" <%=questionCorrection.equals(Constants.QuizQuestionCorrectionTypes.FINAL_CORRECTION) ? "selected" : "" %>>
                <%=Constants.QuizQuestionCorrectionTypes.FINAL_CORRECTION%>
            </option>
            <option value="<%=Constants.QuizQuestionCorrectionTypes.IMMEDIATE_CORRECTION%>" <%=questionCorrection.equals(Constants.QuizQuestionCorrectionTypes.IMMEDIATE_CORRECTION) ? "selected" : "" %>>
                <%=Constants.QuizQuestionCorrectionTypes.IMMEDIATE_CORRECTION%>
            </option>
        </select>
    </div>

    <form id = "edit-question-form" action = "edit-question" method = "get">
        <input type="hidden" name="<%=Constants.RequestParameters.QUIZ_ID%>" id="quizId" value="<%=quizId%>">
        <button type="submit" id="edit-question-btn">Edit Questions</button>
    </form>

    <form action="quiz-overview.jsp" method="get">
        <input type="hidden" name="<%=Constants.RequestParameters.QUIZ_ID%>" value="<%=quizId%>">
        <button type="submit">Finish Editing Quiz</button>
    </form>

</body>
</html>
