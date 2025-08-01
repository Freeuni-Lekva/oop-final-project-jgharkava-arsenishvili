<%@ page import="org.ja.utils.Constants" %>
<%@ page import="org.ja.model.quiz.question.Question" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="org.ja.dao.AnswersDao" %>
<%@ page import="org.ja.model.data.Answer" %>
<%@ page import="org.ja.model.data.Match" %>
<%@ page import="org.ja.dao.MatchesDao" %>
<%@ page import="org.ja.model.quiz.Quiz" %>
<%@ page import="java.util.List" %>
<%@ page import="java.time.Instant" %><%--
  Created by IntelliJ IDEA.
  User: tober
  Date: 6/27/2025
  Time: 4:49 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    Integer index = (Integer) session.getAttribute(Constants.SessionAttributes.CURRENT_QUESTION_INDEX);
    Question question = ((ArrayList<Question>) session.getAttribute(Constants.SessionAttributes.QUESTIONS)).get(index);
    String type = question.getQuestionType();
    AnswersDao answersDao = (AnswersDao) application.getAttribute(Constants.ContextAttributes.ANSWERS_DAO);
    MatchesDao matchesDao = (MatchesDao) application.getAttribute(Constants.ContextAttributes.MATCHES_DAO);
    Quiz quiz=(Quiz)session.getAttribute(Constants.SessionAttributes.QUIZ);
%>

<html>
<head>
    <title>Quiz Questions</title>
    <link rel="stylesheet" href="css/single-question.css">
</head>
<body>


<%
    long startTime;
    int duration;
    long now;
    long timeLeft;

    if(Constants.QuizMode.TAKING == (Constants.QuizMode) session.getAttribute(Constants.SessionAttributes.QUIZ_MODE)) {
        startTime = (Long) session.getAttribute("start-time");
        duration = (Integer) session.getAttribute("time-limit-in-seconds");
        now = Instant.now().toEpochMilli();
        timeLeft = duration - (now - startTime) / 1000;
%>

    <script>
        let timeLeft = <%= timeLeft %>;

        function formatTime(secs) {
            const m = Math.floor(secs / 60);
            const s = secs % 60;
            return m + ":" + (s < 10 ? "0" + s : s);
        }

        function updateTimer() {
            if (timeLeft <= 0) {
                document.getElementById("question-form").submit();
            } else {
                document.getElementById("timer").textContent = formatTime(timeLeft);
                timeLeft--;
                setTimeout(updateTimer, 1000);
            }
        }

        window.onload = updateTimer;
    </script>

    <div>Time left: <span id="timer"></span></div>

    <h2>Question <%=index+1%></h2>
<%
    }
%>
<div class="question-container">
    <form id="question-form" action="grade-single-question" method="post">
        <%
            ///  RESPONSE QUESTION
            if (type.equals(Constants.QuestionTypes.RESPONSE_QUESTION)) {%>
        <h3><%=question.getQuestionText()%></h3>
        <input type="text" name="response_1_1"><br><br><%

    }

    /// FILL_IN_THE_BLACK QUESTION
    else if (type.equals(Constants.QuestionTypes.FILL_IN_THE_BLANK_QUESTION)) {%>
        <h3><%=question.getQuestionText().replace("_", "_____")%></h3>
        <input type="text" name="response_1_1"><br><br><%
    }

    /// PICTURE_RESPONSE_QUESTION
    else if (type.equals(Constants.QuestionTypes.PICTURE_RESPONSE_QUESTION)) {
        if (!(question.getQuestionText() == null)  && !question.getQuestionText().trim().isEmpty()) {%>
        <h3><%=question.getQuestionText()%></h3><%
        }%>

        <img src="<%=question.getImageUrl()%>" width="300" height="200"><br>
        <input type="text" name="response_1_1"><br><br><%

    }

        /// MULTI CHOICE QUESTION
        else if (type.equals(Constants.QuestionTypes.MULTIPLE_CHOICE_QUESTION)) {
            List<Answer> answers = answersDao.getQuestionAnswers(question.getQuestionId());%>

        <h3><%=question.getQuestionText()%></h3><%

        for(int j = 0; j < answers.size(); j++) {
            Answer answer = answers.get(j);%>
        <input type="radio" name="response_1_1" value="<%=answer.getAnswerText()%>"><%=answer.getAnswerText()%><br><%
        }
    }

        /// MULTI_CHOICE_MULTI_ANSWER_QUESTION
        else if (type.equals(Constants.QuestionTypes.MULTI_CHOICE_MULTI_ANSWER_QUESTION)) {
            List<Answer> answers = answersDao.getQuestionAnswers(question.getQuestionId());%>

        <h3><%=question.getQuestionText()%></h3><%

        for (int j = 0; j < answers.size(); j++) {
            Answer answer = answers.get(j);%>

        <input type="checkbox" name="response_1_<%=j+1%>" value="<%=answer.getAnswerText()%>"><%=answer.getAnswerText()%><br><%
        }
    }

    /// MULTI_ANSWER_QUESTION
    else if (type.equals(Constants.QuestionTypes.MULTI_ANSWER_QUESTION)) {%>
        <h3><%=question.getQuestionText()%></h3><%

        for (int j = 0; j < question.getNumAnswers(); j++) {%>
        <input type="text" name="response_1_<%=j+1%>"><br><%
        }

    }

    /// MATCHING QUESTION
    else if (type.equals(Constants.QuestionTypes.MATCHING_QUESTION)) {
        List<Match> matches = matchesDao.getQuestionMatches(question.getQuestionId());
        ArrayList<String> leftMatches = new ArrayList<String>();

        for (Match match : matches) {
            leftMatches.add(match.getLeftMatch());
        }

        ArrayList<String> rightMatches = new ArrayList<String>();

        for (Match match : matches) {
            String right = match.getRightMatch();
            if (!rightMatches.contains(right)) {
                rightMatches.add(right);
            }
        }%>

        <h3><%=question.getQuestionText()%></h3><%

        for (String left : leftMatches) {%>
        <label><%=left%></label>
        <select name="response_1_<%=left%>">
            <option value="not selected">select</option> <%
            for (String right : rightMatches) {%>
            <option value="<%=right%>"><%=right%></option><%
            }%>
        </select><br><%
            }
        }
    %>
        <input type="submit" value="Submit Answer" class="next-button">

        <button type="button" class="exit-button" onclick="exitQuiz()">Exit Quiz</button>
    </form>
</div>

<script>
    function exitQuiz() {
        window.location.href = 'quiz-overview.jsp?<%=Constants.RequestParameters.QUIZ_ID%>=<%=quiz.getId()%>';
    }
</script>

</body>
</html>
