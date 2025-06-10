package org.ja.dao;

import org.apache.commons.dbcp2.BasicDataSource;
import org.ja.model.OtherObjects.Message;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
    private BasicDataSource dataSource;
    public MessageDao(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }
    public void insertMessage(Message message) {
        String sql="INSERT INTO messages (sender_user_id, recipient_user_id, message_text, message_send_date) VALUES (?,?,?,?)";
        try(Connection c=dataSource.getConnection()){
            PreparedStatement ps=c.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setLong(1, message.getSenderUserId());
            ps.setLong(2, message.getRecipientUserId());
            ps.setString(3, message.getMessageText());
            ps.setTimestamp(4, message.getMessageSendDate());
            ps.executeUpdate();
            ResultSet rs=ps.getGeneratedKeys();
            if(rs.next()){
                message.setMessageId(rs.getLong(1));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
    public void removeMessage(long id) {
        String sql="DELETE FROM messages WHERE message_id = "+id;
        try(Connection c=dataSource.getConnection()){
            PreparedStatement ps=c.prepareStatement(sql);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public ArrayList<Message> getMutualMessagesSorted(long fromUserId, long toUserId){
        String sql="SELECT * FROM messages WHERE sender_user_id ="+
                fromUserId+" AND recipient_user_id ="+toUserId+" ORDER BY message_send_date";
        try(Connection c=dataSource.getConnection()){
            PreparedStatement ps=c.prepareStatement(sql);
            ResultSet rs=ps.executeQuery();
            ArrayList<Message> mutualMessages = new ArrayList<>();
            while(rs.next()){
                Message message = new Message();
                message.setMessageId(rs.getLong(1));
                message.setSenderUserId(rs.getLong(2));
                message.setRecipientUserId(rs.getLong(3));
                message.setMessageText(rs.getString(4));
                message.setMessageSendDate(rs.getTimestamp(5));
                mutualMessages.add(message);
            }
            return mutualMessages;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public ArrayList<Message> getMessagesForUserSorted(long userId){
        String sql="SELECT * FROM messages WHERE recipient_user_id ="+
                userId+" ORDER BY message_send_date";
        try(Connection c=dataSource.getConnection()){
            PreparedStatement ps=c.prepareStatement(sql);
            ResultSet rs=ps.executeQuery();
            ArrayList<Message> messages = new ArrayList<>();
            while(rs.next()){
                Message message = new Message();
                message.setMessageId(rs.getLong(1));
                message.setSenderUserId(rs.getLong(2));
                message.setRecipientUserId(rs.getLong(3));
                message.setMessageText(rs.getString(4));
                message.setMessageSendDate(rs.getTimestamp(5));
                messages.add(message);
            }
            return messages;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
