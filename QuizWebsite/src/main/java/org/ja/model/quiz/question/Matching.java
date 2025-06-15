package org.ja.model.quiz.question;

import org.ja.model.OtherObjects.Match;
import org.ja.model.quiz.response.Response;

import java.util.List;

public class Matching extends Question {

    public Matching(long questionId, long quizId, String questionText,
                    String imageUrl, String questionType, int numAnswers, String orderStatus) {
        super(questionId, quizId, questionText, imageUrl, questionType, numAnswers, orderStatus);
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
                else grade--;
            }
        }

        return Math.max(0, grade);
    }

}
