package com.tung.musicapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AddSongActivity extends AppCompatActivity {
    private Button addSongButton, pickMp3Button;
    private EditText songNameEditText;
    private TextView songPathTextView;
    private BottomNavigationView bottomNavigationView;
    private DatabaseHelper dbHelper;

    private Uri selectedMp3Uri;

    private String userEmail, userName, userRole;
    private static final int PICK_MP3_REQUEST = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_song);

        // Ánh xạ view
        addSongButton = findViewById(R.id.add_song_button);
        pickMp3Button = findViewById(R.id.pick_mp3_button);
        songNameEditText = findViewById(R.id.song_name_input);
        songPathTextView = findViewById(R.id.song_url_input);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_create);


        dbHelper = new DatabaseHelper(this);

        // Nhận user info
        Intent intent = getIntent();
        userName = intent.getStringExtra("user_name");
        userEmail = intent.getStringExtra("user_email");
        userRole = intent.getStringExtra("user_role");

        pickMp3Button.setOnClickListener(v -> {
            Intent mp3pickintent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            mp3pickintent.addCategory(Intent.CATEGORY_OPENABLE);
            mp3pickintent.setType("audio/mpeg");
            try {
                startActivityForResult(mp3pickintent, PICK_MP3_REQUEST);
            } catch (Exception e) {
                Toast.makeText(this, "Lỗi mở file picker: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });



        addSongButton.setOnClickListener(v -> {
            String songName = songNameEditText.getText().toString().trim();
            String songUrl = selectedMp3Uri != null ? selectedMp3Uri.toString() : "";

            if (!songName.isEmpty() && !songUrl.isEmpty()) {
                dbHelper.addSong(songName, userName, songUrl);
                Toast.makeText(this, "Đã thêm bài hát thành công!", Toast.LENGTH_SHORT).show();

                Intent homeIntent = new Intent(AddSongActivity.this, MainActivity.class);
                homeIntent.putExtra("user_name", userName);
                homeIntent.putExtra("user_email", userEmail);
                homeIntent.putExtra("user_role", userRole);
                startActivity(homeIntent);
                finish();
            } else {
                Toast.makeText(this, "Vui lòng nhập tên bài hát và chọn file MP3!", Toast.LENGTH_SHORT).show();
            }
        });

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_create) {
                showCreateOptions();
                return true;
            } else if (id == R.id.nav_home) {
                navigateTo(MainActivity.class);
                return true;
            } else if (id == R.id.nav_library) {
                navigateTo(LibraryActivity.class);
                return true;
            } else if (id == R.id.nav_profile) {
                navigateTo(ProfileActivity.class);
                return true;
            }
            return false;
        });
    }

    private void navigateTo(Class<?> destination) {
        Intent intent = new Intent(AddSongActivity.this, destination);
        intent.putExtra("user_email", userEmail);
        intent.putExtra("user_name", userName);
        intent.putExtra("user_role", userRole);
        startActivity(intent);
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
                navigateTo(AddPlaylistActivity.class);
                return true;
            } else if (id == R.id.add_song) {
                Toast.makeText(this, "Đã chọn thêm bài hát", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });
        popupMenu.show();
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

            final int takeFlags = data.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            try {
                getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } catch (SecurityException e) {
                Toast.makeText(this, "Không thể cấp quyền vĩnh viễn: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            selectedMp3Uri = uri;
            songPathTextView.setText(uri.toString());
        } else {
            Toast.makeText(this, "Hủy chọn file hoặc lỗi: " + resultCode, Toast.LENGTH_LONG).show();
        }
    }
}
