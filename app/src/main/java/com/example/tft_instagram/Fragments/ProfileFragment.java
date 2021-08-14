package com.example.tft_instagram.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.tft_instagram.Adapters.ProfileAdapter;
import com.example.tft_instagram.EndlessRecyclerViewScrollListener;
import com.example.tft_instagram.R;
import com.example.tft_instagram.Models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends HomeFragment {

    public static final int IMAGE_CAPTURE_REQUEST = 23;
    List<Post> posts;
    ParseUser user;
    ImageView ivProfile;
    TextView tvHandle;
    TextView tvPosts;
    TextView tvFollowing;
    TextView tvFollowers;
    ProfileAdapter adapter;
    File pictureFile;
    ImageView ivCompose;
    String pictureFileName = "photo.jpg";

    public ProfileFragment(Context context, ParseUser user){
        super(context);
        this.user = user;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvPosts = view.findViewById(R.id.rvPosts);
        tvPosts = view.findViewById(R.id.tvPosts);
        tvFollowers = view.findViewById(R.id.tvFollowerLabel);
        tvFollowing = view.findViewById(R.id.tvFollowingLabel);

        tvPosts.setVisibility(View.INVISIBLE);
        tvFollowers.setVisibility(View.INVISIBLE);
        tvFollowing.setVisibility(View.INVISIBLE);

        ivProfile = view.findViewById(R.id.ivProfile);
        ivCompose = view.findViewById(R.id.ivCompose);

        // Load user profile image with a try/catch (in case of no image)
        Glide.with(context)
                .load(user.getParseFile(Post.KEY_PROFILE_PIC).getUrl())
                .circleCrop()
                .override(140, 140)
                .into(ivProfile);

        if(user == ParseUser.getCurrentUser()) {
            ivProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    launchCamera();
                }
            });
        }

        tvHandle = view.findViewById(R.id.tvHandle);
        tvHandle.setText(user.getUsername());

        swipeContainer = view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryPost();
            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 3);
        scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadMorePosts(totalItemsCount);
            }
        };

        posts = new ArrayList<>();
        adapter = new ProfileAdapter(context, posts);

        rvPosts.setLayoutManager(gridLayoutManager);
        rvPosts.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL));
        rvPosts.setAdapter(adapter);
        rvPosts.addOnScrollListener(scrollListener);
        lastPost = 0;
        queryPost();

        ivCompose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity) context;
                Fragment myFragment = new PostFragment(context);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, myFragment).addToBackStack(null).commit();
            }
        });
    }

    @Override
    protected void queryPost() {
        showProgressBar();
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.whereEqualTo(Post.KEY_USER, user);
        query.setLimit(20);
        query.orderByDescending(Post.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if(e != null) {
                    Log.e(TAG, "Exception: " + e.getMessage());
                    return;
                }
                else{
                    tvPosts.setVisibility(View.VISIBLE);
                    tvFollowers.setVisibility(View.VISIBLE);
                    tvFollowing.setVisibility(View.VISIBLE);
                    tvPosts.setText(String.valueOf(objects.size()));
                    posts.clear();
                    posts.addAll(objects);
                    adapter.notifyDataSetChanged();
                    swipeContainer.setRefreshing(false);
                    hideProgressBar();
                }
            }
        });
    }

    public void launchCamera(){
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        pictureFile = getPhotoFileUri(pictureFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(context, "com.example.fbu_instagram", pictureFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, IMAGE_CAPTURE_REQUEST);
        }
    }

    private File getPhotoFileUri(String pictureFileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + pictureFileName);

        return file;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_CAPTURE_REQUEST) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(pictureFile.getAbsolutePath());
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                // -- ivProfile.setImageBitmap(takenImage);
                Glide.with(context)
                        .load(takenImage)
                        .circleCrop()
                        .override(140, 140)
                        .into(ivProfile);
                // Upload pic to Parse
                updateProfilePic(new ParseFile(pictureFile));
            } else { // Result was a failure
                Toast.makeText(context, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void updateProfilePic(ParseFile profilePic) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            // Other attributes than "email" will remain unchanged!
            currentUser.put("profilePic", profilePic);
            // Saves the object.
            currentUser.saveInBackground(e -> {
                if(e==null){
                    //Save successfull
                    Toast.makeText(context, "Save Successful", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            try {
                ParseUser.getCurrentUser().fetchIfNeeded();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
}