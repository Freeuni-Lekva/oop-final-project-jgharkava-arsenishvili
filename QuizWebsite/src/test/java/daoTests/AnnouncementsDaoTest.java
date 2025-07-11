package daoTests;

import org.ja.dao.*;
import org.ja.model.data.*;
import org.ja.utils.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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


    // --- Mockito Tests ---


    @Test
    public void insertAnnouncement_throwsWhenNoRowsAffected() throws Exception {
        BasicDataSource mockDs = mock(BasicDataSource.class);
        Connection mockConn = mock(Connection.class);
        PreparedStatement mockPs = mock(PreparedStatement.class);

        Announcement ann = new Announcement();
        ann.setAdministratorId(1);
        ann.setAnnouncementText("Test");

        when(mockDs.getConnection()).thenReturn(mockConn);
        when(mockConn.prepareStatement(anyString(), eq(PreparedStatement.RETURN_GENERATED_KEYS))).thenReturn(mockPs);
        when(mockPs.executeUpdate()).thenReturn(0); // simulate no rows affected

        AnnouncementsDao dao = new AnnouncementsDao(mockDs);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> dao.insertAnnouncement(ann));
        assertTrue(ex.getMessage().contains("inserting announcement"));
    }


    @Test
    public void insertAnnouncement_throwsWhenNoIdReturned() throws Exception {
        BasicDataSource mockDs = mock(BasicDataSource.class);
        Connection mockConn = mock(Connection.class);
        PreparedStatement mockPs = mock(PreparedStatement.class);
        ResultSet mockRs = mock(ResultSet.class);

        Announcement ann = new Announcement();
        ann.setAdministratorId(1);
        ann.setAnnouncementText("Test");

        when(mockDs.getConnection()).thenReturn(mockConn);
        when(mockConn.prepareStatement(anyString(), eq(PreparedStatement.RETURN_GENERATED_KEYS))).thenReturn(mockPs);
        when(mockPs.executeUpdate()).thenReturn(1);
        when(mockPs.getGeneratedKeys()).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(false); // simulate no ID returned

        AnnouncementsDao dao = new AnnouncementsDao(mockDs);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> dao.insertAnnouncement(ann));
        assertTrue(ex.getMessage().contains("inserting announcement"));
    }


    @Test
    public void insertAnnouncement_throwsWhenFetchCreationDateFails() throws Exception {
        BasicDataSource mockDs = mock(BasicDataSource.class);
        Connection mockConn = mock(Connection.class);
        PreparedStatement mockInsertPs = mock(PreparedStatement.class);
        PreparedStatement mockFetchDatePs = mock(PreparedStatement.class);
        ResultSet mockGeneratedKeys = mock(ResultSet.class);
        ResultSet mockDateRs = mock(ResultSet.class);

        Announcement ann = new Announcement();
        ann.setAdministratorId(1);
        ann.setAnnouncementText("Test");

        when(mockDs.getConnection()).thenReturn(mockConn);

        // Prepare statement for insert
        when(mockConn.prepareStatement(contains("INSERT"), eq(PreparedStatement.RETURN_GENERATED_KEYS))).thenReturn(mockInsertPs);
        when(mockInsertPs.executeUpdate()).thenReturn(1);
        when(mockInsertPs.getGeneratedKeys()).thenReturn(mockGeneratedKeys);
        when(mockGeneratedKeys.next()).thenReturn(true);
        when(mockGeneratedKeys.getLong(1)).thenReturn(123L);

        // Prepare statement for fetchCreationDate
        when(mockConn.prepareStatement(contains("SELECT creation_date"))).thenReturn(mockFetchDatePs);
        when(mockFetchDatePs.executeQuery()).thenReturn(mockDateRs);
        when(mockDateRs.next()).thenReturn(false); // simulate no creation date found

        AnnouncementsDao dao = new AnnouncementsDao(mockDs);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> dao.insertAnnouncement(ann));
        assertTrue(ex.getMessage().contains("inserting announcement"));
    }


    @Test
    public void getAllAnnouncements_throwsOnSQLException() throws Exception {
        BasicDataSource mockDs = mock(BasicDataSource.class);
        Connection mockConn = mock(Connection.class);

        when(mockDs.getConnection()).thenReturn(mockConn);
        when(mockConn.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));

        AnnouncementsDao dao = new AnnouncementsDao(mockDs);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> dao.getAllAnnouncements(10));
        assertTrue(ex.getMessage().contains("querying announcements"));
    }
}
