package com.endpoint.nadres.models;

import java.io.Serializable;
import java.util.List;

public class TeacherModel implements Serializable {
    private List<Data> data;

    public List<Data> getData() {
        return data;
    }

    public class Data implements Serializable {
        private int id;
        private int user_id;
        private String skill_type;
        private TeachersFk teachers_fk;

        public int getId() {
            return id;
        }

        public int getUser_id() {
            return user_id;
        }

        public String getSkill_type() {
            return skill_type;
        }

        public TeachersFk getTeachers_fk() {
            return teachers_fk;
        }

        public class TeachersFk implements Serializable {
            private int id;
            private String name;
            private String email;
            private String city;
            private String phone_code;
            private String phone;
            private String image;

            private String banner_image;
            private String token;
            private String latitude;
            private String longitude;
            private String address;

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

            public String getImage() {
                return image;
            }

            public String getBanner_image() {
                return banner_image;
            }

            public String getToken() {
                return token;
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
        }
    }
}
