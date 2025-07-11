package org.ja.model.filters;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a composite filter that combines multiple filters using a logical AND.
 * <p>
 * This filter builds a WHERE clause by joining the WHERE clauses of all contained filters
 * with the SQL AND operator. It also combines the parameters and ORDER BY clauses
 * of its contained filters.
 * </p>
 * <p>
 * If no filters are added, the WHERE clause defaults to "true", representing no filtering.
 * Similarly, the ORDER BY clause defaults to "true" when empty, which may need overriding.
 * </p>
 */
public class AndFilter extends Filter {
    private final List<Filter> filters;

    /**
     * Constructs an empty AndFilter with no contained filters.
     */
    public AndFilter() {
        filters = new ArrayList<>();
    }

    /**
     * Adds a filter to this composite AND filter.
     *
     * @param filter the Filter to add; should not be null
     */
    public void addFilter(Filter filter) {
        filters.add(filter);
    }

    /**
     * Builds the WHERE clause by combining all contained filters' WHERE clauses with AND.
     * Filters returning the default "true" clause are ignored.
     *
     * @return a SQL WHERE clause string that combines all filters, or "true" if none are present
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

        return clauses.isEmpty() ? "true" : String.join(" and ", clauses);
    }

    /**
     * Builds the ORDER BY clause by concatenating all contained filters' ORDER BY clauses,
     * separated by commas. Filters returning the default "true" clause are ignored.
     *
     * @return a SQL ORDER BY clause string combining all contained filters, or "true" if none
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
     * Collects and returns all parameters from the contained filters in order.
     *
     * @return a List of parameter values for the combined WHERE clause
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
