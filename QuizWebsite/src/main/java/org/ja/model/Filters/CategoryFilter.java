package org.ja.model.Filters;

import java.util.List;

public class CategoryFilter extends Filter {
    private final String categoryName;

    public CategoryFilter(String categoryName) {
        this.categoryName = categoryName;
    }
    @Override
    public String buildWhereClause(){
        return "category_name = ?";
    }

    @Override
    public List<Object> getParameters() {
        return List.of(categoryName);
    }
}
