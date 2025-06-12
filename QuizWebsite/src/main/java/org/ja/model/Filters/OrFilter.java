package org.ja.model.Filters;

import java.util.ArrayList;
import java.util.List;

public class OrFilter extends Filter {
    private final List<Filter> filters;

    public OrFilter() {
        filters = new ArrayList<>();
    }

    public void addFilter(Filter filter) {
        filters.add(filter);
    }

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

    @Override
    public List<Object> getParameters() {
        List<Object> parameters = new ArrayList<>();

        for (Filter filter : filters) {
            parameters.addAll(filter.getParameters());
        }

        return parameters;
    }
}
