package com.tung.musicapp;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "spotify_clone.db";
    private static final int DATABASE_VERSION = 6; // Tăng version để upgrade database

    // Bảng Users
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_ROLE = "role";

    private static final String CREATE_TABLE_USERS =
            "CREATE TABLE " + TABLE_USERS + "(" +
                    COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_EMAIL + " TEXT UNIQUE," +
                    COLUMN_PASSWORD + " TEXT," +
                    COLUMN_NAME + " TEXT," +
                    COLUMN_ROLE + " TEXT)";

    // Bảng Songs
    public static final String TABLE_SONGS = "songs";
    public static final String COLUMN_SONG_ID = "song_id";
    public static final String COLUMN_SONG_NAME = "song_name";
    public static final String COLUMN_ARTIST = "artist";
    public static final String COLUMN_URI = "uri"; // Thêm cột uri

    private static final String CREATE_TABLE_SONGS =
            "CREATE TABLE " + TABLE_SONGS + "(" +
                    COLUMN_SONG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_SONG_NAME + " TEXT," +
                    COLUMN_ARTIST + " TEXT," +
                    COLUMN_URI + " TEXT)";

    public static final String TABLE_PLAYLISTS = "playlists";
    public static final String COLUMN_PLAYLIST_ID = "playlist_id";
    public static final String COLUMN_PLAYLIST_NAME = "playlist_name";
    public static final String COLUMN_USER_EMAIL = "user_email";

    private static final String CREATE_TABLE_PLAYLISTS =
            "CREATE TABLE " + TABLE_PLAYLISTS + "(" +
                    COLUMN_PLAYLIST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_PLAYLIST_NAME + " TEXT," +
                    COLUMN_USER_EMAIL + " TEXT," +
                    "FOREIGN KEY (" + COLUMN_USER_EMAIL + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_EMAIL + "))";
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_SONGS);
        db.execSQL(CREATE_TABLE_PLAYLISTS);

        // Thêm dữ liệu mẫu cho users
        insertUser(db, "musician1@email.com", "123456", "Nguyễn Văn Nhạc" ,  "Musician");
        insertUser(db, "listener1@email.com", "abc123", "Nguyễn Văn Thính", "Listener");

        // Thêm dữ liệu mẫu cho songs
        insertSong(db, "Blinding Lights", "The Weeknd", null);
        insertSong(db, "Shape of You", "Ed Sheeran",  null);
        insertSong(db, "Someone Like You", "Adele", null);
        insertSong(db, "Bohemian Rhapsody", "Queen",  null);
        insertSong(db, "Dancing Queen", "ABBA",  null);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SONGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYLISTS);
        onCreate(db);
    }

    private void insertUser(SQLiteDatabase db, String email, String password, String name,String role) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_ROLE, role);
        db.insert(TABLE_USERS, null, values);
    }

    private void insertSong(SQLiteDatabase db, String name, String artist, String uri) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_SONG_NAME, name);
        values.put(COLUMN_ARTIST, artist);
        values.put(COLUMN_URI, uri);
        db.insert(TABLE_SONGS, null, values);
    }

    // Kiểm tra đăng nhập
    @SuppressLint("Range")
    public User getUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_EMAIL + " = ? AND " + COLUMN_PASSWORD + " = ?",
                new String[]{email, password});
        User user = null;
        if (cursor.moveToFirst()) {
            user = new User(
                    cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_PASSWORD)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_NAME)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_ROLE))
            );
        }
        cursor.close();
        db.close();
        return user;
    }

    // Thêm bài hát
    public boolean addSong(String name, String artist,  String uri) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SONG_NAME, name);
        values.put(COLUMN_ARTIST, artist);
        values.put(COLUMN_URI, uri);
        long result = db.insert(TABLE_SONGS, null, values);
        db.close();
        return result != -1;
    }

    // Lấy tất cả bài hát
    public List<Song> getAllSongs() {
        List<Song> songs = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_SONGS, null);
        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") Song song = new Song(
                        cursor.getString(cursor.getColumnIndex(COLUMN_SONG_NAME)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_ARTIST)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_URI))
                );
                songs.add(song);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return songs;
    }

    public boolean addPlaylist(String playlistName, String userEmail) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PLAYLIST_NAME, playlistName);
        values.put(COLUMN_USER_EMAIL, userEmail);
        long result = db.insert(TABLE_PLAYLISTS, null, values);
        db.close();
        return result != -1;
    }


    // Model User
    public static class User {
        private String email;
        private String password;
        private String name;
        private String role;

        public User(String email, String password, String name,String role) {
            this.email = email;
            this.password = password;
            this.name = name;
            this.role = role;
        }

        public String getEmail() {
            return email;
        }

        public String getName(){
            return name;
        }

        public String getRole() {
            return role;
        }
    }
}