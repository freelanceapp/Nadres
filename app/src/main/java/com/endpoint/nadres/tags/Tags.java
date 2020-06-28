package com.endpoint.nadres.tags;

import android.os.Environment;

public class Tags {

    //https://documenter.getpostman.com/view/432711/SzzoYubP?version=latest
    public static String base_url = "http://nadros.online/";
    public static final String IMAGE_URL= base_url+"storage/";
    public static final String session_login = "login";
    public static final String session_logout = "logout";
    public static final String audio_path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Nadres";
    public static final String not_tag = "nadress_not_tag";
    public static final int not_id = 3253;

}
