package com.example.chatapp;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

public class banBeMyViewHolder  extends RecyclerView.ViewHolder {
    CircleImageView profileImageUrl;
    TextView username,nghenghiep;
    public banBeMyViewHolder(@NonNull View itemView) {
        super(itemView);
        profileImageUrl  = itemView.findViewById(R.id.profileImageShowBanBe);
        username  = itemView.findViewById(R.id.usernameShowBanBe);
        nghenghiep  = itemView.findViewById(R.id.profession);
    }
}
