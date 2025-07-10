<%@ page import="org.ja.utils.Constants" %>
<%@ page import="org.ja.model.quiz.question.Question" %>
<%@ page import="java.util.List" %>
<%@ page import="org.ja.model.quiz.response.Response" %>
<%@ page import="org.ja.dao.AnswersDao" %>
<%@ page import="org.ja.model.OtherObjects.Answer" %>
<%@ page import="org.ja.dao.MatchesDao" %>
<%@ page import="org.ja.model.OtherObjects.Match" %>
<%@ page import="java.util.Map" %>
<%@ page import="org.ja.model.quiz.Quiz" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    Integer index = (Integer) session.getAttribute(Constants.SessionAttributes.CURRENT_QUESTION_INDEX);
    List<Question> questions = (List<Question>) session.getAttribute(Constants.SessionAttributes.QUESTIONS);

    /// question amomaqvs arasworad
    Question question = (Question) request.getAttribute("question");

    Constants.QuizMode quizMode = (Constants.QuizMode) session.getAttribute(Constants.SessionAttributes.QUIZ_MODE);
    List<Integer> grades = (List<Integer>) session.getAttribute("grades");
    List<Integer> respGrades = ((List<List<Integer>>) session.getAttribute("responseGrades"))
            .get(quizMode == Constants.QuizMode.TAKING ? index - 1 : 0);
    Response resp = ((List<Response>) session.getAttribute(Constants.SessionAttributes.RESPONSES))
            .get(quizMode == Constants.QuizMode.TAKING ? index - 1 : 0);
%>
<style>
    .incorrect { color: red; }
    .correct { color: green; }
</style>

<html>
<head>
    <title>Question Feedback</title>
    <link rel="stylesheet" href="css/immediate-correction.css">
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
        now = System.currentTimeMillis();
        timeLeft = duration - (now - startTime) / 1000;%>

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

        <div>Time left: <span id="timer"></span></div><%
    }
%>

<div class="question-block">
    <div class="question-text">
        Question <%=quizMode == Constants.QuizMode.TAKING ? index : ""%>:
        <%=question.getQuestionText() != null ? question.getQuestionText() : ""%>
    </div>

    <% if(question.getImageUrl() != null) { %>
    <img src="<%=question.getImageUrl()%>" alt="Question Image" width="300" height="200">
    <% } %>

    <%
        String type = question.getQuestionType();

        if(Constants.QuestionTypes.MATCHING_QUESTION.equals(type)) {
            MatchesDao matchesDao = (MatchesDao) application.getAttribute(Constants.ContextAttributes.MATCHES_DAO);
            List<Match> matches = matchesDao.getQuestionMatches(question.getQuestionId());
    %>
    <div class="answer-block">
        <div class="label">Your Response:</div>
        <% for(int i = 0; i < resp.size(); i++) {
            Match match = resp.getMatch(i); %>
        <div class="<%=respGrades.get(i) > 0 ? "correct" : "incorrect"%>"><%=match.getLeftMatch()%> → <%=match.getRightMatch()%></div>
        <% } %>
    </div>

    <div class="answer-block">
        <div class="label">Correct Response:</div>
        <% for(Match match : matches) { %>
        <div><%=match.getLeftMatch()%> → <%=match.getRightMatch()%></div>
        <% } %>
    </div>

    <% } else if(Constants.QuestionTypes.MULTI_CHOICE_MULTI_ANSWER_QUESTION.equals(type) || Constants.QuestionTypes.MULTIPLE_CHOICE_QUESTION.equals(type)) {
        AnswersDao answersDao = (AnswersDao) application.getAttribute(Constants.ContextAttributes.ANSWERS_DAO);
        List<Answer> answers = answersDao.getQuestionAnswers(question.getQuestionId());
        int responseIndex = 0;
    %>
    <div class="answer-block">
        <div class="label">Your Response:</div>
        <% for (Answer answer : answers) {
            boolean checked = responseIndex < resp.size() && answer.containsAnswer(resp.getAnswer(responseIndex));
            if (checked) responseIndex++;
        %>
        <div class="<%= checked ? "correct" : "" %>">
            <input type="radio" <%= checked ? "checked" : "" %> disabled>
            <p class="<%=checked ? (respGrades.get(responseIndex-1) > 0 ? "correct" : "incorrect") : ""%>"><%= answer.getAnswerText() %></p>
        </div>
        <% } %>
    </div>

    <div class="answer-block">
        <div class="label">Correct Answer(s):</div>
        <% for (Answer answer : answers) { %>
        <div>
            <input type="radio" <%= answer.getAnswerValidity() ? "checked" : "" %> disabled>
            <%= answer.getAnswerText() %>
        </div>
        <% } %>
    </div>

    <% } else {
        AnswersDao answersDao = (AnswersDao) application.getAttribute(Constants.ContextAttributes.ANSWERS_DAO);
        List<Answer> answers = answersDao.getQuestionAnswers(question.getQuestionId());
    %>
    <div class="answer-block">
        <div class="label">Your Response:</div>
        <% for(int i = 0; i < resp.size(); i++) { %>
        <div class="<%=respGrades.get(i) > 0 ? "correct" : "incorrect"%>"><%= resp.getAnswer(i) %></div>
        <% } %>
    </div>

    <div class="answer-block">
        <div class="label">Correct Answer(s):</div>
        <% for(Answer answer : answers) {
            String answerText = answer.getAnswerText();%>
        <div><%= answerText.contains("/") ? answerText.substring(0, answerText.indexOf('/')) : answerText %></div>
        <% } %>
    </div>
    <% } %>

    <div class="score-display">
        <%
            if(Constants.QuizMode.PRACTICE == (Constants.QuizMode) session.getAttribute(Constants.SessionAttributes.QUIZ_MODE)) {%>
        Score: <%=grades.getFirst()%> out of <%=question.getNumAnswers()%><%
    } else {%>
        Score: <%=grades.get(index - 1)%> out of <%=question.getNumAnswers()%><%
        }
    %>
    </div>
</div>

<%
    if(Constants.QuizMode.PRACTICE == quizMode) {
        Map<Question, Integer> masteryMap = (Map<Question, Integer>) session.getAttribute(Constants.SessionAttributes.PRACTICE_QUESTIONS_MASTERY_MAP);%>

        <a href="<%=masteryMap.isEmpty() ? "successful-practice.jsp" : "single-question-page.jsp"%>">
            <button type="button">Next Question</button>
        </a><%
    } else {
        if(index == questions.size()) { %>
            <form id="next-form" method="get" action="quiz-result.jsp">
                <input type="submit" value="View Quiz Result">
            </form><%
        } else { %>
            <form id="next-form" method="get" action="single-question-page.jsp">
                <input type="submit" value="Next Question">
            </form><%
        }
    }
%>

<form action="quiz-overview.jsp" method="get">
    <input type="hidden" name="<%=Constants.RequestParameters.QUIZ_ID%>" value="<%=((Quiz) session.getAttribute(Constants.SessionAttributes.QUIZ)).getId()%>">
    <button type="submit" >Exit Quiz</button>
</form>
</body>
</html>
