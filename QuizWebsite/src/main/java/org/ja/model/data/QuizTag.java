package org.ja.model.data;

/**
 * Represents an association between a Quiz and a Tag.
 * Each instance corresponds to a link between a quiz and a tag, typically used
 * for categorizing or labeling quizzes.
 *
 * <p>This class corresponds to the {@code quiz_tags} table in the database.</p>
 */
public class QuizTag {
    private long quizId;
    private long tagId;

    /**
     * Constructs a {@code QuizTag} object linking a quiz with a tag.
     *
     * @param quizId the ID of the quiz
     * @param tagId the ID of the tag
     */
    public QuizTag(long quizId, long tagId) {
        this.quizId = quizId;
        this.tagId = tagId;
    }

    /**
     * @return the ID of the quiz
     */
    public long getQuizId() {
        return quizId;
    }

    /**
     * @param quizId the quiz ID to set
     */
    public void setQuizId(long quizId) {
        this.quizId = quizId;
    }

    /**
     * @return the ID of the tag
     */
    public long getTagId() {
        return tagId;
    }

    /**
     * @param tagId the tag ID to set
     */
    public void setTagId(long tagId) {
        this.tagId = tagId;
    }

    /**
     * Checks equality of two {@code QuizTag} objects based on quizId and tagId.
     *
     * @param o the object to compare
     * @return true if both quizId and tagId are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QuizTag)) return false;

        QuizTag other = (QuizTag) o;
        return quizId == other.getQuizId() &&
                tagId == other.getTagId();
    }
}
