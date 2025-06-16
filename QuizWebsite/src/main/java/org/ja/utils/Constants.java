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
        public static final String USERS_DAO = "userDao";
        public static final String QUIZZES_DAO = "quizzesDao";
        public static final String HISTORIES_DAO = "historyDao";
        public static final String ANSWERS_DAO = "answersDao";
        public static final String ACHIEVEMENTS_DAO = "achievementsDao";
        public static final String ANNOUNCEMENTS_DAO = "announcementsDao";
        public static final String CATEGORIES_DAO = "categoriesDao";
        public static final String CHALLENGES_DAO = "challengesDao";
        public static final String FRIENDSHIPS_DAO = "friendshipsDao";
        public static final String MATCHES_DAO = "matchesDao";
        public static final String MESSAGE_DAO = "messageDao";
        public static final String QUESTIONS_DAO = "questionDao";
        public static final String QUIZ_RATING_DAO = "quizRatingsDao";
        public static final String QUIZ_TAG_DAO = "quizTagDao";
        public static final String TAGS_DAO = "tagsDao";
        public static final String USER_ACHIEVEMENTS_DAO = "userAchievementsDao";
    }

    public static class SessionAttributes {
        public static final String USER = "current-user";
        public static final String QUIZ = "current-quiz";
        public static final String RESPONSES = "current-responses";
        public static final String ANSWERS = "answers";
    }



}