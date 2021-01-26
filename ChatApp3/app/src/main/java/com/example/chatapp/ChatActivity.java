package com.example.chatapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.chatapp.Utills.tinNhan;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;
    EditText noidungtinnhan ;
    ImageView guihinhanh , btnGuiTinNhan;
    CircleImageView userProfileImageAppbarChat;
    TextView usernameAppbarTitel, usernameTrangThaiHoatDong;


    String OtherUserID, OtherTenHienThi, OtherHinhDaidien , OtherTrangthai;

    DatabaseReference mUserRef,tinnhanRef;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    StorageReference storagReSendImage;

    FirebaseRecyclerOptions<tinNhan>options;
    FirebaseRecyclerAdapter<tinNhan,ChatMyViewHolder>adapter;
    String ImageProfile;
    String URL = "https//fcm.googleapis.com/fcm/send";
    RequestQueue requestQueue;
    private static final int REQUEST_CODE= 785;
    Uri uri_image;
    String Url_image_send;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        OtherUserID = getIntent().getStringExtra("OtherUserID");

        requestQueue = Volley.newRequestQueue(this);
        recyclerView = findViewById(R.id.reyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        noidungtinnhan = findViewById(R.id.inputNoiDungTinNhan);
        guihinhanh = findViewById(R.id.imageViewChatSend);
        btnGuiTinNhan = findViewById(R.id.imageSendChat);
        userProfileImageAppbarChat = findViewById(R.id.userProfileImageAppbarChat);
        usernameAppbarTitel = findViewById(R.id.usernameAppbarTitel);
        usernameTrangThaiHoatDong = findViewById(R.id.usernameTrangThaiHoatDong);




        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        tinnhanRef = FirebaseDatabase.getInstance().getReference().child("Message");
        storagReSendImage = FirebaseStorage.getInstance().getReference().child("ImageChats");
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        btnGuiTinNhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GuiTinNhan();
            }
        });
        guihinhanh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImageDevice();
            }
        });
        // Gọi hàm tải trạng thái người dùng
        LoadOtherUser();
        LoadTinNhan();
        LoadMyProFile();
        

    }

    private void openImageDevice() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "select image"), REQUEST_CODE);
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode== REQUEST_CODE && resultCode== RESULT_OK && data!= null){
            uri_image = data.getData();
           // anhbia.setImageURI(imageUriAnhbia);
            uploadImage();
        }
    }
    private void uploadImage(){
        storagReSendImage.child(mUser.getUid()).putFile(uri_image).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    storagReSendImage.child(mUser.getUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            final  HashMap hashMap = new HashMap();
                            hashMap.put("image", uri.toString());
                            hashMap.put("status" , "unseen");
                            hashMap.put("userID" , mUser.getUid());
                            hashMap.put("sms" , "SendImage");
                            tinnhanRef.child(OtherUserID).child(mUser.getUid()).push().updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if(task.isSuccessful())
                                    {
                                        tinnhanRef.child(mUser.getUid()).child(OtherUserID).push().updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                                            @Override
                                            public void onComplete(@NonNull Task task) {
                                                if(task.isSuccessful()){
                                                    noidungtinnhan.setText(null);
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    });
                }
            }
        });
    }
    private void LoadMyProFile() {
        mUserRef.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    ImageProfile = snapshot.child("profileImage").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void LoadTinNhan() {
       options = new FirebaseRecyclerOptions.Builder<tinNhan>()
               .setQuery(tinnhanRef.child(mUser.getUid()).child(OtherUserID),tinNhan.class).build();
       adapter = new FirebaseRecyclerAdapter<tinNhan, ChatMyViewHolder>(options) {
           @Override
           protected void onBindViewHolder(@NonNull ChatMyViewHolder holder, int position, @NonNull tinNhan model) {
               if(model.getUserID().equals(mUser.getUid())){
                   if(model.getSms().equals("SendImage") ){
                       holder.firstUserText.setVisibility(View.GONE);
                       holder.firstUserProfile.setVisibility(View.GONE);
                       holder.secondUserText.setVisibility(View.GONE);
                       holder.secondUserProfile.setVisibility(View.GONE);
                       holder.chat_image_send_right.setVisibility(View.VISIBLE);
                       Glide.with(ChatActivity.this).load(model.getImage().toString()).into(holder.chat_image_send_right);
                   }else{
                       holder.firstUserText.setVisibility(View.GONE);
                       holder.firstUserProfile.setVisibility(View.GONE);
                       holder.secondUserText.setVisibility(View.VISIBLE);
                       holder.secondUserProfile.setVisibility(View.VISIBLE);
                       holder.secondUserText.setText(model.getSms());
                       Picasso.get().load(ImageProfile).into(holder.secondUserProfile);

                   }

               }else{
                   if(model.getSms().equals("SendImage") ){
                       holder.firstUserText.setVisibility(View.GONE);
                       holder.firstUserProfile.setVisibility(View.GONE);
                       holder.secondUserText.setVisibility(View.GONE);
                       holder.secondUserProfile.setVisibility(View.GONE);
                       holder.chat_image_send_left.setVisibility(View.VISIBLE);
                       Glide.with(ChatActivity.this).load(model.getImage().toString()).into(holder.chat_image_send_left);
                   }else{
                       holder.firstUserText.setVisibility(View.VISIBLE);
                       holder.firstUserProfile.setVisibility(View.VISIBLE);
                       holder.secondUserText.setVisibility(View.GONE);
                       holder.secondUserProfile.setVisibility(View.GONE);
                       holder.firstUserText.setText(model.getSms());
                       Picasso.get().load(OtherHinhDaidien).into(holder.firstUserProfile);
                   }

               }

           }

           @NonNull
           @Override
           public ChatMyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
               View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.signview_tinnhan,parent,false);
               return new ChatMyViewHolder(view);
           }
       };
       adapter.startListening();
       recyclerView.setAdapter(adapter);

    }

    private void GuiTinNhan() {
        final String noidung  = noidungtinnhan.getText().toString().trim();
        if(noidung.isEmpty()){
        }else{
            final HashMap hashMap  = new HashMap();
            hashMap.put("sms" , noidung);
            hashMap.put("status" , "unseen");
            hashMap.put("userID" , mUser.getUid());
            tinnhanRef.child(OtherUserID).child(mUser.getUid()).push().updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful())
                    {
                        tinnhanRef.child(mUser.getUid()).child(OtherUserID).push().updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if(task.isSuccessful()){
                                    senNotification(noidung);
                                    noidungtinnhan.setText(null);
                                }
                            }
                        });
                    }
                }
            });


        }
    }

    private void senNotification(String noidung) {
        JSONObject  jsonObject = new JSONObject();
        try {
            jsonObject.put("to","/topics/"+OtherUserID);
            JSONObject jsonObject1  = new JSONObject();
            jsonObject1.put("title","Messenge from "+OtherTenHienThi);
            jsonObject1.put("body",noidung);
            jsonObject.put("notification" ,jsonObject1);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,URL, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String , String> map = new HashMap<>();
                    map.put("content-type","application/json");
                    map.put("authorization","key=AAAAZqmaSRQ:APA91bExxcuM1LGgr_FQgXux6OfUNRkkRFGkSUWYtkoKnKkfsSTjAQSilASItbHKc2eurOwHJMazaw2hVZ8vybMrz_12nxEY9JREsGj--R69XiqrHEBbE2Rlf9ihzb1aJ00AZDCKHqqs");
                    return map;
                }
            };
            requestQueue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void LoadOtherUser() {
        mUserRef.child(OtherUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    OtherTenHienThi = snapshot.child("username").getValue().toString();
                    OtherHinhDaidien = snapshot.child("profileImage").getValue().toString();
                    OtherTrangthai= snapshot.child("status").getValue().toString();
                    Picasso.get().load(OtherHinhDaidien).into(userProfileImageAppbarChat);
                    usernameAppbarTitel.setText(OtherTenHienThi);
                    usernameTrangThaiHoatDong.setText(OtherTrangthai);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatActivity.this, ""+error.getMessage()
                        , Toast.LENGTH_SHORT).show();
            }
        });
    }
}