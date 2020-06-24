package com.endpoint.nadres.models;

import java.io.Serializable;
import java.util.List;

public class MessageDataModel implements Serializable {
    private int current_page;
    private int last_page;
    private SingleRoomModel room;
    private List<MessageModel> data;
    private List<UserModel.User> room_users;


    public int getCurrent_page() {
        return current_page;
    }

    public int getLast_page() {
        return last_page;
    }

    public SingleRoomModel getRoom() {
        return room;
    }

    public List<MessageModel> getData() {
        return data;
    }

    public List<UserModel.User> getRoom_users() {
        return room_users;
    }

    public static class MessageModel implements Serializable{
        private int id;
        private int room_id;
        private int from_id;
        private String message_type;
        private String message;
        private String attachment;
        private long date;
        private UserModel.User user_data;

        public int getId() {
            return id;
        }

        public int getRoom_id() {
            return room_id;
        }

        public int getFrom_id() {
            return from_id;
        }

        public String getMessage_type() {
            return message_type;
        }

        public String getMessage() {
            return message;
        }

        public String getAttachment() {
            return attachment;
        }

        public long getDate() {
            return date;
        }

        public UserModel.User getUser_data() {
            return user_data;
        }
    }
}
