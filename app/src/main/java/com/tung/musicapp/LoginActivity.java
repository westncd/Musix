package com.tung.musicapp;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
public class LoginActivity extends AppCompatActivity {
    private EditText emailInput, passwordInput;
    private Button loginButton;
    private DatabaseHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        loginButton = findViewById(R.id.login_button);
        dbHelper = new DatabaseHelper(this);
        loginButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Nhập email và password đi bro!", Toast.LENGTH_SHORT).show();
                return;
            }
            DatabaseHelper.User user = dbHelper.getUser(email, password);
            if (user == null) {
                Toast.makeText(this, "Sai email hoặc password, thử lại đi!", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("user_email", user.getEmail());
            intent.putExtra("user_name", user.getName());
            intent.putExtra("user_role", user.getRole());
            startActivity(intent);
            finish();
        });
    }
}