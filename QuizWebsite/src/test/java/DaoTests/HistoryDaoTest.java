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
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.*;


public class HistoryDaoTest {

    private BasicDataSource basicDataSource;
    private HistoriesDao dao;
    private UsersDao usersDao;
    private QuizzesDao quizzesDao;
    private CategoriesDao categoriesDao;
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
                if (!sql.trim().isEmpty()) {
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

            dao=new HistoriesDao(basicDataSource);
            finishSetup();
        }
    }
    private void finishSetup() throws SQLException {
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

    }
    private History h1;
    private History h2;
    private History h3;
    private History h4;
    private History h5;
    private History h6;

    @Test
    public void testInsert() {
        h1=new History(-1, 1, 1,7.4,56,null);
        h2=new History(-1, 1, 2,8.4,60,null);
        h3=new History(-1, 2, 3,7.4,56,null);
        h4=new History(-1, 2, 4,7.4,56,null);
        dao.insertHistory(h1);
        assertTrue(dao.contains(h1));
        assertFalse(dao.contains(h2));
        dao.insertHistory(h2);
        dao.insertHistory(h3);
        dao.insertHistory(h4);
        assertEquals(4, dao.getCount());
        dao.insertHistory(h1);
        assertEquals(4, dao.getCount());
    }
    @Test
    public void testRemove() {
        testInsert();
        dao.removeHistory(1);
        assertFalse(dao.contains(h1));
        assertEquals(3, dao.getCount());
        dao.removeHistory(1);
        assertEquals(3, dao.getCount());
        dao.removeHistory(3);
        assertFalse(dao.contains(h3));
        assertEquals(2, dao.getCount());
    }
    @Test
    public void testGetHistories(){
        testInsert();
        ArrayList<History> arr=dao.getHistoriesByUserIdSortedByDate(1);
        assertEquals(2, arr.size());
        assertTrue(arr.contains(h1));
        assertTrue(arr.contains(h2));
    }
    @Test
    public void testGetUserHistoriesByQuizId(){
        testInsert();
        h4=new History(-1, 1, 1,9.4,56,null);
        h5=new History(-1, 1, 1,10,56,null);
        dao.insertHistory(h4);
        dao.insertHistory(h5);
        ArrayList<History> arr=dao.getUserHistoryByQuiz(1,1);
        assertEquals(3, arr.size());
        assertTrue(arr.contains(h4));
    }
    @Test
    public void testGetHistoriesByQuizId(){
        testInsert();
        h4=new History(-1, 1, 2,9.4,56,null);
        h5=new History(-1, 1, 1,10,56,null);
        dao.insertHistory(h4);
        dao.insertHistory(h5);
        ArrayList<History> arr=dao.getHistoriesByQuizIdSortedByDate(2);
        assertEquals(2, arr.size());
        assertTrue(arr.contains(h4));
    }




}
