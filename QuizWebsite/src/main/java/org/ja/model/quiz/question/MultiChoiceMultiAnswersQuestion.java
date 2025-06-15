package org.ja.model.quiz.question;

import org.ja.model.OtherObjects.Answer;
import org.ja.model.quiz.response.Response;

import java.util.List;

public class MultiChoiceMultiAnswersQuestion extends Question{
    public MultiChoiceMultiAnswersQuestion(long questionId, long quizId, String questionText,
                               String imageUrl, String questionType, int numAnswers, String orderStatus){
        super(questionId, quizId, questionText, imageUrl, questionType, numAnswers, orderStatus);
    }

    @Override
    public int gradeResponse(List<?> correctAnswersList, Response response){
        int grade = 0;

        if (!correctAnswersList.isEmpty() && correctAnswersList.get(0) instanceof Answer){
            @SuppressWarnings("unchecked")
            List<Answer> correctAnswers = (List<Answer>) correctAnswersList;

            for (int i = 0; i < response.size(); i++){
                String currResponse = response.getAnswer(i);
                boolean isCorrect = false;

                for (int j = 0; j < correctAnswers.size(); j++){
                    if (correctAnswers.get(i).containsAnswer(currResponse)) {
                        grade++;
                        isCorrect = true;
                        break;
                    }
                }

                if (!isCorrect) grade--;
            }
        }

        return Math.max(0, grade);
    }

}
