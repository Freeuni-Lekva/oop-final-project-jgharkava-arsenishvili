package org.ja.model.quiz.question;

import org.ja.model.OtherObjects.Match;
import org.ja.model.quiz.response.Response;
import org.ja.utils.Constants;
import java.util.List;

public class MatchingQuestion extends Question {

    public MatchingQuestion(long questionId, long quizId, String questionText,
                    String imageUrl, String questionType, int numAnswers, String orderStatus) {
        super(questionId, quizId, questionText, imageUrl, questionType, numAnswers, orderStatus);
    }

    public MatchingQuestion(String questionText, int numAnswers){
        super(0L, 0L, questionText, null, Constants.QuestionTypes.MATCHING_QUESTION,
                numAnswers, Constants.OrderTypes.ORDERED);
    }

    @Override
    public int gradeResponse(List<?> correctMatchesList, Response response){
        int grade = 0;

        if (!correctMatchesList.isEmpty() && correctMatchesList.get(0) instanceof Match) {
            @SuppressWarnings("unchecked")
            List<Match> correctMatches = (List<Match>) correctMatchesList;

            for (int i = 0; i < response.size(); i++) {
                Match currResponse = response.getMatch(i);

                if (correctMatches.contains(currResponse)) grade++;
            }
        }

        return Math.max(0, grade);
    }

}
