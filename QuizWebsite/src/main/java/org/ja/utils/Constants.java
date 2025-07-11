package org.ja.utils;

/**
 * A utility class that contains constants used across the application,
 * including filter field names, context attributes, session keys, and formatters.
 */
public class Constants {


    /**
     * Constants used for filter field names in UI forms or tables.
     */
    public static class FilterFields {
        public static final String USERNAME = "username-filter";
        public static final String QUIZ_NAME = "quiz-name-filter";
        public static final String CATEGORY = "category-filter";
        public static final String TAG = "tag-filter";
        public static final String ORDER = "order-filter";
        public static final String ORDER_PLACEHOLDER = "order-placeholder";
    }


    /**
     * Attribute names used for storing DAO instances in servlet context.
     */
    public static class ContextAttributes {
        public static final String USERS_DAO = "userDao";
        public static final String QUIZZES_DAO = "quizzesDao";
        public static final String HISTORIES_DAO = "historyDao";
        public static final String ADMINISTRATORS_DAO = "administratorsDao";
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


    /**
     * Constants representing the different types of quiz questions.
     */
    public static class QuestionTypes {
        public static final String RESPONSE_QUESTION = "question-response";
        public static final String FILL_IN_THE_BLANK_QUESTION = "fill-in-the-blank";
        public static final String MULTIPLE_CHOICE_QUESTION = "multiple-choice";
        public static final String PICTURE_RESPONSE_QUESTION = "picture-response";
        public static final String MULTI_ANSWER_QUESTION = "multi-answer";
        public static final String MULTI_CHOICE_MULTI_ANSWER_QUESTION = "multi-choice-multi-answers";
        public static final String MATCHING_QUESTION = "matching";
    }


    /**
     * Constants representing ordering types of questions.
     */
    public static class OrderTypes {
        public static final String ORDERED = "ordered";
        public static final String UNORDERED = "unordered";
    }


    /**
     * Constants for how quiz questions are ordered (ordered or randomized).
     */
    public static class QuizQuestionOrderTypes {
        public static final String QUESTIONS_ORDERED = "ordered";
        public static final String QUESTIONS_UNORDERED = "randomized";
    }


    /**
     * Constants for how quiz questions are displayed (one page or multiple pages).
     */
    public static class QuizQuestionPlacementTypes {
        public static final String ONE_PAGE = "one-page";
        public static final String MULTIPLE_PAGE = "multiple-page";
    }


    /**
     * Constants for quiz correction strategies (immediate-correction or final-correction).
     */
    public static class QuizQuestionCorrectionTypes {
        public static final String IMMEDIATE_CORRECTION = "immediate-correction";
        public static final String FINAL_CORRECTION = "final-correction";
    }


    /**
     * Constants used to store attributes in the HTTP session.
     */
    public static class SessionAttributes {
        public static final String MESSAGES_TO_DELETE = "messages-to-delete";

        public static final String USER = "current-user";
        public static final String QUIZ = "current-quiz";
        public static final String RESPONSES = "current-responses";
        public static final String ANSWERS = "answers";
        public static final String QUESTIONS = "questions";
        public static final String CURRENT_QUESTION_INDEX = "question-index";
        public static final String TAGS_TO_ADD = "tags-to-add";
        public static final String TAG_TO_CREATE = "tag-to-create";
        public static final String MATCHES = "current-matches";
        public static final String HAS_QUESTIONS = "hasQuestions";

        public static final String QUIZ_MODE = "quiz-mode";
        public static final String PRACTICE_QUESTIONS_MASTERY_MAP = "practice-question-mastery-map";
    }


    /**
     * Constants for HTTP request parameters.
     */
    public static class RequestParameters {
        public static final String QUIZ_ID = "current-quiz-id";
        public static final String USER_ID = "current-user-id";
    }


    /**
     * Constants representing IDs of default achievements of this project.
     */
    public static class AchievementIds {
        public static final long FIRST_STEP = 1;
        public static final long QUIZ_ADDICT = 2;
        public static final long FLAWLESS_VICTORY = 3;
        public static final long QUIZ_MASTER = 4;
        public static final long SPEED_DEMON = 5;
    }


    /**
     * Constants defining different user types.
     */
    public static class UserTypes {
        public static final String USER = "user";
        public static final String ADMINISTRATOR = "administrator";
    }


    /**
     * Constant image_url when the user photo not found.
     */
    public static String IMAGE_URL_NOT_FOUND = "https://i.imgur.com/VDR01iL.png";


    /**
     * The default maximum number of records to fetch in a single database query.
     * Used to limit result sets and prevent excessive memory usage.
     */
    public static int FETCH_LIMIT = 50;


    /**
     * Enum representing different quiz modes.
     */
    public enum QuizMode {
        TAKING,
        PRACTICE
    }

}
