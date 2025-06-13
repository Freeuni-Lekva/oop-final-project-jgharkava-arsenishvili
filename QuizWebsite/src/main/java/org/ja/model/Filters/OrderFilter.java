package org.ja.model.Filters;

public class OrderFilter extends Filter {
    public static final int INCREASING = 1;
    public static final int DECREASING = 2;

    private final String orderClause;
    private final int orderMonotonicity;

    public OrderFilter(String orderClause, int orderMonotonicity) {
        if(orderMonotonicity != INCREASING && orderMonotonicity != DECREASING)
            throw new IllegalArgumentException("Invalid order monotonicity");
        this.orderMonotonicity = orderMonotonicity;
        this.orderClause = orderClause;
    }

    @Override
    public String buildOrderByClause() {
        return orderClause + (orderMonotonicity == DECREASING ? " desc" : " asc");
    }

}
