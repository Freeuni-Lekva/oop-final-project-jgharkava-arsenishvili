package org.ja.model.filters;

/**
 * Represents a filter for ordering query results based on a specified column and order direction.
 * <p>
 * This filter is used to generate an SQL ORDER BY clause fragment.
 * </p>
 * <p>
 * The order direction can be either increasing (ascending) or decreasing (descending).
 * </p>
 */
public class OrderFilter extends Filter {
    /** Constant indicating ascending (increasing) order. */
    public static final int INCREASING = 1;

    /** Constant indicating descending (decreasing) order. */
    public static final int DECREASING = 2;

    private final String orderClause;
    private final int orderMonotonicity;

    /**
     * Constructs an OrderFilter with the specified order clause and monotonicity.
     *
     * @param orderClause the name of the column or expression to order by (e.g., "quiz_name")
     * @param orderMonotonicity one of {@link #INCREASING} or {@link #DECREASING}
     * @throws IllegalArgumentException if {@code orderMonotonicity} is not valid
     */
    public OrderFilter(String orderClause, int orderMonotonicity) {
        if (orderMonotonicity != INCREASING && orderMonotonicity != DECREASING) {
            throw new IllegalArgumentException("Invalid order monotonicity");
        }
        this.orderMonotonicity = orderMonotonicity;
        this.orderClause = orderClause;
    }

    /**
     * Builds the SQL ORDER BY clause fragment for this filter.
     *
     * @return a string representing the order by clause, such as "quiz_name asc" or "quiz_name desc"
     */
    @Override
    public String buildOrderByClause() {
        return orderClause + (orderMonotonicity == DECREASING ? " desc" : " asc");
    }
}
