package org.ja.model.data;

import java.util.Arrays;

/**
 * Represents an answer to a quiz question.
 * Each answer is linked to a question and contains the answer text,
 * display order, and a flag indicating whether it is correct.
 */
public class Answer {

    private long answerId;
    private long questionId;
    private String answerText;
    private int answerOrder;
    private boolean answerValidity;

    /**
     * Default no-argument constructor.
     */
    public Answer() {}

    /**
     * Constructs an Answer with all fields specified.
     *
     * @param answerId the unique ID of the answer
     * @param questionId the ID of the associated question
     * @param answerText the text of the answer
     * @param answerOrder the order in which the answer should appear
     * @param answerValidity true if the answer is correct, false otherwise
     */
    public Answer(long answerId, long questionId, String answerText, int answerOrder, boolean answerValidity) {
        this.answerId = answerId;
        this.questionId = questionId;
        this.answerText = answerText;
        this.answerOrder = answerOrder;
        this.answerValidity = answerValidity;
    }

    /**
     * Constructs a default valid answer with order 1.
     *
     * @param answerText the text of the answer
     */
    public Answer(String answerText) {
        this.answerText = answerText;
        this.answerOrder = 1;
        this.answerValidity = true;
    }

    /**
     * Constructs an answer with text, order, and validity.
     *
     * @param answerText the text of the answer
     * @param answerOrder the display order of the answer
     * @param answerValidity true if the answer is correct
     */
    public Answer(String answerText, int answerOrder, boolean answerValidity) {
        this.answerText = answerText;
        this.answerOrder = answerOrder;
        this.answerValidity = answerValidity;
    }

    /**
     * Returns the unique ID of the answer.
     *
     * @return the answer ID
     */
    public long getAnswerId() {
        return answerId;
    }

    /**
     * Sets the unique ID of the answer.
     *
     * @param answerId the ID to set
     */
    public void setAnswerId(long answerId) {
        this.answerId = answerId;
    }

    /**
     * Returns the ID of the question this answer belongs to.
     *
     * @return the question ID
     */
    public long getQuestionId() {
        return questionId;
    }

    /**
     * Sets the ID of the associated question.
     *
     * @param questionId the question ID to set
     */
    public void setQuestionId(long questionId) {
        this.questionId = questionId;
    }

    /**
     * Returns the text of the answer.
     *
     * @return the answer text
     */
    public String getAnswerText() {
        return answerText;
    }

    /**
     * Sets the text of the answer.
     *
     * @param answerText the answer text to set
     */
    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    /**
     * Returns the display order of the answer.
     *
     * @return the answer order
     */
    public int getAnswerOrder() {
        return answerOrder;
    }

    /**
     * Sets the display order of the answer.
     *
     * @param answerOrder the order to set
     */
    public void setAnswerOrder(int answerOrder) {
        this.answerOrder = answerOrder;
    }

    /**
     * Returns whether the answer is correct.
     *
     * @return true if the answer is correct, false otherwise
     */
    public boolean getAnswerValidity() {
        return answerValidity;
    }

    /**
     * Sets whether the answer is correct.
     *
     * @param answerValidity true if the answer is correct
     */
    public void setAnswerValidity(boolean answerValidity) {
        this.answerValidity = answerValidity;
    }

    /**
     * Checks equality based on question ID and answer text.
     *
     * @param o the object to compare with
     * @return true if equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Answer)) return false;

        Answer other = (Answer) o;
        return getQuestionId() == other.getQuestionId() &&
                answerText.equals(other.getAnswerText());
    }

    /**
     * Checks if the provided answer string matches any of the answer parts
     * (split by '¶') in a case-insensitive manner.
     *
     * @param answer the string to check against
     * @return true if a match is found, false otherwise
     */
    public boolean containsAnswer(String answer) {
        return Arrays.stream(answerText.split("¶"))
                .anyMatch(part -> (!part.trim().isEmpty() && part.trim().equalsIgnoreCase(answer.trim())));
    }

    /**
     * Returns the answer text as the string representation.
     *
     * @return the answer text
     */
    @Override
    public String toString() {
        return answerText;
    }
}
