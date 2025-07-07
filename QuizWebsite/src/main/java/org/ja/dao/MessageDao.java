package org.ja.dao;

import org.apache.commons.dbcp2.BasicDataSource;
import org.ja.model.OtherObjects.Message;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MessageDao {
    private final BasicDataSource dataSource;
    private long cnt=0;
    public MessageDao(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void insertMessage(Message message) {
        String sql = "INSERT INTO messages (sender_user_id, recipient_user_id, message_text) VALUES (?,?,?)";

        try (Connection c = dataSource.getConnection();
            PreparedStatement ps = c.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)){

            ps.setLong(1, message.getSenderUserId());
            ps.setLong(2, message.getRecipientUserId());
            ps.setString(3, message.getMessageText());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()){
                if (rs.next()){
                    cnt++;
                    long messageId = rs.getLong(1);
                    message.setMessageId(messageId);
                    String s = "SELECT message_send_date FROM messages where message_id = ?";

                    try (PreparedStatement preparedStatement = c.prepareStatement(s)){
                        preparedStatement.setLong(1, messageId);

                        try (ResultSet r = preparedStatement.executeQuery()) {
                            if (r.next()) {
                                message.setMessageSendDate(r.getTimestamp("message_send_date"));
                            }
                        }
                    }
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error inserting Message to database", e);
        }

    }

    public void removeMessage(long messageId) {
        String sql = "DELETE FROM messages WHERE message_id = ?";
        try (Connection c = dataSource.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){

            ps.setLong(1, messageId);

            if(ps.executeUpdate() > 0)
                cnt--;
        } catch (SQLException e) {
            throw new RuntimeException("Error removing Message from database", e);
        }
    }

    public ArrayList<Message> getMutualMessagesSorted(long senderId, long recipientId){
        ArrayList<Message> mutualMessages = new ArrayList<>();

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

    public ArrayList<Message> getMessagesForUserSorted(long userId){
        ArrayList<Message> messages = new ArrayList<>();

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

    public boolean contains(Message m){
        if(m==null){
            return false;
        }
        String sql = "SELECT COUNT(*) FROM messages WHERE message_id = ? AND sender_user_id=?" +
                "AND recipient_user_id=? AND message_text=? AND message_send_date=?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, m.getMessageId());
            ps.setLong(2, m.getSenderUserId());
            ps.setLong(3, m.getRecipientUserId());
            ps.setString(4, m.getMessageText());
            ps.setTimestamp(5, m.getMessageSendDate());

            try (ResultSet rs = ps.executeQuery()){
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Error checking user existence", e);
        }
    }
    public boolean contains(long mid){
        String sql = "SELECT COUNT(*) FROM messages WHERE message_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, mid);

            try (ResultSet rs = ps.executeQuery()){
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Error checking user existence", e);
        }
    }
    public long getCount(){
        return cnt;
    }
    private Message retrieveMessage(ResultSet rs) throws SQLException {
        return new Message(rs.getLong("message_id"), rs.getLong("sender_user_id"),
                rs.getLong("recipient_user_id"), rs.getString("message_text"),
                rs.getTimestamp("message_send_date"));
    }

}

