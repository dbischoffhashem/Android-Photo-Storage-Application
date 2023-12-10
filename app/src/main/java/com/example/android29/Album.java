package com.example.android29;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Album implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private List<Photo> photos; // Store the paths of photos

    public Album(String name) {
        this.name = name;
        this.photos = new ArrayList<>();
    }

    // Getters and setters
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;

    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public void addPhoto(Photo photo) {
        photos.add(photo);
    }
}

