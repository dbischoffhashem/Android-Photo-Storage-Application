package com.example.android29;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Photo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String path;
    private String caption;
    private Date date;
    private List <Tag> tags;

    public Photo(String path, String caption, Date date) { //should tag be an argument?
        this.path = path;
        this.caption = caption;
        this.date = date;
        this.tags = new ArrayList<>();
    }

    // Getters and setters
    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public Date getDate() {
        return date;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public String getPath() {
        return path;
    }

    public void addTag(Tag photoTag) {
        tags.add(photoTag);

    }
}
