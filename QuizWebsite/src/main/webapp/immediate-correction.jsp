<%@ page import="org.ja.utils.Constants" %>
<%@ page import="org.ja.model.quiz.question.Question" %>
<%@ page import="java.util.List" %>
<%@ page import="java.lang.module.ResolutionException" %>
<%@ page import="org.ja.model.quiz.response.Response" %>
<%@ page import="org.ja.dao.AnswersDao" %>
<%@ page import="org.ja.model.OtherObjects.Answer" %>
<%@ page import="org.ja.dao.MatchesDao" %>
<%@ page import="org.ja.model.OtherObjects.Match" %><%--
  Created by IntelliJ IDEA.
  User: tober
  Date: 7/1/2025
  Time: 9:50 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
  Integer index = (Integer) session.getAttribute(Constants.SessionAttributes.CURRENT_QUESTION_INDEX);
  List<Question> questions = (List<Question>) session.getAttribute(Constants.SessionAttributes.QUESTIONS);
  Question question = questions.get(index-1);
  List<Integer> grades = (List<Integer>) session.getAttribute("grades");
  Response resp = ((List<Response>) session.getAttribute(Constants.SessionAttributes.RESPONSES)).get(index-1);
%>

<html>
<head>
    <title>Taking Quiz</title>
</head>
<body>
  <h2>Question Feedback</h2>

  <h3>Question <%=index%>:</h3>
  <%
    if(question.getQuestionText() != null) { %>
      <h3><%=question.getQuestionText()%></h3><%
    }

    if(question.getImageUrl() != null) {%>
      <img src="<%=question.getImageUrl()%>" width="300" height="200"><br><%
    }
  %>

  <%
    String type = question.getQuestionType();

    /// MATCHING QUESTION
    if(Constants.QuestionTypes.MATCHING_QUESTION.equals(type)) {
      MatchesDao matchesDao = (MatchesDao) application.getAttribute(Constants.ContextAttributes.MATCHES_DAO);
      List<Match> matches = matchesDao.getQuestionMatches(question.getQuestionId());%>

      <h1>Your Response</h1><%

      for(int i = 0; i < resp.size(); i++) {
        Match match = resp.getMatch(i);%>

        <h3><%=match.getLeftMatch()%> - <%=match.getRightMatch()%></h3><%
      }%>

      <h1>Correct Responses</h1><%

      for(Match match : matches) {%>
        <h3><%=match.getLeftMatch()%> - <%=match.getRightMatch()%></h3><%
      }
    }

    /// MULTIPLE CHOICE QUESTIONS
    else if(Constants.QuestionTypes.MULTI_CHOICE_MULTI_ANSWER_QUESTION.equals(type) || Constants.QuestionTypes.MULTIPLE_CHOICE_QUESTION.equals(type)) {
      AnswersDao answersDao = (AnswersDao) application.getAttribute(Constants.ContextAttributes.ANSWERS_DAO);
      List<Answer> answers = answersDao.getQuestionAnswers(question.getQuestionId());

      int responseIndex = 0;

      for (Answer answer : answers) {
        if(responseIndex < resp.size() && answer.containsAnswer(resp.getAnswer(responseIndex))) {
          responseIndex++;%>
            <input type="radio"><%=%>


  <%


        }
      }
    }

    else {
      AnswersDao answersDao = (AnswersDao) application.getAttribute(Constants.ContextAttributes.ANSWERS_DAO);
      List<Answer> answers = answersDao.getQuestionAnswers(question.getQuestionId());%>

      <h1>Your Response</h1>
  <%
      for(int i = 0; i < resp.size(); i++) {%>
        <h3><%=resp.getAnswer(i)%></h3><%
      }%>

      <h1>Correct Response</h1><%

      for(Answer answer : answers) {%>

        <h3><%=answer.getAnswerText()%></h3><%
      }
    }
  %>

















  <%=grades.get(index-1)%>/<%=question.getNumAnswers()%>

  <%
    if(index == questions.size()) {%>
      <form method="get" action="quiz-result.jsp">
        <input type="submit" value="View Quiz Result">
      </form>
  <%
    } else {%>
      <form method="get" action="single-question-page.jsp">
        <input type="submit" value="Next Question">
      </form>
  <%
    }
  %>

</body>
</html>
