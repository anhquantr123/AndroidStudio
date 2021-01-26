package com.example.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    TextView createNewAcc, tvQuenMatKhau;
    private TextInputLayout email , password;
    Button btndangnhap;
    FirebaseAuth mAuth;
    ProgressDialog mloasdingbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // ax
        createNewAcc =  findViewById(R.id.createnewAccount);
        tvQuenMatKhau = findViewById(R.id.forgotPassword);
        email = findViewById(R.id.inputUserName);
        btndangnhap = findViewById(R.id.dangnhap);
        password = findViewById(R.id.textInputLayout);
        mloasdingbar   = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        // end ax
        createNewAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this ,CreateAccount.class);
                startActivity(intent);
            }
        });

        //
        btndangnhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoiHamLogin();
            }
        });
        tvQuenMatKhau.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));

            }
        });

    }

    private void GoiHamLogin() {
        String Username  = email.getEditText().getText().toString().trim();
        String Password = password.getEditText().getText().toString().trim();

        // thuc hien test
        if(Username.isEmpty() || Username.equals("")){
            showErrow(email," Email Là Không Hợp Lệ!");
        }else if (Password.isEmpty() || Password.length()<5){
            showErrow(password, "PassWord trống hoặc nhỏ hơn 5 kí tự!");
        }else{
            mloasdingbar.setTitle("Đăng Nhập");
            mloasdingbar.setMessage("Vui Lòng Chờ Giây Lát");
            mloasdingbar.setCanceledOnTouchOutside(false);
            mloasdingbar.show();
            mAuth.signInWithEmailAndPassword(Username,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()) {
                        mloasdingbar.dismiss();
                        Toast.makeText(LoginActivity.this, "Đăng Nhập Thành Công", Toast.LENGTH_SHORT).show();
                        Intent  intent = new Intent(LoginActivity.this,Setup.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }else{
                        mloasdingbar.dismiss();
                        Toast.makeText(LoginActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

    private void showErrow(TextInputLayout loi, String text) {
        loi.setError(text);
        loi.requestFocus();
    }

}