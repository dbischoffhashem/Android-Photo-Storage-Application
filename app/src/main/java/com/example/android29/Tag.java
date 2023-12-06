package com.example.android29;

import java.io.Serializable;

public class Tag implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String value;

    public Tag(String name, String value) {
        this.name = name;
        this.value = value;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public String toString() {
        return name + ": " + value;
    }
}