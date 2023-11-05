package com.example.login;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.Manifest;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UpdateAvatar extends AppCompatActivity {
    private static final int MY_REQUEST_CODE = 10;
    public static final String TAG = UpdateAvatar.class.getName();
    private EditText hvt, mssv;
    private ImageView image;
    private TextView check_connect,check_hvt, check_mssv;
    private Button save;
    private byte[] imageData;
    private String username;
    Connection connection;
    // get image from gallery and set it on image TextView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_avatar);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, PackageManager.PERMISSION_GRANTED);
        hvt = findViewById(R.id.hvt);
        mssv = findViewById(R.id.mssv);
        check_connect = findViewById(R.id.check_connect);
        image = findViewById(R.id.image);
        save = findViewById(R.id.save);
        check_hvt = findViewById(R.id.check_hvt);
        check_mssv = findViewById(R.id.check_mssv);
        connection = SQLConnection.getConnection();
        username = getIntent().getStringExtra("STRING_VALUE");
       // data.setText(username);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickRequestPermission();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String studentName = hvt.getText().toString().trim();
                String studentCode = mssv.getText().toString().trim();
                Drawable drawable = image.getDrawable();
                if (TextUtils.isEmpty(studentName)) {
                    check_hvt.setText("Bạn tên là gì nè!");
                }
                else if (TextUtils.isEmpty(studentCode)) {
                    check_mssv.setText("mssv nửa nè!");
                }else if(drawable==null){
                    Toast.makeText(UpdateAvatar.this,"Chọn ảnh làm avatar đi nè!!!",Toast.LENGTH_SHORT).show();
                }else{

                    // Thực hiện lưu thông tin sinh viên vào SQL Server
                    boolean isSaved = addStudentToDatabase(studentName, studentCode, imageData, username);

                    if (isSaved) {
                        Toast.makeText(UpdateAvatar.this,"da cap nhap anh dai dien!",Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(UpdateAvatar.this, AdminLogin.class);
                        startActivity(i);
                        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_left);
                    }else{
                        Toast.makeText(UpdateAvatar.this,"Loi truy van du lieu!",Toast.LENGTH_SHORT).show();
                    }
                }
                }
        });
    }

    private ActivityResultLauncher<Intent> mActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.e(TAG, "onActivityResult");
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data == null) {
                            return;
                        }
                        Uri imageUri = data.getData();    //get image from gallery
                        //Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                        if(imageUri !=null) {
                            image.setImageURI(imageUri);  //set image on image's TextView
                        }

                        imageData = convertImageToBytes(imageUri);
                        if(imageData ==null) {
                            Toast.makeText(UpdateAvatar.this, "Error! imageData is empty",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

    private byte[] convertImageToBytes(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
            inputStream.close();
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void onClickRequestPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            openGallery();
            return;
        }

        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        } else {
            String[] permission = {Manifest.permission.READ_EXTERNAL_STORAGE};
            requestPermissions(permission, MY_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            }
        }
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        mActivityResultLauncher.launch(intent.createChooser(intent, "Select Picture"));
    }

    // Hàm này thêm sinh viên vào cơ sở dữ liệu
    private boolean addStudentToDatabase(String studentName, String studentCode, byte[] imageData, String username) {
        boolean isSaved = false;
        if (connection != null) {
            check_connect.setText("Successful Connection");
            try {

                // Truy vấn SQL để lấy classID từ bảng CLASS dựa trên subjectName
                String query = "SELECT COUNT(*) FROM sinhvien WHERE username = ?";
                PreparedStatement ps = connection.prepareStatement(query);
                ps.setString(1, username);
                ResultSet rs = ps.executeQuery();
                rs.next();

                //kiem tra username co trong bang hay khongs
                int count = rs.getInt(1);
                if (count > 0){
                //data.setText("Tim thay username");
                // Thực hiện truy vấn SQL để thêm sinh viên vào cơ sở dữ liệu
                String updateQuery = "UPDATE sinhvien SET name = ?, mssv = ?, avatar = ? WHERE username = ?";
                ps = connection.prepareStatement(updateQuery);
                ps.setString(1, studentName);
                ps.setString(2, studentCode);
                ps.setBytes(3,  imageData);
                ps.setString(4, username);

                int rowsInserted = ps.executeUpdate();

                if (rowsInserted > 0) {
                    isSaved = true;
                }
                    ps.close();
                } else {
                    // Không tìm thấy username trong bang sinhvien
                    rs.close();
                    ps.close();
                    isSaved = false;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return isSaved;
    }
}