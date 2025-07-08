package DaoTests;

import org.ja.dao.AdministratorsDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the AdministratorsDao class using an in-memory H2 database.
 */
public class AdministratorsDaoTest extends BaseDaoTest{
    private AdministratorsDao adminsDao;

    @BeforeEach
    public void setUp() throws Exception {
        setUpDataSource();

        executeSqlFile("database/drop.sql");
        executeSqlFile("database/schema.sql");

        adminsDao = new AdministratorsDao(basicDataSource);

        try (Connection connection = basicDataSource.getConnection();
            Statement st = connection.createStatement()){

            // Insert users
            st.execute("INSERT INTO users (user_id, username, password_hashed, salt, registration_date, user_photo, user_status) " +
                    "VALUES (1, 'A', '123', 'salt1', '2025-06-14', 'a.jpg', 'user')");
            st.execute("INSERT INTO users (user_id, username, password_hashed, salt, registration_date, user_photo, user_status) " +
                    "VALUES (2, 'B', '123', 'salt2', '2025-06-14', 'b.jpg', 'administrator')");
            st.execute("INSERT INTO users (user_id, username, password_hashed, salt, registration_date, user_photo, user_status) " +
                    "VALUES (3, 'C', '123', 'salt3', '2025-06-14', 'c.jpg', 'user')");

            // Insert categories
            st.execute("INSERT INTO categories (category_id, category_name) VALUES (1, 'General')");
            st.execute("INSERT INTO categories (category_id, category_name) VALUES (2, 'Science')");
            st.execute("INSERT INTO categories (category_id, category_name) VALUES (3, 'History')");

            // Insert quizzes
            st.execute("INSERT INTO quizzes (quiz_id, quiz_name, quiz_description, quiz_score, average_rating, participant_count, creation_date, time_limit_in_minutes, category_id, creator_id, question_order_status, question_placement_status, question_correction_status) " +
                    "VALUES (1, 'Quiz A', 'Intro to A', 5, 4.0, 10, CURRENT_TIMESTAMP, 1, 1, 1, 'randomized', 'one-page', 'final-correction')");
            st.execute("INSERT INTO quizzes (quiz_id, quiz_name, quiz_description, quiz_score, average_rating, participant_count, creation_date, time_limit_in_minutes, category_id, creator_id, question_order_status, question_placement_status, question_correction_status) " +
                    "VALUES (2, 'Quiz B', 'Basics of B', 8, 3.5, 20, CURRENT_TIMESTAMP, 2, 1, 2, 'ordered', 'one-page', 'final-correction')");
            st.execute("INSERT INTO quizzes (quiz_id, quiz_name, quiz_description, quiz_score, average_rating, participant_count, creation_date, time_limit_in_minutes, category_id, creator_id, question_order_status, question_placement_status, question_correction_status) " +
                    "VALUES (3, 'Quiz C', 'Core C', 10, 4.8, 0, CURRENT_TIMESTAMP, 3, 1, 3, 'randomized', 'multiple-page', 'immediate-correction')");

            // Insert history
            st.execute("INSERT INTO history (user_id, quiz_id, score, completion_time) VALUES (1, 1, 80, 120.0)");
            st.execute("INSERT INTO history (user_id, quiz_id, score, completion_time) VALUES (2, 1, 90, 110.5)");
            st.execute("INSERT INTO history (user_id, quiz_id, score, completion_time) VALUES (3, 2, 75, 130.2)");
            st.execute("INSERT INTO history (user_id, quiz_id, score, completion_time) VALUES (1, 3, 95, 100.0)");
        }
    }

    @Test
    public void testPromoteToAdministrator(){
        assertTrue(adminsDao.promoteToAdministrator(1));
        // Promote again does nothing
        assertFalse(adminsDao.promoteToAdministrator(1225));
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
        assertTrue(adminsDao.removeUserById(1));
        assertFalse(adminsDao.removeUserById(1)); // already deleted
    }
    @Test
    public void testClearQuizHistory() throws Exception {
        assertTrue(adminsDao.clearQuizHistory(1));
        assertFalse(adminsDao.clearQuizHistory(1225)); // quiz not found
    }
}
