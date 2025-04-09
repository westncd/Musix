package com.tung.musicapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ListView songListView;
    private Button addSongButton, logoutButton;
    private DatabaseHelper dbHelper;
    private SongAdapter songAdapter;
    private String userRole;
    private ImageButton playPauseButton, prevButton, nextButton;
    private BottomNavigationView bottomNavigationView;
    private static final int PICK_MP3_REQUEST = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Thiết lập Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Khởi tạo views
        songListView = findViewById(R.id.song_list);
        addSongButton = findViewById(R.id.add_song_button);
        logoutButton = findViewById(R.id.logout_button);
        playPauseButton = findViewById(R.id.play_pause_button);
        prevButton = findViewById(R.id.prev_button);
        nextButton = findViewById(R.id.next_button);
        toolbar = findViewById(R.id.toolbar);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        dbHelper = new DatabaseHelper(this);

        Intent intent = getIntent();
        String userName = intent.getStringExtra("user_name");
        toolbar.setTitle("Xin chào " + userName );

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_create) {
                showCreateOptions();
                return true;
            } else if (id == R.id.nav_home) {
                Intent homeIntent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(homeIntent);
                return true;
            } else if (id == R.id.nav_search) {
                // xử lý tìm kiếm
                return true;
            }
            else if (id == R.id.nav_library) {
                // xử lý thư viện
                return true;
            }
            else if (id == R.id.nav_profile) {
                // xử lý trang cá nhân
                return true;
            }

            return false;
        });



        // Load danh sách bài hát
        loadSongs();

        // Nút Add Song
        addSongButton.setOnClickListener(v -> {
            Intent pickIntent = new Intent(Intent.ACTION_GET_CONTENT);
            pickIntent.setType("audio/mpeg");
            try {
                startActivityForResult(pickIntent, PICK_MP3_REQUEST);
            } catch (Exception e) {
                Toast.makeText(this, "Lỗi mở file picker: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        // Nút Logout
        logoutButton.setOnClickListener(v -> {
            Intent logoutIntent = new Intent(MainActivity.this, LoginActivity.class);
            logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(logoutIntent);
            finish();
        });
    }
    private void showCreateOptions() {
        Context wrapper = new ContextThemeWrapper(this, R.style.PopupMenuDark);
        PopupMenu popupMenu = new PopupMenu(wrapper, findViewById(R.id.bottom_navigation));
        popupMenu.getMenuInflater().inflate(R.menu.create_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(menuItem -> {
            int id = menuItem.getItemId();
            if (id == R.id.create_playlist) {
                Intent intent = new Intent(MainActivity.this, AddPlaylistActivity.class);
                startActivity(intent);
                return true;
            } else if (id == R.id.add_song) {
                Toast.makeText(this, "Thêm bài hát", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });

        popupMenu.show();
    }



    private void loadSongs() {
        List<Song> songList = dbHelper.getAllSongs();
        songAdapter = new SongAdapter(this, songList);
        songListView.setAdapter(songAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_MP3_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri == null) {
                Toast.makeText(this, "Không lấy được file MP3, Uri null!", Toast.LENGTH_LONG).show();
                return;
            }
            String uriString = uri.toString();
            String songName = getFileNameFromUri(uri);
            if (songName == null) {
                songName = "Unknown Song";
            }
            // Lưu bài hát vào database (giả lập artist và album)
            boolean success = dbHelper.addSong(songName, "Unknown Artist", "Unknown Album", uriString);
            if (success) {
                loadSongs();
                Toast.makeText(this, "Thêm bài hát thành công: " + songName, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Lỗi thêm bài hát!", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Hủy chọn file hoặc lỗi: " + resultCode, Toast.LENGTH_LONG).show();
        }
    }

    private String getFileNameFromUri(Uri uri) {
        String fileName = null;
        String[] projection = {MediaStore.Audio.Media.DISPLAY_NAME};
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME);
                if (columnIndex != -1) {
                    fileName = cursor.getString(columnIndex);
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi lấy tên file: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return fileName;
    }
}