package DaoTests;

import org.apache.commons.dbcp2.BasicDataSource;
import org.ja.dao.*;
import org.ja.model.CategoriesAndTags.Category;
import org.ja.model.CategoriesAndTags.Tag;
import org.ja.model.OtherObjects.Answer;
import org.ja.model.OtherObjects.Match;
import org.ja.model.quiz.Quiz;
import org.ja.model.quiz.question.Question;
import org.ja.model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.*;

/*
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

 */

public class MatchesDaoTest {
    private BasicDataSource basicDataSource;
    private MatchesDao dao;
    private QuestionDao questionsDao;
    private CategoriesDao categoriesDao;
    private UsersDao usersDao;
    private QuizzesDao quizzesDao;
    Question qu11;
    Question qu12;
    Question qu13;

    @BeforeEach
    public void setUp() throws Exception {
        basicDataSource = new BasicDataSource();
        basicDataSource.setUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        basicDataSource.setUsername("sa");
        basicDataSource.setPassword("");

        try (
                Connection connection = basicDataSource.getConnection();
                Statement statement = connection.createStatement()
        ) {
            // Read SQL file
            StringBuilder sqlBuilder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader("database/drop.sql"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sqlBuilder.append(line).append("\n");
                }
            }

            // Split and execute SQL commands (if there are multiple)
            String[] sqlStatements = sqlBuilder.toString().split(";");
            for (String sql : sqlStatements) {
                if (!sql.trim().isEmpty() && !sql.trim().startsWith("use")) {
                    statement.execute(sql.trim());
                }
            }
        }
        try (
                Connection connection = basicDataSource.getConnection();
                Statement statement = connection.createStatement()
        ) {
            // Read SQL file
            StringBuilder sqlBuilder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader("database/schema.sql"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sqlBuilder.append(line).append("\n");
                }
            }

            // Split and execute SQL commands (if there are multiple)
            String[] sqlStatements = sqlBuilder.toString().split(";");
            for (String sql : sqlStatements) {
                if (!sql.trim().isEmpty() && !sql.trim().startsWith("use")) {
                    statement.execute(sql.trim());
                }
            }
            questionsDao=new QuestionDao(basicDataSource);
            usersDao=new UsersDao(basicDataSource);
            categoriesDao=new CategoriesDao(basicDataSource);
            quizzesDao=new QuizzesDao(basicDataSource);

            User sandro=new User(1, "Sandro", "123", "2025-6-14",null, "sth.jpg", "administrator");
            usersDao.insertUser(sandro);
            User tornike=new User(12, "Tornike", "123", "2025-6-14",null, "sth.jpg", "administrator");
            usersDao.insertUser(tornike);
            User liza=new User(12, "Liza", "123", "2025-6-14",null, "sth.jpg", "administrator");
            usersDao.insertUser(liza);
            User nini=new User(12, "Nini", "123", "2025-6-14",null, "sth.jpg", "administrator");
            usersDao.insertUser(nini);


            Category h=new Category(1, "history");
            categoriesDao.insertCategory(h);
            Category g=new Category(2, "geography");
            categoriesDao.insertCategory(g);
            Category m=new Category(3, "maths");
            categoriesDao.insertCategory(m);
            Category p=new Category(4, "physics");
            categoriesDao.insertCategory(p);
            Category a=new Category(5, "arts");
            categoriesDao.insertCategory(a);

            Quiz q1 = new Quiz(1, "historyQuiz","description",
                    10, 7.3, 0,
                    new Timestamp(123),12, 1, 1,
                    "randomized", "one-page", "final-correction");
            Quiz q2 = new Quiz(2, "geoQuiz","description",
                    10, 7.3, 0,
                    new Timestamp(123),12, 2, 1,
                    "randomized", "one-page", "final-correction");
            Quiz q3 = new Quiz(3, "mathsQuiz","description",
                    10, 7.3, 0,
                    new Timestamp(123),12, 3, 1,
                    "randomized", "one-page", "final-correction");
            Quiz q4 = new Quiz(4, "physicsQuiz","description",
                    10, 7.3, 0,
                    new Timestamp(123),12, 3, 1,
                    "randomized", "one-page", "final-correction");
            quizzesDao.insertQuiz(q1);
            quizzesDao.insertQuiz(q2);
            quizzesDao.insertQuiz(q3);
            quizzesDao.insertQuiz(q4);
            qu11=new Question(1, 1, "Which of these wasn't a president", "sth.jpg",
                    "question-response", 50,"ordered");
            qu12=new Question(42, 1, "historyQuizQuestion2", "sth2.jpg",
                    "question-response", 2,"ordered");
            qu13=new Question(1, 1, "historyQuizQuestion3", "null",
                    "question-response", 50,"ordered");
            Question qu21=new Question(1, 2, "historyQuizQuestion3", "null",
                    "question-response", 50,"ordered");
            questionsDao.insertQuestion(qu11);
            questionsDao.insertQuestion(qu12);
            questionsDao.insertQuestion(qu13);
            questionsDao.insertQuestion(qu21);
            dao=new MatchesDao(basicDataSource);

            m1=new Match(3,1,"france", "paris");
            m2=new Match(3,1,"germany", "berlin");
            m3=new Match(3,1,"italy", "rome");
            m4=new Match(3,2,"Georgia", "Tbilisi");
        }
    }
    private Match m1;
    private Match m2;
    private Match m3;
    private Match m4;
    @Test
    public void testInsert() {
        dao.insertMatch(m1);
        assertEquals(1, m1.getMatchId());
        dao.insertMatch(m2);
        dao.insertMatch(m3);
        assertEquals(3, dao.getCount());
        assertTrue(dao.contains(m1));
        assertTrue(dao.contains(m2));
        assertFalse(dao.contains(m4));
    }
    @Test
    public void testRemove() {
        dao.insertMatch(m1);
        dao.insertMatch(m2);
        dao.insertMatch(m3);
        dao.removeMatch(2);
        assertEquals(2, dao.getCount());
        assertFalse(dao.contains(m2));
        dao.removeMatch(2);
        assertEquals(2, dao.getCount());

    }
    @Test
    public void testQuestionMatches(){
        dao.insertMatch(m1);
        dao.insertMatch(m2);
        dao.insertMatch(m3);        ArrayList<Match> arr=dao.getQuestionMatches(1);
        assertEquals(3, arr.size());
        assertTrue(arr.contains(m1));
        assertTrue(arr.contains(m2));
        assertTrue(arr.contains(m3));
        assertFalse(arr.contains(m4));
    }
}
