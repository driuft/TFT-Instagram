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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.tft_instagram.MainActivity;
import com.example.tft_instagram.R;
import com.example.tft_instagram.Models.Post;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;

import static android.app.Activity.RESULT_OK;

public class PostFragment extends Fragment {

    public static final String TAG = "PostFragment";
    public static final int IMAGE_CAPTURE_REQUEST = 1023;
    public String pictureFileName = "photo.jpg";
    Context context;

    File pictureFile;
    FloatingActionButton fabSubmit;
    EditText etDescription;
    ImageView ivPicture;
    MenuItem bar;

    public PostFragment(Context context){
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_post, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        bar = MainActivity.miActionProgressItem;
        fabSubmit = view.findViewById(R.id.fabSubmit);
        etDescription = view.findViewById(R.id.etDescription);
        ivPicture = view.findViewById(R.id.ivPicture);

        ivPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCamera();
            }
        });

        fabSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser currentUser = ParseUser.getCurrentUser();
                String description = etDescription.getText().toString();
                if(description.isEmpty()){
                    etDescription.setError("Cannot be empty!");
                    return;
                }
                if(pictureFile == null || ivPicture.getDrawable() == getResources().getDrawable(android.R.drawable.ic_menu_report_image)){
                    Toast.makeText(context, "Picture cannot be empty!", Toast.LENGTH_SHORT).show();
                    return;
                }
                savePost(description, currentUser);
                getFragmentManager().popBackStackImmediate();
            }
        });
    }

    private void savePost(String description, ParseUser currentUser) {
        showProgressBar();
        Post post = new Post();
        post.setDescription(description);
        post.setUser(currentUser);
        post.setImage(new ParseFile(pictureFile));
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                hideProgressBar();
                if(e != null){
                    Log.e(TAG, e.getMessage());
                    Toast.makeText(context, "Cannot save post: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
                etDescription.setText("");
                ivPicture.setImageResource(0);
                Toast.makeText(context, "Successfully uploaded picture", Toast.LENGTH_LONG).show();
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
        Uri fileProvider = FileProvider.getUriForFile(context, "com.example.tft_instagram", pictureFile);
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
                ImageView ivPreview = (ImageView) getView().findViewById(R.id.ivPicture);
                ivPreview.setImageBitmap(takenImage);
            } else { // Result was a failure
                Toast.makeText(context, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void showProgressBar() {
        // Show progress item
        bar.setVisible(true);
    }

    public void hideProgressBar() {
        // Hide progress item
        bar.setVisible(false);
    }
}