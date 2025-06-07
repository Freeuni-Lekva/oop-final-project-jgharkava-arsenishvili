use ja_project_db;

drop table if exists users;
create table users (
    user_id bigint primary key auto_increment,
    username varchar(64) unique not null,
    registration_date timestamp default current_timestamp,
    user_photo mediumblob,
    user_status enum('administrator', 'user') not null default 'user'
);

drop table if exists categories;
create table categories(
    category_id bigint primary key auto_increment,
    category_name varchar(64) unique not null
);

drop table if exists tags;
create table tags(
    tag_id bigint primary key auto_increment,
    tag_name varchar(64) unique not null
);

drop table if exists quizzes;
create table quizzes(
    quiz_id bigint primary key auto_increment,
    quiz_name varchar(64) unique not null,
    quiz_description text,
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

drop table if exists questions;
create table questions(
    question_id bigint primary key auto_increment,
    quiz_id bigint not null,
    question text not null,
    image_url varchar(256) default null,
    question_type enum('question-response', 'fill-in-the-blank', 'multiple-choice', 'picture-response',
       'multi-answer', 'multi-choice-multi-answers', 'matching') not null,

    num_answers int not null default 1,
    order_status enum('unordered', 'ordered') not null default 'ordered',

    check (
        question_type != 'picture-response'
        or image_url is not null
    ),

    check (
        question_type not in ('multi-answer', 'matching')
        or num_answers > 1
    ),

    check (
        question_type != 'multi-answer'
        or order_status = 'unordered'
    ),

    foreign key (quiz_id) references quizzes(quiz_id) on delete cascade
);


drop table if exists answers;
create table answers(
    answer_id bigint primary key auto_increment,
    question_id bigint not null,
    answer_text text not null,
    answer_order int not null default 1,
    answer_validity boolean not null default true,

    foreign key (question_id) references questions(question_id) on delete cascade
);

drop table if exists matches;
create table matches(
    match_id bigint primary key auto_increment,
    question_id bigint not null,
    left_match text not null,
    right_match text not null,

    foreign key (question_id) references questions(question_id) on delete cascade
);

drop table if exists quiz_tag;
create table quiz_tag(
    quiz_id bigint not null,
    tag_id bigint not null,

    unique(quiz_id, tag_id),
    foreign key (quiz_id) references quizzes(quiz_id) on delete cascade,
    foreign key (tag_id) references tags(tag_id) on delete cascade
);

drop table if exists friendships;
create table friendships(
    first_user_id bigint not null,
    second_user_id bigint not null,
    friendship_date timestamp default current_timestamp,
    friendship_status enum('pending', 'friends'),

    primary key (first_user_id, second_user_id),
    foreign key (first_user_id) references users(user_id) on delete cascade,
    foreign key (second_user_id) references users(user_id) on delete cascade
);

drop table if exists achievements;
create table achievements(
    achievement_id bigint primary key auto_increment,
    achievement_name varchar(64) unique not null,
    achievement_description text,
    achievement_photo mediumblob
);

drop table if exists user_achievement;
create table user_achievement(
    user_id bigint not null,
    achievement_id bigint not null,
    achievement_date timestamp default current_timestamp,

    primary key (user_id, achievement_id),
    foreign key (user_id) references users(user_id) on delete cascade,
    foreign key (achievement_id) references achievements(achievement_id) on delete cascade
);

drop table if exists messages;
create table messages(
    message_id bigint primary key auto_increment,
    sender_user_id bigint not null,
    recipient_user_id bigint not null,
    message_text text not null,
    message_send_date timestamp default current_timestamp,

    foreign key (sender_user_id) references users(user_id),
    foreign key (recipient_user_id) references users(user_id)
);

drop table if exists challenges;
create table challenges(
    challenge_id bigint primary key auto_increment,
    sender_user_id bigint not null,
    recipient_user_id bigint not null,
    quiz_id bigint not null,

    foreign key (sender_user_id) references users(user_id),
    foreign key (recipient_user_id) references users(user_id),
    foreign key (quiz_id) references quizzes(quiz_id)
);

drop table if exists history;
create table history(
    history_id bigint primary key auto_increment,
    user_id bigint not null,
    quiz_id bigint not null,
    score double not null default 0,
    completion_time bigint not null,

    foreign key (quiz_id) references quizzes(quiz_id) on delete cascade,
    foreign key (user_id) references users(user_id) on delete cascade
);

drop table if exists quiz_rating;
create table quiz_rating(
    quiz_id bigint not null,
    user_id bigint not null,
    rating tinyint not null check (rating between 0 and 5),
    review text,

    primary key (quiz_id, user_id),
    foreign key (quiz_id) references quizzes(quiz_id) on delete cascade,
    foreign key (user_id) references users(user_id) on delete cascade
)




