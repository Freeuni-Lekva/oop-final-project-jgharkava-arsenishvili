<%@ page import="org.ja.model.quiz.question.Question" %>
<%@ page import="org.ja.utils.Constants" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="org.ja.dao.AnswersDao" %>
<%@ page import="org.ja.model.OtherObjects.Answer" %>
<%@ page import="java.util.Collections" %>
<%@ page import="org.ja.model.OtherObjects.Match" %>
<%@ page import="org.ja.dao.MatchesDao" %>
<%@ page import="java.util.List" %>
<%@ page import="org.ja.model.quiz.Quiz" %>
<%@ page import="java.time.Instant" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    Quiz quiz=(Quiz)session.getAttribute(Constants.SessionAttributes.QUIZ);
    ArrayList<Question> questions = (ArrayList<Question>) session.getAttribute(Constants.SessionAttributes.QUESTIONS);
    AnswersDao answersDao = (AnswersDao) application.getAttribute(Constants.ContextAttributes.ANSWERS_DAO);
    MatchesDao matchesDao = (MatchesDao) application.getAttribute(Constants.ContextAttributes.MATCHES_DAO);
%>

<html>
<head>
    <title>Quiz Questions</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="css/all-questions-page.css">
</head>
<body>

<%
    long startTime = (Long) session.getAttribute("start-time");
    int duration = (Integer) session.getAttribute("time-limit-in-seconds");
    long now = Instant.now().toEpochMilli();
    long timeLeft = duration - (now - startTime) / 1000;
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
            document.getElementById("questions-form").submit();
        } else {
            document.getElementById("timer").textContent = formatTime(timeLeft);
            timeLeft--;
            setTimeout(updateTimer, 1000);
        }
    }

    window.onload = updateTimer;
</script>

<div class="timer-container">Time left: <span id="timer"></span></div>

<div class="quiz-container">
    <form id="questions-form" action="grade-single-page-quiz" method="post">
        <%
            for (int i = 0; i < questions.size(); i++) {
                Question question = questions.get(i);
                String type = question.getQuestionType();%>

        <div class="question-block">
            <div class="question-numbers">Question <%=i+1%></div><%

            ///  RESPONSE QUESTION
            if (type.equals(Constants.QuestionTypes.RESPONSE_QUESTION)) {%>
                <h3><%=question.getQuestionText()%></h3>
                <input type="text" name="response_<%=i+1%>_1"><br><br><%
            }

            /// FILL_IN_THE_BLANK QUESTION
            else if (type.equals(Constants.QuestionTypes.FILL_IN_THE_BLANK_QUESTION)) {%>
                <h3><%=question.getQuestionText().replace("_", "_____")%></h3>
                <input type="text" name="response_<%=i+1%>_1"><br><br><%
            }

            /// PICTURE RESPONSE QUESTION
            else if (type.equals(Constants.QuestionTypes.PICTURE_RESPONSE_QUESTION)) {
                if (!(question.getQuestionText() == null)  && !question.getQuestionText().trim().isEmpty()) {%>
                    <h3><%=question.getQuestionText()%></h3><%
                }%>
                    <img src="<%=question.getImageUrl()%>" width="300" height="200"><br>
                    <input type="text" name="response_<%=i+1%>_1"><br><br><%
            }

            /// MULTIPLE CHOICE QUESTION
            else if (type.equals(Constants.QuestionTypes.MULTIPLE_CHOICE_QUESTION)) {
                List<Answer> answers = answersDao.getQuestionAnswers(question.getQuestionId());%>

                    <h3><%=question.getQuestionText()%></h3><%

                for(int j = 0; j < answers.size(); j++) {
                    Answer answer = answers.get(j);%>

                    <input type="radio" name="response_<%=i+1%>_1" value="<%=answer.getAnswerText()%>"><%=answer.getAnswerText()%><br><%
                }
            }

            /// MULTI CHOICE MULTI ANSWER QUESTION
            else if (type.equals(Constants.QuestionTypes.MULTI_CHOICE_MULTI_ANSWER_QUESTION)) {
                List<Answer> answers = answersDao.getQuestionAnswers(question.getQuestionId());%>

                    <h3><%=question.getQuestionText()%></h3><%

                for (int j = 0; j < answers.size(); j++) {
                    Answer answer = answers.get(j);%>
                    <label>
                        <input type="checkbox" name="response_<%=i+1%>_<%=j+1%>" value="<%=answer.getAnswerText()%>"><%=answer.getAnswerText()%>
                    </label><br><%
                }
            }

            /// MULTI ANSWER QUESTION
            else if (type.equals(Constants.QuestionTypes.MULTI_ANSWER_QUESTION)) {%>
                    <h3><%=question.getQuestionText()%></h3><%

                for (int j = 0; j < question.getNumAnswers(); j++) {%>
                    <input type="text" name="response_<%=i+1%>_<%=j+1%>"><br><%
                }
            }
            /// MATCHING QUESTION
            else if (type.equals(Constants.QuestionTypes.MATCHING_QUESTION)) {
                List<Match> matches = matchesDao.getQuestionMatches(question.getQuestionId());
                List<String> leftMatches = new ArrayList<String>();
                for (int k = 0; k < matches.size(); k++) {
                    leftMatches.add(matches.get(k).getLeftMatch());
                }

                List<String> rightMatches = new ArrayList<String>();
                for (int k = 0; k < matches.size(); k++) {
                    String right = matches.get(k).getRightMatch();
                    if (!rightMatches.contains(right)) {
                        rightMatches.add(right);
                    }
                }%>
            <h3 class="question-text"><%=question.getQuestionText()%></h3>
            <div class="matching-container"><%

            for (String left : leftMatches) {%>
            <div class="matching-row">
                <div class="matching-label"><%=left%></div>
                <select name="response_<%=i+1%>_<%=left%>">
                    <option value="not selected">-- Select Match --</option><%
                    for (String right : rightMatches) {%>
                    <option value="<%=right%>"><%=right%></option><%
                    }%>
                </select>
            </div><%
                    }%>
            </div><%
            }%>
        </div><%
            }%>
            <div class="form-actions">
                <button type="submit" class="submit-btn">Submit Quiz</button>
                <button type="button" class="exit-btn" onclick="exitQuiz()">Exit Quiz</button>
            </div>
    </form>
</div>

<script>
    function exitQuiz() {
        const form = document.createElement('form');
        form.method = 'GET';
        form.action = 'quiz-overview.jsp';

        const input = document.createElement('input');
        input.type = 'hidden';
        input.name = '<%=Constants.RequestParameters.QUIZ_ID%>';
        input.value = '<%=quiz.getId()%>';

        form.appendChild(input);
        document.body.appendChild(form);
        form.submit();
    }
</script>
</body>
</html>
