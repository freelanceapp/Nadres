package com.endpoint.nadres.models;

import java.io.Serializable;

public class ChatUserModel implements Serializable {
    private int room_id;
    private String name;
    private String image;
    private String chat_type;
    private String message_type;
    private String message;

    public ChatUserModel() {
    }

    public ChatUserModel(int room_id, String name, String image, String chat_type) {
        this.room_id = room_id;
        this.name = name;
        this.image = image;
        this.chat_type = chat_type;
    }

    public int getRoom_id() {
        return room_id;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getChat_type() {
        return chat_type;
    }

    public String getMessage_type() {
        return message_type;
    }

    public void setMessage_type(String message_type) {
        this.message_type = message_type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
