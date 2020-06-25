package com.endpoint.nadres.models;

import java.io.Serializable;
import java.util.List;

public class MyRoomDataModel implements Serializable {
    private int current_page;
    private int last_page;
    private List<MyRoomModel> data;

    public int getCurrent_page() {
        return current_page;
    }

    public int getLast_page() {
        return last_page;
    }

    public List<MyRoomModel> getData() {
        return data;
    }

    public static class MyRoomModel implements Serializable{
        private int id;
        private int room_id;
        private int user_id;
        private int teacher_id;
        private SingleRoomModel room;

        public int getId() {
            return id;
        }

        public int getRoom_id() {
            return room_id;
        }

        public int getUser_id() {
            return user_id;
        }

        public int getTeacher_id() {
            return teacher_id;
        }

        public SingleRoomModel getRoom() {
            return room;
        }
    }
}
