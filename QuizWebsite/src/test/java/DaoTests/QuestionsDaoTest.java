package DaoTests;

import org.apache.commons.dbcp2.BasicDataSource;
import org.ja.dao.*;
import org.ja.model.CategoriesAndTags.Category;
import org.ja.model.CategoriesAndTags.Tag;
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


public class QuestionsDaoTest {

    private BasicDataSource basicDataSource;
    private QuestionDao dao;
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
        basicDataSource.setUsername("sa"); // h2 username
        basicDataSource.setPassword(""); // h2 password

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
            dao=new QuestionDao(basicDataSource);
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
        }
    }
    @Test
    public void testInsert() {
        qu11=new Question(1, 1, "historyQuizQuestion1", "sth.jpg",
                "question-response", 50,"ordered");
        qu12=new Question(42, 1, "historyQuizQuestion2", "sth2.jpg",
                "question-response", 2,"ordered");
        qu13=new Question(1, 1, "historyQuizQuestion3", "null",
                "question-response", 50,"ordered");
        dao.insertQuestion(qu11);
        assertEquals(1, dao.getCount());
        assertEquals(1, qu11.getQuestionId());
        dao.insertQuestion(qu12);
        assertEquals(2, qu12.getQuestionId());
        dao.insertQuestion(qu13);
        assertEquals(3, dao.getCount());
        assertEquals(3, qu13.getQuestionId());
        assertTrue(dao.contains(qu13));
        dao.insertQuestion(qu13);
        assertEquals(3, dao.getCount());
    }
    @Test
    public void testUpdate() {
        testInsert();
        qu13.setImageUrl("newImage.jpg");
        dao.updateQuestion(qu13);
        assertEquals("newImage.jpg", dao.getQuestionById(qu13.getQuestionId()).getImageUrl());
    }
    @Test
    public void testRemove(){
        testInsert();
        dao.removeQuestion(1);
        assertEquals(2, dao.getCount());
        assertFalse(dao.contains(qu11));
    }
    @Test
    public void testQuizQuestions(){
        testInsert();
        Question qu21=new Question(1, 2, "historyQuizQuestion3", "null",
                "question-response", 50,"ordered");
        dao.insertQuestion(qu21);
        ArrayList<Question> arr=dao.getQuizQuestions(1);
        assertEquals(3, arr.size());
        assertFalse(arr.contains(qu21));
        assertTrue(arr.contains(qu11));
        assertTrue(arr.contains(qu12));
        assertTrue(arr.contains(qu13));
    }
}
