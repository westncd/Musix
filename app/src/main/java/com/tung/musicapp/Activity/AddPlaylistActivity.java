package com.tung.musicapp.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.tung.musicapp.DatabaseHelper;
import com.tung.musicapp.R;

public class AddPlaylistActivity extends AppCompatActivity {
    private EditText playlistNameInput;
    private DatabaseHelper dbHelper;
    private Button createButton;
    private BottomNavigationView bottomNavigationView;
    private String userEmail, userName, userRole;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_playlist);
        playlistNameInput = findViewById(R.id.playlist_name_input);
        createButton = findViewById(R.id.create_button);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_create);
        dbHelper = new DatabaseHelper(this);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_create) {
                showCreateOptions();
                return true;
            } else if (id == R.id.nav_home) {
                Intent homeIntent = new Intent(AddPlaylistActivity.this, MainActivity.class);
                homeIntent.putExtra("user_email", userEmail);
                homeIntent.putExtra("user_name", userName);
                homeIntent.putExtra("user_role", userRole);
                startActivity(homeIntent);
                return true;
            } else if (id == R.id.nav_search) {
                // xử lý tìm kiếm
                return true;
            }
            else if (id == R.id.nav_library) {
                Intent libIntent = new Intent(AddPlaylistActivity.this, LibraryActivity.class);
                libIntent.putExtra("user_email", userEmail);
                libIntent.putExtra("user_name", userName);
                libIntent.putExtra("user_role", userRole);
                startActivity(libIntent);
                return true;
            }
            else if (id == R.id.nav_profile) {
                Intent profileIntent = new Intent(AddPlaylistActivity.this, ProfileActivity.class);
                profileIntent.putExtra("user_email", userEmail);
                profileIntent.putExtra("user_name", userName);
                profileIntent.putExtra("user_role", userRole);
                startActivity(profileIntent);
                return true;
            }

            return false;
        });
        Intent intent = getIntent();
        userEmail = intent.getStringExtra("user_email");
        userName = intent.getStringExtra("user_name");
        createButton.setOnClickListener(v -> {
            String playlistName = playlistNameInput.getText().toString().trim();
            if (playlistName.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tên playlist!", Toast.LENGTH_SHORT).show();
                return;
                }
            boolean success = dbHelper.addPlaylist(playlistName, userEmail);
            if (success) {
                Intent libIntent = new Intent(AddPlaylistActivity.this, LibraryActivity.class);
                libIntent.putExtra("user_email", userEmail);
                libIntent.putExtra("user_name", userName);
                libIntent.putExtra("user_role", userRole);
                startActivity(libIntent);
                Toast.makeText(this, "Tạo playlist thành công!", Toast.LENGTH_SHORT).show();
                finish(); // Đóng activity sau khi tạo thành công
            }
            else {
                Toast.makeText(this, "Tạo playlist thất bại!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void showCreateOptions() {
        Context wrapper = new ContextThemeWrapper(this, R.style.PopupMenuDark);
        PopupMenu popupMenu = new PopupMenu(wrapper, findViewById(R.id.bottom_navigation));
        popupMenu.getMenuInflater().inflate(R.menu.create_menu, popupMenu.getMenu());
        if (!"Musician".equals(userRole)) {
            popupMenu.getMenu().findItem(R.id.add_song).setVisible(false);
        }
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            int id = menuItem.getItemId();
            if (id == R.id.create_playlist) {
                Toast.makeText(this, "Đã chọn tạo playlist", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.add_song) {
                Intent intent = new Intent(AddPlaylistActivity.this, AddSongActivity.class);
                intent.putExtra("user_email", userEmail);
                intent.putExtra("user_name", userName);
                intent.putExtra("user_role", userRole);
                startActivity(intent);
                return true;
            }
            return false;
        });
        popupMenu.show();
    }
}