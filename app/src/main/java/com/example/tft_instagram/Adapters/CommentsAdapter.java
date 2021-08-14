package com.example.tft_instagram.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.tft_instagram.Models.Post;
import com.example.tft_instagram.databinding.ItemCommentBinding;
import com.example.tft_instagram.Models.Comment;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {

    ItemCommentBinding binding;
    Context context;
    List<Comment> comments;

    public CommentsAdapter(Context context, List<Comment> comments){
        this.context = context;
        this.comments = comments;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ItemCommentBinding.inflate(LayoutInflater.from(context), parent, false);
        return new CommentsAdapter.ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsAdapter.ViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.bind(comment);
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView ivCommenterProfile;
        TextView tvBody;
        TextView tvTimestamp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCommenterProfile = binding.ivCommenterProfile;
            tvBody = binding.tvBody;
            tvTimestamp = binding.tvTimestamp;
        }

        public void bind(Comment comment){
            try {
                Glide.with(context)
                        .load(comment.getUser().fetchIfNeeded().getParseFile(Post.KEY_PROFILE_PIC).getUrl())
                        .centerCrop()
                        .transform(new CircleCrop())
                        .into(ivCommenterProfile);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            tvBody.setText(comment.getBody());
        }
    }
}