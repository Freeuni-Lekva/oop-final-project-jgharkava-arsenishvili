package org.ja.model.quiz.question;

import org.ja.model.OtherObjects.Answer;
import org.ja.model.quiz.response.Response;
import org.ja.utils.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MultiAnswerQuestion extends Question{
    public MultiAnswerQuestion(long questionId, long quizId, String questionText,
                               String imageUrl, String questionType, int numAnswers, String orderStatus){
        super(questionId, quizId, questionText, imageUrl, questionType, numAnswers, orderStatus);
    }

    public MultiAnswerQuestion(String questionText, int numAnswers, String orderStatus){
        super(0L, 0L, questionText, null,
                Constants.QuestionTypes.MULTI_ANSWER_QUESTION, numAnswers, orderStatus);
    }

    @Override
    public List<Integer> gradeResponse(List<?> correctAnswersList, Response response){
        List<Integer> grades = new ArrayList<>();

        if (!correctAnswersList.isEmpty() && correctAnswersList.get(0) instanceof Answer) {
            @SuppressWarnings("unchecked")
            List<Answer> correctAnswers = (List<Answer>) correctAnswersList;

            if (orderStatus.equals(Constants.OrderTypes.ORDERED)) {
                grades = IntStream.range(0, correctAnswers.size())
                         .map(i -> (correctAnswers.get(i)).containsAnswer(response.getAnswer(i)) ? 1 : 0)
                         .boxed()
                         .toList();
            } else {
                Boolean[] unorderedCorrectAnswers = new Boolean[correctAnswers.size()];
                Arrays.fill(unorderedCorrectAnswers, false);

                for (int i = 0; i < response.size(); i++) {
                    String currResponse = response.getAnswer(i);
                    for (int j = 0; j < correctAnswers.size(); j++) {
                        if (correctAnswers.get(j).containsAnswer(currResponse) && !unorderedCorrectAnswers[j]) {
                            unorderedCorrectAnswers[j] = true;
                            grades.add(1);
                            break;
                        }
                    }
                    if(grades.size() == i) grades.add(0);
                }
            }
        }

        return List.copyOf(grades);
    }
}
