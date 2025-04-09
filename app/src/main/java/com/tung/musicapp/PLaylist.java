package com.tung.musicapp;

public class PLaylist {
    public String id ;
    public String playlistName;
    public String userEmail;
    public PLaylist(String id, String playlistName, String userEmail) {
        this.id = id;
        this.playlistName = playlistName;
        this.userEmail = userEmail;
    }
    public String getId() {
        return id;
    }
    public String getPlaylistName() {
        return playlistName;
    }
    public String getUserEmail() {
        return userEmail;
    }
}
