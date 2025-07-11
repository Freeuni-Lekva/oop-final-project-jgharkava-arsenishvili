package org.ja.model.filters;

import java.util.List;

/**
 * Filter implementation that restricts results to a specific category name.
 * <p>
 * This filter generates a SQL WHERE clause for filtering by exact category name,
 * and provides the corresponding parameter value.
 * </p>
 */
public class CategoryFilter extends Filter {
    private final String categoryName;

    /**
     * Creates a new CategoryFilter for the given category name.
     *
     * @param categoryName the category name to filter by; must not be null
     */
    public CategoryFilter(String categoryName) {
        this.categoryName = categoryName;
    }

    /**
     * Builds the WHERE clause fragment to filter by category name.
     *
     * @return the SQL WHERE clause string "category_name = ?"
     */
    @Override
    public String buildWhereClause() {
        return "category_name = ?";
    }

    /**
     * Returns the list of parameters for the WHERE clause.
     *
     * @return a singleton list containing the category name parameter
     */
    @Override
    public List<Object> getParameters() {
        return List.of(categoryName);
    }
}

