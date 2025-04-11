package com.tung.musicapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.tung.musicapp.DatabaseHelper;
import com.tung.musicapp.R;

public class RegisterActivity extends AppCompatActivity {
    private EditText nameInput, emailInput, passwordInput;
    private Spinner roleSpinner;
    private Button registerButton;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Khởi tạo các view
        nameInput = findViewById(R.id.name_input);
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        roleSpinner = findViewById(R.id.role_spinner);
        registerButton = findViewById(R.id.register_button);
        dbHelper = new DatabaseHelper(this);

        // Thiết lập Spinner cho vai trò (Musician/Listener)
        String[] roles = {"Musician", "Listener"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(adapter);

        // Xử lý sự kiện khi nhấn nút Register
        registerButton.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            String role = roleSpinner.getSelectedItem().toString();

            // Kiểm tra các trường nhập liệu
            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Thêm người dùng mới vào cơ sở dữ liệu
            boolean success = dbHelper.addUser(name, email, password, role);
            if (success) {
                Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                // Chuyển về màn hình đăng nhập sau khi đăng ký thành công
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Đăng ký thất bại! Email có thể đã tồn tại.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}