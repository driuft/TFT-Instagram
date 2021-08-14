package com.example.tft_instagram.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tft_instagram.R;
import com.example.tft_instagram.Models.Post;

import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder> {

    List<Post> posts;
    Context context;

    public ProfileAdapter(Context context, List<Post> posts){
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_profile_post, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileAdapter.ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView ivPicture;

        public ViewHolder(@NonNull  View itemView) {
            super(itemView);
            ivPicture = itemView.findViewById(R.id.ivPicture);
        }

        public void bind(Post post) {
            Glide.with(context)
                    .load(post.getParseFile(Post.KEY_IMAGE).getUrl())
                    .override(210, 415)
                    .centerCrop()
                    .into(ivPicture);
        }
    }
}