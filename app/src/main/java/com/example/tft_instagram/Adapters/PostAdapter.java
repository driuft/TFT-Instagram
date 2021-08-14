package com.example.tft_instagram.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tft_instagram.App;
import com.example.tft_instagram.Fragments.DetailFragment;
import com.example.tft_instagram.Fragments.ProfileFragment;
import com.example.tft_instagram.R;
import com.example.tft_instagram.Models.Post;

import org.parceler.Parcels;

import java.util.Date;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    Context context;
    List<Post> posts;

    public PostAdapter(Context context, List<Post> posts){
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PostAdapter.ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView ivProfile;
        ImageView ivPicture;
        TextView tvTimestamp;
        TextView tvUserhandle;
        TextView tvDescription;
        TextView tvLikes;
        ImageButton ibLike;
        ImageButton ibComment;
        ImageButton ibSend;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPicture = itemView.findViewById(R.id.ivPicture);
            ivProfile = itemView.findViewById(R.id.ivProfile);
            tvUserhandle = itemView.findViewById(R.id.tvProfilename);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            ibLike = itemView.findViewById(R.id.ibLike);
            ibComment = itemView.findViewById(R.id.ibComment);
            ibSend = itemView.findViewById(R.id.ibSend);
            tvLikes = itemView.findViewById(R.id.tvLikes);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
        }

        public void bind(Post post){
            String description = "<b>" + post.getUser().getUsername() + "</b> " + post.getDescription();
            tvDescription.setText(Html.fromHtml(description));
            tvUserhandle.setText(post.getUser().getUsername());
            tvLikes.setText(String.format("%d Likes", post.getLikes()));

            Date date = post.getCreatedAt();
            String dateStr = Post.getRelativeTimeAgo(date.toString());
            tvTimestamp.setText(dateStr);

            if(post.getLikeStatus() == true){
                Drawable unwrapped = AppCompatResources.getDrawable(context, R.drawable.outline_favorite_black_18);
                Drawable wrapped = DrawableCompat.wrap(unwrapped);
                DrawableCompat.setTint(wrapped, Color.RED);
                ibLike.setImageDrawable(wrapped);
            }
            else {
                Drawable unwrapped = AppCompatResources.getDrawable(context, R.drawable.outline_favorite_border_black_18);
                Drawable wrapped = DrawableCompat.wrap(unwrapped);
                DrawableCompat.setTint(wrapped, Color.BLACK);
                ibLike.setImageDrawable(wrapped);
            }

            ibLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(post.getLikeStatus() == true){
                        post.setLikeStatus(false);
                        post.setLikes(post.getLikes() - 1);
                        changeLikeButton(false, post);
                        App.changeLikeStatus(post, false);
                    }
                    else{
                        post.setLikeStatus(true);
                        post.setLikes(post.getLikes() + 1);
                        changeLikeButton(true, post);
                        App.changeLikeStatus(post, true);
                    }
                }
            });

            Glide.with(context)
                    .load(post.getImage().getUrl())
                    .override(410, 250)
                    .centerCrop()
                    .into(ivPicture);

            Glide.with(context)
                    .load(post.getUserProfilePic().getUrl())
                    .circleCrop()
                    .into(ivProfile);

            ivProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppCompatActivity activity = (AppCompatActivity) context;
                    Fragment myFragment = new ProfileFragment(context, post.getUser());
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, myFragment).addToBackStack(null).commit();
                }
            });

            ibComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppCompatActivity activity = (AppCompatActivity) context;
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(DetailFragment.KEY_POST_RECEIVED, Parcels.wrap(post));
                    DetailFragment frag = DetailFragment.newInstance(post, context);
                    frag.setArguments(bundle);
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, frag).addToBackStack(null).commit();
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppCompatActivity activity = (AppCompatActivity) context;
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(DetailFragment.KEY_POST_RECEIVED, Parcels.wrap(post));
                    DetailFragment frag = DetailFragment.newInstance(post, context);
                    frag.setArguments(bundle);
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, frag).addToBackStack(null).commit();
                }
            });
        }

        public void changeLikeButton (boolean likeStatus, Post post){
            if(likeStatus == true){
                Drawable unwrapped = AppCompatResources.getDrawable(context, R.drawable.outline_favorite_black_18);
                Drawable wrapped = DrawableCompat.wrap(unwrapped);
                DrawableCompat.setTint(wrapped, Color.RED);
                ibLike.setImageDrawable(wrapped);
                tvLikes.setText(String.format("%d Likes", post.getLikes()));
            }
            else {
                Drawable unwrapped = AppCompatResources.getDrawable(context, R.drawable.outline_favorite_border_black_18);
                Drawable wrapped = DrawableCompat.wrap(unwrapped);
                DrawableCompat.setTint(wrapped, Color.BLACK);
                ibLike.setImageDrawable(wrapped);
                tvLikes.setText(String.format("%d Likes", post.getLikes()));
            }
        }
    }
}