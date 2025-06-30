use ja_project_db;

-- Users table with basic information, including id, password, username, registration date, photo (might be null)
-- and status: one may be an administrator or a basic user.
create table users (
    user_id bigint primary key auto_increment,
    password_hashed varchar(256) not null,
    salt varchar(256) not null,
    username varchar(64) unique not null,
    registration_date timestamp default current_timestamp,
    user_photo varchar(256),
    user_status enum('administrator', 'user') not null default 'user'
);


-- Categories table.
-- Might include categories like history, geography etc.
create table categories(
    category_id bigint primary key auto_increment,
    category_name varchar(64) unique not null
);


-- Tags table.
-- Might include tags like easy, fun, beginner, timed etc.
create table tags(
    tag_id bigint primary key auto_increment,
    tag_name varchar(64) unique not null
);


-- Quizzes table.
-- Includes main information on quiz including its name, id, description (if any),
-- average rating (contestants might leave ratings and this is their aggregated value),
-- creation date, time limit (represented in minutes, if 0 means no limit), category, creator
-- and question-specific information:
-- Whether the questions appear ordered (as of creation) or randomized, so-called shuffled;
-- Whether the questions should be presented on a single-page or one question per page;
-- Whether (in case of multiple pages) the answers should be corrected immediately or together at once
-- (check constraint is provided so that immediate correction is available only in case of multiple-page option)
create table quizzes(
    quiz_id bigint primary key auto_increment,
    quiz_name varchar(64) unique not null,
    quiz_description text,
    quiz_score int not null,
    average_rating double not null default 0,
    participant_count bigint not null default 0,
    creation_date timestamp default current_timestamp,
    time_limit_in_minutes int not null default 0,
    category_id bigint not null,
    creator_id bigint not null,
    question_order_status enum('ordered', 'randomized') not null default 'ordered',
    question_placement_status enum('one-page', 'multiple-page') not null default 'one-page',
    question_correction_status enum('immediate-correction', 'final-correction')
        not null default 'final-correction',

    check (
        question_placement_status != 'one-page'
        or question_correction_status = 'final-correction'
    ),

    foreign key (creator_id) references users(user_id) on delete cascade,
    foreign key (category_id) references categories(category_id) on delete cascade
);


-- Questions table.
-- Includes basic information like question id, quiz id where this question is present,
-- question itself as a text or as an image_url (for picture-response questions only),
-- question type (we support 7 types of questions) and type-specific details:
-- Whether there are 1 or more answers for a question (In case of multi-answer, for example, one has several answers).
-- If the question is not of type multi-answer or matching, no more than 1 answer is possible;
-- Whether the answers should be presented ordered or unordered. this feature is only available for multi-answer questions.
create table questions(
    question_id bigint primary key auto_increment,
    quiz_id bigint not null,
    question text,
    image_url varchar(256) default null,
    question_type enum('question-response', 'fill-in-the-blank', 'multiple-choice', 'picture-response',
       'multi-answer', 'multi-choice-multi-answers', 'matching') not null,

    num_answers int not null default 1,
    order_status enum('unordered', 'ordered') not null default 'ordered',

    check (
        question is not null or image_url is not null
    ),

    check (
        question_type != 'picture-response'
        or image_url is not null
    ),

    check (
        question_type in ('multi-answer', 'multi-choice-multi-answers', 'matching')
        or num_answers = 1
    ),

    check (
        order_status != 'unordered'
        or question_type = 'multi-answer'
    ),

    foreign key (quiz_id) references quizzes(quiz_id) on delete cascade
);


-- Answers table.
-- Includes answer id, question id, answer text, answer order which is important for ordered multi-answers questions,
-- answer validity boolean which is necessary for choice type of questions (to know whether this particular answer is true).
create table answers(
    answer_id bigint primary key auto_increment,
    question_id bigint not null,
    answer_text text not null,
    answer_order int not null default 1,
    answer_validity boolean not null default true,

    foreign key (question_id) references questions(question_id) on delete cascade,
    unique (question_id, answer_order)
);


-- Matches table.
-- Specifically for matching questions. includes question id and answers to match.
create table matches(
    match_id bigint primary key auto_increment,
    question_id bigint not null,
    left_match text not null,
    right_match text not null,

    foreign key (question_id) references questions(question_id) on delete cascade
);


-- Quiz Tag table.
-- Associates tags to quizzes.
create table quiz_tag(
    quiz_id bigint not null,
    tag_id bigint not null,

    primary key (quiz_id, tag_id),
    foreign key (quiz_id) references quizzes(quiz_id) on delete cascade,
    foreign key (tag_id) references tags(tag_id) on delete cascade
);


-- Friendships table.
-- Shows the relationship between two users. It may be a ng friend request (from the first to the second user) or
-- a two-sided friendship.
create table friendships(
    first_user_id bigint not null,
    second_user_id bigint not null,
    friendship_date timestamp default current_timestamp,
    friendship_status enum('pending', 'friends') default 'pending',

    primary key (first_user_id, second_user_id),
    foreign key (first_user_id) references users(user_id) on delete cascade,
    foreign key (second_user_id) references users(user_id) on delete cascade
);


-- Achievements table.
-- Stores information about achievements. Includes description on each achievement and includes an appropriate image for it.
create table achievements(
    achievement_id bigint primary key auto_increment,
    achievement_name varchar(64) unique not null,
    achievement_description text not null,
    achievement_photo varchar(256)
);


-- User Achievements table.
-- Stores information on the users' achievements.
create table user_achievement(
    user_id bigint not null,
    achievement_id bigint not null,
    achievement_date timestamp default current_timestamp,

    primary key (user_id, achievement_id),
    foreign key (user_id) references users(user_id) on delete cascade,
    foreign key (achievement_id) references achievements(achievement_id) on delete cascade
);

-- Messages table.
-- Stores information about the messages sent from sender to recipient.
-- It includes the message text and send date.
create table messages(
    message_id bigint primary key auto_increment,
    sender_user_id bigint not null,
    recipient_user_id bigint not null,
    message_text text not null,
    message_send_date timestamp default current_timestamp,

    foreign key (sender_user_id) references users(user_id) on delete cascade,
    foreign key (recipient_user_id) references users(user_id) on delete cascade
);

-- Challenges table.
-- Stores information about the challenges sent from sender to recipient.
-- Includes the quiz id that the participant has been challenged to complete.
create table challenges(
    challenge_id bigint primary key auto_increment,
    sender_user_id bigint not null,
    recipient_user_id bigint not null,
    quiz_id bigint not null,

    foreign key (sender_user_id) references users(user_id) on delete cascade,
    foreign key (recipient_user_id) references users(user_id) on delete cascade,
    foreign key (quiz_id) references quizzes(quiz_id) on delete cascade
);

-- History table.
-- Includes information on quizzes completed by a user. Includes the users' score on the test and the completion time.
create table history(
    history_id bigint primary key auto_increment,
    user_id bigint not null,
    quiz_id bigint not null,
    score bigint not null default 0,
    completion_time double not null,
    completion_date timestamp default current_timestamp,

    foreign key (quiz_id) references quizzes(quiz_id) on delete cascade,
    foreign key (user_id) references users(user_id) on delete cascade
);

-- Quiz Rating table.
-- Stores the information on the rating user gave to a certain quiz. Might include a review as well (as a text).
create table quiz_rating(
    quiz_id bigint not null,
    user_id bigint not null,
    rating tinyint not null check (rating between 0 and 5),
    review text,

    primary key (quiz_id, user_id),
    foreign key (quiz_id) references quizzes(quiz_id) on delete cascade,
    foreign key (user_id) references users(user_id) on delete cascade
);

-- Announcements table.
-- Stores the information of announcements including id, the id of an administrator,
-- announcement text and the date when the announcement has been posted.
create table announcements(
    announcement_id bigint primary key auto_increment,
    administrator_id bigint,
    announcement_text text not null,
    creation_date timestamp default current_timestamp,

    foreign key (administrator_id) references users(user_id) on delete cascade
);




