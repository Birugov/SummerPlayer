package com.example.testsummer.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filterable;

import com.example.testsummer.models.SongList;

import java.util.ArrayList;

public class SongAdapter extends ArrayAdapter<SongList> implements Filterable {
    private Context mContext;
    private ArrayList<SongList> songList = new ArrayList<>();

    public SongAdapter(Context mContext, ArrayList<SongList> songList) {
        super(mContext, 0, songList);
        this.mContext = mContext;
        this.songList = songList;
    }

}
