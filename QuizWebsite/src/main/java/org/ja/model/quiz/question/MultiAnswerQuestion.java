package org.ja.model.quiz.question;

import org.ja.model.OtherObjects.Answer;
import org.ja.model.quiz.response.Response;
import org.ja.utils.Constants;

import java.util.List;
import java.util.stream.IntStream;

public class MultiAnswerQuestion extends Question{
    public MultiAnswerQuestion(long questionId, long quizId, String questionText,
                               String imageUrl, String questionType, int numAnswers, String orderStatus){
        super(questionId, quizId, questionText, imageUrl, questionType, numAnswers, orderStatus);
    }

    @Override
    public int gradeResponse(List<?> correctAnswersList, Response response){
        int grade = 0;

        if (!correctAnswersList.isEmpty() && correctAnswersList.get(0) instanceof Answer) {
            @SuppressWarnings("unchecked")
            List<Answer> correctAnswers = (List<Answer>) correctAnswersList;

            if (orderStatus.equals(Constants.OrderTypes.ORDERED)) {
                grade = (int) IntStream.range(0, correctAnswers.size())
                        .filter(i -> (correctAnswers.get(i)).containsAnswer(response.getAnswer(i)))
                        .count();
            } else {
                Boolean[] unorderedCorrectAnswers = new Boolean[correctAnswers.size()];

                for (int i = 0; i < response.size(); i++) {
                    String currResponse = response.getAnswer(i);
                    for (int j = 0; j < correctAnswers.size(); j++) {
                        if (correctAnswers.get(j).containsAnswer(currResponse) && !unorderedCorrectAnswers[j]) {
                            unorderedCorrectAnswers[i] = true;
                            grade++;
                        }
                    }
                }
            }
        }

        return grade;
    }
}
