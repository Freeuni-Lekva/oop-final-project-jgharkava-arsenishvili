package daoTests;

import org.ja.dao.*;
import org.ja.model.data.*;
import org.ja.utils.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the AnnouncementsDao class using an in-memory H2 database.
 */
public class AnnouncementsDaoTest extends BaseDaoTest{
    private AnnouncementsDao dao;

    @BeforeEach
    public void setUp() throws Exception {
        setUpDataSource();

        dao = new AnnouncementsDao(basicDataSource);
    }

    @Test
    public void testInsertAnnouncement(){
        Announcement newAnnouncement = new Announcement(0L, 5L, "Reminder: Submit feedback!", null);

        assertTrue(dao.insertAnnouncement(newAnnouncement));
        assertTrue(newAnnouncement.getAnnouncementId() > 0);
        assertNotNull(newAnnouncement.getCreationDate());

        Announcement fetched = dao.getAllAnnouncements(Constants.FETCH_LIMIT).stream()
                .filter(a -> a.getAnnouncementText().equals("Reminder: Submit feedback!"))
                .findFirst()
                .orElse(null);

        assertNotNull(fetched);
        assertEquals(5L, fetched.getAdministratorId());
    }


    @Test
    public void testGetAllAnnouncements() {
        List<Announcement> announcements = dao.getAllAnnouncements(Constants.FETCH_LIMIT);

        assertEquals(2, announcements.size());
        assertTrue(announcements.stream().anyMatch(a ->
                a.getAnnouncementText().equals("Join the upcoming monthly quiz competition starting July 15th!")));
        assertTrue(announcements.stream().anyMatch(a ->
                a.getAnnouncementText().equals("Check out our new category: Tech!")));
    }
}
