package org.ja.model.filters;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a logical OR combination of multiple {@link Filter} instances.
 * <p>
 * This filter constructs a WHERE clause by combining the WHERE clauses
 * of its child filters with the "OR" SQL operator.
 * It also combines their ORDER BY clauses and parameters accordingly.
 * </p>
 * <p>
 * If no filters are added, the WHERE clause defaults to "true" (no filtering).
 * </p>
 */
public class OrFilter extends Filter {
    private final List<Filter> filters;

    /**
     * Constructs an empty OrFilter.
     */
    public OrFilter() {
        filters = new ArrayList<>();
    }

    /**
     * Adds a child filter to be OR'ed in the WHERE clause.
     *
     * @param filter the filter to add
     */
    public void addFilter(Filter filter) {
        filters.add(filter);
    }

    /**
     * Builds the SQL WHERE clause fragment by joining the WHERE clauses of all
     * child filters with "OR".
     *
     * @return a string representing the combined WHERE clause, or "true" if no filters exist
     */
    @Override
    public String buildWhereClause() {
        List<String> clauses = new ArrayList<>();

        for (Filter filter : filters) {
            String clause = filter.buildWhereClause();
            if (!"true".equals(clause)) {
                clauses.add("(" + clause + ")");
            }
        }

        return clauses.isEmpty() ? "true" : String.join(" or ", clauses);
    }

    /**
     * Builds the SQL ORDER BY clause fragment by joining the ORDER BY clauses
     * of all child filters with commas.
     *
     * @return a string representing the combined ORDER BY clause, or "true" if no orders exist
     */
    @Override
    public String buildOrderByClause() {
        List<String> orders = new ArrayList<>();

        for (Filter filter : filters) {
            String order = filter.buildOrderByClause();
            if (!"true".equals(order)) {
                orders.add(order);
            }
        }

        return orders.isEmpty() ? "true" : String.join(", ", orders);
    }

    /**
     * Returns the combined list of parameters from all child filters.
     *
     * @return a list of parameters for use in a prepared SQL statement
     */
    @Override
    public List<Object> getParameters() {
        List<Object> parameters = new ArrayList<>();

        for (Filter filter : filters) {
            parameters.addAll(filter.getParameters());
        }

        return parameters;
    }
}
