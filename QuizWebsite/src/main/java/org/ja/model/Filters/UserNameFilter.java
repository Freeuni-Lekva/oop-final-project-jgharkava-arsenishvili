package org.ja.model.Filters;

import java.util.List;

public class UserNameFilter extends Filter {
    private final String username;

    public UserNameFilter(String username) {
        this.username = username;
    }

    @Override
    public String buildWhereClause() {
        return "username like ?";
    }

    @Override
    public List<Object> getParameters() {
        return List.of(username + "%");
    }
}
