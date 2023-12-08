package com.example.android29;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Date;
import java.util.List;


public class AlbumDetailsActivity extends AppCompatActivity {

    private Album currentAlbum;
    private static final int MY_PERMISSIONS_REQUEST_READ_MEDIA_IMAGES = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_details);

        // Assuming you have passed the current album to this activity
        currentAlbum = (Album) getIntent().getSerializableExtra("album");

        Button addPhotoButton = findViewById(R.id.addPhoto);
        addPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(AlbumDetailsActivity.this, Manifest.permission.READ_MEDIA_IMAGES)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Permission is not granted, request it
                    ActivityCompat.requestPermissions(AlbumDetailsActivity.this,
                            new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                            MY_PERMISSIONS_REQUEST_READ_MEDIA_IMAGES);
                } else {
                    // Permission is already granted, proceed with your code
                    // For example, open the file picker or interact with the MediaStore
                    openGallery();
                }

                openGallery();
                //showAddPhotoDialog();
            }
        });
        showPhotos();
    }

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    Uri selectedImageUri = data.getData();

                    // Now you can use the selectedImageUri to do whatever you need with the selected image
                    // For example, you can create a Photo object and add it to the current album
                    String imagePath = getPathFromUri(selectedImageUri);
                    if (imagePath != null) {
                        Photo selectedPhoto = new Photo(imagePath, "Caption", new Date());
                        currentAlbum.addPhoto(selectedPhoto);
                        showPhotos();
                    }
                }
            }
    );

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(galleryIntent);
    }
    private Bitmap decodeFile(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4; // Adjust this value as needed
        return BitmapFactory.decodeFile(path, options);
    }

    private String getPathFromUri(Uri contentUri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, projection, null, null, null);
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String imagePath = cursor.getString(columnIndex);
            cursor.close();
            return imagePath;
        }
        return null;
    }

    private void showPhotos() {
        LinearLayout photosLayout = findViewById(R.id.photosLayout);
        photosLayout.removeAllViews();

        List<Photo> photos = currentAlbum.getPhotos();

        for (Photo photo : photos) {
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));

            Bitmap bitmap = decodeFile(photo.getPath());
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
                photosLayout.addView(imageView);
            } else {
                Log.e("Bitmap", "Bitmap is null for path: " + photo.getPath());
            }
        }
    }

}
