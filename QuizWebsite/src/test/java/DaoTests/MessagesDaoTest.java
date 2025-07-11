package DaoTests;

import org.ja.dao.*;
import org.ja.utils.Constants;
import org.ja.model.data.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.Timestamp;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
}
