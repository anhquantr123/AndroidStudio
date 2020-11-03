package com.example.chatapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText edinputEmail;
    Button btnSenddata;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        edinputEmail = findViewById(R.id.inputPasswordreset);
        btnSenddata = findViewById(R.id.btnResetPassWord);
        mAuth = FirebaseAuth.getInstance();

        btnSenddata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email  = edinputEmail.getText().toString().trim();
                if(email.isEmpty()){
                    Toast.makeText(ForgotPasswordActivity.this, "Vui Lòng Nhập Tài Khoản Email Của Bạn", Toast.LENGTH_SHORT).show();
                }
                else
                {
                   mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                       @Override
                       public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(ForgotPasswordActivity.this, "Vui Lòng Check Email của bạn", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(ForgotPasswordActivity.this, "Không Thể Gửi Email", Toast.LENGTH_SHORT).show();
                            }
                       }
                   }) ;
                }
            }
        });
    }
}