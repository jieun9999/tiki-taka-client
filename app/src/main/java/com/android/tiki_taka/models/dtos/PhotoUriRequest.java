package com.android.tiki_taka.models.dtos;


import java.util.ArrayList;

public class PhotoUriRequest {
    private int userId;
    private ArrayList<String> uris;

    public PhotoUriRequest(int userId, ArrayList<String> uris) {
        this.userId = userId;
        this.uris = uris;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public ArrayList<String> getUris() {
        return uris;
    }

    public void setUris(ArrayList<String> uris) {
        this.uris = uris;
    }
}
