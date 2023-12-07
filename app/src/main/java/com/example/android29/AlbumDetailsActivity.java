package com.example.android29;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class AlbumDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_details);

        showPhotos();
    }

    private void showPhotos() {

    }
}