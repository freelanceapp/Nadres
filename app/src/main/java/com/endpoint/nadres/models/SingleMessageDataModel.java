package com.endpoint.nadres.models;

import java.io.Serializable;

public class SingleMessageDataModel implements Serializable {
    private MessageDataModel.MessageModel data;

    public MessageDataModel.MessageModel getData() {
        return data;
    }
}
