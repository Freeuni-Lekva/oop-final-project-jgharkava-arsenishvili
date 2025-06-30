package DaoTests;

import org.apache.commons.dbcp2.BasicDataSource;
import org.ja.dao.*;
import org.ja.model.OtherObjects.Achievement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

public class AchievementDaoTest {
    private AchievementsDao dao;
    private BasicDataSource basicDataSource;

    private Achievement a1;
    private Achievement a2;
    private Achievement a3;

    @BeforeEach
    public void setUp() throws Exception {
        basicDataSource = new BasicDataSource();
        basicDataSource.setUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        basicDataSource.setUsername("sa");
        basicDataSource.setPassword("");

        // Initializing Achievements
        a1=new Achievement(2, "master", "master", "master.jpg");
        a2=new Achievement(2, "beginner", "beginner", "beginner.jpg");
        a3=new Achievement(2, "grandMaster", "grandMaster", "grandMaster.jpg");

        try (Connection connection = basicDataSource.getConnection();
             Statement statement = connection.createStatement()) {

            StringBuilder sqlBuilder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader("database/drop.sql"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sqlBuilder.append(line).append("\n");
                }
            }

            String[] sqlStatements = sqlBuilder.toString().split(";");
            for (String sql : sqlStatements) {
                if (!sql.trim().isEmpty() && !sql.trim().startsWith("use")) {
                    statement.execute(sql.trim());
                }
            }
        }
        try (Connection connection = basicDataSource.getConnection();
             Statement statement = connection.createStatement()) {

            StringBuilder sqlBuilder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader("database/schema.sql"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sqlBuilder.append(line).append("\n");
                }
            }

            String[] sqlStatements = sqlBuilder.toString().split(";");
            for (String sql : sqlStatements) {
                if (!sql.trim().isEmpty() && !sql.trim().startsWith("use")) {
                    statement.execute(sql.trim());
                }
            }
        }

        dao = new AchievementsDao(basicDataSource);
    }

    @Test
    public void testInsert() {
        dao.insertAchievement(a1);
        assertEquals(1, a1.getAchievementId());
        dao.insertAchievement(a2);
        assertEquals(2, a2.getAchievementId());
        assertTrue(dao.contains(a1));
        dao.insertAchievement(a3);
        assertEquals(3, a3.getAchievementId());
        assertEquals(3, dao.getCount());
        assertThrows(RuntimeException.class, () -> {
            dao.insertAchievement(a3);
        });
        assertEquals(3, dao.getCount());

    }
    @Test
    public void testRemove() {
        dao.insertAchievement(a1);
        dao.insertAchievement(a2);
        dao.insertAchievement(a3);

        dao.removeAchievement(1);
        assertEquals(2, dao.getCount());
        assertFalse(dao.contains(a1));
        dao.removeAchievement(1);
        assertEquals(2, dao.getCount());
        assertEquals(dao.getAchievement(2), a2);
    }
}
