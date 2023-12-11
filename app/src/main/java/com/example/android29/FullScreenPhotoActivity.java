package com.example.android29;

import static com.example.android29.Home.USER_FILE_NAME;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;
import android.widget.Toast;


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

        // Set click listener for Remove Tag button
        findViewById(R.id.removeTagButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRemoveTagPopup();
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

        boolean dupExists= false;
        boolean isLocationTag = tagName.equalsIgnoreCase("Location");
        boolean locationTagExists = false;
        if (currentPhoto.getTags() != null && !currentPhoto.getTags().isEmpty())
        {
            for(Tag existingTag: currentPhoto.getTags())
            {
                if(existingTag.getValue().equalsIgnoreCase(newTag.getValue()))
                {
                    dupExists = true;
                }
                if(existingTag.getName().equalsIgnoreCase("Location"))
                {
                    locationTagExists = true;
                }
            }
        }
        if (isLocationTag && locationTagExists) {
            // Show error message for duplicate location tag
            showToast("Location tag already exists.");
        } else if (!isLocationTag && dupExists) {
            // Show error message for duplicate tag (excluding location tag)
            showToast("Duplicate tag name.");
        } else {
            // Add the new tag
            currentPhoto.addTag(newTag);
            showTags();
        }
    }

    private void showToast(String message) {
        // Display a short-lived message on the screen (Toast)
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showRemoveTagPopup() {
        // Inflate the popup layout
        View popupView = getLayoutInflater().inflate(R.layout.popup_remove_tag, null);

        // Initialize UI components in the popup layout
        Spinner removeTagSpinner = popupView.findViewById(R.id.removeTagSpinner);

        // Populate the dropdown options for the remove tag spinner
        ArrayAdapter<Tag> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, currentPhoto.getTags());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        removeTagSpinner.setAdapter(adapter);

        // Create the AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(popupView)
                .setTitle("Remove Tag")
                .setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Retrieve selected tag to remove
                        Tag selectedTag = (Tag) removeTagSpinner.getSelectedItem();

                        // Remove the selected tag
                        removeTag(selectedTag);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void removeTag(Tag tagToRemove) {
        // Remove the selected tag from the tags list of the current Photo object
        currentPhoto.getTags().remove(tagToRemove);

        // Update the UI to reflect the changes
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

    public void startSlideshow(View view) {
        // Get the index of the current photo in the album
        final int[] currentIndex = {currentAlbum.getPhotos().indexOf(currentPhoto)};

        // Create a layout inflater to inflate the slideshow popup
        LayoutInflater inflater = getLayoutInflater();
        View popupView = inflater.inflate(R.layout.popup_slideshow, null);

        // Initialize UI components in the popup layout
        ImageView slideshowImageView = popupView.findViewById(R.id.slideshowImageView);
        Button nextButton = popupView.findViewById(R.id.nextButton);
        Button prevButton = popupView.findViewById(R.id.prevButton);

        // Display the current photo in the slideshow popup
        Bitmap currentBitmap = BitmapFactory.decodeFile(currentPhoto.getPath());
        slideshowImageView.setImageBitmap(currentBitmap);

        // Set up click listeners for next and previous buttons
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Move to the next photo in the album
                currentIndex[0] = (currentIndex[0] + 1) % currentAlbum.getPhotos().size();
                updateSlideshowImage(slideshowImageView, currentIndex[0]);
            }
        });

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Move to the previous photo in the album
                currentIndex[0] = (currentIndex[0] - 1 + currentAlbum.getPhotos().size()) % currentAlbum.getPhotos().size();
                updateSlideshowImage(slideshowImageView, currentIndex[0]);
            }
        });

        // Create the AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(popupView)
                .setTitle("Slideshow")
                .setNegativeButton("Close", null)
                .show();
    }

    private void updateSlideshowImage(ImageView imageView, int index) {
        // Update the image in the slideshow popup based on the given index
        Photo newPhoto = currentAlbum.getPhotos().get(index);
        Bitmap bitmap = BitmapFactory.decodeFile(newPhoto.getPath());
        imageView.setImageBitmap(bitmap);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Save user data when the app is paused (e.g., when going to the background)
        saveUser();
    }

    private User loadUser() {
        if (SerializationHelper.fileExists(this, USER_FILE_NAME)) {
            return (User) SerializationHelper.deserialize(this, USER_FILE_NAME);
        } else {
            // Handle the case when the file doesn't exist
            return null;
        }
    }

    private void saveUser() {
        SerializationHelper.serialize(this, currentUser, USER_FILE_NAME);
    }

//    @Override
//    public void onBackPressed() {
//        Intent intent = new Intent();
//        intent.putExtra("currentUser", currentUser);
//        intent.putExtra("album", currentAlbum.getName());
//        setResult(Activity.RESULT_OK, intent);
//        super.onBackPressed(); // Call the default back button behavior
//    }

}