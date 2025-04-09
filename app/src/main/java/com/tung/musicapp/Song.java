package com.tung.musicapp;
public class Song {
    private int id;
    private String name;
    private String artist;
    private String uri;

    public Song(String name, String artist, String uri) {
        this.name = name;
        this.artist = artist;
        this.uri = uri;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getArtist() {
        return artist;
    }


    public String getUri() {
        return uri;
    }
}