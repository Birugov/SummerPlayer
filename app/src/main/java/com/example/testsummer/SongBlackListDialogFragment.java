package com.example.testsummer;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;

public class SongBlackListDialogFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        ArrayList<Track> arrayTracks = new ArrayList<>(acticity_setting.arrayList_toCopy);



        String[] whiteList = new String[arrayTracks.size()];
        //load white_list
        for(int i = 0; i < whiteList.length; i++){
            whiteList[i] = arrayTracks.get(i).title;
        }
        String[] blackList = new String[acticity_setting.blackList.getAll().size()];

        //Load black_list
        Object[] blackListLoader = acticity_setting.blackList.getAll().keySet().toArray();
        for(int i = 0; i < blackListLoader.length; i++){
            blackList[i] = (String) blackListLoader[i];
        }

        String[] allTracks = new String[blackList.length + whiteList.length];

        for(int i = 0; i < blackList.length; i++){
            allTracks[i] = blackList[i];
        }

        for(int i = 0; i < whiteList.length; i++){
            allTracks[i+blackList.length] = whiteList[i];
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Black List")
                .setItems(allTracks, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        String songName = allTracks[which];

                        boolean trackOnBlack = acticity_setting.blackList.getBoolean(songName, false);
                        if(!trackOnBlack){
                            acticity_setting.blackList.edit().putBoolean(songName, true).apply();
                            acticity_setting.arrayList_toCopy.remove(which-blackList.length);
                        } else {
                            acticity_setting.arrayList_toCopy.add(new Track(songName, "", "", new byte[0]));
                            acticity_setting.blackList.edit().remove(songName).apply();
                        }
                    }
                });

        return builder.create();
    }
}



