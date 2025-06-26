package DaoTests;

import org.apache.commons.dbcp2.BasicDataSource;
import org.ja.dao.AdministratorsDao;
import org.ja.dao.QuizzesDao;
import org.ja.dao.UsersDao;
import org.ja.model.quiz.Quiz;
import org.ja.model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

public class AdministratorsDaoTest {
    private AdministratorsDao adminsDao;
    private UsersDao usersDao;
    private BasicDataSource basicDataSource;
    private QuizzesDao quizzesDao;
    @BeforeEach
    public void setUp() throws Exception {
        basicDataSource = new BasicDataSource();
        basicDataSource.setUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        basicDataSource.setUsername("sa"); // h2 username
        basicDataSource.setPassword(""); // h2 password

        try (Connection connection = basicDataSource.getConnection();
             Statement statement = connection.createStatement()) {
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
        try (Connection connection = basicDataSource.getConnection();
                Statement st = connection.createStatement()) {
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
                    st.execute(sql.trim());
                }
            }
            adminsDao = new AdministratorsDao(basicDataSource);
            usersDao = new UsersDao(basicDataSource);
            quizzesDao = new QuizzesDao(basicDataSource);
            usersDao.insertUser(new User(1, "A", "123", "2025-06-14", null, "a.jpg", "user"));
            usersDao.insertUser(new User(2, "B", "123", "2025-06-14", null, "b.jpg", "administrator"));
            usersDao.insertUser(new User(3, "C", "123", "2025-06-14", null, "c.jpg", "user"));
            st.execute("INSERT INTO categories (category_id, category_name) VALUES (1, 'General')");
            st.execute("INSERT INTO categories (category_id, category_name) VALUES (2, 'Science')");
            st.execute("INSERT INTO categories (category_id, category_name) VALUES (3, 'History')");
            quizzesDao.insertQuiz(new Quiz(1, "Quiz A", "Intro to A", 5, 4.0, 10, new Timestamp(System.currentTimeMillis()), 1, 1, 2, "randomized", "one-page", "final-correction"));
            quizzesDao.insertQuiz(new Quiz(2, "Quiz B", "Basics of B", 8, 3.5, 20, new Timestamp(System.currentTimeMillis()), 2, 1, 3, "ordered", "one-page", "final-correction"));
            quizzesDao.insertQuiz(new Quiz(3, "Quiz C", "Core C", 10, 4.8, 0, new Timestamp(System.currentTimeMillis()), 3, 1, 1, "randomized", "multiple-page", "immediate-correction"));
            st.execute("INSERT INTO history (user_id, quiz_id, score, completion_time) VALUES (1, 1, 80, 120.0)");
            st.execute("INSERT INTO history (user_id, quiz_id, score, completion_time) VALUES (2, 1, 90, 110.5)");
            st.execute("INSERT INTO history (user_id, quiz_id, score, completion_time) VALUES (3, 2, 75, 130.2)");
            st.execute("INSERT INTO history (user_id, quiz_id, score, completion_time) VALUES (1, 3, 95, 100.0)");
        }
    }

    @Test
    public void testPromoteToAdministrator(){
        adminsDao.promoteToAdministrator(1);
        assertEquals("administrator", usersDao.getUserById(1).getStatus());
    }

    @Test
    public void testGetUserCount(){
        assertEquals(3,adminsDao.getUserCount());
    }

    @Test
    public void testTakenQuizzesCount(){
        assertEquals(3, adminsDao.getTakenQuizzesCount());
    }

    @Test
    public void testRemoveUserById(){
        adminsDao.removeUserById(1);
        assertEquals(2, adminsDao.getUserCount());
    }
    @Test
    public void testClearQuizHistory() throws Exception {
        adminsDao.clearQuizHistory(1);
        assertEquals(2, adminsDao.getTakenQuizzesCount());
    }
}
