package com.tung.musicapp.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.tung.musicapp.DatabaseHelper;
import com.tung.musicapp.Adapter.LibraryAdapter;
import com.tung.musicapp.Models.Playlist;
import com.tung.musicapp.R;

import java.util.ArrayList;
public class LibraryActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private String userEmail, userName, userRole;
    private DatabaseHelper dbHelper;
    private LibraryAdapter libraryAdapter;
    private ListView playlistListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);
        dbHelper = new DatabaseHelper(this);
        playlistListView = findViewById(R.id.playlist_list);
        Intent intent = getIntent();
        userEmail = intent.getStringExtra("user_email");
        userName = intent.getStringExtra("user_name");
        userRole = intent.getStringExtra("user_role");
        Log.d("LibraryActivity", "User Email: " + userEmail);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_library);
        loadPlaylists();
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
                return true;
            } else if (id == R.id.nav_library) {
                Toast.makeText(this, "Đã chọn thư viện", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.nav_profile) {
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
    private void loadPlaylists() {
        if (userEmail == null) {
            Toast.makeText(this, "Không tìm thấy email người dùng", Toast.LENGTH_LONG).show();
            return;
        }
        ArrayList<Playlist> playlists = dbHelper.getAllPlaylists(userEmail);
        libraryAdapter = new LibraryAdapter(this, playlists, userEmail, userName, userRole);
        playlistListView.setAdapter(libraryAdapter);
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
            Intent intent = null;
            if (id == R.id.create_playlist) {
                intent = new Intent(LibraryActivity.this, AddPlaylistActivity.class);
            } else if (id == R.id.add_song) {
                intent = new Intent(LibraryActivity.this, AddSongActivity.class);
            }
            if (intent != null) {
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