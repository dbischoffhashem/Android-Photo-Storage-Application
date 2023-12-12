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

import java.util.ArrayList;
import java.util.List;

public class SearchTwoTagsActivity extends AppCompatActivity {

    private User currentUser;
    private Spinner firstTagSpinner;
    private Spinner secondTagSpinner;
    private EditText searchEditText;
    private EditText searchEditText2;

    private Button searchButton;
    private GridLayout searchResultsGrid;
    private List<String> autocompleteOptionsFirstTag;
    private List<String> autocompleteOptionsSecondTag;
    private String selectedFirstTagType;
    private String selectedSecondTagType;
    private Spinner logicalOperatorSpinner;

    private boolean firstBoxSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_two_tags);

        currentUser = (User) getIntent().getSerializableExtra("currentUser");

        firstTagSpinner = findViewById(R.id.tag1Spinner);
        secondTagSpinner = findViewById(R.id.tag2Spinner);
        searchEditText = findViewById(R.id.searchEditText1);
        searchEditText2 = findViewById(R.id.searchEditText2);
        searchButton = findViewById(R.id.searchTwoTagsButton);

        searchResultsGrid = findViewById(R.id.searchResultsGrid);

        // Initialize the list of autocomplete options for the first and second tags
        autocompleteOptionsFirstTag = new ArrayList<>();
        autocompleteOptionsSecondTag = new ArrayList<>();

        String[] logicalOperators = {"AND", "OR"};
        ArrayAdapter<String> logicalOperatorAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, logicalOperators);
        logicalOperatorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        logicalOperatorSpinner = findViewById(R.id.logicalOperatorSpinner);
        logicalOperatorSpinner.setAdapter(logicalOperatorAdapter);

        // Set up the tag spinners
        List<String> tagNames = currentUser.getTagNames();
        ArrayAdapter<String> tagAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tagNames);
        tagAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        firstTagSpinner.setAdapter(tagAdapter);
        secondTagSpinner.setAdapter(tagAdapter);

        searchResultsGrid.setColumnCount(3); // Adjust this as needed

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performSearch();
            }
        });

        firstTagSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Update the selected first tag type when the user selects a tag from the spinner
                selectedFirstTagType = firstTagSpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });

        secondTagSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Update the selected second tag type when the user selects a tag from the spinner
                selectedSecondTagType = secondTagSpinner.getSelectedItem().toString();
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
                firstBoxSelected = true;
                updateAutocompleteOptions(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Not needed for this example
            }
        });

        searchEditText2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Not needed for this example
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Update the autocomplete options based on the current input
                firstBoxSelected = false;
                updateAutocompleteOptions(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Not needed for this example
            }
        });
    }

    private void performSearch() {
        String firstSelectedTag = firstTagSpinner.getSelectedItem().toString();
        String secondSelectedTag = secondTagSpinner.getSelectedItem().toString();
        String searchTerm = searchEditText.getText().toString().toLowerCase();
        String searchTerm2 = searchEditText2.getText().toString().toLowerCase();
        String logicalOperator = logicalOperatorSpinner.getSelectedItem().toString();

        searchResultsGrid.removeAllViews();

        for (Album album : currentUser.getAlbums()) {
            for (final Photo photo : album.getPhotos()) {
                List<Tag> photoTags = photo.getTags();

                boolean firstTagFound = false;
                boolean secondTagFound = false;

                // Loop through each tag in the photo
                for (Tag tag : photoTags) {
                    if (tag.getName().equalsIgnoreCase(firstSelectedTag) &&
                            tag.getValue().toLowerCase().equals(searchTerm)) {
                        firstTagFound = true;
                    }

                    if (tag.getName().equalsIgnoreCase(secondSelectedTag) &&
                            tag.getValue().toLowerCase().equals(searchTerm2)) {
                        secondTagFound = true;
                    }
                }

                if (("AND".equals(logicalOperator) && firstTagFound && secondTagFound)
                        || ("OR".equals(logicalOperator) && (firstTagFound || secondTagFound))) {
                    // Display the photo...
                //if (firstTagFound && secondTagFound) {
                    ImageView imageView = new ImageView(SearchTwoTagsActivity.this);

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
        autocompleteOptionsFirstTag.clear();
        autocompleteOptionsSecondTag.clear();

        // Separate autocomplete options for different tag types
        List<String> locationOptions = new ArrayList<>();
        List<String> personOptions = new ArrayList<>();
        // Retrieve all photos from your data source (replace it with your actual data retrieval logic)
        List<Photo> allPhotos = getAllPhotos();

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
        if ("Location".equalsIgnoreCase(selectedFirstTagType)) {
            autocompleteOptionsFirstTag.addAll(locationOptions);
        } else if ("Person".equalsIgnoreCase(selectedFirstTagType)) {
            autocompleteOptionsFirstTag.addAll(personOptions);
        }

        if ("Location".equalsIgnoreCase(selectedSecondTagType)) {
            autocompleteOptionsSecondTag.addAll(locationOptions);
        } else if ("Person".equalsIgnoreCase(selectedSecondTagType)) {
            autocompleteOptionsSecondTag.addAll(personOptions);
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

        // Determine which tag is currently being processed and set the adapter accordingly
        ArrayAdapter<String> adapter;
        if (firstBoxSelected) {
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, autocompleteOptionsFirstTag);
        } else {
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, autocompleteOptionsSecondTag);
        }

        listView.setAdapter(adapter);

        // Set an item click listener for the ListView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Handle the selected item, e.g., set the selected option in the EditText
                String selectedOption;
                if (firstBoxSelected) {
                    selectedOption = autocompleteOptionsFirstTag.get(position);
                    searchEditText.setText(selectedOption);

                } else  {
                    selectedOption = autocompleteOptionsSecondTag.get(position);
                    searchEditText2.setText(selectedOption);

                }

                // Dismiss the PopupWindow
                popupWindow.dismiss();
            }
        });

        // Set the content view for the PopupWindow
        popupWindow.setContentView(listView);

        // Calculate the position to show the PopupWindow below the EditText
        int[] location = new int[2];

        if(firstBoxSelected) {
            searchEditText.getLocationOnScreen(location);
            int offsetY = searchEditText.getHeight();

            // Show the PopupWindow at the calculated position
            popupWindow.showAtLocation(searchEditText, Gravity.NO_GRAVITY, location[0], location[1] + offsetY);
        } else {
            searchEditText2.getLocationOnScreen(location);
            int offsetY = searchEditText2.getHeight();

            // Show the PopupWindow at the calculated positions
            popupWindow.showAtLocation(searchEditText2, Gravity.NO_GRAVITY, location[0], location[1] + offsetY);

        }

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