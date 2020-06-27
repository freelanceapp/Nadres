package com.endpoint.nadres.models;

import java.io.Serializable;
import java.util.List;

public class MessageDataModel implements Serializable {
    private int current_page;
    private int last_page;
    private RoomModel room;
    private List<MessageModel> data;


    public int getCurrent_page() {
        return current_page;
    }

    public int getLast_page() {
        return last_page;
    }

    public RoomModel getRoom() {
        return room;
    }

    public List<MessageModel> getData() {
        return data;
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
        private boolean isLoaded = false;
        private int progress = 0;
        private int max_duration =0;


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

        public void setUser_data(UserModel.User user_data) {
            this.user_data = user_data;
        }


        public boolean isLoaded() {
            return isLoaded;
        }

        public void setLoaded(boolean loaded) {
            isLoaded = loaded;
        }



        public int getProgress() {
            return progress;
        }

        public void setProgress(int progress) {
            this.progress = progress;
        }

        public int getMax_duration() {
            return max_duration;
        }

        public void setMax_duration(int max_duration) {
            this.max_duration = max_duration;
        }
    }
}
