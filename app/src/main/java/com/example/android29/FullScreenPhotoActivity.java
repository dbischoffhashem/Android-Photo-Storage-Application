package com.example.android29;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class FullScreenPhotoActivity extends AppCompatActivity {

    private User currentUser;
    private Album currentAlbum;
    private Photo currentPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_screen_photo);

        currentUser = (User) getIntent().getSerializableExtra("currentUser");
        List<Album> albums = currentUser.getAlbums();
        for (Album album : albums) {
            String currentAlbumName = getIntent().getStringExtra("albumName");
            if(album.getName().equals(currentAlbumName)) {
                currentAlbum = album;
                break;
            }
        }
        List<Photo> photos = currentAlbum.getPhotos();
        String photoPath = getIntent().getStringExtra("photoPath");

        for (Photo photo : photos) {
            if(photo.getPath().equals(photoPath)) {
                currentPhoto = photo;
                break;
            }
        }

        // Load and display the photo in an ImageView (replace with your UI elements)
        ImageView imageView = findViewById(R.id.fullScreenImageView);
        Bitmap bitmap = BitmapFactory.decodeFile(photoPath);
        imageView.setImageBitmap(bitmap);

        // Display the tags in a TextView
        TextView tagsTextView = findViewById(R.id.tagsTextView);
        if (currentPhoto.getTags() != null && !currentPhoto.getTags().isEmpty()) {
            StringBuilder tagsString = new StringBuilder("Tags: ");
            for (Tag tag : currentPhoto.getTags()) {
                tagsString.append(tag.toString()).append(" ");
            }
            tagsTextView.setText(tagsString.toString());
        } else {
            tagsTextView.setText("No tags");
        }
    }
}