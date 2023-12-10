package com.example.android29;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;
import com.example.android29.R;

public class Home extends AppCompatActivity {
    protected static final String USER_FILE_NAME = "user_data.ser";
    private User currentUser;

    private void showAlbums() {
        LinearLayout albumsLayout = findViewById(R.id.albumsLayout); // Assuming you have a LinearLayout with id albumsLayout in home.xml
        albumsLayout.removeAllViews(); // Clear existing views

        // Create a button for each album
        for (Album album : currentUser.getAlbums()) {
            Button albumButton = new Button(this);
            albumButton.setText(album.getName());
            albumButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAlbumOptions(album);
                }
            });

            // Add the album button to the layout
            albumsLayout.addView(albumButton);
        }
    }

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

        Button createAlbumButton = findViewById(R.id.createAlbumButton);
        createAlbumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCreateAlbumPopup(currentUser);
            }
        });

        Button searchButton = findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSearchActivity();
            }
        });

        showAlbums();
    }

    private void openSearchActivity() {
        Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtra("currentUser", currentUser);
        startActivity(intent);
    }

    private void showAlbumOptions(final Album album) {
        PopupMenu popupMenu = new PopupMenu(this, findViewById(R.id.albumsLayout));
        popupMenu.getMenuInflater().inflate(R.menu.album_options_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.menu_open) {
                    // Handle "Open" option
                    openAlbumDetails(album);
                    return true;
                } else if (item.getItemId() == R.id.menu_rename) {
                    // Handle "Rename" option
                    showRenameAlbumDialog(album);
                    return true;
                } else if (item.getItemId() == R.id.menu_delete) {
                    // Handle "Delete" option
                    showDeleteAlbumDialog(album);
                    return true;
                } else {
                    return false;
                }
            }
        });

        popupMenu.show();
    }

    private void showRenameAlbumDialog(final Album album) {
        // Implement logic to show a dialog for renaming the album
        // You can use AlertDialog.Builder for this purpose
        // Example:
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Rename Album");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(album.getName()); // Pre-fill the current album name
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newAlbumName = input.getText().toString().trim();

                if (!newAlbumName.isEmpty()) {
                    // Update the album name
                    album.setName(newAlbumName);
                    showAlbums(); // Refresh the displayed albums
                } else {
                    // Show an error message if the new name is empty
                    Toast.makeText(Home.this, "Please enter a non-empty album name.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void showDeleteAlbumDialog(final Album album) {
        // Implement logic to show a dialog for confirming album deletion
        // You can use AlertDialog.Builder for this purpose
        // Example:
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Album");
        builder.setMessage("Are you sure you want to delete the album '" + album.getName() + "'?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Remove the album from the user's list of albums
                currentUser.getAlbums().remove(album);
                showAlbums(); // Refresh the displayed albums
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
    private void openAlbumDetails(Album album) {
        Intent intent = new Intent(this, AlbumDetailsActivity.class);
        intent.putExtra("currentUser", currentUser);
        intent.putExtra("album", album.getName());
        startActivity(intent);
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
                if (albumNameEditText != null) {
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