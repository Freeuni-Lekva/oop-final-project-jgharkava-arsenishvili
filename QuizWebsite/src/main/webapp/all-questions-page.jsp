<%@ page import="org.ja.model.quiz.question.Question" %>
<%@ page import="org.ja.utils.Constants" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="org.ja.dao.AnswersDao" %>
<%@ page import="org.ja.model.OtherObjects.Answer" %>
<%@ page import="java.util.Collections" %>
<%@ page import="org.ja.model.OtherObjects.Match" %>
<%@ page import="org.ja.dao.MatchesDao" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.stream.Collectors" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    ArrayList<Question> questions = (ArrayList<Question>) session.getAttribute(Constants.SessionAttributes.QUESTIONS);
    AnswersDao answersDao = (AnswersDao) application.getAttribute(Constants.ContextAttributes.ANSWERS_DAO);
    MatchesDao matchesDao = (MatchesDao) application.getAttribute(Constants.ContextAttributes.MATCHES_DAO);
%>

<html>
<head>
    <title>Quiz Page</title>
</head>
<body>
<form action="" method="post">
    <%
        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            String type = question.getQuestionType();

            if (type.equals(Constants.QuestionTypes.RESPONSE_QUESTION) ||
                    type.equals(Constants.QuestionTypes.FILL_IN_THE_BLANK_QUESTION)) {
    %>
    <h3><%=question.getQuestionText()%></h3>
    <input type="text" name="response_<%=i%>_1"><br><br>
    <%
    } else if (type.equals(Constants.QuestionTypes.PICTURE_RESPONSE_QUESTION)) {
        if (!(question.getQuestionText() == null)  && !question.getQuestionText().trim().isEmpty()) {
    %>
    <h3><%=question.getQuestionText()%></h3>
    <%
        }
    %>
    <img src="<%=question.getImageUrl()%>" width="300" height="200"><br>
    <input type="text" name="response_<%=i%>_1"><br><br>
    <%
    } else if (type.equals(Constants.QuestionTypes.MULTIPLE_CHOICE_QUESTION) ||
            type.equals(Constants.QuestionTypes.MULTI_CHOICE_MULTI_ANSWER_QUESTION)) {
        ArrayList<Answer> answers = answersDao.getQuestionAnswers(question.getQuestionId());
        Collections.shuffle(answers);
    %>
    <h3><%=question.getQuestionText()%></h3>
    <%
        for (int j = 0; j < answers.size(); j++) {
            Answer answer = answers.get(j);
    %>
    <label>
        <input type="checkbox" name="response_<%=i%>_<%=j%>" value="<%=answer.getAnswerText()%>">
        <%=answer.getAnswerText()%>
    </label><br>
    <%
        }
    } else if (type.equals(Constants.QuestionTypes.MULTI_ANSWER_QUESTION)) {
    %>
    <h3><%=question.getQuestionText()%></h3>
    <%
        for (int j = 0; j < question.getNumAnswers(); j++) {
    %>
    <input type="text" name="response_<%=i%>_<%=j%>"><br>
    <%
        }
    } else if (type.equals(Constants.QuestionTypes.MATCHING_QUESTION)) {
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
        }
    %>
    <h3><%=question.getQuestionText()%></h3>
    <%
        for (String left : leftMatches) {
    %>
    <label><%=left%></label>
    <select name="response_<%=i%>_<%=left%>">
        <%
            for (String right : rightMatches) {
        %>
        <option value="<%=right%>"><%=right%></option>
        <%
            }
        %>
    </select><br>
    <%
                }
            }
        }
    %>
    <input type="submit" value="Submit Quiz">
</form>
</body>
</html>
