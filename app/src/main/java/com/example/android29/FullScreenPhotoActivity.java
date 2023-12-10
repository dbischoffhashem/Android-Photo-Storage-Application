package com.example.android29;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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
        showTags();

        // Set click listener for Add Tag button
        findViewById(R.id.addTagButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddTagPopup();
            }
        });
    }

    private void showAddTagPopup() {
        // Inflate the popup layout
        View popupView = getLayoutInflater().inflate(R.layout.popup_add_tag, null);

        // Initialize UI components in the popup layout
        Spinner popupTagNameSpinner = popupView.findViewById(R.id.tagNameSpinner);
        EditText popupTagValueEditText = popupView.findViewById(R.id.tagValueEditText);

        // Set up the dropdown options for the tag name spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.tag_names, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        popupTagNameSpinner.setAdapter(adapter);

        // Create the AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(popupView)
                .setTitle("Add Tag")
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Retrieve selected tag name and tag value from the popup
                        String tagName = popupTagNameSpinner.getSelectedItem().toString();
                        String tagValue = popupTagValueEditText.getText().toString().trim();

                        // Add the tag using the retrieved values
                        addTag(tagName, tagValue);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    private void addTag(String tagName, String tagValue) {
        // Create a new Tag object with the selected name and value
        Tag newTag = new Tag(tagName, tagValue);

        // Add the new tag to the tags list of the current Photo object
        currentPhoto.addTag(newTag);
        showTags();
    }


    private void showTags() {
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