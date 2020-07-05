package com.endpoint.nadres.models;

import java.io.Serializable;
import java.util.List;

public class RequestDataModel implements Serializable {

    private List<RequestModel> data;

    public List<RequestModel> getData() {
        return data;
    }

    public static class RequestModel implements Serializable{
        private int id;
        private String title;
        private String message;
        private String action_type;
        private String image;
        private int room_id;
        private int to_user;
        private int from_user;

        public int getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getMessage() {
            return message;
        }

        public String getAction_type() {
            return action_type;
        }

        public String getImage() {
            return image;
        }

        public int getTo_user() {
            return to_user;
        }

        public int getFrom_user() {
            return from_user;
        }

        public int getRoom_id() {
            return room_id;
        }
    }
}
