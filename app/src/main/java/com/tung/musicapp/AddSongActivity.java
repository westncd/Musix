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

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AddSongActivity extends AppCompatActivity {
    private Button addSongButton, pickMp3Button;
    private DatabaseHelper dbHelper;
    private EditText songNameEditText;
    private String userEmail, userName, userRole;
    private BottomNavigationView bottomNavigationView;
    private Uri selectedMp3Uri;

    private static final int PICK_MP3_REQUEST = 102;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_song);

        addSongButton = findViewById(R.id.add_song_button);
        songNameEditText = findViewById(R.id.song_name_input);
        pickMp3Button = findViewById(R.id.pick_mp3_button);
        dbHelper = new DatabaseHelper(this);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        Intent intent = getIntent();
        userName = intent.getStringExtra("user_name");
        userEmail = intent.getStringExtra("user_email");
        userRole = intent.getStringExtra("user_role");

        pickMp3Button.setOnClickListener(v -> {
            Intent pickIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            pickIntent.addCategory(Intent.CATEGORY_OPENABLE);
            pickIntent.setType("audio/mpeg"); // chỉ chọn file mp3
            try {
                startActivityForResult(pickIntent, PICK_MP3_REQUEST);
            } catch (Exception e) {
                Toast.makeText(this, "Lỗi mở file picker: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


        addSongButton.setOnClickListener(v->{
            String songName = songNameEditText.getText().toString().trim();
            String songUrl = selectedMp3Uri != null ? selectedMp3Uri.toString() : "";
            if (!songName.isEmpty() && !songUrl.isEmpty()) {
                dbHelper.addSong(songName, userName, songUrl);
                Intent homeIntent = new Intent(AddSongActivity.this, MainActivity.class);
                homeIntent.putExtra("user_name", userName);
                homeIntent.putExtra("user_email", userEmail);
                homeIntent.putExtra("user_role", userRole);
                startActivity(homeIntent);
                Toast.makeText(this, "Đã thêm bài hát thành công!", Toast.LENGTH_SHORT).show();
                finish(); // Đóng activity sau khi thêm thành công
            } else {
                Toast.makeText(this, "Vui lòng nhập tên bài hát và chọn file MP3!", Toast.LENGTH_SHORT).show();
            }
        });

        intent = getIntent();
        userName = intent.getStringExtra("user_name");
        userEmail = intent.getStringExtra("user_email");
        userRole = intent.getStringExtra("user_role");
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_create) {
                showCreateOptions();
                return true;
            } else if (id == R.id.nav_home) {
                Intent homeIntent = new Intent(AddSongActivity.this, MainActivity.class);
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
                Intent libIntent = new Intent(AddSongActivity.this, LibraryActivity.class);
                libIntent.putExtra("user_email", userEmail);
                libIntent.putExtra("user_name", userName);
                libIntent.putExtra("user_role", userRole);
                startActivity(libIntent);
                return true;
            }
            else if (id == R.id.nav_profile) {
                Intent profileIntent = new Intent(AddSongActivity.this, ProfileActivity.class);
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
                Intent intent = new Intent(AddSongActivity.this, AddPlaylistActivity.class);
                intent.putExtra("user_email", userEmail);
                intent.putExtra("user_name", userName);
                intent.putExtra("user_role", userRole);
                startActivity(intent);
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

            // 🔐 Cấp quyền truy cập vĩnh viễn
            final int takeFlags = data.getFlags() & Intent.FLAG_GRANT_READ_URI_PERMISSION;
            getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

            selectedMp3Uri = uri; // lưu lại Uri
            String uriString = uri.toString();

            TextView songPathTextView = findViewById(R.id.song_url_input); // TextView hiển thị đường dẫn
            songPathTextView.setText(uriString);
        } else {
            Toast.makeText(this, "Hủy chọn file hoặc lỗi: " + resultCode, Toast.LENGTH_LONG).show();
        }
    }

}