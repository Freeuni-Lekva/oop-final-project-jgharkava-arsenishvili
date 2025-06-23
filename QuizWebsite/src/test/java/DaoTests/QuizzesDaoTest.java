package DaoTests;

import org.apache.commons.dbcp2.BasicDataSource;
import org.ja.dao.CategoriesDao;
import org.ja.dao.QuizzesDao;
import org.ja.dao.TagsDao;
import org.ja.dao.UsersDao;
import org.ja.model.CategoriesAndTags.Category;
import org.ja.model.CategoriesAndTags.Tag;
import org.ja.model.quiz.Quiz;
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
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.*;

public class QuizzesDaoTest {
    private QuizzesDao dao;
    private UsersDao usersDao;
    private CategoriesDao categoriesDao;
    private BasicDataSource basicDataSource;
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
            dao=new QuizzesDao(basicDataSource);
            usersDao=new UsersDao(basicDataSource);
            categoriesDao=new CategoriesDao(basicDataSource);

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
        }
    }
    @Test
    public void testInsert() throws SQLException {

        Quiz q1 = new Quiz(12, "historyQuiz","description",
                10, 7.3, 0,
                new Timestamp(123),12, 1, 1,
                "randomized", "one-page", "final-correction");
        assertEquals(0, dao.getCount());
        dao.insertQuiz(q1);
        assertTrue(dao.containsQuiz("historyQuiz"));
        assertFalse(dao.containsQuiz("other"));
        assertEquals(1, dao.getCount());
        assertEquals(1, q1.getId());
        Quiz q2 = new Quiz(12, "geoQuiz","description",
                10, 7.3, 0,
                new Timestamp(123),12, 2, 1,
                "randomized", "one-page", "final-correction");
        dao.insertQuiz(q2);
        assertEquals(2, dao.getCount());

    }
    @Test
    public void testUpdate() throws SQLException {
        testInsert();

        Quiz q3 = new Quiz(12, "mathsQuiz","description",
                10, 7.3, 0,
                new Timestamp(123),12, 3, 1,
                "randomized", "one-page", "final-correction");
        dao.updateQuizById(q3, 2);
        assertEquals(2, dao.getCount());
        assertEquals("mathsQuiz", dao.getQuizById(2).getName());

        Quiz q4 = new Quiz(12, "physicsQuiz","description",
                10, 7.3, 0,
                new Timestamp(123),12, 3, 1,
                "randomized", "one-page", "final-correction");
        dao.insertQuiz(q4);
        assertEquals(3, dao.getCount());
        dao.updateQuizRating(3);
        dao.updateQuizParticipantCount(3);
        q4.setDescription("new description");
        dao.updateQuiz(q4);
        assertEquals("new description", dao.getQuizById(3).getDescription());
    }
    @Test
    public void testRemove() throws SQLException {
        testUpdate();
        dao.removeQuizById(1);
        assertEquals(2, dao.getCount());
        dao.removeQuizByName("mathsQuiz");
        assertEquals(1, dao.getCount());
        assertNull(dao.getQuizById(1));
        assertNull(dao.getQuizById(2));
    }
}

