package com.example.android29;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<Album> albums;
    private List<String> tagNames;

    public User() {
        //this.username = username;
        this.albums = new ArrayList<>();
        tagNames = new ArrayList<>();
        tagNames.add("Location");
        tagNames.add("Person");
    }

    // Getters and setters

    public List<Album> getAlbums() {
        return albums;
    }

    public void setAlbums(List<Album> albums) {
        this.albums = albums;
    }

    public void addAlbum(Album album) {
        albums.add(album);
    }

    public List<String> getTagNames() {
        return tagNames;
    }
}
