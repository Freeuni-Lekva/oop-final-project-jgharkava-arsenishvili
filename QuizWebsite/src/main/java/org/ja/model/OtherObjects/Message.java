package org.ja.model.OtherObjects;

import java.sql.Timestamp;

public class Message {
    private long messageId;
    private long senderUserId;
    private long recipientUserId;
    private String messageText;
    private Timestamp messageSendDate;

    // Empty constructor
    public Message() {
    }

    // Constructor with all parameters
    public Message(long messageId, long senderUserId, long recipientUserId, String messageText, Timestamp messageSendDate) {
        this.messageId = messageId;
        this.senderUserId = senderUserId;
        this.recipientUserId = recipientUserId;
        this.messageText = messageText;
        this.messageSendDate = messageSendDate;
    }

    // Getters and Setters

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public long getSenderUserId() {
        return senderUserId;
    }

    public void setSenderUserId(long senderUserId) {
        this.senderUserId = senderUserId;
    }

    public long getRecipientUserId() {
        return recipientUserId;
    }

    public void setRecipientUserId(long recipientUserId) {
        this.recipientUserId = recipientUserId;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public Timestamp getMessageSendDate() {
        return messageSendDate;
    }

    public void setMessageSendDate(Timestamp messageSendDate) {
        this.messageSendDate = messageSendDate;
    }
}
