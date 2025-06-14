package org.ja.model.Filters;

import java.util.List;

public class TagFilter extends Filter {
    private final String tagName;

    public TagFilter(String tagName) {
        this.tagName = tagName;
    }

    @Override
    public String buildWhereClause(){
        return "tag_name = ?";
    }

    @Override
    public List<Object> getParameters() {
        return List.of(tagName);
    }
}
