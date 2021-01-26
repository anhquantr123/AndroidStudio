package com.example.chatapp;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewFriendActivity extends AppCompatActivity {

    DatabaseReference mUserRef,requestRef,friendRef , mUserRef2;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    String profileImageUrl , username  , thanhpho , quocgia ;
    String profileImageUrl2 , username25 , thanhpho2, quocgia2;

    CircleImageView profileImage;
    TextView Username , address;

    Button btnKetBan , btnTuChoi ;

    String status  = "nothing_happen";
    String nghenghiep , nghenghiep2 ;
    String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_friend);
         userID = getIntent().getStringExtra("userkey");
        //Toast.makeText(this, ""+userID, Toast.LENGTH_SHORT).show();

        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
        requestRef = FirebaseDatabase.getInstance().getReference().child("Request");
        friendRef = FirebaseDatabase.getInstance().getReference().child("Friends");

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mUserRef2 = FirebaseDatabase.getInstance().getReference().child("Users").child(mUser.getUid().toString());

        btnKetBan = findViewById(R.id.btnNhanTinShowBanBe);
        btnTuChoi = findViewById(R.id.btnHuyKetBanShowBanbe);

        profileImage = findViewById(R.id.profileImageShowBanBe);
        Username = findViewById(R.id.usernameShowBanBe);
        address = findViewById(R.id.addressShowBanBe);



        LoadUser();
        LoadUser2();
        btnKetBan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                KetBanAction(userID);
            }
        });
        KiemTraUserExit(userID);
        btnTuChoi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HuyKetBan(userID);
            }
        });


    }

    private void HuyKetBan(final String userID) {
        if(status.equals("friend")){
            friendRef.child(mUser.getUid()).child(userID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        friendRef.child(userID).child(mUser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    status ="nothing_happen";
                                    btnKetBan.setText("Kết bạn");
                                    btnTuChoi.setVisibility(View.GONE);
                                }
                            }
                        });
                        //tests

                        //
                    }
                }
            });
        }
        if(status.equals("he_sent_pending")){
            HashMap hashMap = new HashMap();
            hashMap.put("status","decline");
            requestRef.child(userID).child(mUser.getUid()).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        status ="he_sent_pending";
                        btnKetBan.setText("Kết Bạn");
                        btnTuChoi.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    private void KiemTraUserExit(String userID) {
        friendRef.child(mUser.getUid()).child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    status = "friend";
                    btnKetBan.setText("Gửi Tin Nhắn");
                    btnTuChoi.setText("Hủy kết Bạn");
                    btnTuChoi.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        friendRef.child(userID).child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    status = "friend";
                    btnKetBan.setText("Gửi Tin Nhắn");
                    btnTuChoi.setText("Hủy kết Bạn");
                    btnTuChoi.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        requestRef.child(mUser.getUid()).child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    if(snapshot.child("status").getValue().toString().equals("pending")){
                        status = "I_sent_pending";
                        btnKetBan.setText("Hủy Lời Mời");
                        btnTuChoi.setVisibility(View.GONE);
                    }
                    if(snapshot.child("status").getValue().toString().equals("decline")){
                        status = "I_sent_decline";
                        btnKetBan.setText("Hủy Lời Mời");
                        btnTuChoi.setVisibility(View.GONE);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        requestRef.child(userID).child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    if(snapshot.child("status").getValue().toString().equals("pending")){
                        status = "he_sent_pending";
                        btnKetBan.setText("Chấp nhận Kết bạn");
                        btnTuChoi.setText("Từ Chối Yêu Cầu");
                        btnTuChoi.setVisibility(View.VISIBLE);
                    }

                }else{
                    status = "nothing_happen";
                    btnKetBan.setText("Kết Bạn");
                    btnTuChoi.setText("Từ Chối Yêu Cầu");
                    btnTuChoi.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        if(status.equals("nothing_happen")){
            status = "nothing_happen";
            btnKetBan.setText("Kết Bạn");
            btnTuChoi.setVisibility(View.GONE);
        }
    }

    private void KetBanAction(final String userID) {
        if(status.equals("nothing_happen")){
            HashMap hashMap = new HashMap();
            hashMap.put("status","pending");
            requestRef.child(mUser.getUid()).child(userID).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        btnTuChoi.setVisibility(View.GONE);
                        status = "I_sent_pending";
                        btnKetBan.setText("Hủy Lời Mời");
                    }
                    else
                    {
                        Toast.makeText(ViewFriendActivity.this, ""+task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        if(status.equals("I_sent_pending")||status.equals("I_sent_decline")){
            requestRef.child(mUser.getUid()).child(userID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        status = "nothing_happen";
                        btnKetBan.setText("Kết Bạn");
                        btnTuChoi.setVisibility(View.GONE);
                    }
                    else
                    {
                        Toast.makeText(ViewFriendActivity.this, ""+task.getException().toString(), Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }

        if(status.equals("he_sent_pending")){
            requestRef.child(userID).child(mUser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        final HashMap hashMap = new HashMap();
                        hashMap.put("status","friend");
                        hashMap.put("username",username);
                        hashMap.put("profileImageUrl",profileImageUrl);
                        hashMap.put("nghenghiep",nghenghiep);

                        friendRef.child(mUser.getUid()).child(userID).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if(task.isSuccessful()){
                                    //
                                    LoadUser2();
                                    HashMap hashMap1 = new HashMap();
                                    hashMap1.put("status","friend");
                                    hashMap1.put("profileImageUrl",profileImageUrl2);
                                    hashMap1.put("nghenghiep",nghenghiep2);
                                    hashMap1.put("username",username25);
                                    //
                                    friendRef.child(userID).child(mUser.getUid()).updateChildren(hashMap1).addOnCompleteListener(new OnCompleteListener() {
                                        @Override
                                        public void onComplete(@NonNull Task task) {
                                            status ="friend";
                                            btnKetBan.setText("Gửi Tin Nhắn");
                                            btnTuChoi.setText("Hủy kết bạn");
                                            btnTuChoi.setVisibility(View.VISIBLE);
                                        }
                                    });
                                }
                            }
                        });

                    }
                }
            });
        }
        if(status.equals("friend")){
            //start
//          if(btnKetBan.getText().equals("Gửi Tin Nhắn")){
//              btnKetBan.setOnClickListener(new View.OnClickListener() {
//                  @Override
//                  public void onClick(View view) {
//                      Intent intent = new Intent(ViewFriendActivity.this,ShowProfileBanBe.class);
//                      intent.putExtra("OtherUserID", userID);
//                      startActivity(intent);
//                  }
//              });
//          }
          // end
            Intent intent = new Intent(ViewFriendActivity.this,ShowProfileBanBe.class);
                      intent.putExtra("OtherUserID", userID);
                      startActivity(intent);
        }
    }

    private void LoadUser2() {
        mUserRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    profileImageUrl2 = snapshot.child("profileImage").getValue().toString();
                    username25 = snapshot.child("username").getValue().toString();
                    thanhpho2 = snapshot.child("city").getValue().toString();
                    quocgia2 = snapshot.child("quocgia").getValue().toString();
                    nghenghiep2 = snapshot.child("nghenghiep").getValue().toString();
                }else{
                    Toast.makeText(ViewFriendActivity.this, "Du lieu ko ton tai 2  ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewFriendActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void LoadUser() {
        mUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    profileImageUrl = snapshot.child("profileImage").getValue().toString();
                    username = snapshot.child("username").getValue().toString();
                    thanhpho = snapshot.child("city").getValue().toString();
                    quocgia = snapshot.child("quocgia").getValue().toString();
                    nghenghiep = snapshot.child("nghenghiep").getValue().toString();

                    Picasso.get().load(profileImageUrl).into(profileImage);
                    Username.setText(username);
                    address.setText("Thành Phố "+thanhpho+", "+quocgia);
                }else{
                    Toast.makeText(ViewFriendActivity.this, "Du lieu ko ton tai ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewFriendActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}