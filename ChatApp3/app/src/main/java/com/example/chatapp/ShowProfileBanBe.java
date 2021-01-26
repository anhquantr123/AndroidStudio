package com.example.chatapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ShowProfileBanBe extends AppCompatActivity {

    DatabaseReference mUserRef;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    CircleImageView profileImage;
    Button btnNhantin , btnHuyKetBan;
    TextView Tenhienthi, Nghenghiep , Thanhpho  , Diachi , tvtenHienThi, tvDenTuShow;
    String profileImageUrl, tenhienthi , nghenghiep , thanhpho , quocgia ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_profile_ban_be);

        final String OtherUserID = getIntent().getStringExtra("OtherUserID");
        btnNhantin = findViewById(R.id.btnNhanTinShowProfileBanBe);
        Tenhienthi = findViewById(R.id.tvTenHienThiShowProfileBanBe);
        tvtenHienThi = findViewById(R.id.tvUserNameHienThi);
        Thanhpho = findViewById(R.id.tvThanhPhoHienthi);
        Diachi = findViewById(R.id.tvNgheNghiepShowProfileBanBe);
        profileImage = findViewById(R.id.anhDaidienShowProfileBanBe);
        tvDenTuShow = findViewById(R.id.tvDentuShow);
        Nghenghiep = findViewById(R.id.tvNgheNghiep);
        btnHuyKetBan = findViewById(R.id.btHuyketBan);


        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(OtherUserID);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        LoadUser();

        btnHuyKetBan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HuyketBan();
            }
        });
        btnNhantin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShowProfileBanBe.this, ChatActivity.class);
                intent.putExtra("OtherUserID",OtherUserID);
                startActivity(intent);
            }
        });
    }

    private void HuyketBan() {
        new AlertDialog.Builder(ShowProfileBanBe.this).setTitle("Bạn Muốn Hủy Bạn Bè").setPositiveButton("Đồng Ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).show();
    }

    private void LoadUser() {
        mUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    profileImageUrl = snapshot.child("profileImage").getValue().toString();
                    tenhienthi = snapshot.child("username").getValue().toString();
                    thanhpho = snapshot.child("city").getValue().toString();
                    quocgia = snapshot.child("quocgia").getValue().toString();
                    nghenghiep = snapshot.child("nghenghiep").getValue().toString();

                    Picasso.get().load(profileImageUrl).into(profileImage);
                    Tenhienthi.setText(tenhienthi);
                    Diachi.setText("Thành Phố "+thanhpho+", "+quocgia);
                    tvtenHienThi.setText("Tên người dùng : chapapp/"+tenhienthi);
                    Thanhpho.setText("Sống tại: "+thanhpho);
                    tvDenTuShow.setText("Đến từ: "+thanhpho+" "+quocgia);
                    Nghenghiep.setText("Công việc hiện tại: "+nghenghiep);
                }else{
                    Toast.makeText(ShowProfileBanBe.this, "Du lieu ko ton tai ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ShowProfileBanBe.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}