package org.ja.model.quiz.question;

import org.ja.model.data.Match;
import org.ja.model.quiz.response.Response;
import org.ja.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a matching type of quiz question, where pairs must be matched correctly.
 * Extends the base Question class and implements grading specific to matching questions.
 */
public class MatchingQuestion extends Question {

    /**
     * Constructs a MatchingQuestion with all parameters specified.
     *
     * @param questionId the unique ID of the question
     * @param quizId the ID of the quiz this question belongs to
     * @param questionText the text of the question
     * @param imageUrl optional image URL for the question
     * @param questionType the question type identifier (should be matching)
     * @param numAnswers the number of answer pairs expected
     * @param orderStatus the ordering status of the question
     */
    public MatchingQuestion(long questionId, long quizId, String questionText,
                            String imageUrl, String questionType, int numAnswers, String orderStatus) {
        super(questionId, quizId, questionText, imageUrl, questionType, numAnswers, orderStatus);
    }

    /**
     * Constructs a MatchingQuestion with default questionId and quizId,
     * no image, specified questionText, number of answers, matching type,
     * and ordered question order.
     *
     * @param questionText the text of the question
     * @param numAnswers the number of answer pairs expected
     */
    public MatchingQuestion(String questionText, int numAnswers){
        super(0L, 0L, questionText, null, Constants.QuestionTypes.MATCHING_QUESTION,
                numAnswers, Constants.OrderTypes.ORDERED);
    }

    /**
     * Grades the response for this matching question by comparing each match
     * in the response with the list of correct matches.
     *
     * @param correctMatchesList the list of correct Match objects
     * @param response the Response object containing user answers
     * @return an immutable list of integers representing grades (1 for correct, 0 for incorrect) for each match
     */
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
