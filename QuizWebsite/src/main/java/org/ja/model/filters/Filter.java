package org.ja.model.filters;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for building SQL query filter components.
 * <p>
 * Provides default implementations for building WHERE and ORDER BY clauses
 * and retrieving the corresponding parameters.
 * Subclasses should override these methods to provide specific filtering logic.
 * </p>
 */
public abstract class Filter {

    /**
     * Builds the SQL WHERE clause fragment for this filter.
     *
     * @return a SQL WHERE clause fragment as a String.
     *         Default implementation returns "true" to represent no filtering.
     */
    public String buildWhereClause() {
        return "true";
    }

    /**
     * Returns the list of parameters to be used in a prepared statement
     * corresponding to the WHERE clause placeholders.
     *
     * @return a List of parameter objects; empty by default.
     */
    public List<Object> getParameters() {
        return new ArrayList<>();
    }

    /**
     * Builds the SQL ORDER BY clause fragment for this filter.
     *
     * @return a SQL ORDER BY clause fragment as a String.
     *         Default implementation returns "true" which is not a valid ORDER BY clause,
     *         so subclasses should override this method accordingly.
     */
    public String buildOrderByClause() {
        return "true";
    }
}
