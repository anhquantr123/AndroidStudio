package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.auth.User;

public class CreateAccount extends AppCompatActivity {
    // khai bao cho cac thanh phan
    TextInputEditText inputUsername,inputpass, inputconfrimpass;
    Button btntaotaikhoan;
    TextView textdacotaikhoan;
    FirebaseAuth mAuth;
    ProgressDialog mloasdingbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        inputUsername =  findViewById(R.id.taousername1);
        inputpass =  findViewById(R.id.taopass1);
        inputconfrimpass =  findViewById(R.id.confrimpass1);
        textdacotaikhoan =  findViewById(R.id.dacotaikhoan1);
        btntaotaikhoan =  findViewById(R.id.taotaikhoan1);
        mAuth = FirebaseAuth.getInstance();
        mloasdingbar = new ProgressDialog(this);



        btntaotaikhoan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DangKiTaiKhoanMoi();
            }
        });
        textdacotaikhoan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent  = new Intent(CreateAccount.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void DangKiTaiKhoanMoi() {
        String Username  = inputUsername.getText().toString().trim();
        String Password = inputpass.getText().toString().trim();
        String nhaplaipass = inputconfrimpass.getText().toString().trim();

        // thuc hien test
        if(Username.isEmpty() || Username.equals("")){
            showErrow(inputUsername," Email Là Không Hợp Lệ!");
        }else if (Password.isEmpty() || Password.length()<5){
            showErrow(inputpass, "PassWord trống hoặc nhỏ hơn 5 kí tự!");
        }else if (!Password.equals(nhaplaipass)){
            showErrow(inputconfrimpass, "Password Bạn Nhập không giống nhau!");
        }else{
            mloasdingbar.setTitle("Tạo Tài Khoản");
            mloasdingbar.setMessage("Vui Lòng Chờ Giây Lát");
            mloasdingbar.setCanceledOnTouchOutside(false);
            mloasdingbar.show();
            mAuth.createUserWithEmailAndPassword(Username,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        mloasdingbar.dismiss();
                        Toast.makeText(CreateAccount.this, "Tạo tài khoản thành công", Toast.LENGTH_SHORT).show();
                        Intent  intent = new Intent(CreateAccount.this,LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();

                    }else{
                        mloasdingbar.dismiss();
                        Toast.makeText(CreateAccount.this, "Tạo tài khoản không thành công", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void showErrow(TextInputEditText loi, String text) {
        loi.setError(text);
        loi.requestFocus();
    }


}