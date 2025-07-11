package org.ja.model.filters;

import java.util.List;

/**
 * Filter that matches entities by a specific tag name.
 * <p>
 * Produces a WHERE clause fragment to filter by exact tag name equality.
 * </p>
 */
public class TagFilter extends Filter {
    private final String tagName;

    /**
     * Constructs a TagFilter to filter by the given tag name.
     *
     * @param tagName the name of the tag to filter on
     */
    public TagFilter(String tagName) {
        this.tagName = tagName;
    }

    /**
     * Builds the WHERE clause fragment for filtering by tag name.
     *
     * @return the SQL WHERE clause fragment "tag_name = ?"
     */
    @Override
    public String buildWhereClause() {
        return "tag_name = ?";
    }

    /**
     * Returns the list of parameters to be used in the prepared statement.
     *
     * @return a list containing the tag name parameter
     */
    @Override
    public List<Object> getParameters() {
        return List.of(tagName);
    }
}
