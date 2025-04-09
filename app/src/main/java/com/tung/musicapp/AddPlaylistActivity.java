package com.tung.musicapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AddPlaylistActivity extends AppCompatActivity {
    private EditText playlistNameInput;
    private DatabaseHelper dbHelper;
    private Button createButton;
    private BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_playlist);

        playlistNameInput = findViewById(R.id.playlist_name_input);
        createButton = findViewById(R.id.create_button);
        dbHelper = new DatabaseHelper(this);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_create) {
                showCreateOptions();
                return true;
            } else if (id == R.id.nav_home) {
                Intent homeIntent = new Intent(AddPlaylistActivity.this, MainActivity.class);
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
        createButton.setOnClickListener(v -> {
            String playlistName = playlistNameInput.getText().toString().trim();
            if (playlistName.isEmpty()) {
                Toast.makeText(this, "Nhập tên playlist đi bro!", Toast.LENGTH_SHORT).show();
                return;
                }
            boolean success = dbHelper.addPlaylist(playlistName);
            if (success) {
                Toast.makeText(this, "Tạo playlist thành công!", Toast.LENGTH_SHORT).show();
                finish(); // Đóng activity sau khi tạo thành công
                } else {
                Toast.makeText(this, "Lỗi tạo playlist!", Toast.LENGTH_SHORT).show();
                }
            });
    }
    private void showCreateOptions() {
        Context wrapper = new ContextThemeWrapper(this, R.style.PopupMenuDark);
        PopupMenu popupMenu = new PopupMenu(wrapper, findViewById(R.id.bottom_navigation));
        popupMenu.getMenuInflater().inflate(R.menu.create_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(menuItem -> {
            int id = menuItem.getItemId();
            if (id == R.id.create_playlist) {
                Intent intent = new Intent(AddPlaylistActivity.this, AddPlaylistActivity.class);
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
}