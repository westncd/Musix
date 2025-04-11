package com.tung.musicapp;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.tung.musicapp.Models.Playlist;
import com.tung.musicapp.Models.Song;

import java.util.ArrayList;
import java.util.List;
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "musix.db";
    private static final int DATABASE_VERSION = 8;
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
    public static final String COLUMN_SONG_COUNT = "song_count";
    private static final String CREATE_TABLE_PLAYLISTS =
            "CREATE TABLE " + TABLE_PLAYLISTS + "(" +
                    COLUMN_PLAYLIST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_PLAYLIST_NAME + " TEXT," +
                    COLUMN_USER_EMAIL + " TEXT," +
                    COLUMN_SONG_COUNT + " INTEGER DEFAULT 0," +
                    "FOREIGN KEY (" + COLUMN_USER_EMAIL + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_EMAIL + "))";
    private static final String TABLE_PLAYLIST_SONGS = "playlist_songs";
    private static final String COLUMN_PLAYLIST_SONG_ID = "playlist_song_id";
    private static final String COLUMN_PL_ID = "playlist_id";
    private static final String COLUMN_S_ID = "song_id";
    private static final String CREATE_TABLE_PLAYLIST_SONGS =
            "CREATE TABLE " + TABLE_PLAYLIST_SONGS + "(" +
                    COLUMN_PLAYLIST_SONG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_PL_ID + " INTEGER," +
                    COLUMN_S_ID + " INTEGER," +
                    "FOREIGN KEY (" + COLUMN_PL_ID + ") REFERENCES " + TABLE_PLAYLISTS + "(" + COLUMN_PLAYLIST_ID + ")," +
                    "FOREIGN KEY (" + COLUMN_S_ID + ") REFERENCES " + TABLE_SONGS + "(" + COLUMN_SONG_ID + "))";
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_SONGS);
        db.execSQL(CREATE_TABLE_PLAYLISTS);
        db.execSQL(CREATE_TABLE_PLAYLIST_SONGS);
        insertUser(db, "musician1@email.com", "123456", "Nguyễn Văn Nhạc" ,  "Musician");
        insertUser(db, "listener1@email.com", "abc123", "Nguyễn Văn Thính", "Listener");

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
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYLIST_SONGS);
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
    public List<Song> getAllSongs() {
        List<Song> songs = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_SONGS, null);
        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") Song song = new Song(
                        cursor.getInt(cursor.getColumnIndex(COLUMN_SONG_ID)),
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
    public ArrayList<Playlist> getAllPlaylists(String userEmail) {
        ArrayList<Playlist> playlists = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM playlists WHERE user_email = ?",
                new String[]{userEmail}
        );

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("playlist_id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("playlist_name"));
                int songCount = cursor.getInt(cursor.getColumnIndexOrThrow("song_count"));
                playlists.add(new Playlist(id, name, userEmail, songCount));

            } while (cursor.moveToNext());
        }


        cursor.close();
        db.close();
        return playlists;
    }
    public void addSongToPlaylist(int songId, int playlistId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PL_ID, playlistId);
        values.put(COLUMN_S_ID, songId);

        long result = db.insert(TABLE_PLAYLIST_SONGS, null, values);
        if (result == -1) {
            Log.e("DatabaseHelper", "Lỗi khi thêm bài hát vào playlist.");
        } else {
            Log.d("DatabaseHelper", "Thêm bài hát vào playlist thành công.");
            db.execSQL("UPDATE playlists SET song_count = song_count + 1 WHERE playlist_id = ?",
                    new Object[]{playlistId});
        }

        db.close();
    }
    public List<Song> getSongsInPlaylist(int playlistId) {
        List<Song> songs = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT s.song_id, s.song_name AS name, s.artist, s.uri FROM songs s " +
                "JOIN playlist_songs ps ON s.song_id = ps.song_id " +
                "WHERE ps.playlist_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(playlistId)});

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("song_id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String artist = cursor.getString(cursor.getColumnIndexOrThrow("artist"));
                String uri = cursor.getString(cursor.getColumnIndexOrThrow("uri"));
                songs.add(new Song(id, name, artist, uri));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return songs;
    }
    public void removeSongFromPlaylist(int songId, int playlistId) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            // Xoá bài hát khỏi bảng trung gian
            int rowsAffected = db.delete(
                    TABLE_PLAYLIST_SONGS,
                    COLUMN_S_ID + " = ? AND " + COLUMN_PL_ID + " = ?",
                    new String[]{String.valueOf(songId), String.valueOf(playlistId)}
            );

            // Nếu xoá thành công thì giảm song_count
            if (rowsAffected > 0) {
                db.execSQL("UPDATE " + TABLE_PLAYLISTS +
                                " SET " + COLUMN_SONG_COUNT + " = " + COLUMN_SONG_COUNT + " - 1 " +
                                "WHERE " + COLUMN_PLAYLIST_ID + " = ?",
                        new Object[]{playlistId});
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }
    public void deletePlaylist(String playlistId) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.delete("playlist_songs", "playlist_id = ?", new String[]{playlistId});
            db.delete("playlists", "playlist_id = ?", new String[]{playlistId}); // <-- đã sửa tại đây
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }
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