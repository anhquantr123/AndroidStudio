package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ToolbarWidgetWrapper;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import io.grpc.Context;

public class Setup extends AppCompatActivity {
    private static final int REQUEST_CODE= 101;
    CircleImageView profileImageView;
    EditText edUserName , edThanhPho, edQuocGia , edNgheNghiep;
    Button btnsave;
    Uri imageUri;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference mRef;
    StorageReference StorageRe;

    ProgressDialog mloadingbar;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        //ax
        btnsave  = findViewById(R.id.btnSave);
        edUserName = findViewById(R.id.inputUser);
        edThanhPho = findViewById(R.id.inputAdress);
        edQuocGia = findViewById(R.id.inputQuocGia);
        edNgheNghiep = findViewById(R.id.inputpersion);
        profileImageView = findViewById(R.id.profile_image);
       
        //


        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mRef = FirebaseDatabase.getInstance().getReference().child("Users");
        StorageRe = FirebaseStorage.getInstance().getReference().child("ProfileImages");

        mloadingbar = new ProgressDialog(this);

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,REQUEST_CODE);
            }
        });

        //
         btnsave.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 SaveData();
             }
         });
    }

    private void SaveData() {
        final String username = edUserName.getText().toString();
        final String city  = edThanhPho.getText().toString();
        final String quocgia = edQuocGia.getText().toString();
        final String nghenghiep = edNgheNghiep.getText().toString();

       if(username.isEmpty() || username.length()< 3){
           showError(edUserName , "Username trống hoặc nhỏ hơn 3 kí tự");
       }
       else if(city.isEmpty() || city.length()< 3){
           showError(edThanhPho , "Thành Phố bạn đang trống");
       }
       else if(quocgia.isEmpty() ){
           showError(edQuocGia, "Quốc Gia  bạn đang trống ");
       }
       else if (nghenghiep.isEmpty()){
           showError(edNgheNghiep , "Nghề Nghiệp đang trống ");
       }
//       else if(imageUri== null){
//           Toast.makeText(this, "Chọn Một Image", Toast.LENGTH_SHORT).show();
//       }
       else{
           mloadingbar.setTitle("Update...");
           mloadingbar.setCanceledOnTouchOutside(false);
           mloadingbar.show();
           StorageRe.child(mUser.getUid()).putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
               @Override
               public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                   if(task.isSuccessful()){
                       StorageRe.child(mUser.getUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                           @Override
                           public void onSuccess(Uri uri) {
                               HashMap hashMap = new HashMap();
                               hashMap.put("username",username);
                               hashMap.put("city",city);
                               hashMap.put("quocgia",quocgia);
                               hashMap.put("nghenghiep",nghenghiep);
                               hashMap.put("profileImage", uri.toString());
                               hashMap.put("status","Offline");

                                mRef.child(mUser.getUid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener() {
                                    @Override
                                    public void onSuccess(Object o) {
                                        Intent intent = new Intent(Setup.this,MainActivity.class);
                                        startActivity(intent);
                                        mloadingbar.dismiss();;
                                        Toast.makeText(Setup.this, "Hoàn Tất Hồ Sơ Của Bạn", Toast.LENGTH_SHORT).show();

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(Setup.this, "Lỗi Khi Cập Nhật Hồ Sơ", Toast.LENGTH_SHORT).show();
                                    }
                                });
                           }
                       });
                   }
               }
           });
       }
    }

    private void showError(EditText ed, String s) {
        ed.setError(s);
        ed.requestFocus();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode== REQUEST_CODE && resultCode== RESULT_OK && data!= null){
            imageUri = data.getData();
            profileImageView.setImageURI(imageUri);
        }

    }
}