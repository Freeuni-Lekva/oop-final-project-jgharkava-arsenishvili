package org.ja.utils;

public class Constants {
    public static class FilterFields {
        public static final String USERNAME = "username-filter";
        public static final String QUIZ_NAME = "quiz-name-filter";
        public static final String CATEGORY = "category-filter";
        public static final String TAG = "tag-filter";
        public static final String ORDER = "order-filter";
    }

    public static class ContextAttributes {
        public static final String USER_DAO = "userDao";
        public static final String QUIZZES_DAO = "quizzesDao";
        public static final String HISTORY_DAO = "historyDao";
        // Add others as needed
    }

    public static class QuestionTypes {
        public static final String RESPONSE_QUESTION = "question-response";
        public static final String FILL_IN_THE_BLANK_QUESTION = "fill-in-the-blank";
        public static final String MULTIPLE_CHOICE_QUESTION = "multiple-choice";
        public static final String PICTURE_RESPONSE_QUESTION = "picture-response";
        public static final String MULTI_ANSWER_QUESTION = "multi-answer";
        public static final String MULTI_CHOICE_MULTI_ANSWER_QUESTION = "multi-choice-multi-answers";
        public static final String MATCHING_QUESTION = "matching";
    }

    public static class OrderTypes {
        public static final String ORDERED = "ordered";
        public static final String UNORDERED = "unordered";
    }

    public static class ResponseFields {

    }
}