package com.example.chatapp;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.AboutApp.about_app;
import com.example.chatapp.Utills.BaiViet;
import com.example.chatapp.Utills.BinhLuan;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener{
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    //
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference mUserRef , postRef , likeRef ,CommentRef;
    String progileImageUrlV,usernamev;
    CircleImageView profileImageHeaderMenu;
    TextView usernameHeaderMenu;

    ImageView addImagePost,sendImagePost;
    EditText edinputPostDesc;
    private static final int REQUEST_CODE= 101;
    Uri imageUri;
    ProgressDialog mLoadingBar;
    StorageReference postImageRef;

    FirebaseRecyclerAdapter<BaiViet,MyViewHolder>adapter;
    FirebaseRecyclerOptions<BaiViet>options;
    RecyclerView recyclerView;
    FirebaseRecyclerOptions<BinhLuan>BinhluanOption;
    FirebaseRecyclerAdapter<BinhLuan,ComnentViewHolder>CommentAdapter;


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("ChatApp");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);


        mLoadingBar = new ProgressDialog(this);
        recyclerView = findViewById(R.id.ac_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        addImagePost = findViewById(R.id.addImagePost);
        sendImagePost = findViewById(R.id.ac_send_post_imgae);
        edinputPostDesc = findViewById(R.id.activity_inputAddpost);


        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mUserRef = FirebaseDatabase.getInstance().getReference().child(("Users"));
        postRef = FirebaseDatabase.getInstance().getReference().child(("Posts"));
        postImageRef = FirebaseStorage.getInstance().getReference().child("PostImages");
        likeRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        CommentRef = FirebaseDatabase.getInstance().getReference().child("Comments");

        FirebaseMessaging.getInstance().subscribeToTopic(mUser.getUid());

        drawerLayout = findViewById(R.id.activiti_drawLayout);
        navigationView = findViewById(R.id.activity_navView);

        View view = navigationView.inflateHeaderView(R.layout.draer_header_bar);
        profileImageHeaderMenu = view.findViewById(R.id.profile_image);
        usernameHeaderMenu = view.findViewById(R.id.username_header);
        navigationView.setNavigationItemSelectedListener(this);


        sendImagePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddPost();
            }
        });
        addImagePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,REQUEST_CODE);
            }
        });

        LoadPost();

    }

    private void LoadPost() {
        options  = new FirebaseRecyclerOptions.Builder<BaiViet>().setQuery(postRef,BaiViet.class).build();
        adapter = new FirebaseRecyclerAdapter<BaiViet, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final MyViewHolder holder, int position, @NonNull final BaiViet model) {
                final String posKey  = getRef(position).getKey();
                holder.postDesc.setText(model.getPostDesc());
                //holder.timeago.setText(model.getDate());
                String timeAgo = calculateTimeAgo(model.getDate());
                holder.timeago.setText(timeAgo);
                holder.username.setText(model.getUsername());
                Picasso.get().load(model.getPostImageUri()).into(holder.postImage1);
                Picasso.get().load(model.getUserProfileImageUrl()).into(holder.profileImage);
                holder.CouterLikesPost(posKey,mUser.getUid(),likeRef);
                holder.CouterComments(posKey,mUser.getUid(),CommentRef);

                holder.likeimage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        likeRef.child(posKey).child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    likeRef.child(posKey).child(mUser.getUid()).removeValue();
                                    holder.likeimage.setColorFilter(Color.GRAY);
                                    notifyDataSetChanged();
                                }else{
                                    likeRef.child(posKey).child(mUser.getUid()).setValue("Like");
                                    holder.likeimage.setColorFilter(Color.BLUE);
                                    notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // viết gì ở đây này
                            }
                        });
                    }
                });
                holder.imageCommentsSend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String comment = holder.inputComments.getText().toString();
                        if(comment.isEmpty()){
                            Toast.makeText(MainActivity.this, "Bạn chưa nhập bình luận nào ?", Toast.LENGTH_SHORT).show();
                        }else{
                                AddComment(holder,posKey,CommentRef,mUser.getUid(),comment);
                        }
                    }
                });
                LoadComment(posKey); // tai binh luan
                holder.postImage1.setOnClickListener(new View.OnClickListener() { // bắt sự kiện nếu click vào image bài viết
                    @Override
                    public void onClick(View view) {
                        // chuyển màn hình khi sự kiện được kích hoạt
                        Intent intent  =  new Intent(MainActivity.this , XemHinhAnhActivity.class);
                        intent.putExtra("urlImage",model.getPostImageUri() ); // gửi địa chỉ của hình ảnh nhận được thông qua intent
                        startActivity(intent);
                    }
                });
            }
            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_post,parent,false);
                return new MyViewHolder(view);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    private void LoadComment(String posKey) {
        MyViewHolder.recyclerView.setLayoutManager((new LinearLayoutManager(MainActivity.this)));
        BinhluanOption =  new FirebaseRecyclerOptions.Builder<BinhLuan>().setQuery(CommentRef.child(posKey),BinhLuan.class).build();
        CommentAdapter = new FirebaseRecyclerAdapter<BinhLuan, ComnentViewHolder>(BinhluanOption) {
            @Override
            protected void onBindViewHolder(@NonNull ComnentViewHolder holder, int position, @NonNull BinhLuan model) {
                Picasso.get().load(model.getProfileImageUrl()).into(holder.priofileImage);
                holder.username.setText(model.getUsername());
                holder.comment.setText(model.getComments());
            }

            @NonNull
            @Override
            public ComnentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_comment,parent,false);
                return new ComnentViewHolder(view);
            }
        };
        CommentAdapter.startListening();
        MyViewHolder.recyclerView.setAdapter(CommentAdapter);
    }

    private void AddComment(final MyViewHolder holder, String posKey, DatabaseReference commentRef, String uid, String comment) {
        HashMap hashMap = new HashMap();
        hashMap.put("username",usernamev);
        hashMap.put("profileImageUrl",progileImageUrlV);
        hashMap.put("comments",comment);


        commentRef.child(posKey).child(uid).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful()){
                    adapter.notifyDataSetChanged();
                    holder.inputComments.setText(null);
                }else{
                    
                }
            }
        });
    }

    private String calculateTimeAgo(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
        //sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            long time = sdf.parse(date).getTime();
            long now = System.currentTimeMillis();
            CharSequence ago =
                    DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS);
            return  ago+"";
        } catch (Exception  e) {
            e.printStackTrace();
        }
        return "";
    }
//


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode== REQUEST_CODE && resultCode== RESULT_OK && data!= null){
            imageUri = data.getData();
            addImagePost.setImageURI(imageUri);
        }
    }

    private void AddPost() {
        final String postDesc = edinputPostDesc.getText().toString();
        if(postDesc.isEmpty() || postDesc.length() < 2){
            edinputPostDesc.setError("Có gì đó không ổn");
        }else if(imageUri ==  null){
            Toast.makeText(this, "Image is empty, please select an Image", Toast.LENGTH_SHORT).show();
             }else{
            mLoadingBar.setTitle("Đăng bài viết của bạn");
            mLoadingBar.setCanceledOnTouchOutside(false);
            mLoadingBar.show();

            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
            final String strdate = formatter.format(date);


            postImageRef.child(mUser.getUid()+strdate).putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        postImageRef.child(mUser.getUid()+strdate).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                HashMap hashMap = new HashMap();
                                hashMap.put("date", strdate);
                                hashMap.put("postImageUri",uri.toString());
                                hashMap.put("postDesc",postDesc);
                                hashMap.put("userProfileImageUrl",progileImageUrlV);
                                hashMap.put("username",usernamev);

                                postRef.child(mUser.getUid()+strdate).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        if(task.isSuccessful()){
                                            mLoadingBar.dismiss();
                                            //Toast.makeText(MainActivity.this, "ok", Toast.LENGTH_SHORT).show();
                                            addImagePost.setImageResource(R.drawable.ic_image_add_post);
                                            edinputPostDesc.setText("");
                                        }else{
                                            mLoadingBar.dismiss();
                                            Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            }
                        });
                    }else{
                        mLoadingBar.dismiss();
                        Toast.makeText(MainActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mUser == null){
            SendUserToLoginActivity();
        }else{
            mUserRef.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        progileImageUrlV = snapshot.child("profileImage").getValue().toString();
                        usernamev = snapshot.child("username").getValue().toString();
                        Picasso.get().load(progileImageUrlV).into(profileImageHeaderMenu);
                        usernameHeaderMenu.setText(usernamev);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(MainActivity.this, "Lỗi không mong muốn ", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void SendUserToLoginActivity() {
        Intent intent   = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId())
        {
            case R.id.about:
                startActivity(new Intent(MainActivity.this,about_app.class));
                break;
            case R.id.profile:
                startActivity(new Intent(MainActivity.this,ProfileActivity.class));
                break;
            case R.id.firend:
                startActivity(new Intent(MainActivity.this,Danh_Sach_Ban_Be.class));
                break;
            case R.id.find_friend:
                startActivity(new Intent(MainActivity.this,FindFriendActivity.class));
                break;
            case R.id.chat:
                startActivity(new Intent(MainActivity.this , ShowAllUserChat.class));
                break;
            case R.id.logout:
                mAuth.signOut();
                Intent intent = new Intent(MainActivity.this , LoginActivity.class );
                startActivity(intent);
                finish();
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.messenger_icon_menu_mainactivity, menu);
        return true;
    }

    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            drawerLayout.openDrawer((GravityCompat.START));
            return  true;
        }
        if(item.getItemId() == R.id.icon_messenger){
            startActivity(new Intent(MainActivity.this , ShowAllUserChat.class));
        }
        return true;
    }
}