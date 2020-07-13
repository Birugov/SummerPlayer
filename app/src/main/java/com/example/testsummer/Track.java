package com.example.testsummer;

import java.net.URL;

public class Track {
    public String title;
    public String artist;
    public String file;
    public byte[] image = {};

    public Track(String title, String artist, String file, byte[] image) {
        this.title = title;
        this.artist = artist;
        this.file = file;
        if (image != null)
        this.image = image.clone();
    }
}
