package com.tung.musicapp.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.tung.musicapp.DatabaseHelper;
import com.tung.musicapp.R;
import com.tung.musicapp.Models.Song;
import com.tung.musicapp.Adapter.SongAdapter;

import java.util.List;
public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private SeekBar seekBar;
    private Handler handler = new Handler();
    private ListView songListView;
    private List<Song> songList;
    private DatabaseHelper dbHelper;
    private SongAdapter songAdapter;
    private String userRole, userName, userEmail;
    private ImageButton playPauseButton, prevButton, nextButton;
    private BottomNavigationView bottomNavigationView;
    private MediaPlayer mediaPlayer;
    private TextView currentSongName;
    private int currentSongIndex = -1;
    private TextView currentTimeText, totalDurationText;
    private Runnable updateSeekBar = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                int currentPosition = mediaPlayer.getCurrentPosition();
                seekBar.setProgress(currentPosition);
                currentTimeText.setText(formatTime(currentPosition));
                handler.postDelayed(this, 500);
            }
        }
    };
    private String formatTime(int millis) {
        int minutes = (millis / 1000) / 60;
        int seconds = (millis / 1000) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeViews();
        getUserInfo();
        setSupportActionBar(toolbar);
        toolbar.setTitle("Xin chào " + userName);
        loadSongList();
        songListView.setOnItemClickListener((parent, view, position, id) -> {
            currentSongIndex = position;
            playSong(songList.get(position));
        });
        setupControlButtons();
        setupBottomNavigation();
        setupSeekBar();
    }
    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        seekBar = findViewById(R.id.seekBar);
        songListView = findViewById(R.id.song_list);
        playPauseButton = findViewById(R.id.play_pause_button);
        prevButton = findViewById(R.id.prev_button);
        nextButton = findViewById(R.id.next_button);
        currentTimeText = findViewById(R.id.current_time);
        totalDurationText = findViewById(R.id.total_duration);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        currentSongName = findViewById(R.id.current_song_name);
        dbHelper = new DatabaseHelper(this);
    }
    private void getUserInfo() {
        Intent intent = getIntent();
        userName = intent.getStringExtra("user_name");
        userEmail = intent.getStringExtra("user_email");
        userRole = intent.getStringExtra("user_role");
    }
    private void loadSongList() {
        songList = dbHelper.getAllSongs();
        songAdapter = new SongAdapter(this, songList, userEmail);
        songListView.setAdapter(songAdapter);
    }
    private void setupControlButtons() {
        playPauseButton.setOnClickListener(v -> playPause());
        prevButton.setOnClickListener(v -> playPrevious());
        nextButton.setOnClickListener(v -> playNext());
    }
    private void setupBottomNavigation() {
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_create) {
                showCreateOptions();
                return true;
            } else if (id == R.id.nav_home) {
                Toast.makeText(this, "Đã chọn trang chủ", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.nav_search) {
                return true;
            } else if (id == R.id.nav_library) {
                startActivityWithExtras(LibraryActivity.class);
                return true;
            } else if (id == R.id.nav_profile) {
                startActivityWithExtras(ProfileActivity.class);
                return true;
            }
            return false;
        });
    }
    private void setupSeekBar() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    mediaPlayer.seekTo(progress);
                    currentTimeText.setText(formatTime(progress));
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                handler.removeCallbacks(updateSeekBar);
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                handler.postDelayed(updateSeekBar, 0);
            }
        });
    }
    private void playPause() {
        if (mediaPlayer == null) {
            Toast.makeText(this, "Chưa chọn bài hát nào!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            playPauseButton.setImageResource(R.drawable.play);
            handler.removeCallbacks(updateSeekBar);
        } else {
            mediaPlayer.start();
            playPauseButton.setImageResource(R.drawable.pause);
            handler.postDelayed(updateSeekBar, 0);
        }
    }
    private void playPrevious() {
        if (songList == null || songList.isEmpty() || currentSongIndex <= 0) {
            Toast.makeText(this, "Không có bài hát trước đó!", Toast.LENGTH_SHORT).show();
            return;
        }
        currentSongIndex--;
        playSong(songList.get(currentSongIndex));
    }
    private void playNext() {
        if (songList == null || songList.isEmpty() || currentSongIndex >= songList.size() - 1) {
            Toast.makeText(this, "Không có bài hát tiếp theo!", Toast.LENGTH_SHORT).show();
            return;
        }
        currentSongIndex++;
        playSong(songList.get(currentSongIndex));
    }
    private void playSong(Song song) {
        stopCurrentMediaPlayer();
        try {
            Uri songUri = Uri.parse(song.getUri());
            Log.d("SONG_URI", "Đang phát bài: " + song.getName() + " - " + songUri.toString());
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(this, songUri);
            mediaPlayer.prepare();
            mediaPlayer.start();
            currentSongName.setText("Đang phát: " + song.getName());
            playPauseButton.setImageResource(R.drawable.pause);
            seekBar.setMax(mediaPlayer.getDuration());
            totalDurationText.setText(formatTime(mediaPlayer.getDuration()));
            handler.postDelayed(updateSeekBar, 0);
            mediaPlayer.setOnCompletionListener(mp -> {
                seekBar.setProgress(0);
                currentTimeText.setText("00:00");
                playPauseButton.setImageResource(R.drawable.play);
                handler.removeCallbacks(updateSeekBar);
                playNext();
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi phát bài hát: " + e.getMessage(), Toast.LENGTH_LONG).show();
            currentSongName.setText("Đang phát: Không có bài hát");
        }
    }
    private void stopCurrentMediaPlayer() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
            handler.removeCallbacks(updateSeekBar);
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
            if (id == R.id.create_playlist) {
                startActivityWithExtras(AddPlaylistActivity.class);
                return true;
            } else if (id == R.id.add_song) {
                startActivityWithExtras(AddSongActivity.class);
                return true;
            }
            return false;
        });
        popupMenu.show();
    }
    private void startActivityWithExtras(Class<?> activityClass) {
        Intent intent = new Intent(MainActivity.this, activityClass);
        intent.putExtra("user_email", userEmail);
        intent.putExtra("user_name", userName);
        intent.putExtra("user_role", userRole);
        if (mediaPlayer != null && currentSongIndex >= 0 && currentSongIndex < songList.size()) {
            Song currentSong = songList.get(currentSongIndex);
            intent.putExtra("current_song_name", currentSong.getName());
            intent.putExtra("current_song_uri", currentSong.getUri());
            intent.putExtra("current_song_position", mediaPlayer.getCurrentPosition());
            intent.putExtra("current_song_index", currentSongIndex);
            intent.putExtra("is_playing", mediaPlayer.isPlaying());
        }
        startActivity(intent);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopCurrentMediaPlayer();
        handler.removeCallbacks(updateSeekBar);
    }
}