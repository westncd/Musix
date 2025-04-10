package com.tung.musicapp.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.tung.musicapp.R;

public class ProfileActivity extends AppCompatActivity {
    private Button logoutButton;
    private TextView userNameTextView, userEmailTextView;
    private String userName, userEmail, userRole;
    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        logoutButton = findViewById(R.id.logout_button);
        userEmailTextView = findViewById(R.id.user_email);
        userNameTextView = findViewById(R.id.user_name);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_profile);
        Intent intent = getIntent();
        userName = intent.getStringExtra("user_name");
        userEmail = intent.getStringExtra("user_email");
        userRole = intent.getStringExtra("user_role");
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        userNameTextView.setText(userName);
        userEmailTextView.setText(userEmail);
        logoutButton.setOnClickListener(v -> {
            Intent logoutIntent = new Intent(ProfileActivity.this, LoginActivity.class);
            logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(logoutIntent);
            finish();
        });
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_create) {
                showCreateOptions();
                return true;
            } else if (id == R.id.nav_home) {
                Intent homeIntent = new Intent(ProfileActivity.this, MainActivity.class);
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
                Intent libIntent = new Intent(ProfileActivity.this, LibraryActivity.class);
                libIntent.putExtra("user_email", userEmail);
                libIntent.putExtra("user_name", userName);
                libIntent.putExtra("user_role", userRole);
                startActivity(libIntent);
                return true;
            }
            else if (id == R.id.nav_profile) {
                Toast.makeText(this, "Đã chọn hồ sơ", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
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
                Intent intent = new Intent(ProfileActivity.this, AddPlaylistActivity.class);
                intent.putExtra("user_email", userEmail);
                intent.putExtra("user_name", userName);
                intent.putExtra("user_role", userRole);
                startActivity(intent);
                return true;
            } else if (id == R.id.add_song) {
                Intent intent = new Intent(ProfileActivity.this, AddSongActivity.class);
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