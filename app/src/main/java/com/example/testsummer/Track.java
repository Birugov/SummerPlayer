package com.example.testsummer;

public class Track {
    public String title;
    public String artist;
    public String file;
    public int image;

    public Track(String title, String artist, String file, int image) {
        this.title = title;
        this.artist = artist;
        this.file = file;
        this.image = image;
    }

    public String getTitle(){
        return title;
    }

    public String getArtist(){
        return artist;
    }
    public String getFile(){
        return file;
    }
    public int getImage(){
        return image;
    }
}