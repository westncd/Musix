package com.tung.musicapp.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.tung.musicapp.DatabaseHelper;
import com.tung.musicapp.Adapter.PlaylistAdapter;
import com.tung.musicapp.R;
import com.tung.musicapp.Models.Song;

import java.util.List;

public class PlaylistActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ListView playlistSongListView;
    private BottomNavigationView bottomNavigationView;
    private PlaylistAdapter adapter;
    private DatabaseHelper dbHelper;
    private String playlistId;
    private String userEmail, playlistName, userName, userRole;  // nếu cần dùng
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);
        dbHelper = new DatabaseHelper(this);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        playlistId = getIntent().getStringExtra("playlist_id");
        playlistName = getIntent().getStringExtra("playlist_name");
        userEmail = getIntent().getStringExtra("user_email");
        userName = getIntent().getStringExtra("user_name");
        userRole = getIntent().getStringExtra("user_role");
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_library);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_create) {
                showCreateOptions();
                return true;
            } else if (id == R.id.nav_home) {
                Intent homeIntent = new Intent(PlaylistActivity.this, MainActivity.class);
                homeIntent.putExtra("user_email", userEmail);
                homeIntent.putExtra("user_name", userName);
                homeIntent.putExtra("user_role", userRole);
                startActivity(homeIntent);
                return true;
            }
            else if (id == R.id.nav_library) {
                Intent libIntent = new Intent( PlaylistActivity.this, LibraryActivity.class);
                libIntent.putExtra("user_email", userEmail);
                libIntent.putExtra("user_name", userName);
                libIntent.putExtra("user_role", userRole);
                startActivity(libIntent);
                return true;
            }
            else if (id == R.id.nav_profile) {
                Intent profileIntent = new Intent(PlaylistActivity.this, ProfileActivity.class);
                profileIntent.putExtra("user_email", userEmail);
                profileIntent.putExtra("user_name", userName);
                profileIntent.putExtra("user_role", userRole);
                startActivity(profileIntent);
                return true;
            }
            return false;
        });
        if (playlistName != null) {
            toolbar.setTitle(playlistName);
        }
        playlistSongListView = findViewById(R.id.playlist_songs);
        loadSongs();
        userEmail = intent.getStringExtra("user_email");
        if (playlistName != null) {
            toolbar.setTitle(playlistName);
        } else {
            toolbar.setTitle("Playlist");
        }
        playlistSongListView = findViewById(R.id.playlist_songs);
        loadSongs();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.playlist_toolbar_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete_playlist) {
            confirmDeletePlaylist();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void confirmDeletePlaylist() {
        new AlertDialog.Builder(this)
                .setTitle("Xoá playlist")
                .setMessage("Bạn có chắc chắn muốn xoá playlist này không?")
                .setPositiveButton("Xoá", (dialog, which) -> {
                    dbHelper.deletePlaylist(playlistId);
                    Toast.makeText(this, "Đã xoá playlist!", Toast.LENGTH_SHORT).show();

                    // Đặt kết quả để LibraryActivity biết là có xoá
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("playlist_deleted", true);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                })
                .setNegativeButton("Huỷ", null)
                .show();
    }

    private void loadSongs() {
        if (playlistId == null || playlistId.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy playlist.", Toast.LENGTH_SHORT).show();
            return;
        }
        int id = Integer.parseInt(playlistId);
        List<Song> songs = dbHelper.getSongsInPlaylist(id);
        adapter = new PlaylistAdapter(this, songs, song -> {
            dbHelper.removeSongFromPlaylist(song.getId(), id);
            songs.remove(song);
            adapter.notifyDataSetChanged();
            Toast.makeText(this, "Đã xoá bài hát", Toast.LENGTH_SHORT).show();
        });

        playlistSongListView.setAdapter(adapter);
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
                Intent intent = new Intent(PlaylistActivity.this, AddPlaylistActivity.class);
                intent.putExtra("user_email", userEmail);
                intent.putExtra("user_name", userName);
                intent.putExtra("user_role", userRole);
                startActivity(intent);
                return true;
            } else if (id == R.id.add_song) {
                Intent intent = new Intent(PlaylistActivity.this, AddSongActivity.class);
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