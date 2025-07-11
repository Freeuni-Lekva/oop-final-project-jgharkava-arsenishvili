package org.ja.dao;

import org.apache.commons.dbcp2.BasicDataSource;
import org.ja.model.data.Message;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * Data Access Object for managing messages between users in the database.
 */
public class MessageDao {
    private final BasicDataSource dataSource;


    /**
     * Constructs a MessageDao with the given data source.
     *
     * @param dataSource the database connection pool
     */
    public MessageDao(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }


    /**
     * Inserts a new message into the database. Sets the generated message ID and
     * send date into the provided Message object.
     *
     * @param message the Message object to insert and update with generated fields
     * @return true if insertion succeeded, false otherwise
     * @throws RuntimeException if a database error occurs or if no generated key is returned
     */
    public boolean insertMessage(Message message) {
        String sql = "INSERT INTO messages (sender_user_id, recipient_user_id, message_text) VALUES (?,?,?)";

        try (Connection c = dataSource.getConnection();
            PreparedStatement ps = c.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)){

            ps.setLong(1, message.getSenderUserId());
            ps.setLong(2, message.getRecipientUserId());
            ps.setString(3, message.getMessageText());

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected == 0){
                return false;
            }

            try (ResultSet rs = ps.getGeneratedKeys()){
                if (rs.next()){
                    long messageId = rs.getLong(1);
                    message.setMessageId(messageId);
                    setMessageSendDate(c, message);
                } else {
                    throw new RuntimeException("Insert succeeded but no ID was returned.");
                }
            }

            return true;
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting Message into database", e);
        }

    }


    /**
     * Removes a message from the database by its message ID.
     *
     * @param messageId the ID of the message to remove
     * @return true if a message was deleted, false otherwise
     * @throws RuntimeException if a database error occurs
     */
    public boolean removeMessage(long messageId) {
        String sql = "DELETE FROM messages WHERE message_id = ?";

        try (Connection c = dataSource.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){

            ps.setLong(1, messageId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error removing Message from database", e);
        }
    }


    /**
     * Removes all messages received by a specific user.
     *
     * @param userId the recipient user's ID
     * @return true if any messages were deleted, false otherwise
     * @throws RuntimeException if a database error occurs
     */
    public boolean removeMessagesByUser(long userId){
        String sql = "DELETE FROM messages WHERE recipient_user_id = ?";

        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)){

            ps.setLong(1, userId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error removing Message from database", e);
        }
    }


    /**
     * Retrieves all messages exchanged between two users, sorted by send date descending.
     *
     * @param senderId    the ID of the first user
     * @param recipientId the ID of the second user
     * @return a List of Message objects sorted by most recent first
     * @throws RuntimeException if a database error occurs
     */
    public List<Message> getMutualMessages(long senderId, long recipientId){
        List<Message> mutualMessages = new ArrayList<>();

        String sql = "SELECT * FROM messages " +
                "WHERE (sender_user_id = ? AND recipient_user_id = ?) " +
                "OR (sender_user_id = ? AND recipient_user_id = ?) " +
                "ORDER BY message_send_date DESC";

        try (Connection c = dataSource.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){

            ps.setLong(1, senderId);
            ps.setLong(2, recipientId);
            ps.setLong(3, recipientId);
            ps.setLong(4, senderId);

            try (ResultSet rs = ps.executeQuery()){
                while (rs.next())
                    mutualMessages.add(retrieveMessage(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error querying Messages for users from database", e);
        }

        return mutualMessages;
    }


    /**
     * Retrieves all messages sent to a particular user, sorted by send date descending.
     *
     * @param userId the recipient user's ID
     * @return a List of Message objects sorted by most recent first
     * @throws RuntimeException if a database error occurs
     */
    public List<Message> getMessagesForUser(long userId){
        List<Message> messages = new ArrayList<>();

        String sql = "SELECT * FROM messages WHERE recipient_user_id = ? ORDER BY message_send_date DESC";

        try (Connection c = dataSource.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){

            ps.setLong(1, userId);

            try (ResultSet rs = ps.executeQuery()){
                while(rs.next())
                    messages.add(retrieveMessage(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error querying Messages for user from database", e);
        }

        return messages;
    }


    // --- Helper Methods ---


    /**
     * Helper to set the send date of the message by querying the database for the stored timestamp.
     *
     * @param connection the active database connection
     * @param message    the Message object to update with the send date
     * @throws SQLException if a database access error occurs or no timestamp is found
     */
    private void setMessageSendDate(Connection connection, Message message) throws SQLException {
        String selectDateSql = "SELECT message_send_date FROM messages WHERE message_id = ?";

        try (PreparedStatement psDate = connection.prepareStatement(selectDateSql)) {
            psDate.setLong(1, message.getMessageId());

            try (ResultSet rsDate = psDate.executeQuery()) {
                if (rsDate.next()) {
                    message.setMessageSendDate(rsDate.getTimestamp("message_send_date"));
                } else {
                    throw new SQLException("No message_send_date found for message ID: " + message.getMessageId());
                }
            }
        }
    }

    /**
     * Helper to extract a Message object from the current row of the given ResultSet.
     *
     * @param rs the ResultSet positioned at the current row
     * @return a Message object populated with data from the current row
     * @throws SQLException if a database access error occurs
     */
    private Message retrieveMessage(ResultSet rs) throws SQLException {
        return new Message(rs.getLong("message_id"), rs.getLong("sender_user_id"),
                rs.getLong("recipient_user_id"), rs.getString("message_text"),
                rs.getTimestamp("message_send_date"));
    }

}

