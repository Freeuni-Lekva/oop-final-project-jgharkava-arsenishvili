package org.ja.dao;

import org.apache.commons.dbcp2.BasicDataSource;
import org.ja.model.OtherObjects.Message;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
/*
create table messages(
    message_id bigint primary key auto_increment,
    sender_user_id bigint not null,
    recipient_user_id bigint not null,
    message_text text not null,
    message_send_date timestamp default current_timestamp,

    foreign key (sender_user_id) references users(user_id),
    foreign key (recipient_user_id) references users(user_id)
);
 */
public class MessageDao {
    private final BasicDataSource dataSource;

    public MessageDao(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void insertMessage(Message message) {
        String sql = "INSERT INTO messages (sender_user_id, recipient_user_id, message_text, message_send_date) VALUES (?,?,?,?)";

        try (Connection c = dataSource.getConnection();
            PreparedStatement ps = c.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)){

            ps.setLong(1, message.getSenderUserId());
            ps.setLong(2, message.getRecipientUserId());
            ps.setString(3, message.getMessageText());
            ps.setTimestamp(4, message.getMessageSendDate());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()){
                if (rs.next()){
                    message.setMessageId(rs.getLong(1));
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

            ps.executeUpdate();
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

        String sql = "SELECT * FROM messages WHERE recipient_user_id = ? ORDER BY message_send_date";

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

    private Message retrieveMessage(ResultSet rs) throws SQLException {
        return new Message(rs.getLong("message_id"), rs.getLong("sender_user_id"),
                rs.getLong("recipient_user_id"), rs.getString("message_text"),
                rs.getTimestamp("message_send_date"));
    }

}
