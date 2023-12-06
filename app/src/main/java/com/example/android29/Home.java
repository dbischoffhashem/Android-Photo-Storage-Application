package com.example.android29;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.Toast;
import com.example.android29.R;

public class Home extends AppCompatActivity {
    private static final String USER_FILE_NAME = "user_data.ser";
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

       currentUser = loadUser();
       if (currentUser == null) {
           // First-time setup: Create a new user instance
           currentUser = new User();
           saveUser();
       }

        //GridLayout albumGrid = findViewById(R.id.albumGrid);

        Button createAlbumButton = findViewById(R.id.createAlbumButton);
        createAlbumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCreateAlbumPopup(currentUser);
            }
        });

    }

    private void showAlbums() {
        //GridLayout albumGrid = findViewById(R.id.albumGrid);
        GridLayout albumGrids = null;
        if (findViewById(R.id.albumGrid) == null) {
            int i = 1;
        }
        //albumGrid.removeAllViews(); // Clear existing views in case this method is called again

        int buttonSize = 200;

        for (Album album : currentUser.getAlbums()) {
            // Create a Button for each album
            Button albumButton = new Button(this);
            albumButton.setId(View.generateViewId()); // Generate a unique ID for the button
            albumButton.setText(album.getName());

            // Set layout parameters
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = buttonSize;
            params.height = buttonSize;
            albumButton.setLayoutParams(params);

            // Set onClickListener
            albumButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle album button click, e.g., navigate to the album's details
                    Toast.makeText(Home.this, "Album clicked: " + album.getName(), Toast.LENGTH_SHORT).show();
                }
            });

            // Add the Button to the GridLayout
            albumGrids.addView(albumButton);
        }
    }


    private void showCreateAlbumPopup(User currentUser) {
        // Inflate the popup layout
        LayoutInflater inflater = LayoutInflater.from(this);
        View popupView = inflater.inflate(R.layout.create_album_popup, null);

        // Create the AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(popupView);

        // Set up the buttons and their actions
        Button okButton = popupView.findViewById(R.id.okButton);
        Button cancelButton = popupView.findViewById(R.id.cancelButton);

        final AlertDialog dialog = builder.create();

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle OK button click
                EditText albumNameEditText = popupView.findViewById(R.id.albumNameEditText);
                String albumName = "";
                if(albumNameEditText != null){
                    albumName = albumNameEditText.getText().toString();

                }

                if (!albumName.isEmpty()) {
                    // Check if any of the user's other albums have the same name
                    if (!isAlbumNameUnique(currentUser, albumName)) {
                        // Show an error message if the album name is not unique
                        Toast.makeText(Home.this, "Album name must be unique.", Toast.LENGTH_SHORT).show();
                    } else {
                        // Create a new album instance with the provided name
                        Album newAlbum = new Album(albumName);

                        // Add the new album to the user's list of albums
                        currentUser.addAlbum(newAlbum);
                        showAlbums();

                        // Dismiss the popup
                        dialog.dismiss();
                    }
                } else {
                    // Show an error message if the album name is empty
                    Toast.makeText(Home.this, "Please enter a non-empty album name.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle Cancel button click
                dialog.dismiss(); // Dismiss the popup
            }
        });

        // Show the AlertDialog
        dialog.show();
    }

    // Helper method to check if the album name is unique among the user's albums
    private boolean isAlbumNameUnique(User user, String albumName) {
        for (Album album : user.getAlbums()) {
            if (album.getName().equals(albumName)) {
                return false; // Album name is not unique
            }
        }
        return true; // Album name is unique
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
}