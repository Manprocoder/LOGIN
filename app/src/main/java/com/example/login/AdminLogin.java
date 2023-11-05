package com.example.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;

public class AdminLogin extends AppCompatActivity {

    String user = "admin";
    int id = 123;
    private TextView textView,check1,check2;
    private EditText editText5, editText6;
    private Button btndangnhap, back_admin;
    private Connection connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);
        showDialog();
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, PackageManager.PERMISSION_GRANTED);
        textView = findViewById(R.id.textView);
        check1 = findViewById(R.id.check1);
        check2 = findViewById(R.id.check2);
        editText5 = findViewById(R.id.editText5);
        editText6 = findViewById(R.id.editText6);
        btndangnhap = findViewById(R.id.btndangnhap);
        back_admin = findViewById(R.id.back_admin);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        connection = SQLConnection.getConnection();

        if (connection != null) {
            textView.setText("SUCCESS CONNECT WITH SQL SERVER");
        } else {
            textView.setText("ERROR");
        }
    }

    public void Login(View view) {
        String username = editText5.getText().toString();
        String password = editText6.getText().toString();
        if (TextUtils.isEmpty(username)) {
            check1.setText("Vui lòng nhập tên đăng nhập!");
        } else if (TextUtils.isEmpty(password)) {
            check2.setText("Vui lòng nhập ID!");
        }else{

        if (username.equals(user) && password.equals(String.valueOf(id))) {
            Toast.makeText(getApplicationContext(), "Bạn đã đăng nhập vào hệ thống", Toast.LENGTH_SHORT).show();
            //Intent intent = new Intent(AdminLogin.this, ???.class);
            //startActivity(intent);
        } else {
            Toast.makeText(getApplicationContext(), "Tên đăng nhập hoặc id không đúng", Toast.LENGTH_SHORT).show();
        }}
    }

    public void comeback(View view) {
        showDialog();
    }

    public void showDialog() {
        final Dialog dialog = new Dialog(AdminLogin.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_login_dialog);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            WindowManager.LayoutParams windowAttributes = window.getAttributes();
            window.setAttributes(windowAttributes);
            window.setWindowAnimations(R.style.DialogAnimation);
        }
        Button qlButton = dialog.findViewById(R.id.qlButton);
        Button gvButton = dialog.findViewById(R.id.gvButton);
        Button svButton = dialog.findViewById(R.id.svButton);

        qlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss(); // Dismiss the dialog when the button is clicked
            }
        });

        gvButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchActivity(LecturerLogin.class);
                dialog.dismiss(); // Dismiss the dialog when the button is clicked
            }
        });

        svButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchActivity(StudentLogin.class);
                dialog.dismiss(); // Dismiss the dialog when the button is clicked
            }
        });
        dialog.show();
    }

    private void launchActivity(Class<?> activityClass) {
        Intent intent = new Intent(AdminLogin.this, activityClass);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }
}

