package com.example.android29;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

public class FullScreenPhotoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_screen_photo);

        // Retrieve the photo path from the intent
        String photoPath = getIntent().getStringExtra("photoPath");

        // Load and display the photo in an ImageView (replace with your UI elements)
        ImageView imageView = findViewById(R.id.fullScreenImageView);
        Bitmap bitmap = BitmapFactory.decodeFile(photoPath);
        imageView.setImageBitmap(bitmap);
    }
}