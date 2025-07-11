package modelTests.dataTests;

import org.ja.model.data.Message;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Message} class.
 */
public class MessageTest {

    @Test
    public void testConstructorWithoutIdAndDate() {
        Message m = new Message(1L, 2L, "Hello");
        assertEquals(1L, m.getSenderUserId());
        assertEquals(2L, m.getRecipientUserId());
        assertEquals("Hello", m.getMessageText());
        assertEquals(0L, m.getMessageId());
        assertNull(m.getMessageSendDate());
    }

    @Test
    public void testFullConstructor() {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        Message m = new Message(10L, 1L, 2L, "Hi", now);
        assertEquals(10L, m.getMessageId());
        assertEquals(1L, m.getSenderUserId());
        assertEquals(2L, m.getRecipientUserId());
        assertEquals("Hi", m.getMessageText());
        assertEquals(now, m.getMessageSendDate());
    }

    @Test
    public void testSettersAndGetters() {
        Message m = new Message(0L, 0L, "");
        m.setMessageId(5L);
        m.setSenderUserId(3L);
        m.setRecipientUserId(4L);
        m.setMessageText("Test message");
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        m.setMessageSendDate(ts);

        assertEquals(5L, m.getMessageId());
        assertEquals(3L, m.getSenderUserId());
        assertEquals(4L, m.getRecipientUserId());
        assertEquals("Test message", m.getMessageText());
        assertEquals(ts, m.getMessageSendDate());
    }

    @Test
    public void testEquals_sameObject() {
        Message m = new Message(1L, 2L, "Hi");
        assertEquals(m, m);
    }

    @Test
    public void testEquals_equalObjects() {
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        Message m1 = new Message(1L, 1L, 2L, "Hello", ts);
        Message m2 = new Message(1L, 1L, 2L, "Hello", ts);
        assertEquals(m1, m2);
    }

    @Test
    public void testEquals_differentObjects() {
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        Message m1 = new Message(1L, 1L, 2L, "Hello", ts);
        Message m2 = new Message(2L, 1L, 2L, "Hello", ts);
        assertNotEquals(m1, m2);
    }

    @Test
    public void testEquals_nullAndOtherClass() {
        Message m = new Message(1L, 2L, "Hi");
        assertNotEquals(null, m);
        assertNotEquals("not a message", m);
    }
}

