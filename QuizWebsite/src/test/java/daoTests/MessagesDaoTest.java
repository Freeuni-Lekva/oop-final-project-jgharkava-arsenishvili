package daoTests;

import org.ja.dao.*;
import org.ja.utils.Constants;
import org.ja.model.data.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.Timestamp;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class MessagesDaoTest extends BaseDaoTest{

    private MessageDao dao;

    @BeforeEach
    public void setUp() throws Exception {
        setUpDataSource();

        dao = new MessageDao(basicDataSource);
    }


    @Test
    public void testInsertAndRemoveMessage() {
        // Insert a new message between two users
        Message message = new Message(7L, 8L, "JUnit test message");

        boolean inserted = dao.insertMessage(message);
        assertTrue(inserted, "Message should be inserted");

        assertTrue(message.getMessageId() > 0, "Inserted message should have an ID");
        assertNotNull(message.getMessageSendDate(), "Inserted message should have send date set");

        boolean removed = dao.removeMessage(message.getMessageId());
        assertTrue(removed, "Message should be removed");

        // already removed
        boolean removedAgain = dao.removeMessage(message.getMessageId());
        assertFalse(removedAgain, "Removing again should fail");
    }


    @Test
    public void testRemoveMessageByUser() {
        Message m1 = new Message(5L, 7L, "Message 1");
        dao.insertMessage(m1);

        Message m2 = new Message(6L, 7L, "Message 2");
        dao.insertMessage(m2);

        boolean removedAny = dao.removeMessagesByUser(7L);
        assertTrue(removedAny, "Should remove messages for user 99999");

        // Confirm no messages remain for this user
        List<Message> messages = dao.getMessagesForUser(7L, Constants.FETCH_LIMIT);
        assertTrue(messages.isEmpty(), "No messages should remain for user 99999");
    }


    @Test
    public void testGetMessagesForUserSorted() {
        List<Long> expectedSenders = List.of(5L, 8L);

        List<Message> messages = dao.getMessagesForUser(6L, Constants.FETCH_LIMIT);
        assertFalse(messages.isEmpty(), "Messages for user 6 should not be empty");

        Timestamp previous = null;
        for (Message m : messages) {
            assertEquals(6L, m.getRecipientUserId(), "Recipient should be user 6");
            assertTrue(expectedSenders.contains(m.getSenderUserId()), "Sender must be 5 or 8");

            if (previous != null) {
                assertTrue(m.getMessageSendDate().compareTo(previous) <= 0,
                        "Messages should be sorted by send date descending");
            }
            previous = m.getMessageSendDate();
        }
    }


    // --- Mockito Tests ---


    @Test
    public void testInsertMessage_success() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);
        PreparedStatement psInsert = mock(PreparedStatement.class);
        PreparedStatement psSelectDate = mock(PreparedStatement.class);
        ResultSet rsGeneratedKeys = mock(ResultSet.class);
        ResultSet rsDate = mock(ResultSet.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString(), eq(PreparedStatement.RETURN_GENERATED_KEYS))).thenReturn(psInsert);
        when(conn.prepareStatement(startsWith("SELECT message_send_date"))).thenReturn(psSelectDate);

        when(psInsert.executeUpdate()).thenReturn(1);
        when(psInsert.getGeneratedKeys()).thenReturn(rsGeneratedKeys);
        when(rsGeneratedKeys.next()).thenReturn(true);
        when(rsGeneratedKeys.getLong(1)).thenReturn(42L);

        when(psSelectDate.executeQuery()).thenReturn(rsDate);
        when(rsDate.next()).thenReturn(true);
        when(rsDate.getTimestamp("message_send_date")).thenReturn(new Timestamp(System.currentTimeMillis()));

        MessageDao dao = new MessageDao(ds);
        Message message = new Message(0, 1L, 2L, "Hello!", null);

        boolean result = dao.insertMessage(message);

        assertTrue(result);
        assertEquals(42L, message.getMessageId());
        assertNotNull(message.getMessageSendDate());

        verify(psInsert).setLong(1, 1L);
        verify(psInsert).setLong(2, 2L);
        verify(psInsert).setString(3, "Hello!");
    }


    @Test
    public void testInsertMessage_throwsExceptionOnInsert() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);
        PreparedStatement psInsert = mock(PreparedStatement.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString(), eq(PreparedStatement.RETURN_GENERATED_KEYS))).thenReturn(psInsert);
        when(psInsert.executeUpdate()).thenThrow(new SQLException("Insert error"));

        MessageDao dao = new MessageDao(ds);
        Message message = new Message(0, 1L, 2L, "Hi!", null);

        assertThrows(RuntimeException.class, () -> dao.insertMessage(message));
    }


    @Test
    public void testInsertMessage_noGeneratedKey() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);
        PreparedStatement psInsert = mock(PreparedStatement.class);
        ResultSet rsGeneratedKeys = mock(ResultSet.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString(), eq(PreparedStatement.RETURN_GENERATED_KEYS))).thenReturn(psInsert);
        when(psInsert.executeUpdate()).thenReturn(1);
        when(psInsert.getGeneratedKeys()).thenReturn(rsGeneratedKeys);
        when(rsGeneratedKeys.next()).thenReturn(false);

        MessageDao dao = new MessageDao(ds);
        Message message = new Message(0, 1L, 2L, "Test", null);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> dao.insertMessage(message));
        assertTrue(ex.getMessage().contains("no ID was returned"));
    }


    @Test
    public void testRemoveMessage_success() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(1);

        MessageDao dao = new MessageDao(ds);

        boolean deleted = dao.removeMessage(123L);

        assertTrue(deleted);
        verify(ps).setLong(1, 123L);
    }


    @Test
    public void testRemoveMessage_notFound() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(0);

        MessageDao dao = new MessageDao(ds);

        boolean deleted = dao.removeMessage(123L);

        assertFalse(deleted);
    }


    @Test
    public void testRemoveMessage_throwsException() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenThrow(new SQLException("Delete error"));

        MessageDao dao = new MessageDao(ds);

        assertThrows(RuntimeException.class, () -> dao.removeMessage(123L));
    }


    @Test
    public void testRemoveMessagesByUser_success() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(3);

        MessageDao dao = new MessageDao(ds);

        boolean deleted = dao.removeMessagesByUser(456L);

        assertTrue(deleted);
        verify(ps).setLong(1, 456L);
    }


    @Test
    public void testRemoveMessagesByUser_notFound() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(0);

        MessageDao dao = new MessageDao(ds);

        boolean deleted = dao.removeMessagesByUser(456L);

        assertFalse(deleted);
    }


    @Test
    public void testRemoveMessagesByUser_throwsException() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenThrow(new SQLException("Delete error"));

        MessageDao dao = new MessageDao(ds);

        assertThrows(RuntimeException.class, () -> dao.removeMessagesByUser(456L));
    }


    @Test
    public void testGetMessagesForUser_success() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(ps);

        when(ps.executeQuery()).thenReturn(rs);

        // Simulate 2 messages
        when(rs.next()).thenReturn(true, true, false);

        when(rs.getLong("message_id")).thenReturn(1L, 2L);
        when(rs.getLong("sender_user_id")).thenReturn(10L, 20L);
        when(rs.getLong("recipient_user_id")).thenReturn(100L, 200L);
        when(rs.getString("message_text")).thenReturn("Hello", "World");
        when(rs.getTimestamp("message_send_date")).thenReturn(new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()));

        MessageDao dao = new MessageDao(ds);

        List<Message> messages = dao.getMessagesForUser(100L, 5);

        assertEquals(2, messages.size());
        assertEquals("Hello", messages.get(0).getMessageText());
        assertEquals("World", messages.get(1).getMessageText());

        verify(ps).setLong(1, 100L);
        verify(ps).setInt(2, 5);
    }


    @Test
    public void testGetMessagesForUser_throwsException() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenThrow(new SQLException("Query error"));

        MessageDao dao = new MessageDao(ds);

        assertThrows(RuntimeException.class, () -> dao.getMessagesForUser(100L, 5));
    }

}
