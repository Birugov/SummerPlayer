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

        ArrayList<Track> arrayTracks = activity_main.allTrack;

        boolean[] checkedItemsArray = new boolean[arrayTracks.size()];

        for(int i = 0; i < checkedItemsArray.length; i++){
           checkedItemsArray[i] = acticity_setting.blackList.getBoolean(arrayTracks.get(i).title, false);
        }

        String[] allTracks = new String[arrayTracks.size()];
        for(int i = 0; i < allTracks.length; i++){
            allTracks[i] = arrayTracks.get(i).title;
        }


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Black List")
                .setMultiChoiceItems(allTracks, checkedItemsArray, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        checkedItemsArray[which] = isChecked;
                    }
                }).setPositiveButton("Apply", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for(int i = 0; i < checkedItemsArray.length; i++){
                            if(checkedItemsArray[i]){
                                acticity_setting.blackList.edit().putBoolean(allTracks[i], true).apply();
                            } else{
                                acticity_setting.blackList.edit().putBoolean(allTracks[i], false).apply();
                            }

                        }
                        dialog.cancel();
                    }
                });

        return builder.create();
    }
}



