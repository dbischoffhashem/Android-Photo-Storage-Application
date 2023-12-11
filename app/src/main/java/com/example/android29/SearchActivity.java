package com.example.android29;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
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
    private List<String> autocompleteOptions;
    private String selectedTagType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);

        currentUser = (User) getIntent().getSerializableExtra("currentUser");

        tagSpinner = findViewById(R.id.tagSpinner);
        searchEditText = findViewById(R.id.searchEditText);
        searchButton = findViewById(R.id.searchButton);
        searchResultsGrid = findViewById(R.id.searchResultsGrid);
        // Initialize the list of autocomplete options
        autocompleteOptions = new ArrayList<>();

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

        tagSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Update the selected tag type when the user selects a tag from the spinner
                selectedTagType = tagSpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });

        // Set up the TextWatcher for dynamic autocomplete
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Not needed for this example
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Update the autocomplete options based on the current input
                updateAutocompleteOptions(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Not needed for this example
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

    private void updateAutocompleteOptions(String currentInput) {
        // Clear the existing autocomplete options
        autocompleteOptions.clear();

        // Retrieve all photos from your data source (replace it with your actual data retrieval logic)
        List<Photo> allPhotos = getAllPhotos();

        // Separate autocomplete options for different tag types
        List<String> locationOptions = new ArrayList<>();
        List<String> personOptions = new ArrayList<>();

        // Loop through each photo and its tags
        for (Photo photo : allPhotos) {
            List<Tag> photoTags = photo.getTags();

            // Loop through each tag in the photo
            for (Tag tag : photoTags) {
                // Check if the tag value contains the current input (you can modify the condition as needed)
                if (tag.getValue().toLowerCase().startsWith(currentInput.toLowerCase())) {
                    // Add the tag value to the corresponding autocomplete options list
                    if ("Location".equalsIgnoreCase(tag.getName()) && !locationOptions.contains(tag.getValue())) {
                        locationOptions.add(tag.getValue());
                    } else if ("Person".equalsIgnoreCase(tag.getName()) && !personOptions.contains(tag.getValue())) {
                        personOptions.add(tag.getValue());
                    }
                }
            }
        }

        // Add options from the relevant list to the main autocomplete options list
        if ("Location".equalsIgnoreCase(selectedTagType)) {
            autocompleteOptions.addAll(locationOptions);
        } else if ("Person".equalsIgnoreCase(selectedTagType)) {
            autocompleteOptions.addAll(personOptions);
        }

        // Show the filtered options in a dropdown or another UI element
        showAutocompleteOptions();
    }

    private void showAutocompleteOptions() {
        // Create a PopupWindow to display autocomplete options
        PopupWindow popupWindow = new PopupWindow(this);
        popupWindow.setFocusable(true);

        // Create a ListView to hold the autocomplete options
        ListView listView = new ListView(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, autocompleteOptions);
        listView.setAdapter(adapter);

        // Set an item click listener for the ListView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Handle the selected item, e.g., set the selected option in the EditText
                String selectedOption = autocompleteOptions.get(position);
                searchEditText.setText(selectedOption);

                // Dismiss the PopupWindow
                popupWindow.dismiss();
            }
        });

        // Set the content view for the PopupWindow
        popupWindow.setContentView(listView);

        // Calculate the position to show the PopupWindow below the EditText
        int[] location = new int[2];
        searchEditText.getLocationOnScreen(location);
        int offsetY = searchEditText.getHeight();

        // Show the PopupWindow at the calculated position
        popupWindow.showAtLocation(searchEditText, Gravity.NO_GRAVITY, location[0], location[1] + offsetY);
    }


    private List<Photo> getAllPhotos() {
        List<Photo> allPhotos = new ArrayList<>();

        // Get the current user from your existing logic
        if (currentUser != null) {
            // Iterate through each album
            List<Album> userAlbums = currentUser.getAlbums();
            for (Album album : userAlbums) {
                // Iterate through each photo in the album
                List<Photo> albumPhotos = album.getPhotos();
                allPhotos.addAll(albumPhotos);
            }
        }

        return allPhotos;
    }


}
