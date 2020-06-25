package com.endpoint.nadres.models;

import java.io.Serializable;
import java.util.List;

public class RoomModel implements Serializable {

    private int id;
    private int teacher_id;
    private String skill_type;
    private String room_type;
    private String names;
    private LastMsg last_msg;
    private List<RoomUsers> room_users;

    public int getId() {
        return id;
    }

    public int getTeacher_id() {
        return teacher_id;
    }

    public String getSkill_type() {
        return skill_type;
    }

    public String getRoom_type() {
        return room_type;
    }

    public String getNames() {
        return names;
    }

    public LastMsg getLast_msg() {
        return last_msg;
    }

    public List<RoomUsers> getRoom_users() {
        return room_users;
    }

    public static class RoomUsers implements Serializable {
        private int id;
        private int room_id;
        private int user_id;
        private int teacher_id;
        private String user_type;
        private UserModel.User user_data;

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

        public String getUser_type() {
            return user_type;
        }

        public UserModel.User getUser_data() {
            return user_data;
        }

    }

    public static class LastMsg implements Serializable {
        private int id;
        private int room_id;
        private int from_id;
        private String message_type;
        private String message;
        private String attachment;
        private UserModel.User user_data;
        private long date;

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
