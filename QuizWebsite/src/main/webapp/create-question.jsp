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

<script>
  const hasQuestionFromSession = <%= session.getAttribute(Constants.SessionAttributes.HAS_QUESTIONS) %>;
</script>

<head>
  <title>Create Question</title>
  <link rel = "stylesheet" href = "css/create-question.css" />
  <script src = "js/create-question.js" defer></script>
</head>

<body>
<h2>Create a Quiz Question</h2>

<form id = "create-question-form" action = "create-question" method = "post">
  <label for = "questionType">Choose question type:</label>
  <select id = "questionType" name = "questionType" onchange = "showQuestionForm()" required>
    <option value = "">Select</option>
    <option value = "<%=Constants.QuestionTypes.RESPONSE_QUESTION%>"><%=Constants.QuestionTypes.RESPONSE_QUESTION%></option>
    <option value = "<%=Constants.QuestionTypes.PICTURE_RESPONSE_QUESTION%>"><%=Constants.QuestionTypes.PICTURE_RESPONSE_QUESTION%></option>
    <option value = "<%=Constants.QuestionTypes.FILL_IN_THE_BLANK_QUESTION%>"><%=Constants.QuestionTypes.FILL_IN_THE_BLANK_QUESTION%></option>
    <option value = "<%=Constants.QuestionTypes.MULTIPLE_CHOICE_QUESTION%>"><%=Constants.QuestionTypes.MULTIPLE_CHOICE_QUESTION%></option>
    <option value = "<%=Constants.QuestionTypes.MULTI_ANSWER_QUESTION%>"><%=Constants.QuestionTypes.MULTI_ANSWER_QUESTION%></option>
    <option value = "<%=Constants.QuestionTypes.MULTI_CHOICE_MULTI_ANSWER_QUESTION%>"><%=Constants.QuestionTypes.MULTI_CHOICE_MULTI_ANSWER_QUESTION%></option>
    <option value = "<%=Constants.QuestionTypes.MATCHING_QUESTION%>"><%=Constants.QuestionTypes.MATCHING_QUESTION%></option>
  </select>

  <input type = "hidden" name = "questionType" id = "questionTypeHidden">

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
      <textarea name = "imageUrl" placeholder = "Enter image link here..." rows = "2" required></textarea>
    </label>

    <label>
      <textarea name = "questionText" placeholder = "Type the question..." rows = "2"></textarea>
    </label>

    <div id = "picture-answer-container">
      <label>Answer 1:</label>
      <label>
        <textarea name = "answer" placeholder = "Type the correct answer..." rows = "2" required></textarea>
      </label>

      <button type = "button" onclick = "addAnswerOption('picture-answer-container')">Add another answer</button>
    </div>
  </div>

  <div id="<%=Constants.QuestionTypes.FILL_IN_THE_BLANK_QUESTION%>-form" class="questionForm">
    <h3><%=Constants.QuestionTypes.FILL_IN_THE_BLANK_QUESTION%></h3>

    <label>Question:</label>
    <input type = "text" id="rawQuestionInput" placeholder = "Type your question here..." oninput="renderQuestionWithBlanks()" required/>

    <div id="interactiveQuestionPreview"></div>

    <input type="hidden" name="questionText" id="questionText" />

    <div id = "fill-in-the-blank-answer-container">
      <label>Answer 1:</label>

      <label>
        <textarea name = "answer" placeholder="Type the correct answer..." rows = "2" required></textarea>
      </label>

      <button type = "button" onclick = "addAnswerOption('fill-in-the-blank-answer-container')">Add another answer</button>
    </div>
  </div>

  <div id="<%=Constants.QuestionTypes.MULTIPLE_CHOICE_QUESTION%>-form" class="questionForm">
    <h3><%=Constants.QuestionTypes.MULTIPLE_CHOICE_QUESTION%></h3>

    <label>
      <textarea name="questionText" placeholder="Type the question..." rows="2" required></textarea>
    </label>

    <div id="multiple-choice-answer-container">
      <div class="option-block">
        <label>Option 1:</label><br>
        <textarea name="answer" placeholder="Type the option..." rows="2" required></textarea>

        <input type="hidden" name="isCorrect" value="false">

        <div class="button-row">
          <button type="button" class="mark-button">Mark as True</button>
          <button type="button" onclick="addAnswerOption('multiple-choice-answer-container')">Add another option</button>
        </div>
      </div>
    </div>
  </div>

  <div id="<%=Constants.QuestionTypes.MULTI_CHOICE_MULTI_ANSWER_QUESTION%>-form" class="questionForm">
    <h3><%=Constants.QuestionTypes.MULTI_CHOICE_MULTI_ANSWER_QUESTION%></h3>

    <label>
      <textarea name="questionText" placeholder="Type the question..." rows="2" required></textarea>
    </label>

    <div id="multi-choice-multi-answer-container">
      <div class="option-block">
        <label>Option 1:</label><br>
        <textarea name="answer" placeholder="Type the option..." rows="2" required></textarea>

        <input type="hidden" name="isCorrect" value="false">

        <div class="button-row">
          <button type="button" class="mark-button">Mark as True</button>
          <button type="button" onclick="addAnswerOption('multi-choice-multi-answer-container')">Add another option</button>
        </div>
      </div>
    </div>
  </div>

  <div id="<%=Constants.QuestionTypes.MULTI_ANSWER_QUESTION%>-form" class="questionForm">
    <h3><%=Constants.QuestionTypes.MULTI_ANSWER_QUESTION%></h3>

    <label>
      <textarea name="questionText" placeholder="Type the question..." rows="2" required></textarea>
    </label>

    <label>
      <input type = "checkbox" name = "isOrdered" id = "isOrderedCheckbox">
      Answers are ordered
    </label>

    <div id="multi-answer-container">

    <button type = "button" onclick = "addAnswerGroup()">Add new answer</button>

    </div>
  </div>

  <div id="<%=Constants.QuestionTypes.MATCHING_QUESTION%>-form" class="questionForm">
    <h3><%=Constants.QuestionTypes.MATCHING_QUESTION%></h3>

    <label>
      <textarea name="questionText" placeholder="Type the question..." rows="2" required></textarea>
    </label>

    <div id="matching-container">
      <div class="matching-columns">
        <div class="left-column">
          <h4>Left Options</h4>
          <div id="left-options"></div>
          <button type="button" onclick="addLeftOption()">Add Left Option</button>
        </div>

        <div class="right-column">
          <h4>Right Options</h4>
          <div id="right-options"></div>
          <button type="button" onclick="addRightOption()">Add Right Option</button>
        </div>
      </div>
    </div>
  </div>

  <div class = "save-question-button">
    <button type = "submit" id = "saveQuestionButton">Save Question</button>
  </div>
</form>

<form id = "finish-quiz-form" action = "finish-quiz" method = "post">
  <button type = "submit" id = "finishQuizButton" disabled>Finish Creating Quiz</button>
</form>

<form id = "discard-form" action = "discard-quiz" method = "post" onsubmit = "return confirmDiscard();">
  <button type = "submit" id = "discardChangesButton">Discard All Changes</button>
</form>

<div id = "discardQuestionContainer" style = "display: none;">
  <form id = "discard-question-form" action = "create-question" method = "get" onsubmit = "return confirmQuestionDiscard();">
    <button type = "submit" id = "discardQuestionButton">Discard Question</button>
  </form>

</div>

</body>

</html>
