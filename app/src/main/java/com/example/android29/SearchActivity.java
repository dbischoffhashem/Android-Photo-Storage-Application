package com.example.android29;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

// Import statements

public class SearchActivity extends AppCompatActivity {

    private User currentUser;
    private Spinner tagSpinner;
    private EditText searchEditText;
    private Button searchButton;
    private GridLayout searchResultsGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);

        currentUser = (User) getIntent().getSerializableExtra("currentUser");

        tagSpinner = findViewById(R.id.tagSpinner);
        searchEditText = findViewById(R.id.searchEditText);
        searchButton = findViewById(R.id.searchButton);
        searchResultsGrid = findViewById(R.id.searchResultsGrid);

        // Set up the tag spinner
        List<String> tagNames = currentUser.getTagNames();
        ArrayAdapter<String> tagAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tagNames);
        tagAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tagSpinner.setAdapter(tagAdapter);

        searchResultsGrid.setColumnCount(3); // Adjust this as needed

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performSearch();
            }
        });
    }

    private void performSearch() {
        String selectedTag = tagSpinner.getSelectedItem().toString();
        String searchTerm = searchEditText.getText().toString().toLowerCase();

        searchResultsGrid.removeAllViews();

        for (Album album : currentUser.getAlbums()) {
            for (final Photo photo : album.getPhotos()) {
                for (Tag tag : photo.getTags()) {
                    if (tag.getName().equalsIgnoreCase(selectedTag) &&
                            tag.getValue().toLowerCase().contains(searchTerm)) {
                        ImageView imageView = new ImageView(SearchActivity.this);

                        int screenWidth = getResources().getDisplayMetrics().widthPixels;
                        int squareSize = screenWidth / 3;
                        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                        params.width = squareSize;
                        params.height = squareSize;
                        imageView.setLayoutParams(params);

                        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        imageView.setPadding(8, 8, 8, 8);

                        Bitmap bitmap = decodeFile(photo.getPath());
                        if (bitmap != null) {
                            imageView.setImageBitmap(bitmap);

                            searchResultsGrid.addView(imageView);
                        } else {
                            Log.e("Bitmap", "Bitmap is null for path: " + photo.getPath());
                        }
                        break;
                    }
                }
            }
        }
    }

    private Bitmap decodeFile(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        return BitmapFactory.decodeFile(path, options);
    }

}
