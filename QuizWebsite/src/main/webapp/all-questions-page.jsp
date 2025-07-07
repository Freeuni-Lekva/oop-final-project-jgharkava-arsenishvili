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
</head>
<body>

<%
    long startTime = (Long) session.getAttribute("start-time");
    int duration = (Integer) session.getAttribute("time-limit-in-seconds");
    long now = System.currentTimeMillis();
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

<div>Time left: <span id="timer"></span></div>

<form id="questions-form" action="grade-single-page-quiz" method="post">
    <%
        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            String type = question.getQuestionType();%>

            <h2>Question <%=i+1%></h2><%

            ///  RESPONSE OF FILL_IN_THE_BLANK questions
            if (type.equals(Constants.QuestionTypes.RESPONSE_QUESTION) || type.equals(Constants.QuestionTypes.FILL_IN_THE_BLANK_QUESTION)) {%>
                <h3><%=question.getQuestionText()%></h3>
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
                ArrayList<Answer> answers = answersDao.getQuestionAnswers(question.getQuestionId());
                Collections.shuffle(answers);%>

                <h3><%=question.getQuestionText()%></h3><%

                for(int j = 0; j < answers.size(); j++) {
                    Answer answer = answers.get(j);%>

                    <input type="radio" name="response_<%=i+1%>_1" value="<%=answer.getAnswerText()%>"><%=answer.getAnswerText()%><%
                }
            }

            /// MULTI CHOICE MULTI ANSWER QUESTION
            else if (type.equals(Constants.QuestionTypes.MULTI_CHOICE_MULTI_ANSWER_QUESTION)) {
                ArrayList<Answer> answers = answersDao.getQuestionAnswers(question.getQuestionId());%>

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
                ArrayList<Match> matches = matchesDao.getQuestionMatches(question.getQuestionId());
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

                <h3><%=question.getQuestionText()%></h3><%

                for (String left : leftMatches) {%>
                    <label><%=left%></label>
                    <select name="response_<%=i+1%>_<%=left%>">
                        <option value="not selected">select</option> <%
                    for (String right : rightMatches) {%>
                        <option value="<%=right%>"><%=right%></option><%
                    }%>
                    </select><br><%
                }
            }
        }%>
    <br><br>
    <input type="submit" value="Submit Quiz">
</form>
<form action="quiz-overview.jsp" method="get">
    <input type="hidden" name="<%=Constants.RequestParameters.QUIZ_ID%>" value="<%=quiz.getId()%>">
    <button type="submit">Exit Quiz</button>
</form>
</body>
</html>
