package DaoTests;

import org.apache.commons.dbcp2.BasicDataSource;
import org.ja.dao.*;
import org.ja.model.CategoriesAndTags.Category;
import org.ja.model.CategoriesAndTags.Tag;
import org.ja.model.OtherObjects.Achievement;
import org.ja.model.OtherObjects.Answer;
import org.ja.model.OtherObjects.Friendship;
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


public class AchievementDaoTest {

    private BasicDataSource basicDataSource;
    private AchievementsDao dao;

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

            dao=new AchievementsDao(basicDataSource);
        }
    }
    private Achievement a1;
    private Achievement a2;
    private Achievement a3;
    private Achievement a4;
    @Test
    public void testInsert() {
        a1=new Achievement(2, "master", "master", "master.jpg");
        a2=new Achievement(2, "beginner", "beginner", "beginner.jpg");
        a3=new Achievement(2, "grandMaster", "grandMaster", "grandMaster.jpg");
        dao.insertAchievement(a1);
        assertEquals(1, a1.getAchievementId());
        dao.insertAchievement(a2);
        assertEquals(2, a2.getAchievementId());
        assertTrue(dao.contains(a1));
        dao.insertAchievement(a3);
        assertEquals(3, a3.getAchievementId());
        assertEquals(3, dao.getCount());
        dao.insertAchievement(a3);
        assertEquals(3, dao.getCount());

    }
    @Test
    public void testRemove() {
        testInsert();
        dao.removeAchievement(1);
        assertEquals(2, dao.getCount());
        assertFalse(dao.contains(a1));
        dao.removeAchievement(1);
        assertEquals(2, dao.getCount());
        assertEquals(dao.getAchievement(2), a2);
    }




}
