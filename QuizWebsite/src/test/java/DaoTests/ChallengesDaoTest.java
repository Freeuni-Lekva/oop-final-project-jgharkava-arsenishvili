package DaoTests;

import org.apache.commons.dbcp2.BasicDataSource;
import org.ja.dao.*;
import org.ja.model.CategoriesAndTags.Category;
import org.ja.model.OtherObjects.*;
import org.ja.model.quiz.Quiz;
import org.ja.model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

public class ChallengesDaoTest {
    private BasicDataSource basicDataSource;
    private ChallengesDao dao;
    private UsersDao usersDao;
    private QuizzesDao quizzesDao;
    private CategoriesDao categoriesDao;

    private Challenge c1;
    private Challenge c2;
    private Challenge c3;
    private Challenge c4;

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
        }

        dao=new ChallengesDao(basicDataSource);
        finishSetup();
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

        c1=new Challenge(-1,1,2,1);
        c2=new Challenge(-1,2,1,2);
        c3=new Challenge(-1,3,2,1);
        c4=new Challenge(-1,4,2,1);

    }

    @Test
    public void testInsert() {
        dao.insertChallenge(c1);
        assertTrue(dao.contains(c1));
        assertEquals(1, c1.getChallengeId());
        assertFalse(dao.contains(c2));
        dao.insertChallenge(c2);
        dao.insertChallenge(c3);
        dao.insertChallenge(c4);
        assertEquals(4, dao.getCount());
    }

    @Test
    public void testRemove() {
        dao.insertChallenge(c1);
        dao.insertChallenge(c2);
        dao.insertChallenge(c3);
        dao.insertChallenge(c4);

        dao.removeChallenge(1);
        assertFalse(dao.contains(c1));
        assertEquals(3, dao.getCount());
        dao.removeChallenge(2);
        assertFalse(dao.contains(c2));
        assertEquals(2, dao.getCount());
        dao.removeChallenge(1);
        assertEquals(2, dao.getCount());
    }

    @Test
    public void testChallengesAsSender(){
        dao.insertChallenge(c1);
        dao.insertChallenge(c2);
        dao.insertChallenge(c3);
        dao.insertChallenge(c4);

        ArrayList<Challenge> arr=dao.challengesAsSender(1);
        assertEquals(1, arr.size());
        assertTrue(arr.contains(c1));
    }

    @Test
    public void testChallengesAsReceiver(){
        dao.insertChallenge(c1);
        dao.insertChallenge(c2);
        dao.insertChallenge(c3);
        dao.insertChallenge(c4);

        ArrayList<Challenge> arr=dao.challengesAsReceiver(2);
        assertEquals(3, arr.size());
        assertTrue(arr.contains(c1));
        assertTrue(arr.contains(c3));
        assertTrue(arr.contains(c4));
    }
}
