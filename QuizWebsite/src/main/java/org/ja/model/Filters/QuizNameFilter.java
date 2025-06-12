package org.ja.model.Filters;

import java.util.List;

public class QuizNameFilter extends Filter{
    private final String quizName;

    public QuizNameFilter(String quizName) {
        this.quizName = quizName;
    }

    @Override
    public String buildWhereClause() {
        return "quiz_name like ?";
    }

    @Override
    public List<Object> getParameters() {
        return List.of(quizName + "%");
    }
}
