package com.example.chatapp;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

public class ComnentViewHolder extends RecyclerView.ViewHolder {
    CircleImageView priofileImage;
    TextView username,comment;
    public ComnentViewHolder(@NonNull View itemView) {
        super(itemView);
        priofileImage = itemView.findViewById(R.id.profileImage_comment);
        username = itemView.findViewById(R.id.usernameComment);
        comment = itemView.findViewById(R.id.commentTV);
    }
}
