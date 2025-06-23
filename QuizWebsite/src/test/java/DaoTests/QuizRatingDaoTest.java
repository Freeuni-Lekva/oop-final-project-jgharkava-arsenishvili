package DaoTests;

import org.apache.commons.dbcp2.BasicDataSource;
import org.ja.dao.*;
import org.ja.model.CategoriesAndTags.Category;
import org.ja.model.CategoriesAndTags.Tag;
import org.ja.model.OtherObjects.*;
import org.ja.model.quiz.Quiz;
import org.ja.model.quiz.question.Question;
import org.ja.model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.*;


public class QuizRatingDaoTest {

    private BasicDataSource basicDataSource;
    private QuizRatingsDao dao;
    private UsersDao usersDao;
    private QuizzesDao quizzesDao;
    private CategoriesDao categoriesDao;
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

            dao=new QuizRatingsDao(basicDataSource);
            finishSetup();
        }
    }

    private void finishSetup() throws SQLException, NoSuchAlgorithmException {
        usersDao=new UsersDao(basicDataSource);
        User sandro=new User(1, "Sandro", "123", "2025-6-14",null, "sth.jpg", "administrator");
        usersDao.insertUser(sandro);
        User tornike=new User(2, "Tornike", "123", "2025-6-14",null, "sth.jpg", "administrator");
        usersDao.insertUser(tornike);
        User liza=new User(3, "Liza", "123", "2025-6-14",null, "sth.jpg", "administrator");
        usersDao.insertUser(liza);
        User nini=new User(4, "Nini", "123", "2025-6-14",null, "sth.jpg", "administrator");
        usersDao.insertUser(nini);


        categoriesDao=new CategoriesDao(basicDataSource);
        Category h=new Category(1, "history");
        categoriesDao.insertCategory(h);
        Category g=new Category(2, "geography");
        categoriesDao.insertCategory(g);
        Category m=new Category(3, "Maths");
        categoriesDao.insertCategory(m);

        quizzesDao=new QuizzesDao(basicDataSource);
        Quiz q1 = new Quiz(12, "historyQuiz","description",
                10, 7.3, 0,
                new Timestamp(123),12, 1, 1,
                "randomized", "one-page", "final-correction");

        Quiz q2 = new Quiz(12, "geoQuiz","description",
                10, 7.3, 0,
                new Timestamp(123),12, 2, 1,
                "randomized", "one-page", "final-correction");
        Quiz q3 = new Quiz(12, "mathsQuiz","description",
                10, 7.3, 0,
                new Timestamp(123),12, 1, 1,
                "randomized", "one-page", "final-correction");
        Quiz q4 = new Quiz(12, "physicsQuiz","description",
                10, 7.3, 0,
                new Timestamp(123),12, 2, 1,
                "randomized", "one-page", "final-correction");
        quizzesDao.insertQuiz(q1);
        quizzesDao.insertQuiz(q2);
        quizzesDao.insertQuiz(q3);
        quizzesDao.insertQuiz(q4);

        qr1=new QuizRating(1, 1, 2, "mid af");
        qr2=new QuizRating(1, 2, 3, "pretty good ngl");
        qr3=new QuizRating(2, 3, 5, "awesome");
        qr4=new QuizRating(3, 1, 3, "it's alright like...");
        qr5=new QuizRating(1, 1, 3, "actually, ok");
    }

    private QuizRating qr1;
    private QuizRating qr2;
    private QuizRating qr3;
    private QuizRating qr4;
    private QuizRating qr5;

    @Test
    public void testInsert() {
        dao.insertQuizRating(qr1);
        assertTrue(dao.contains(qr1));
        dao.insertQuizRating(qr2);
        dao.insertQuizRating(qr3);
        dao.insertQuizRating(qr4);
        assertEquals(4, dao.getCount());
        dao.insertQuizRating(qr5);
        assertTrue(dao.contains(qr5));
        assertFalse(dao.contains(qr1));
        assertEquals(4, dao.getCount());
    }

    @Test
    public void testRemove() {
        testInsert();
        dao.removeQuizRating(1,1);
        assertFalse(dao.contains(qr5));
        assertEquals(3, dao.getCount());
    }

    @Test
    public void testGetQuizRatingsByUserId() {
        testInsert();
        ArrayList<QuizRating> arr=dao.getQuizRatingsByUserId(1);
        assertEquals(2, arr.size());
        assertTrue(arr.contains(qr5));
        assertTrue(arr.contains(qr4));
    }

    @Test
    public void testGetQuizRatingsByQuizId() {
        testInsert();
        ArrayList<QuizRating> arr=dao.getQuizRatingsByQuizId(1);
        assertEquals(2, arr.size());
        assertTrue(arr.contains(qr5));
        assertTrue(arr.contains(qr2));
    }
}
