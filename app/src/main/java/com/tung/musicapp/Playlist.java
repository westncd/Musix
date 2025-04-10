package com.tung.musicapp;
public class Playlist {
    public int id ;
    public String playlistName;
    public String userEmail;
    public int songCount;
    public Playlist(int id, String playlistName, String userEmail, int songCount) {
        this.id = id;
        this.playlistName = playlistName;
        this.userEmail = userEmail;
        this.songCount = songCount;
    }
    public int getId() {
        return id;
    }
    public String getPlaylistName() {
        return playlistName;
    }
    public int getSongCount() {
        return songCount;
    }
    public String getUserEmail() {
        return userEmail;
    }
}
