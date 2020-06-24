package com.endpoint.nadres.models;

import org.stringtemplate.v4.ST;

import java.io.Serializable;
import java.util.List;

public class SingleRoomModel implements Serializable {
    private Room room;

    public Room getRoom() {
        return room;
    }

    public class Room implements Serializable {
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

        public class RoomUsers implements Serializable {
            private int id;
            private int room_id;
            private int user_id;
            private int teacher_id;
            private String user_type;
            private UserData user_data;

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

            public UserData getUser_data() {
                return user_data;
            }

            protected class UserData implements Serializable {

                private int id;
                private String name;
                private String email;
                private String city;
                private String phone_code;
                private String phone;
                private String logo;
                private String latitude;
                private String longitude;
                private String address;
                private String user_type;
                private String details;
                private String block;
                private String is_login;
                private long logout_time;

                public int getId() {
                    return id;
                }

                public String getName() {
                    return name;
                }

                public String getEmail() {
                    return email;
                }

                public String getCity() {
                    return city;
                }

                public String getPhone_code() {
                    return phone_code;
                }

                public String getPhone() {
                    return phone;
                }

                public String getLogo() {
                    return logo;
                }

                public String getLatitude() {
                    return latitude;
                }

                public String getLongitude() {
                    return longitude;
                }

                public String getAddress() {
                    return address;
                }

                public String getUser_type() {
                    return user_type;
                }

                public String getDetails() {
                    return details;
                }

                public String getBlock() {
                    return block;
                }

                public String getIs_login() {
                    return is_login;
                }

                public long getLogout_time() {
                    return logout_time;
                }
            }
        }
         public class LastMsg implements Serializable {
            private int id;
             private int room_id;
             private int from_id;
                  private String message_type;
             private String message;
             private String attachment;
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
         }
    }
}