package modelTests.dataTests;

import org.ja.model.data.Announcement;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Announcement} class.
 */
public class AnnouncementTest {

    @Test
    public void testDefaultConstructor() {
        Announcement announcement = new Announcement();

        assertEquals(0, announcement.getAnnouncementId());
        assertEquals(0, announcement.getAdministratorId());
        assertNull(announcement.getAnnouncementText());
        assertNull(announcement.getCreationDate());
    }

    @Test
    public void testFullConstructor() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Announcement announcement = new Announcement(1L, 101L, "Site maintenance", timestamp);

        assertEquals(1L, announcement.getAnnouncementId());
        assertEquals(101L, announcement.getAdministratorId());
        assertEquals("Site maintenance", announcement.getAnnouncementText());
        assertEquals(timestamp, announcement.getCreationDate());
    }

    @Test
    public void testPartialConstructor() {
        Announcement announcement = new Announcement(202L, "New feature launched");

        assertEquals(202L, announcement.getAdministratorId());
        assertEquals("New feature launched", announcement.getAnnouncementText());
        assertEquals(0L, announcement.getAnnouncementId()); // default value
        assertNull(announcement.getCreationDate());
    }

    @Test
    public void testSettersAndGetters() {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        Announcement announcement = new Announcement();

        announcement.setAnnouncementId(5L);
        announcement.setAdministratorId(33L);
        announcement.setAnnouncementText("Welcome message");
        announcement.setCreationDate(now);

        assertEquals(5L, announcement.getAnnouncementId());
        assertEquals(33L, announcement.getAdministratorId());
        assertEquals("Welcome message", announcement.getAnnouncementText());
        assertEquals(now, announcement.getCreationDate());
    }

    @Test
    public void testEqualsSameObject() {
        Announcement a = new Announcement();
        assertEquals(a, a);
    }

    @Test
    public void testEqualsEqualObjects() {
        Timestamp date = Timestamp.valueOf("2025-07-11 13:00:00");
        Announcement a1 = new Announcement(1L, 99L, "Important Update", date);
        Announcement a2 = new Announcement(1L, 99L, "Important Update", date);

        assertEquals(a1, a2);
        assertEquals(a1.hashCode(), a2.hashCode());
    }

    @Test
    public void testEqualsDifferentId() {
        Timestamp date = Timestamp.valueOf("2025-07-11 13:00:00");
        Announcement a1 = new Announcement(1L, 99L, "Update", date);
        Announcement a2 = new Announcement(2L, 99L, "Update", date);

        assertNotEquals(a1, a2);
    }

    @Test
    public void testEqualsDifferentText() {
        Timestamp date = Timestamp.valueOf("2025-07-11 13:00:00");
        Announcement a1 = new Announcement(1L, 99L, "A", date);
        Announcement a2 = new Announcement(1L, 99L, "B", date);

        assertNotEquals(a1, a2);
    }

    @Test
    public void testEqualsDifferentTimestamp() {
        Announcement a1 = new Announcement(1L, 99L, "News", Timestamp.valueOf("2025-07-11 12:00:00"));
        Announcement a2 = new Announcement(1L, 99L, "News", Timestamp.valueOf("2025-07-11 12:01:00"));

        assertNotEquals(a1, a2);
    }

    @Test
    public void testEqualsNullAndDifferentClass() {
        Announcement a = new Announcement();
        assertNotEquals(null, a);
        assertNotEquals("string", a);
    }

    @Test
    public void testHashCodeConsistency() {
        Announcement a1 = new Announcement(1L, 10L, "News", new Timestamp(System.currentTimeMillis()));
        Announcement a2 = new Announcement(2L, 20L, "News", new Timestamp(System.currentTimeMillis()));

        // hashCode is based only on announcementText
        assertEquals(a1.hashCode(), a2.hashCode());
    }
}
