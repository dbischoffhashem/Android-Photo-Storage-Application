package com.example.android29;

import static com.example.android29.Home.USER_FILE_NAME;

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
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
//import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.gridlayout.widget.GridLayout;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AlbumDetailsActivity extends AppCompatActivity {

    private Album currentAlbum;
    private User currentUser;
    private GridLayout photoGrid;
    private static final int MY_PERMISSIONS_REQUEST_READ_MEDIA_IMAGES = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_details);

        photoGrid =(GridLayout) findViewById(R.id.photoGrid);
        currentUser = (User) getIntent().getSerializableExtra("currentUser");
        List<Album> albums = currentUser.getAlbums();
        for (Album album : albums) {
            String currentAlbumName = getIntent().getStringExtra("album");
            if(album.getName().equals(currentAlbumName)) {
                currentAlbum = album;
                break;
            }
        }

        registerForContextMenu(photoGrid); //ADDED

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

                //openGallery();
            }
        });
        showPhotos();
    }


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
        photoGrid.removeAllViews();

        List<Photo> photos = currentAlbum.getPhotos();

        for (int i = 0; i < photos.size(); i++) {
            Photo photo = photos.get(i);

            ImageView imageView = new ImageView(this);

            // Set the context menu for each ImageView
            //registerForContextMenu(imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPhotoOptions(photo);
                }
            });
            // Calculate the width and height of each square based on the screen width
            int screenWidth = getResources().getDisplayMetrics().widthPixels;
            int squareSize = screenWidth / 3; // Three squares per row
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = squareSize;
            params.height = squareSize;
            imageView.setLayoutParams(params);

            //registerForContextMenu(imageView);

            // Set your desired properties for the ImageView, such as scale type, padding, etc.
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);

            // Decode the bitmap from the file path
            Bitmap bitmap = decodeFile(photo.getPath());
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);

                // Add the ImageView to the GridLayout
                photoGrid.addView(imageView);
            } else {
                Log.e("Bitmap", "Bitmap is null for path: " + photo.getPath());
            }
        }
        registerForContextMenu(photoGrid);
    }

    private void showPhotoOptions(final Photo photo) {
        PopupMenu popupMenu = new PopupMenu(this, findViewById(R.id.photoGrid));
        popupMenu.getMenuInflater().inflate(R.menu.photo_options_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.menu_openPhoto) {
                    // Handle "Open" option
                    openPhoto(photo);
                    return true;
                } else if (item.getItemId() == R.id.menu_deletePhoto) {
                    // Handle "Delete" option
                    deletePhoto(photo);
                    return true;
                } else if (item.getItemId() == R.id.menu_movePhoto) {
                    // Handle "Move Photo" option
                    showMovePhotoPopup(photo);
                    return true;
                } else {
                    return false;
                }
            }
        });

        popupMenu.show();
    }

    private void showMovePhotoPopup(final Photo photo) {
        // Inflate the popup layout
        View popupView = getLayoutInflater().inflate(R.layout.popup_move_photo, null);

        // Initialize UI components in the popup layout
        Spinner moveAlbumSpinner = popupView.findViewById(R.id.moveAlbumSpinner);

        // Set up the dropdown options for the move album spinner
        List<Album> userAlbums = currentUser.getAlbums();
        List<String> albumNames = new ArrayList<>();
        for(Album album: userAlbums) {
            albumNames.add(album.getName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, albumNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        moveAlbumSpinner.setAdapter(adapter);

        // Create the AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(popupView)
                .setTitle("Move Photo")
                .setPositiveButton("Move", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Retrieve selected album to move the photo
                        String selectedAlbumName = (String) moveAlbumSpinner.getSelectedItem();
                        // Find the corresponding Album object based on the selected album name
                        Album selectedAlbum = findAlbumByName(selectedAlbumName);

                        // Move the photo to the selected album
                        movePhotoToAlbum(photo, selectedAlbum);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private Album findAlbumByName(String albumName) {
        List<Album> userAlbums = currentUser.getAlbums();
        for (Album album : userAlbums) {
            if (album.getName().equals(albumName)) {
                return album;
            }
        }
        return null; // Handle the case when the album is not found
    }


    private void movePhotoToAlbum(Photo photo, Album destinationAlbum) {
        // Remove the photo from the current album
        currentAlbum.getPhotos().remove(photo);

        // Add the photo to the destination album
        destinationAlbum.addPhoto(photo);

        // Refresh the displayed photos
        showPhotos();
    }

    private void openPhoto(final Photo photo) {

        Intent intent = new Intent(this, FullScreenPhotoActivity.class);
        intent.putExtra("currentUser", currentUser);
        intent.putExtra("albumName", currentAlbum.getName());
        intent.putExtra("photoPath", photo.getPath());
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //ADDED
        startActivity(intent);
    }


    private void deletePhoto(final Photo photo) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Photo");
        builder.setMessage("Are you sure you want to delete the photo?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Remove the album from the user's list of albums
                currentAlbum.getPhotos().remove(photo);
                showPhotos(); // Refresh the displayed albums
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


    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    Uri selectedImageUri = data.getData();

                    // Now you can use the selectedImageUri to do whatever you need with the selected image
                    // For example, you can create a Photo object and add it to the current album
                    String imagePath = getPathFromUri(selectedImageUri);
                    String comparePath = null;
                    boolean duplicatePhoto = false;
                    if (imagePath != null)
                    {
                        Photo selectedPhoto = new Photo(imagePath, "Caption", new Date());
                        for (Photo p : currentAlbum.getPhotos())
                        {
                            comparePath = p.getPath();
                            if (comparePath.equalsIgnoreCase(imagePath))
                            {
                                duplicatePhoto = true;
                            }
                        }
                        if (!duplicatePhoto)
                        {
                            currentAlbum.addPhoto(selectedPhoto);
                            showPhotos();
                        }
                        else
                        {
                            showToast("Can not add photo, there is a duplicate!");
                        }
                    }
                    else
                    {
                        showToast("Can not add photo, there is no path!");
                    }
                }
            }
    );

    private void showToast(String message) {
        // Display a short-lived message on the screen (Toast)
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}

