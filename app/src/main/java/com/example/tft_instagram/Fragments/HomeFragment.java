package com.example.tft_instagram.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.tft_instagram.Adapters.PostAdapter;
import com.example.tft_instagram.EndlessRecyclerViewScrollListener;
import com.example.tft_instagram.R;
import com.example.tft_instagram.Models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    public static final String TAG = "HomeFragment";
    int lastPost;
    Context context;
    List<Post> posts;
    RecyclerView rvPosts;
    PostAdapter adapter;
    SwipeRefreshLayout swipeContainer;
    EndlessRecyclerViewScrollListener scrollListener;
    //MenuItem bar;

    public HomeFragment() {
        // Required empty public constructor
    }

    public HomeFragment(Context context){
        this.context = context;
    }

    public void showProgressBar() {
        // Show progress item
        //bar.setVisible(true);
    }

    public void hideProgressBar() {
        // Hide progress item
        //bar.setVisible(false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //bar = MainActivity.miActionProgressItem;
        rvPosts = view.findViewById(R.id.rvPosts);
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

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadMorePosts(totalItemsCount);
            }
        };

        posts = new ArrayList<>();
        adapter = new PostAdapter(context, posts);
        rvPosts.setLayoutManager(linearLayoutManager);
        rvPosts.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        rvPosts.setAdapter(adapter);
        rvPosts.addOnScrollListener(scrollListener);
        lastPost = 0;
        queryPost();
    }

    protected void queryPost(){
        showProgressBar();
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
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
                    lastPost = objects.size();
                    posts.clear();
                    posts.addAll(objects);
                    adapter.notifyDataSetChanged();
                    swipeContainer.setRefreshing(false);
                    hideProgressBar();
                }
            }
        });
    }

    public void loadMorePosts(int offset){
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.setLimit(20);
        query.setSkip(offset);
        query.orderByDescending(Post.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if(e != null) {
                    Log.e(TAG, "Exception: " + e.getMessage());
                    return;
                }
                else{
                    posts.addAll(posts.size(), objects);
                    // 2. Notify the adapter of the update
                    adapter.notifyItemRangeInserted(offset, posts.size() - 1);
                    // 3. Reset endless scroll listener when performing a new search
                    scrollListener.resetState();
                }
            }
        });
    }
}