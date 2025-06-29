package DaoTests;

import org.apache.commons.dbcp2.BasicDataSource;
import org.ja.dao.*;
import org.ja.model.OtherObjects.*;
import org.ja.model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;


public class AnnouncementsDaoTest {
    private BasicDataSource basicDataSource;
    private AnnouncementsDao dao;
    private UsersDao usersDao;

    private Announcement a1;
    private Announcement a2;
    private Announcement a3;
    private Announcement a4;

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

        try (
                Connection connection = basicDataSource.getConnection();
                Statement statement = connection.createStatement()
        ) {
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

        dao = new AnnouncementsDao(basicDataSource);

        finishSetup();
    }

    private void finishSetup() throws SQLException, NoSuchAlgorithmException {
        usersDao = new UsersDao(basicDataSource);
        User sandro = new User(1, "Sandro", "123", "salt1",null, "sth.jpg", "administrator");
        usersDao.insertUser(sandro);
        User tornike = new User(2, "Tornike", "123", "salt2",null, "sth.jpg", "administrator");
        usersDao.insertUser(tornike);
        User liza = new User(3, "Liza", "123", "salt3",null, "sth.jpg", "administrator");
        usersDao.insertUser(liza);
        User nini = new User(4, "Nini", "123", "salt4",null, "sth.jpg", "administrator");
        usersDao.insertUser(nini);

        a1 = new Announcement(-1, 1,"t1", null);
        a2 = new Announcement(-1, 2,"t2", null);
        a3 = new Announcement(-1, 3,"t3", null);
        a4 = new Announcement(-1, 4,"t4", null);
    }

    @Test
    public void testInsert() {
        dao.insertAnnouncement(a1);
        assertEquals(1, a1.getAnnouncementId());
        assertTrue(dao.contains(a1));
        assertFalse(dao.contains(a2));
        dao.insertAnnouncement(a2);
        dao.insertAnnouncement(a3);
        dao.insertAnnouncement(a4);
        assertEquals(4, dao.getCount());
    }

    @Test
    public void testRemove() {
        dao.insertAnnouncement(a1);
        dao.insertAnnouncement(a2);
        dao.insertAnnouncement(a3);
        dao.insertAnnouncement(a4);

        dao.removeAnnouncement(1);
        assertFalse(dao.contains(a1));
        assertEquals(3, dao.getCount());
        dao.removeAnnouncement(6);
        assertEquals(3, dao.getCount());
    }

    @Test
    public void testGetAll() {
        dao.insertAnnouncement(a1);
        dao.insertAnnouncement(a2);
        dao.insertAnnouncement(a3);
        dao.insertAnnouncement(a4);

        ArrayList<Announcement> arr=dao.getAllAnnouncements();
        assertEquals(4, arr.size());
        assertTrue(arr.contains(a1));
        assertTrue(arr.contains(a3));
        assertTrue(arr.contains(a2));
        assertTrue(arr.contains(a4));
    }
}
