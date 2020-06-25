package com.endpoint.nadres.models;


import java.io.Serializable;

public class SingleRoomModel implements Serializable {
    private RoomModel room;

    public RoomModel getRoomModel() {
        return room;
    }

}