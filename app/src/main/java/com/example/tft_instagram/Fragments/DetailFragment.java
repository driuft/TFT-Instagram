package com.example.tft_instagram.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.tft_instagram.Adapters.CommentsAdapter;
import com.example.tft_instagram.Models.Comment;
import com.example.tft_instagram.Models.ParcelableObject;
import com.example.tft_instagram.Models.Post;
import com.example.tft_instagram.databinding.FragmentDetailBinding;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class DetailFragment extends Fragment {
    private static final String TAG = "PostDetailsFragment";
    public static final String KEY_POST_RECEIVED = "post to display!";

    FragmentDetailBinding binding;
    Post post;
    RecyclerView rvComments;
    ImageView posterProfile;
    TextView tvDescription;
    TextView tvTimestamp;
    EditText etCommentInput;
    ImageView currUserProfile;
    Button btnComment;

    CommentsAdapter commentsAdapter;
    List<Comment> comments;
    Context context;

    public DetailFragment() {
    }

    public static DetailFragment newInstance(Post post, Context context){
        DetailFragment fragment = new DetailFragment();
        fragment.post = post;
        fragment.context = context;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDetailBinding.inflate(getLayoutInflater(), container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvComments = binding.rvComments;
        posterProfile = binding.ivPosterProfile;
        tvDescription = binding.tvPostDescription;
        tvTimestamp = binding.tvTimestamp;
        etCommentInput = binding.etComment;
        currUserProfile = binding.ivCurrUserProfile;
        btnComment = binding.btnComment;

        comments = new ArrayList<>();
        commentsAdapter = new CommentsAdapter(context, comments);
        //Set on click listener for btnComment:
        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(etCommentInput.getText().toString()) == true){
                    etCommentInput.setError("Cannot be empty");
                    return;
                }
                Comment newComment = post.addComment(etCommentInput.getText().toString(), null);
                comments.add(0, newComment);
                commentsAdapter.notifyItemInserted(0);
                etCommentInput.setText("");

            }
        });

        // Get the comments
        post.getComments(new FindCallback<Comment>() {
            @Override
            public void done(List<Comment> objects, ParseException e) {
                comments.addAll(objects);
                commentsAdapter.notifyDataSetChanged();
            }
        });

        //Populate views:
        //The person that posted:
        tvDescription.setText(post.getDescription());
        tvTimestamp.setText(post.getFormattedTimestamp());

        // Load img of poster
        Glide.with(getContext())
                .load(post.getUser().getParseFile(Post.KEY_PROFILE_PIC).getUrl())
                .centerCrop()
                .transform(new CircleCrop())
                .override(60, 60)
                .into(posterProfile);

        // Load image of current user
        Glide.with(getContext())
                .load(ParseUser.getCurrentUser().getParseFile(Post.KEY_PROFILE_PIC).getUrl())
                .centerCrop()
                .transform(new CircleCrop())
                .into(currUserProfile);

        //Set up recycler view:
        rvComments.setAdapter(commentsAdapter);
        rvComments.setLayoutManager(new LinearLayoutManager(getContext()));

    }
}