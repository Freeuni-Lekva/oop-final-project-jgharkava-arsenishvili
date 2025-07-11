package org.ja.model.filters;

import java.util.List;

/**
 * Filter that matches entities by username prefix.
 * <p>
 * Produces a WHERE clause fragment to filter usernames that start with the given prefix.
 * </p>
 */
public class UserNameFilter extends Filter {
    private final String username;

    /**
     * Constructs a UserNameFilter to filter by usernames starting with the specified prefix.
     *
     * @param username the prefix of the username to filter on
     */
    public UserNameFilter(String username) {
        this.username = username;
    }

    /**
     * Builds the WHERE clause fragment for filtering by username prefix.
     *
     * @return the SQL WHERE clause fragment "username like ?"
     */
    @Override
    public String buildWhereClause() {
        return "username like ?";
    }

    /**
     * Returns the list of parameters to be used in the prepared statement.
     * The parameter is the username prefix with a trailing wildcard.
     *
     * @return a list containing the username prefix parameter followed by "%"
     */
    @Override
    public List<Object> getParameters() {
        return List.of(username + "%");
    }
}
