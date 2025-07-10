use ja_project_db;

insert into users (user_id, password_hashed, salt, username, user_status) values
(5, '8jHD92kdslPp39jS9kdJskLDs092==', 'ASDJasdlk123ASD=', 'Mariam', 'administrator'),
(6, '8jHD92kdslPp39jS9kdJskLDs092==', 'ASDJasdlk123ASD=', 'Gio', 'user'),
(7, '8jHD92kdslPp39jS9kdJskLDs092==', 'ASDJasdlk123ASD=', 'Ani', 'user'),
(8, '8jHD92kdslPp39jS9kdJskLDs092==', 'ASDJasdlk123ASD=', 'Beka', 'user');

insert into categories (category_id, category_name) values
(5, 'Math'),
(6, 'Movies'),
(7, 'Technology'),
(8, 'Art');

insert into tags (tag_id, tag_name) values
(4, 'timed'),
(5, 'tricky'),
(6, 'visual');

insert into quizzes(quiz_id, quiz_name, quiz_description, quiz_score, time_limit_in_minutes, category_id, creator_id,
                    question_order_status, question_placement_status, question_correction_status) values
(4, 'Basic Algebra', 'Challenge your math basics', 2, 10, 5, 6, 'ordered', 'one-page', 'final-correction'),
(5, 'Oscar Winners', 'Guess Oscar-winning movies', 2, 7, 6, 5, 'randomized', 'multiple-page', 'immediate-correction'),
(6, 'Tech Milestones', 'Identify key events in tech history', 10, 0, 7, 7, 'ordered', 'one-page', 'final-correction');

insert into questions(quiz_id, question, image_url, question_type, num_answers, order_status) values
(4, 'What is the result of 5 + 7?', null, 'question-response', 1, 'ordered'),
(4, 'Solve for x: 2x = 10', null, 'fill-in-the-blank', 1, 'ordered'),
(5, 'Which movie won Best Picture in 2022?', null, 'multiple-choice', 1, 'ordered'),
(5, 'Identify the film from the poster', 'https://example.com/poster.jpg', 'picture-response', 1, 'ordered'),
(6, 'List 3 tech companies founded before 2000', null, 'multi-answer', 3, 'unordered'),
(6, 'Select all programming languages', null, 'multi-choice-multi-answers', 3, 'ordered'),
(6, 'Match the inventor to the invention', null, 'matching', 4, 'ordered');

insert into answers(question_id, answer_text, answer_order, answer_validity) values
(1, '12', 1, true),
(2, '5', 1, true),
(3, 'CODA', 1, true),
(3, 'Dune', 2, false),
(3, 'The Power of the Dog', 3, false),
(4, 'Star Wars', 1, true),
(5, 'Apple', 1, true),
(5, 'Facebook', 2, true),
(5, 'Microsoft', 3, true),
(6, 'Python', 1, true),
(6, 'HTML', 2, false),
(6, 'C++', 3, true),
(6, 'Java', 4, true),
(6, 'Photoshop', 5, false);

insert into matches(question_id, left_match, right_match) values
(7, 'Thomas Edison', 'Light Bulb'),
(7, 'Alexander Graham Bell', 'Telephone'),
(7, 'Tim Berners-Lee', 'World Wide Web'),
(7, 'James Watt', 'Steam Engine');

insert into quiz_tag(quiz_id, tag_id) values
(4, 4),
(5, 5),
(6, 6),
(6, 4);

insert into friendships(first_user_id, second_user_id, friendship_status) values
(5, 6, 'friends'),
(6, 7, 'pending'),
(7, 8, 'friends'),
(5, 8, 'pending');

insert into achievements(achievement_name, achievement_description) values
('Brainstormer', 'Finish 5 quizzes with above 80%'),
('Perfectionist', 'Score full points in 3 quizzes'),
('Early Bird', 'Complete a quiz before 8am'),
('Veteran', 'Be registered for over a year');

insert into user_achievement(user_id, achievement_id) values
(5, 1),
(6, 2),
(7, 3),
(8, 4),
(6, 4);

insert into messages(sender_user_id, recipient_user_id, message_text) values
(5, 6, 'Let’s collaborate on a math quiz!'),
(7, 8, 'Your Oscar quiz was really good!'),
(8, 6, 'Try the tech challenge I just created!');

insert into challenges(sender_user_id, recipient_user_id, quiz_id) values
(6, 8, 4),
(5, 7, 6),
(7, 5, 5);

insert into history(user_id, quiz_id, score, completion_time, completion_date) values
(5, 4, 6, 9, '2025-07-01 10:15:00'),
(6, 5, 4, 6, '2025-06-30 11:45:00'),
(7, 6, 5, 10, '2025-07-02 12:00:00'),
(8, 6, 3, 7, '2025-07-03 13:30:00'),
(8, 5, 4, 8, '2025-06-29 09:00:00');

insert into quiz_rating(quiz_id, user_id, rating, review) values
(4, 6, 4, 'Nice and challenging'),
(5, 7, 5, 'Loved the movie questions!'),
(6, 8, 5, 'Very informative');

insert into announcements(administrator_id, announcement_text) values
(5, 'Join the upcoming monthly quiz competition starting July 15th!'),
(5, 'Check out our new category: Tech!');

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