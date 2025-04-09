package com.tung.musicapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class LibraryActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private String userEmail, userName, userRole;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        Intent intent = getIntent();
        userEmail = intent.getStringExtra("user_email");
        userName = intent.getStringExtra("user_name");
        userRole = intent.getStringExtra("user_role");
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_create) {
                showCreateOptions();
                return true;
            } else if (id == R.id.nav_home) {
                Intent homeIntent = new Intent(LibraryActivity.this, MainActivity.class);
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
                Toast.makeText(this, "Đã chọn thư viện", Toast.LENGTH_SHORT).show();
                return true;
            }
            else if (id == R.id.nav_profile) {
                Intent profileIntent = new Intent(LibraryActivity.this, ProfileActivity.class);
                profileIntent.putExtra("user_email", userEmail);
                profileIntent.putExtra("user_name", userName);
                profileIntent.putExtra("user_role", userRole);
                startActivity(profileIntent);
                return true;
            }

            return false;
        });

    }
    private void showCreateOptions() {
        Context wrapper = new ContextThemeWrapper(this, R.style.PopupMenuDark);
        PopupMenu popupMenu = new PopupMenu(wrapper, findViewById(R.id.bottom_navigation));
        popupMenu.getMenuInflater().inflate(R.menu.create_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(menuItem -> {
            int id = menuItem.getItemId();
            if (id == R.id.create_playlist) {
                Intent intent = new Intent(LibraryActivity.this, AddPlaylistActivity.class);
                intent.putExtra("user_email", userEmail);
                intent.putExtra("user_name", userName);
                intent.putExtra("user_role", userRole);
                startActivity(intent);
                return true;
            } else if (id == R.id.add_song) {
                Intent intent = new Intent(LibraryActivity.this, AddSongActivity.class);
                intent.putExtra("user_email", userEmail);
                intent.putExtra("user_name", userName);
                intent.putExtra("user_role", userRole);                return true;
            }
            return false;
        });

        popupMenu.show();
    }
}