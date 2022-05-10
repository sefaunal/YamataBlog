package com.example.yamatablog.Models;

import com.google.firebase.database.ServerValue;

public class Message {
    String senderID, receiverID, messageContent;
    Object messageTime;
    boolean messageStatus;

    public Message(String senderID, String receiverID, String messageContent,boolean messageStatus) {
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.messageContent = messageContent;
        this.messageTime = ServerValue.TIMESTAMP;
        this.messageStatus = messageStatus;
    }

    public Message() {
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getReceiverID() {
        return receiverID;
    }

    public void setReceiverID(String receiverID) {
        this.receiverID = receiverID;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public Object getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(Object messageTime) {
        this.messageTime = messageTime;
    }

    public boolean isMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(boolean messageStatus) {
        this.messageStatus = messageStatus;
    }
}
