use ja_project_db;


insert into users (user_id, password_hashed, salt, username, user_status, user_photo) values
(1, 'ayyD4E76rFdkB9O8E3UM2aZEh9KGIogi72agOHR+pXw=', 'IdvsWXtGSMRxtKBreJkQnA==', 'Nini', 'administrator', 'https://i.imgur.com/5PbPhtO.png'),
(2, 'ayyD4E76rFdkB9O8E3UM2aZEh9KGIogi72agOHR+pXw=', 'IdvsWXtGSMRxtKBreJkQnA==', 'Sandro', 'user', 'https://i.imgur.com/g5y8rN3.png'),
(3, 'ayyD4E76rFdkB9O8E3UM2aZEh9KGIogi72agOHR+pXw=', 'IdvsWXtGSMRxtKBreJkQnA==', 'Tornike', 'user', 'https://i.imgur.com/C1WXE7I.png'),
(4, 'ayyD4E76rFdkB9O8E3UM2aZEh9KGIogi72agOHR+pXw=', 'IdvsWXtGSMRxtKBreJkQnA==', 'Liza', 'user', 'https://i.imgur.com/ajmeQC0.png');


insert into categories (category_id, category_name) values
(1, 'History'),
(2, 'Geography'),
(3, 'Science'),
(4, 'Literature');


insert into tags (tag_id, tag_name) values
(1, 'trivia'),
(2, 'brain-teasers'),
(3, 'classic'),
(4, 'modern'),
(5, 'pop-quiz'),
(6, 'timed'),
(7, 'multiplayer'),
(8, 'educational'),
(9, 'puzzle'),
(10, 'strategy');


insert into quizzes(quiz_id, quiz_name, quiz_description, quiz_score, average_rating, participant_count, creation_date,
                    time_limit_in_minutes, category_id, creator_id, question_order_status,
                    question_placement_status, question_correction_status) values
(1, 'WWII', 'Test your basic world history knowledge!', 5, 2.5, 4,
'2020-04-01 19:22:11', 10, 1, 4, 'ordered',
'one-page', 'final-correction'),

(2, 'World Capitals', 'Geography challenge quiz!', 5, 3.5, 4,
 '2018-07-05 20:55:15',5, 2, 1, 'randomized',
'multiple-page', 'immediate-correction'),

(3, 'Periodic Table Challenge', 'Match elements to their symbols', 10, 3.5,
 4, '2025-06-30 22:48:05', 3, 3, 3, 'randomized',
'one-page', 'final-correction'),

(4, 'Literature Quiz', 'Test your knowledge on classic and modern literature!', 6, 3,
 4, '2024-07-10 15:30:00', 7, 4,4, 'ordered',
 'multiple-page', 'immediate-correction');


insert into questions(quiz_id, question, image_url, question_type, num_answers, order_status) values
(1, 'WWII began in _', null, 'fill-in-the-blank', 1, 'ordered'),
(1, 'Who was the British Prime Minister during most of WWII?', null,'multiple-choice', 1, 'ordered'),
(1, 'List 3 Axis powers during WWII', null, 'multi-answer', 3, 'unordered'),
(2, 'Match the countries to their capitals', null, 'matching', 4, 'ordered'),
(2, null, 'https://upload.wikimedia.org/wikipedia/commons/thumb/d/d9/Flag_of_Norway.svg/2560px-Flag_of_Norway.svg.png','picture-response', 1, 'ordered'),
(3, 'Select all noble gases', null, 'multi-choice-multi-answers', 3, 'ordered'),
(3, 'Name first three elements of the periodic table', null, 'multi-answer', 3, 'ordered'),
(3, 'Match the elements to their atomic number', null, 'matching', 4, 'ordered'),
(4, 'Who wrote Pride and Prejudice?', null, 'question-response', 1, 'ordered'),
(4, 'Which of the following are works by William Shakespeare?', null, 'multi-choice-multi-answers', 3, 'ordered'),
(4, '''It was the best of times, it was the worst of times...'' is the opening line of _ by Charles Dickens.', null, 'fill-in-the-blank', 1, 'ordered'),
(4,'This image depicts the legendary author of two ancient Greek epics. Who is he?',
 'https://upload.wikimedia.org/wikipedia/commons/1/1c/Homer_British_Museum.jpg',
 'picture-response', 1, 'ordered');


insert into answers(question_id, answer_text, answer_order, answer_validity) values
(1, '1939¶39', 1, true),
(2, 'Winston Churchill', 1, true),
(2, 'Neville Chamberlain', 2, false),
(2, 'Franklin D. Roosevelt', 3, false),
(3, 'Germany', 1, true),
(3, 'Italy', 2, true),
(3, 'Japan', 3, true),
(5, 'oslo', 1, true),
(6, 'Neon', 1, true),
(6, 'Helium', 2, true),
(6, 'Argon', 3, true),
(6,  'Oxygen', 4, false),
(6, 'Nitrogen', 5, false),
(7, 'Hydrogen¶H', 1, true),
(7, 'Helium¶He', 2, true),
(7, 'Lithium¶Li', 3, true),
(9, 'Jane Austen¶Austen', 1, true),
(10, 'Hamlet', 1, true),
(10, 'Othello', 2, true),
(10, 'The Canterbury Tales', 3, false),
(10, 'Macbeth', 4, true),
(11, 'A Tale of Two Cities', 1, true),
(12, 'Homer', 1, true);


insert into matches(question_id, left_match, right_match) values
(4, 'France', 'Paris'),
(4, 'Germany', 'Berlin'),
(4, 'Italy', 'Rome'),
(4, 'Spain', 'Madrid'),
(8, 'Hydrogen', '1'),
(8, 'Helium', '2'),
(8, 'Lithium', '3'),
(8, 'Beryllium', '4');


insert into quiz_tag(quiz_id, tag_id) values
(1, 1),
(1, 3),
(1, 8),
(2, 1),
(2, 8),
(3, 2),
(3, 8),
(3, 9),
(4, 1),
(4, 3),
(4, 5);


insert into friendships(first_user_id, second_user_id, friendship_status) values
(1, 2, 'friends'),
(1, 3, 'friends'),
(1, 4, 'pending'),
(2, 3, 'pending'),
(2, 4, 'pending'),
(3, 4, 'friends');


insert into achievements(achievement_name, achievement_description, achievement_photo) values
('First step', 'Complete your first quiz', 'https://i.imgur.com/13UgB9t.png'),
('Quiz Addict', 'Complete 10 quizzes', 'https://i.imgur.com/uDXCVba.png'),
('Flawless Victory', 'Complete a quiz with no wrong answers', 'https://i.imgur.com/koveqao.png'),
('Quiz Master', 'Score 100% on 5 quizzes', 'https://i.imgur.com/T0PqbI7.png'),
('Speed Demon', 'Finish a quiz in under 1 minute', 'https://i.imgur.com/RXU33E6.png');


insert into user_achievement(user_id, achievement_id) values
(1, 1),
(2, 1),
(3, 1),
(3, 3),
(3, 5),
(4, 1),
(4, 5);


insert into messages(sender_user_id, recipient_user_id, message_text) values
(4, 3, 'Try my quiz on WWII! you''ll never get a 100'),
(3, 4, 'Your history quiz was really detailed, thanks!'),
(2, 1, 'Nice mix of easy and hard questions in your quiz.'),
(3, 1, 'Fun quiz! Will recommend to friends.'),
(1, 4, 'Can you add more quizzes on pop culture?'),
(4, 1, 'Thanks for the geography quiz, very informative!');


insert into challenges(sender_user_id, recipient_user_id, quiz_id) values
(4, 3, 1),
(3, 1, 2),
(4, 1, 4),
(3, 4, 1),
(1, 2,3),
(1, 3, 2);


insert into history(user_id, quiz_id, score, completion_time, completion_date) values
(1, 4, 5, 6, '2025-06-28 14:45:00'),
(1, 2, 3, 5.7, '2019-06-20 12:00:00'),
(1, 3, 2, 2.9, '2024-05-28 14:04:00'),
(2, 2, 4, 3, '2025-02-27 10:30:00'),
(2, 3, 9, 2.1, '2025-02-19 10:30:00'),
(2, 2, 4, 2.5, '2025-06-25 09:00:00'),
(2, 2, 3, 2.8, '2025-01-22 15:45:00'),
(3, 1, 4, 9.8, '2025-01-30 15:45:00'),
(3, 2, 5, 2, '2025-06-27 11:00:00'),
(3, 4, 5, 6.5, '2025-06-23 13:15:00'),
(3, 2, 3, 0.8, '2025-06-20 16:00:00'),
(4, 4, 5, 0.9, '2025-05-30 09:00:00'),
(4, 1, 3, 8, '2025-03-26 14:00:00'),
(4, 2, 2, 4.3, '2024-12-24 08:30:00');


insert into quiz_rating(quiz_id, user_id, rating, review) values
(1, 2, 3, 'Could be better'),
(1, 3, 4, 'Interesting quiz'),
(1, 4, 1, 'Too easy for me'),
(2, 2, 4, null),
(2, 3, 3, 'Average difficulty'),
(3, 2, 5, 'Loved it!'),
(3, 4, 4, 'Recommend to anyone interested in science'),
(4, 1, 1, 'Too hard'),
(4, 2, 2, 'Could improve'),
(4, 4, 5, 'Perfect quiz!');


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










































