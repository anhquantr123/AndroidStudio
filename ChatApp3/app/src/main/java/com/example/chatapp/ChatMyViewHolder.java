package com.example.chatapp;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatMyViewHolder extends RecyclerView.ViewHolder {

    CircleImageView firstUserProfile, secondUserProfile;
    TextView firstUserText, secondUserText;
    ImageView chat_image_send_right, chat_image_send_left;
    public ChatMyViewHolder(@NonNull View itemView) {
        super(itemView);
        firstUserProfile = itemView.findViewById(R.id.firstUserProfile);
        secondUserProfile = itemView.findViewById(R.id.secondUserProfile);
        firstUserText = itemView.findViewById(R.id.firstUserText);
        secondUserText = itemView.findViewById(R.id.secondtUserText);
        chat_image_send_right = itemView.findViewById(R.id.chat_image_send_right);
        chat_image_send_left = itemView.findViewById(R.id.chat_image_send_left);
    }
}
