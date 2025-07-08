package DaoTests;

import org.ja.dao.*;
import org.ja.model.OtherObjects.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the AnnouncementsDao class using an in-memory H2 database.
 */
public class AnnouncementsDaoTest extends BaseDaoTest{
    private AnnouncementsDao dao;

    private Announcement a1;
    private Announcement a2;
    private Announcement a3;
    private Announcement a4;

    @BeforeEach
    public void setUp() throws Exception {
        setUpDataSource();

        executeSqlFile("database/drop.sql");
        executeSqlFile("database/schema.sql");

        insertTestUsers();

        dao = new AnnouncementsDao(basicDataSource);

        a1 = new Announcement(-1, 1, "t1", null);
        a2 = new Announcement(-1, 2, "t2", null);
        a3 = new Announcement(-1, 3, "t3", null);
        a4 = new Announcement(-1, 4, "t4", null);
    }

    private void insertTestUsers() throws Exception {
        try (Connection connection = basicDataSource.getConnection();
             Statement st = connection.createStatement()) {

            st.execute("INSERT INTO users (user_id, username, password_hashed, salt, user_photo, user_status) VALUES " +
                    "(1, 'Sandro', '123', 'salt1', 'sth.jpg', 'administrator')," +
                    "(2, 'Tornike', '123', 'salt2', 'sth.jpg', 'administrator')," +
                    "(3, 'Liza', '123', 'salt3', 'sth.jpg', 'administrator')," +
                    "(4, 'Nini', '123', 'salt4', 'sth.jpg', 'administrator')");
        }
    }

    @Test
    public void testInsertSetsIdAndTimestamp() {
        assertTrue(dao.insertAnnouncement(a1));
        assertTrue(a1.getAnnouncementId() > 0);
        assertNotNull(a1.getCreationDate());

        List<Announcement> announcements = dao.getAllAnnouncements();
        assertEquals(1, announcements.size());
        assertEquals("t1", announcements.get(0).getAnnouncementText());
    }

    @Test
    public void testGetAllAnnouncements() {
        assertTrue(dao.insertAnnouncement(a1));
        assertTrue(dao.insertAnnouncement(a2));
        assertTrue(dao.insertAnnouncement(a3));
        assertTrue(dao.insertAnnouncement(a4));

        List<Announcement> all = dao.getAllAnnouncements();

        assertEquals(4, all.size());
        assertTrue(all.stream().anyMatch(a -> a.getAnnouncementText().equals("t1")));
        assertTrue(all.stream().anyMatch(a -> a.getAnnouncementText().equals("t2")));
        assertTrue(all.stream().anyMatch(a -> a.getAnnouncementText().equals("t3")));
        assertTrue(all.stream().anyMatch(a -> a.getAnnouncementText().equals("t4")));
    }
}
