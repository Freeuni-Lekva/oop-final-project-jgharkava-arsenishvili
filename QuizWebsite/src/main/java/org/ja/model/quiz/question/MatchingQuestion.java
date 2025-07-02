package org.ja.model.quiz.question;

import org.ja.model.OtherObjects.Match;
import org.ja.model.quiz.response.Response;
import org.ja.utils.Constants;

import java.util.ArrayList;
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
    public List<Integer> gradeResponse(List<?> correctMatchesList, Response response){
        List<Integer> grades = new ArrayList<>();

        if (!correctMatchesList.isEmpty() && correctMatchesList.get(0) instanceof Match) {
            @SuppressWarnings("unchecked")
            List<Match> correctMatches = (List<Match>) correctMatchesList;

            for (int i = 0; i < response.size(); i++) {
                Match currResponse = response.getMatch(i);

                if (correctMatches.contains(currResponse)) grades.add(1);
                else grades.add(0);
            }
        }

        return List.copyOf(grades);
    }

}
