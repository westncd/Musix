package com.tung.musicapp.Activity;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
    private ActivityResultLauncher<Intent> playlistLauncher;
    private ImageButton playPauseButton, prevButton, nextButton;
    private MediaPlayer mediaPlayer;
    private TextView currentSongName;
    private int currentSongIndex = -1;

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



        playPauseButton = findViewById(R.id.play_pause_button);
        prevButton = findViewById(R.id.prev_button);
        nextButton = findViewById(R.id.next_button);

        Log.d("LibraryActivity", "User Email: " + userEmail);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_library);

        playlistLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getBooleanExtra("playlist_deleted", false)) {
                            loadPlaylists();
                            Toast.makeText(this, "Đã cập nhật thư viện", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        loadPlaylists();

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_create) {
                showCreateOptions();
                return true;
            } else if (id == R.id.nav_home) {
                Intent homeIntent = new Intent(this, MainActivity.class);
                homeIntent.putExtra("user_email", userEmail);
                homeIntent.putExtra("user_name", userName);
                homeIntent.putExtra("user_role", userRole);
                startActivity(homeIntent);
                return true;
            }
            else if (id == R.id.nav_library) {
                Toast.makeText(this, "Đã chọn Thư viện", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.nav_profile) {
                Intent profileIntent = new Intent(this, ProfileActivity.class);
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
        if (libraryAdapter == null) {
            libraryAdapter = new LibraryAdapter(this, playlists, userEmail, userName, userRole, playlistLauncher);
            playlistListView.setAdapter(libraryAdapter);
        } else {
            libraryAdapter.clear();
            libraryAdapter.addAll(playlists);
        }
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
                intent = new Intent(this, AddPlaylistActivity.class);
            } else if (id == R.id.add_song) {
                intent = new Intent(this, AddSongActivity.class);
            }
            if (intent != null) {
                intent.putExtra("user_email", userEmail);
                intent.putExtra("user_name", userName);
                intent.putExtra("user_role", userRole);
                intent.putExtra("current_song_name", currentSongName.getText().toString().replace("Đang phát: ", ""));
                startActivity(intent);
                return true;
            }
            return false;
        });

        popupMenu.show();
    }
}
