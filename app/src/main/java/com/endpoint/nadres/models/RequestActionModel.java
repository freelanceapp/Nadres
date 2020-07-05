package com.endpoint.nadres.models;

import java.io.Serializable;

public class RequestActionModel implements Serializable {
   private String action;
   private int room_id;

    public RequestActionModel(String action,int room_id) {
        this.action = action;
        this.room_id = room_id;
    }

    public String getAction() {
        return action;
    }

    public int getRoom_id() {
        return room_id;
    }
}
