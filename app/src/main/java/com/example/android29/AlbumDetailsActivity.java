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
import android.widget.Button;
//import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.gridlayout.widget.GridLayout;
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
        photoGrid.removeAllViews();

        List<Photo> photos = currentAlbum.getPhotos();

        for (int i = 0; i < photos.size(); i++) {
            Photo photo = photos.get(i);

            ImageView imageView = new ImageView(this);

            // Set the context menu for each ImageView
            //registerForContextMenu(imageView);

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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.photo_options_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // Get the position/index of the clicked photo
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int photoIndex = info.position;

        if (item.getItemId() == R.id.menu_openPhoto) {
            // Handle "Open" option (e.g., show a larger version of the photo)
            openPhoto(photoIndex);
            return true;
        }

        if (item.getItemId() == R.id.menu_deletePhoto) {
            // Handle "Delete" option
            deletePhoto(photoIndex);
            return true;
        }
        return super.onContextItemSelected(item);
    }

    private void openPhoto(int photoIndex) {
        // Implement the logic to open the selected photo (e.g., show a larger version)
        // You can use the 'photoIndex' to get the corresponding Photo object from the list
        // and then use its path or other details as needed.
        // Add your code here...
    }

    private void deletePhoto(int photoIndex) {
        // Check if the index is within bounds
        if (photoIndex >= 0 && photoIndex < currentAlbum.getPhotos().size()) {
            // Get the selected photo from the currentAlbum
            Photo selectedPhoto = currentAlbum.getPhotos().get(photoIndex);

            // Remove the selected photo from the currentAlbum
            currentAlbum.getPhotos().remove(photoIndex);

            // Update the displayed photos
            showPhotos();

            // Optionally, show a toast or other notification
            Toast.makeText(this, "Photo deleted", Toast.LENGTH_SHORT).show();
        } else {
            // Handle the case where the index is out of bounds
            Toast.makeText(this, "Invalid photo index", Toast.LENGTH_SHORT).show();
        }
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
