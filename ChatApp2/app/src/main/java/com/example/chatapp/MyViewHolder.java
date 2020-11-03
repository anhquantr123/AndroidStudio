package com.example.chatapp;

import android.graphics.Color;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyViewHolder extends RecyclerView.ViewHolder {
    CircleImageView profileImage;
    ImageView postImage1,likeimage,commantImage,imageCommentsSend;
    TextView username,timeago,postDesc,likeCounter1,commentsCouter1;
    EditText inputComments;
    public  static  RecyclerView recyclerView;
    public MyViewHolder(@NonNull View itemView) {
        super(itemView);

        profileImage = itemView.findViewById(R.id.profileImagePost);
        postImage1 = itemView.findViewById(R.id.postImage);
        username = itemView.findViewById(R.id.profileUsernamePost);
        timeago = itemView.findViewById(R.id.timeAge);
        postDesc = itemView.findViewById(R.id.postDesc);
        likeimage = itemView.findViewById(R.id.imagelikepost);
        commantImage = itemView.findViewById(R.id.imageCommantPost);
        likeCounter1 = itemView.findViewById(R.id.likeCouter);
        commentsCouter1 = itemView.findViewById(R.id.Commantcouter);
        imageCommentsSend = itemView.findViewById(R.id.sendComments);
        inputComments = itemView.findViewById(R.id.inputComments);
        recyclerView = itemView.findViewById(R.id.recyclerViewComment);


    }

    public void CouterLikesPost(String posKey, final String uid, DatabaseReference likeRef) {
        // Couter like post user
        likeRef.child(posKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    int soLike = (int)snapshot.getChildrenCount();
                    likeCounter1.setText(soLike+"");
                }else {
                    likeCounter1.setText("0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        likeRef.child(posKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(uid).exists()){
                    likeimage.setColorFilter(Color.BLUE);
                }else {
                    likeimage.setColorFilter(Color.GRAY);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void CouterComments(String posKey, String uid, DatabaseReference commentRef) {
        // đếm số comment nhận dc
        commentRef.child(posKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    int socommnet = (int)snapshot.getChildrenCount();
                    commentsCouter1.setText(socommnet+"");
                }else {
                    commentsCouter1.setText("0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}
