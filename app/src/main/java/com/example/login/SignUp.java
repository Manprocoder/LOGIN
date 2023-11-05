package com.example.login;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SignUp extends AppCompatActivity {
    Button btnsignup;
    RadioButton gv, sv;
    TextView display_signup, check_user, check_pass, check_confirm, check_email;
    Connection connect = null;
    private EditText username_signup, email_signup, password_signup, confirm_signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        btnsignup = findViewById(R.id.btnsignup);
        username_signup = findViewById(R.id.username_signup);
        password_signup = findViewById(R.id.password_signup);
        confirm_signup = findViewById(R.id.confirm_signup);
        email_signup = findViewById(R.id.email_signup);
        display_signup = findViewById(R.id.display_signup);
        check_user = findViewById(R.id.check_user);
        check_pass = findViewById(R.id.check_pass);
        check_confirm = findViewById(R.id.check_confirm);
        check_email = findViewById(R.id.check_email);
        gv = findViewById(R.id.gv);
        sv = findViewById(R.id.sv);

        SQLConnection b = new SQLConnection();
        connect = b.getConnection();
        if (connect != null) {
            display_signup.setText("SUCCESS");
        } else {
            display_signup.setText("ERROR");
        }

        btnsignup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                signup();
            }

            public void signup() {
                String username = username_signup.getText().toString().trim();
                String password = password_signup.getText().toString().trim();
                String confirm = confirm_signup.getText().toString().trim();
                String email = email_signup.getText().toString().trim();
                if (TextUtils.isEmpty(username)) {
                    check_user.setText("Tên đăng nhập là bắt buộc");
                }else if(isUsernameExists(username)){
                    check_user.setText("Tên đăng nhập đã tồn tại!");
                }else if (TextUtils.isEmpty(email)) {
                    check_email.setText("Email là bắt buộc");
                }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    check_email.setText("Định dạng sai!!!");
                }else if (TextUtils.isEmpty(password)) {
                    check_pass.setText("Mật khẩu là bắt buộc");
                }else if (TextUtils.isEmpty(confirm)) {
                    check_confirm.setText("Vui lòng xác nhận mật khẩu");
                }else if (!password.equals(confirm)) {
                    check_confirm.setText("Mật khẩu và xác nhận phải giống nhau!");
                }else if (!gv.isChecked() && !sv.isChecked()) {
                    Toast.makeText(getApplicationContext(), "Bạn là Giảng viên hay Sinh viên", Toast.LENGTH_SHORT).show();
                }else{

                if (addAccount(username, password, email)) {
                    Toast.makeText(getApplicationContext(), "Đăng ký tài khoản thành công!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignUp.this, AdminLogin.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Đăng ký tài khoản thất bại", Toast.LENGTH_SHORT).show();
                }}
            }

            private boolean isUsernameExists(String username) {
                boolean isUsernameExists = false;
                try {
                    String query = "SELECT COUNT(*) FROM sinhvien,giangvien WHERE username = ?";
                    PreparedStatement ps = connect.prepareStatement(query);
                    ps.setString(1, username);
                    ResultSet rs = ps.executeQuery();
                    rs.next();
                    int count = rs.getInt(1);
                    if (count > 0){
                        isUsernameExists = true;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    isUsernameExists = true;
                }
                return isUsernameExists;
            }

            private boolean addAccount(String username, String password, String email) {
                String table = null;
                if (gv.isChecked()) {
                    table = "giangvien";
                } else if (sv.isChecked()) {
                    table = "sinhvien";
                }
                try {

                    String query = "INSERT INTO " + table + " (username, password, email) VALUES (?, ?, ?)";
                    PreparedStatement ps = connect.prepareStatement(query);
                    ps.setString(1, username);
                    ps.setString(2, password);
                    ps.setString(3, email);

                    int rowsInserted = ps.executeUpdate();
                    ps.close();

                    if (rowsInserted > 0) {
                        return true;
                    } else {
                        return false;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        });
    }
}
