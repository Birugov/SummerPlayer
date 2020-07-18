package com.example.testsummer;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class ShakeModeDialogFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        String[] mode = new String[]{"Next Song", "Play/Pasuse", "None"};

        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setTitle("Shake Mode: " + acticity_setting.shakeFunStatus.getString("mode", "None")).
                setSingleChoiceItems(mode, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (mode[which]){
                            case "Next Song":
                                acticity_setting.shakeFunStatus.edit().putString("mode", mode[which]).apply();
                                break;
                            case "Play/Pasuse":
                                acticity_setting.shakeFunStatus.edit().putString("mode", mode[which]).apply();
                                break;
                            case "None":
                                acticity_setting.shakeFunStatus.edit().putString("mode", mode[which]).apply();
                                break;
                        }
                    }
                }).setPositiveButton("Apply", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        return builder.create();
    }
}
