use ja_project_db;

insert into users (user_id, password_hashed, salt, username, user_status) values
(1, 'hashed_password1', 'salt1', 'Nini', 'administrator'),
(2, 'hashed_password2', 'salt2', 'Sandro', 'user'),
(3, 'hashed_password3', 'salt3', 'Tornike', 'user'),
(4, 'hashed_password1', 'salt1', 'Liza', 'user');

insert into categories (category_id, category_name) values
(1, 'History'),
(2, 'Geography'),
(3, 'Science'),
(4, 'Literature');

insert into tags (tag_id, tag_name) values
(1, 'easy'),
(2, 'fun'),
(3, 'advanced');

insert into quizzes(quiz_id, quiz_name, quiz_description, quiz_score, time_limit_in_minutes, category_id, creator_id,
                    question_order_status, question_placement_status, question_correction_status) values
(1, 'WWII', 'Test your basic world history knowledge!', 5,
10, 1, 4, 'ordered',
'one-page', 'final-correction'),

(2, 'World Capitals', 'Geography challenge quiz!', 5,
5, 2, 2, 'randomized',
'multiple-page', 'immediate-correction'),

(3, 'Periodic Table Challenge', 'Match elements to their symbols', 10,
5, 3, 3, 'randomized',
'one-page', 'final-correction');

insert into questions(quiz_id, question, image_url, question_type, num_answers, order_status) values
(2, 'WWII began in _', null, 'fill-in-the-blank', 1, 'ordered'),
(2, 'Who was the British Prime Minister during most of WWII?', null,'multiple-choice', 1, 'ordered'),
(2, 'List 3 Axis powers during WWII', null, 'multi-answer', 3, 'unordered'),
(2, 'Match the countries to their capitals', null, 'matching', 4, 'ordered'),
(2, null, 'https://upload.wikimedia.org/wikipedia/commons/thumb/d/d9/Flag_of_Norway.svg/2560px-Flag_of_Norway.svg.png','picture-response', 1, 'ordered'),
(2, 'Select all noble gases', null, 'multi-choice-multi-answers', 3, 'ordered'),
(2, 'Name first three elements of the periodic table', null, 'multi-answer', 3, 'ordered'),
(2, 'Match the elements to their atomic number', null, 'matching', 4, 'ordered'),
(2, 'Who discovered penicillin?', null, 'question-response', 1, 'ordered'),
(2, 'WWI began in _', null, 'fill-in-the-blank', 1, 'ordered');

insert into answers(question_id, answer_text, answer_order, answer_validity) values
(1, '1939/39', 1, true),
(2, 'Winston Churchill', 1, true),
(2, 'Neville Chamberlain', 2, false),
(2, 'Franklin D. Roosevelt', 3, false),
(3, 'Germany', 1, true),
(3, 'France', 2, false),
(3, 'Italy', 3, true),
(3, 'Japan', 4, true),
(3, 'USA', 5, false),
(5, 'Oslo/oslo/Paris/Berlin/Reykjavik', 1, true),
(6, 'Neon', 1, true),
(6, 'Helium', 2, true),
(6, 'Argon', 3, true),
(6,  'Oxygen', 4, false),
(6, 'Nitrogen', 5, false),
(7, 'Hydrogen/H', 1, true),
(7, 'Helium/He', 2, true),
(7, 'Lithium/Li', 3, true),
(9, 'Alexander Fleming/Fleming', 1, true),
(10, '1914/14', 1, true);

insert into matches(question_id, left_match, right_match) values
(4, 'France', 'Paris'),
(4, 'Normandy', 'Paris'),
(4, 'Italy', 'Rome'),
(4, 'Spain', 'Madrid'),
(8, 'Hydrogen', '1'),
(8, 'Helium', '2'),
(8, 'Lithium', '3'),
(8, 'Beryllium', '4');

insert into quiz_tag(quiz_id, tag_id) values
(1, 1),
(2, 2),
(3, 2),
(3, 3);

insert into friendships(first_user_id, second_user_id, friendship_status) values
(1, 2, 'friends'),
(1, 3, 'friends'),
(4, 3, 'pending'),
(2, 3, 'pending');

insert into achievements(achievement_name, achievement_description) values
('First step', 'Complete your first quiz'),
('Quiz Addict', 'Complete 10 quizzes'),
('Flawless Victory', 'Complete a quiz with no wrong answers'),
('Quiz Master', 'Score 100% on 5 quizzes'),
('Speed Demon', 'Finish a quiz in under 1 minute');

insert into user_achievement(user_id, achievement_id) values
(1, 1),
(2, 1),
(3, 1),
(4, 5),
(4, 1);

insert into messages(sender_user_id, recipient_user_id, message_text) values
(1, 2, 'I enjoyed your quiz on World Capitals a lot!'),
(4, 3, 'Try my quiz on WWII! you''ll never get a 100'),
(2, 4, 'I got stuck on one question. Tough one!');

insert into challenges(sender_user_id, recipient_user_id, quiz_id) values
(2, 4, 1),
(1, 2, 3),
(4, 3, 2);

insert into history(user_id, quiz_id, score, completion_time) values
(1, 2, 4, 8),
(2, 1, 4, 4),
(4, 3, 2, 0.5);

insert into history(user_id, quiz_id, score, completion_time, completion_date) values
(1, 2, 5, 8, '2025-06-28 14:45:00'),
(1, 2, 3, 7, '2025-06-20 12:00:00'),
(1, 2, 2, 10, '2025-05-28 14:04:00'),
(2, 2, 4, 9, '2025-02-27 10:30:00'),
(2, 2, 4, 8, '2025-06-25 09:00:00'),
(2, 2, 3, 7, '2025-01-22 15:45:00'),
(3, 2, 1, 10, '2025-06-27 11:00:00'),
(3, 2, 1, 8, '2025-06-23 13:15:00'),
(3, 2, 0, 9, '2025-06-20 16:00:00'),
(4, 2, 5, 12, '2025-05-30 09:00:00'),
(4, 2, 5, 10, '2025-03-26 14:00:00'),
(4, 2, 2, 11, '2024-12-24 08:30:00');


insert into quiz_rating(quiz_id, user_id, rating, review) values
(2, 1, 5, 'tough one'),
(1, 2, 4, null),
(3, 4, 5, 'Recommend to anyone interested in science');

insert into announcements(administrator_id, announcement_text) values
(1, 'Scheduled maintenance on July 1st from 2am to 4am. The platform will be temporarily unavailable.'),
(1, 'New quiz category added: literature!');

select * from users;
select * from categories;
select * from tags;
select * from quizzes;
select * from questions;
select * from answers;
select * from matches;
select * from quiz_tag;
select * from friendships;
select * from achievements;
select * from user_achievement;
select * from messages;
select * from challenges;
select * from history;
select * from quiz_rating;
select * from announcements;










































