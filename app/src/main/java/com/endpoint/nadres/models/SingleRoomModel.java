package com.endpoint.nadres.models;


import java.io.Serializable;

public class SingleRoomModel implements Serializable {
    private RoomModel room;
    private String user_status;

    public String getUser_status() {
        return user_status;
    }

    public RoomModel getRoomModel() {
        return room;
    }

}