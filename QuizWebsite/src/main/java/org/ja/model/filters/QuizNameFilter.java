package org.ja.model.filters;

import java.util.List;

/**
 * Filter that matches quizzes by name using a SQL LIKE clause.
 * <p>
 * Produces a WHERE clause that checks if the quiz name starts with the specified value.
 * The parameter used in the prepared statement appends a '%' wildcard to the quiz name.
 * </p>
 */
public class QuizNameFilter extends Filter {
    private final String quizName;

    /**
     * Constructs a QuizNameFilter for filtering quizzes by name prefix.
     *
     * @param quizName the starting string of the quiz name to match
     */
    public QuizNameFilter(String quizName) {
        this.quizName = quizName;
    }

    /**
     * Builds the WHERE clause fragment using a LIKE operator.
     *
     * @return the SQL WHERE clause fragment "quiz_name like ?"
     */
    @Override
    public String buildWhereClause() {
        return "quiz_name like ?";
    }

    /**
     * Returns the parameter list with the quiz name prefix and wildcard.
     *
     * @return a list containing a single string parameter for the LIKE clause, e.g. "quizName%"
     */
    @Override
    public List<Object> getParameters() {
        return List.of(quizName + "%");
    }
}
