package org.ja.model.Filters;

import java.util.ArrayList;
import java.util.List;

public abstract class Filter {
    public String buildWhereClause() {
        return "true";
    }

    public List<Object> getParameters() {
        return new ArrayList<>();
    }

    public String buildOrderByClause() {
        return "true";
    }
}
