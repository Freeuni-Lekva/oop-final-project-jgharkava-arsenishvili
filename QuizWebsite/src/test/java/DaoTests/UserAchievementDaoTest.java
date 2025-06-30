package DaoTests;

import org.apache.commons.dbcp2.BasicDataSource;
import org.ja.dao.*;
import org.ja.model.CategoriesAndTags.Category;
import org.ja.model.CategoriesAndTags.Tag;
import org.ja.model.OtherObjects.Achievement;
import org.ja.model.OtherObjects.Answer;
import org.ja.model.OtherObjects.Friendship;
import org.ja.model.OtherObjects.UserAchievement;
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


public class UserAchievementDaoTest {

    private BasicDataSource basicDataSource;
    private UserAchievementsDao dao;
    private UsersDao usersDao;
    private AchievementsDao achievementsDao;
    private Achievement a1;
    private Achievement a2;
    private Achievement a3;
    private Achievement a4;
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

            dao=new UserAchievementsDao(basicDataSource);
            finishSetup();
        }
    }
    private void finishSetup() throws SQLException, NoSuchAlgorithmException {
        usersDao=new UsersDao(basicDataSource);
        achievementsDao=new AchievementsDao(basicDataSource);
        User sandro=new User(1, "Sandro", "123", "2025-6-14",null, "sth.jpg", "administrator");
        usersDao.insertUser(sandro);
        User tornike=new User(2, "Tornike", "123", "2025-6-14",null, "sth.jpg", "administrator");
        usersDao.insertUser(tornike);
        User liza=new User(3, "Liza", "123", "2025-6-14",null, "sth.jpg", "administrator");
        usersDao.insertUser(liza);
        User nini=new User(4, "Nini", "123", "2025-6-14",null, "sth.jpg", "administrator");
        usersDao.insertUser(nini);
        a1=new Achievement(1, "master", "master", "master.jpg");
        a2=new Achievement(2, "beginner", "beginner", "beginner.jpg");
        a3=new Achievement(3, "grandMaster", "grandMaster", "grandMaster.jpg");
        achievementsDao.insertAchievement(a1);
        achievementsDao.insertAchievement(a2);
        achievementsDao.insertAchievement(a3);

        ua1=new UserAchievement(1,1,null);
        ua2=new UserAchievement(2,2,null);
        ua3=new UserAchievement(3,3,null);
        ua4=new UserAchievement(4,3,null);
    }
    private UserAchievement ua1;
    private UserAchievement ua2;
    private UserAchievement ua3;
    private UserAchievement ua4;
    private UserAchievement ua5;
    private UserAchievement ua6;

    @Test
    public void testInsert() {
        dao.insertAchievement(ua1);
        assertTrue(dao.contains(ua1));
        assertFalse(dao.contains(ua2));
        dao.insertAchievement(ua2);
        dao.insertAchievement(ua3);
        dao.insertAchievement(ua4);
        assertEquals(4, dao.getCount());
        assertThrows(RuntimeException.class, () -> {
            dao.insertAchievement(ua3);
        });
        assertEquals(4, dao.getCount());
    }
    @Test
    public void testRemove() {
        dao.insertAchievement(ua1);
        dao.insertAchievement(ua2);
        dao.insertAchievement(ua3);
        dao.insertAchievement(ua4);
        dao.removeAchievement(ua1);
        assertFalse(dao.contains(ua1));
        assertEquals(3, dao.getCount());
        dao.removeAchievement(ua1);
        assertEquals(3, dao.getCount());
    }
    @Test
    public void testGetUserAchievements() {
        dao.insertAchievement(ua1);
        dao.insertAchievement(ua2);
        dao.insertAchievement(ua3);
        dao.insertAchievement(ua4);
        ua5=new UserAchievement(1,3,null);
        ua6=new UserAchievement(1,2,null);
        dao.insertAchievement(ua5);
        dao.insertAchievement(ua6);
        ArrayList<UserAchievement> arr=dao.getUserAchievements(1);
        assertEquals(3, arr.size());
        assertTrue(arr.contains(ua5));
    }



}
