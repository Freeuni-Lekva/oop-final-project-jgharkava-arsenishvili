<%@ page import="org.ja.utils.Constants" %><%--
  Created by IntelliJ IDEA.
  User: lizamarsagishvili
  Date: 20.06.25
  Time: 17:48
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>

<head>
  <title>Create Question</title>
  <link rel = "stylesheet" href = "css/create-question.css" />
  <script src = "js/create-question.js" defer></script>
</head>

<body>
<h2>Create a Quiz Question</h2>

<form action = "CreateQuestionServlet" method = "post">
  <label for = "questionType">Choose question type:</label>
  <select id = "questionType" name = "questionType" onchange = "showQuestionForm()">
    <option value = "">Select</option>
    <option value = "<%=Constants.QuestionTypes.RESPONSE_QUESTION%>"><%=Constants.QuestionTypes.RESPONSE_QUESTION%></option>
    <option value = "<%=Constants.QuestionTypes.PICTURE_RESPONSE_QUESTION%>"><%=Constants.QuestionTypes.PICTURE_RESPONSE_QUESTION%></option>
    <option value = "<%=Constants.QuestionTypes.MULTIPLE_CHOICE_QUESTION%>"><%=Constants.QuestionTypes.MULTIPLE_CHOICE_QUESTION%></option>
    <option value = "<%=Constants.QuestionTypes.MULTI_ANSWER_QUESTION%>"><%=Constants.QuestionTypes.MULTI_ANSWER_QUESTION%></option>
    <option value = "<%=Constants.QuestionTypes.MULTI_CHOICE_MULTI_ANSWER_QUESTION%>"><%=Constants.QuestionTypes.MULTI_CHOICE_MULTI_ANSWER_QUESTION%></option>
    <option value = "<%=Constants.QuestionTypes.MATCHING_QUESTION%>"><%=Constants.QuestionTypes.MATCHING_QUESTION%></option>
  </select>

  <div id = "<%=Constants.QuestionTypes.RESPONSE_QUESTION%>-form" class = "questionForm">
    <h3><%=Constants.QuestionTypes.RESPONSE_QUESTION%></h3>

    <label>
      <textarea name = "questionText" placeholder = "Type the question..." rows = "2" required></textarea>
    </label>

    <div id = "response-answer-container">
      <label>Answer 1:</label>
      <label>
        <textarea name = "answer" placeholder = "Type the correct answer..." rows = "2" required></textarea>
      </label>

      <button type = "button" onclick = "addAnswerOption('response-answer-container')">Add another answer</button>

    </div>
  </div>

  <div id = "<%=Constants.QuestionTypes.PICTURE_RESPONSE_QUESTION%>-form" class = "questionForm">
    <h3><%=Constants.QuestionTypes.PICTURE_RESPONSE_QUESTION%></h3>

    <label>
      <textarea name = "questionText" placeholder = "Enter image link here..." rows = "2" required></textarea>
    </label>

    <div id = "picture-answer-container">
      <label>Answer 1:</label>
      <label>
        <textarea name = "answer" placeholder = "Type the correct answer..." rows = "2" required></textarea>
      </label>

      <button type = "button" onclick = "addAnswerOption('picture-answer-container')">Add another answer</button>

    </div>
  </div>
    <br>

  <div class = "save-question-button">
    <button type = "submit">Save Question</button>
  </div>

  <div class = "finish-quiz-creation">
    <button type = "submit">Finish Quiz creation</button>
  </div>

  <div class = "discard-changes">
    <button type = "submit">Discard Changes</button>
  </div>
</form>

</body>

</html>
