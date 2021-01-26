package com.example.chatapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    CircleImageView profileImageView;
    ImageView anhbia;
    TextView edinputUsername,edinputCity,edinputCountry,edinputNgheNghiep;
    ImageView profile_edit_username, profile_edit_city,profile_edit_country, profile_edit_nghenghiep;

    DatabaseReference mUserRef;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    StorageReference StorageRe , StorageReAnhBia;
    ProgressDialog mloadingbar;
    private static final int REQUEST_CODE= 101;
    private static final int REQUEST_CODE1= 111;
    Uri imageUri, imageUriAnhbia;
    String imageUrlAnhDaiDien , imageUrlAnhBia;
     BottomSheetDialog bottomSheetDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        // thuc hien anh xa den cac id da khai bao

        profileImageView = findViewById(R.id.circleImageView);
        edinputUsername = findViewById(R.id.inputUsername);
        edinputCity = findViewById(R.id.inputCity);
        edinputCountry = findViewById(R.id.inputCounty);
        edinputNgheNghiep = findViewById(R.id.inputNgheNghiep);

        profile_edit_username = findViewById(R.id.profile_edit_username);
        profile_edit_city = findViewById(R.id.profile_edit_city);
        profile_edit_country = findViewById(R.id.profile_edit_country);
        profile_edit_nghenghiep = findViewById(R.id.profile_edit_nghenghiep);

        anhbia = findViewById(R.id.view);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        StorageRe = FirebaseStorage.getInstance().getReference().child("ProfileImages");
        StorageReAnhBia = FirebaseStorage.getInstance().getReference().child("anhBia");
        mloadingbar = new ProgressDialog(this);

        profile_edit_username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update_info("username");
            }
        });

        profile_edit_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update_info("city");
            }
        });

        profile_edit_country.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update_info("country");
            }
        });

        profile_edit_nghenghiep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update_info("nghenghiep");
            }
        });

        // khi nhan chon anh bia
        anhbia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(ProfileActivity.this).setPositiveButton("Xem Ảnh Bìa", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent  =  new Intent(ProfileActivity.this , XemHinhAnhActivity.class);
                        intent.putExtra("urlImage",imageUrlAnhBia ); // gửi địa chỉ của hình ảnh nhận được thông qua intent
                        startActivity(intent);
                    }
                }).setNeutralButton("Thay Ảnh Bìa", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent1 = new Intent(Intent.ACTION_GET_CONTENT);
                        intent1.setType("image/*");
                        startActivityForResult(Intent.createChooser(intent1,"Chọn ảnh của bạn"),REQUEST_CODE1);
                    }
                }).show();
            }
        });



        // khi nhan chon anh dai dien
        profileImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                new AlertDialog.Builder(ProfileActivity.this).setTitle("Chọn Thao Tác")
                        .setPositiveButton("Xem Ảnh", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent  =  new Intent(ProfileActivity.this , XemHinhAnhActivity.class);
                                intent.putExtra("urlImage",imageUrlAnhDaiDien ); // gửi địa chỉ của hình ảnh nhận được thông qua intent
                                startActivity(intent);
                            }
                        }).setNegativeButton("Thay Ảnh Đại Diện", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");
                        startActivityForResult(intent,REQUEST_CODE);
                    }
                }).show();

                return true;
            }
        });
        //
        mUserRef.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                   try {
                       String profileImageUrl = snapshot.child("profileImage").getValue().toString();
                       String anhbiaImage = snapshot.child("anhBia").getValue().toString();
                       imageUrlAnhDaiDien = profileImageUrl;
                       imageUrlAnhBia = anhbiaImage;
                       String city = snapshot.child("city").getValue().toString();
                       String quocgia = snapshot.child("quocgia").getValue().toString();
                       String nghenghiep = snapshot.child("nghenghiep").getValue().toString();
                       String username = snapshot.child("username").getValue().toString();

                       Picasso.get().load(profileImageUrl).into(profileImageView);
                       Glide.with(ProfileActivity.this).load(imageUrlAnhBia).into(anhbia);
                       edinputCity.setText(city);
                       edinputUsername.setText(username);
                       edinputCountry.setText(quocgia);
                       edinputNgheNghiep.setText(nghenghiep);
                   }catch (Exception ee){

                   }
                }else{
                    Toast.makeText(ProfileActivity.this, "No Data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, ""+error.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });



    }

    private void update_info(final String ma) {
        View view  = getLayoutInflater().inflate(R.layout.bottom_edit_profile, null);
        bottomSheetDialog = new BottomSheetDialog(this,R.style.BottomSheetDialogThem);
        bottomSheetDialog.setContentView(view);
        TextView title  = view.findViewById(R.id.bottom_title_edit_profile);
        final EditText conten = view.findViewById(R.id.bottom_edit_input_profile);

        if(ma.equals("city")){
            title.setText("Thành Phố");
            conten.setHint("Nhập Tên Thành phố của bạn");
        } else if(ma.equals("country")){
            title.setText("Quốc gia");
            conten.setHint("Nhập Tên Quốc Gia của bạn");
        } else if(ma.equals("nghenghiep")){
            title.setText("Nghề Nghiệp");
            conten.setHint("Nhập nghề nghiệp hiện tại của bạn");
        }

        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                bottomSheetDialog = null;
            }
        });

        bottomSheetDialog.show();
        view.findViewById(R.id.btn_huybo_edit_profile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });

        view.findViewById(R.id.btn_save_edit_profile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = conten.getText().toString().trim();
                if(ma.equals("username")){
                    if(input.isEmpty() || input.equals("")){
                        Toast.makeText(ProfileActivity.this, "Hãy nhập tên UserName mới của bạn!", Toast.LENGTH_SHORT).show();
                    }else{
                        LoadBar();
                        update_username(input);
                        bottomSheetDialog.dismiss();
                    }
                } else if(ma.equals("city")){
                    if(input.isEmpty() || input.equals("")){
                        Toast.makeText(ProfileActivity.this, "Hãy nhập tên Thành Phố mới của bạn!", Toast.LENGTH_SHORT).show();
                    }else{
                        LoadBar();
                        update_City(input);
                        bottomSheetDialog.dismiss();
                    }
                } else if(ma.equals("country")){
                    if(input.isEmpty() || input.equals("")){
                        Toast.makeText(ProfileActivity.this, "Hãy nhập tên Quốc Gia mới của bạn!", Toast.LENGTH_SHORT).show();
                    }else{
                        LoadBar();
                        update_country(input);
                        bottomSheetDialog.dismiss();
                    }
                } else if(ma.equals("nghề nghiệp")){
                    if(input.isEmpty() || input.equals("")){
                        Toast.makeText(ProfileActivity.this, "Hãy nhập Nghề Nghiệp mới của bạn!", Toast.LENGTH_SHORT).show();
                    }else{
                        LoadBar();
                        update_nghenghiep(input);
                        bottomSheetDialog.dismiss();
                    }
                }
            }
        });

    }

    // Thực hiện update thông tin Username
    private void update_username(String input_data) {
        HashMap hashMap = new HashMap();
        hashMap.put("username", input_data);
        hashMap.put("status","Online");
        mUserRef.child(mUser.getUid()).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful()){
                    mloadingbar.dismiss();
                    Toast.makeText(ProfileActivity.this, "Cập Nhật Username Thành Công!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // thực hiện uodate thông tin Thành Phố
    private void update_City(String input_data) {
        HashMap hashMap = new HashMap();
        hashMap.put("city", input_data);
       /// hashMap.put("status","Online");
        mUserRef.child(mUser.getUid()).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful()){
                    mloadingbar.dismiss();
                    Toast.makeText(ProfileActivity.this, "Cập Nhật City Thành Công!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // update quốc gia
    private void update_country(String input_data) {
        HashMap hashMap = new HashMap();
        hashMap.put("quocgia", input_data);
        /// hashMap.put("status","Online");
        mUserRef.child(mUser.getUid()).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful()){
                    mloadingbar.dismiss();
                    Toast.makeText(ProfileActivity.this, "Cập Nhật Quốc Gia Thành Công!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void update_nghenghiep(String input_data) {
        HashMap hashMap = new HashMap();
        hashMap.put("nghenghiep", input_data);
        /// hashMap.put("status","Online");
        mUserRef.child(mUser.getUid()).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful()){
                    mloadingbar.dismiss();
                    Toast.makeText(ProfileActivity.this, "Cập Nhật Nghề Nghiệp Thành Công!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private  void LoadBar(){
        mloadingbar.setTitle("Đang cập nhật");
        mloadingbar.setCanceledOnTouchOutside(false);
        mloadingbar.show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode== REQUEST_CODE && resultCode== RESULT_OK && data!= null){
            imageUri = data.getData();
            profileImageView.setImageURI(imageUri);
            mloadingbar = new ProgressDialog(this);
            mloadingbar.setTitle("Đang cập nhật ảnh đại diện");
            mloadingbar.setCanceledOnTouchOutside(false);
            mloadingbar.show();
            uploadImage(imageUri,"anhdaidien");
        }
        if(requestCode== REQUEST_CODE1 && resultCode== RESULT_OK && data!= null){
            imageUriAnhbia = data.getData();
            anhbia.setImageURI(imageUriAnhbia);
            mloadingbar = new ProgressDialog(this);
            mloadingbar.setTitle("Đang cập nhật  ảnh bìa");
            mloadingbar.setCanceledOnTouchOutside(false);
            mloadingbar.show();
            uploadImage(imageUriAnhbia,"anhbia");
        }
    }

    // hàm cập nhật ảnh đại diện
    protected void  uploadImage(Uri uri, String img){
        if(uri != null && img.equals("anhdaidien") ){
            StorageRe.child(mUser.getUid()).putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        StorageRe.child(mUser.getUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                HashMap hashMap = new HashMap();
                                hashMap.put("profileImage", uri.toString());
                                mUserRef.child(mUser.getUid()).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        if(task.isSuccessful()){
                                            mloadingbar.dismiss();
                                            Toast.makeText(ProfileActivity.this, "Cập Nhật ảnh đại diện thành công", Toast.LENGTH_SHORT).show();
                                        }else {
                                            Toast.makeText(ProfileActivity.this, "Cập Nhật ảnh đại diện thất bại", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });
                    }
                }
            });
        }else if(uri != null && img.equals("anhbia")){
            StorageReAnhBia.child(mUser.getUid()).putFile(imageUriAnhbia).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        StorageReAnhBia.child(mUser.getUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                HashMap hashMap = new HashMap();
                                hashMap.put("anhBia", uri.toString());
                                mUserRef.child(mUser.getUid()).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        if(task.isSuccessful()){
                                            mloadingbar.dismiss();
                                            Toast.makeText(ProfileActivity.this, "Cập Nhật ảnh bìa thành công", Toast.LENGTH_SHORT).show();
                                        }else {
                                            Toast.makeText(ProfileActivity.this, "Cập Nhật ảnh bìa thất bại", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });
                    }
                }
            });
        }
    }

   
}