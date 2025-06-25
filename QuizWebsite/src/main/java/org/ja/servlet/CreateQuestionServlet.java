package org.ja.servlet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ja.model.OtherObjects.Answer;
import org.ja.model.OtherObjects.Match;
import org.ja.model.quiz.question.*;
import org.ja.utils.Constants;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@WebServlet("/create-question")
public class CreateQuestionServlet extends HttpServlet {
    private String questionText;
    private String imageUrl;
    private Question question;
    private String[] answers;
    private String[] isCorrectValues;
    private Map<Question, List<Answer>> questionAnswerMap;
    private Map<Question, List<Match>> questionMatchMap;
    private HttpServletRequest request;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect("create-question.jsp");
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        this.request = request;
        questionText = request.getParameter("questionText");
        String questionType = request.getParameter("questionType");

        imageUrl = request.getParameter("imageUrl");
        answers = request.getParameterValues("answer");

        questionAnswerMap = (Map<Question, List<Answer>>) request.getSession().getAttribute(Constants.SessionAttributes.QUESTIONS);
        questionMatchMap = (Map<Question, List<Match>>) request.getSession().getAttribute(Constants.SessionAttributes.MATCHES);

        switch (questionType) {
            case Constants.QuestionTypes.RESPONSE_QUESTION:
                handleResponseQuestion();
                break;
            case Constants.QuestionTypes.PICTURE_RESPONSE_QUESTION:
                handlePictureQuestion();
                break;
            case Constants.QuestionTypes.FILL_IN_THE_BLANK_QUESTION:
                handleFillInTheBlankQuestion();
                break;
            case Constants.QuestionTypes.MULTIPLE_CHOICE_QUESTION:
                handleMultipleChoiceQuestion();
                break;
            case Constants.QuestionTypes.MULTI_CHOICE_MULTI_ANSWER_QUESTION:
                handleMultiChoiceMultiAnswerQuestion();
                break;
            case Constants.QuestionTypes.MULTI_ANSWER_QUESTION:
                handleMultiAnswerQuestion();
                break;
            case Constants.QuestionTypes.MATCHING_QUESTION:
                handleMatchingQuestion();
                break;
        }

        request.getSession().setAttribute(Constants.SessionAttributes.QUESTIONS, questionAnswerMap);
        request.getSession().setAttribute(Constants.SessionAttributes.MATCHES, questionMatchMap);

        // Debug print just in case TODO delete
//        for (Map.Entry<Question, List<Match>> entry : questionMatchMap.entrySet()) {
//            System.out.println("Question: " + entry.getKey());
//            for (Match a : entry.getValue()) {
//                System.out.println(" → Answer: " + a);
//            }
//        }

        request.getSession().setAttribute(Constants.SessionAttributes.HAS_QUESTIONS, true);
        response.sendRedirect("create-question.jsp");
    }

    private void handleResponseQuestion() {
        question = new ResponseQuestion(questionText);

        String joinedAnswers = Arrays.stream(answers)
                .map(String::trim)
                .collect(Collectors.joining("/"));

        questionAnswerMap.put(question, List.of(new Answer(joinedAnswers)));
    }

    private void handlePictureQuestion() {
        question = new PictureResponseQuestion(imageUrl, questionText);

        String joinedAnswers = Arrays.stream(answers)
                .map(String::trim)
                .collect(Collectors.joining("/"));

        questionAnswerMap.put(question, List.of(new Answer(joinedAnswers)));
    }

    private void handleFillInTheBlankQuestion() {
        question = new FillInTheBlankQuestion(questionText);

        String joinedAnswers = Arrays.stream(answers)
                .map(String::trim)
                .collect(Collectors.joining("/"));

        questionAnswerMap.put(question, List.of(new Answer(joinedAnswers)));
    }

    private void handleMultipleChoiceQuestion() {
        question = new MultipleChoiceQuestion(questionText);
        isCorrectValues = request.getParameterValues("isCorrect");

        List<Answer> answerList = new ArrayList<>();

        for (int i = 0; i < answers.length; i++) {
            boolean isCorrect = Boolean.parseBoolean(isCorrectValues[i]);
            System.out.println(isCorrect);

            answerList.add(new Answer(answers[i].trim(), i + 1, isCorrect));
        }

        questionAnswerMap.put(question, answerList);
    }

    private void handleMultiChoiceMultiAnswerQuestion() {
        isCorrectValues = request.getParameterValues("isCorrect");

        long trueCount = Arrays.stream(isCorrectValues)
                .filter("true"::equalsIgnoreCase)
                .count();

        question = new MultiChoiceMultiAnswersQuestion(questionText, (int) trueCount);

        List<Answer> answerList = new ArrayList<>();

        for (int i = 0; i < answers.length; i++) {
            boolean isCorrect = Boolean.parseBoolean(isCorrectValues[i]);
            System.out.println(isCorrect);
            answerList.add(new Answer(answers[i].trim(), i + 1, isCorrect));
        }

        questionAnswerMap.put(question, answerList);
    }

    private void handleMultiAnswerQuestion(){
        String isOrdered = request.getParameter("isOrdered");

        String answerOrder = request.getParameter("answerOrder");
        String[] orderedIds = answerOrder.split(",");

        List<Answer> answerList = new ArrayList<>();

        int numAnswers = orderedIds.length;

        for (int i = 0; i < numAnswers; i++){
            String groupId = orderedIds[i];

            String answerText = request.getParameter("answerText-" + groupId);
            String[] options = request.getParameterValues("option-" + groupId);

            String joinedAnswers = (options != null) ?
                    answerText.trim() + "/" + Arrays.stream(options)
                    .map(String::trim)
                    .collect(Collectors.joining("/")) : answerText.trim();

            answerList.add(new Answer(joinedAnswers, i + 1, true));
        }

        if (isOrdered != null)
            question = new MultiAnswerQuestion(questionText, numAnswers, Constants.OrderTypes.ORDERED);
        else
            question = new MultiAnswerQuestion(questionText, numAnswers, Constants.OrderTypes.UNORDERED);

        questionAnswerMap.put(question, answerList);
    }

    private void handleMatchingQuestion(){
        Map<String, String[]> paramNames = request.getParameterMap();

        List<String> leftIds = new ArrayList<>();
        Map<String, String> rightValues = new HashMap<>();

        for (String param: paramNames.keySet()){
            if (param.startsWith("left-")) {
                leftIds.add(param.substring(5));
            } else if (param.startsWith("right-")){
                String id = param.substring(6);
                rightValues.put(id, request.getParameter(param).trim());
            }
        }

        List<Match> matches = new ArrayList<>();

        for (String id: leftIds){
            String leftText = request.getParameter("left-" + id).trim();
            String matchRightId = request.getParameter("match-" + id);

            String matchedRightValue = rightValues.get(matchRightId.replace("right-", ""));

            Match match = new Match(leftText, matchedRightValue);
            matches.add(match);
        }

        question = new MatchingQuestion(questionText, leftIds.size());

        questionMatchMap.put(question, matches);
    }

}
