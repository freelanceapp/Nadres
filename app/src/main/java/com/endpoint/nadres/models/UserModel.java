package com.endpoint.nadres.models;

import java.io.Serializable;
import java.util.List;

public class UserModel implements Serializable {

    private User data;

    public User getData() {
        return data;
    }

    public static class User implements Serializable {
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
