package org.ja.model.data;

import java.sql.Timestamp;

/**
 * Represents a message sent from one user to another within the application.
 * Each message contains sender and recipient information, the text content,
 * and the timestamp when it was sent.
 *
 * <p>This class corresponds to the {@code messages} table in the database.</p>
 */
public class Message {
    private long messageId;
    private long senderUserId;
    private long recipientUserId;
    private String messageText;
    private Timestamp messageSendDate;

    /**
     * Constructs a new Message without a message ID or send date.
     * Typically used when creating a new message before it's inserted into the database.
     *
     * @param senderUserId     ID of the user sending the message
     * @param recipientUserId  ID of the user receiving the message
     * @param messageText      content of the message
     */
    public Message(long senderUserId, long recipientUserId, String messageText) {
        this.senderUserId = senderUserId;
        this.recipientUserId = recipientUserId;
        this.messageText = messageText;
    }

    /**
     * Constructs a full Message with all fields provided.
     *
     * @param messageId         the message's unique ID
     * @param senderUserId      ID of the user who sent the message
     * @param recipientUserId   ID of the message recipient
     * @param messageText       the message content
     * @param messageSendDate   timestamp when the message was sent
     */
    public Message(long messageId, long senderUserId, long recipientUserId, String messageText, Timestamp messageSendDate) {
        this.messageId = messageId;
        this.senderUserId = senderUserId;
        this.recipientUserId = recipientUserId;
        this.messageText = messageText;
        this.messageSendDate = messageSendDate;
    }

    /** @return the unique ID of the message */
    public long getMessageId() {
        return messageId;
    }

    /** @param messageId the ID to set for this message */
    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    /** @return the ID of the user who sent this message */
    public long getSenderUserId() {
        return senderUserId;
    }

    /** @param senderUserId the sender's user ID to set */
    public void setSenderUserId(long senderUserId) {
        this.senderUserId = senderUserId;
    }

    /** @return the ID of the user who received this message */
    public long getRecipientUserId() {
        return recipientUserId;
    }

    /** @param recipientUserId the recipient's user ID to set */
    public void setRecipientUserId(long recipientUserId) {
        this.recipientUserId = recipientUserId;
    }

    /** @return the message content as plain text */
    public String getMessageText() {
        return messageText;
    }

    /** @param messageText the message text to set */
    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    /** @return the timestamp when the message was sent */
    public Timestamp getMessageSendDate() {
        return messageSendDate;
    }

    /** @param messageSendDate the send date to set */
    public void setMessageSendDate(Timestamp messageSendDate) {
        this.messageSendDate = messageSendDate;
    }

    /**
     * Compares this message with another for equality based on all fields.
     *
     * @param o the object to compare with
     * @return true if all fields are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message)) return false;

        Message other = (Message) o;
        return messageId == other.messageId &&
                senderUserId == other.senderUserId &&
                recipientUserId == other.recipientUserId &&
                messageText.equals(other.messageText) &&
                messageSendDate.equals(other.messageSendDate);
    }
}
