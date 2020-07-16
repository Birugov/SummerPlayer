package com.example.testsummer;

import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

public class Setting_Loader{

    private SharedPreferences settings;

    Setting_Loader(SharedPreferences settings){
        this.settings = settings;
    }
    public void load(){
        nightModeLoad();
    }


    private void nightModeLoad(){
        final boolean isNightModeOn = settings.getBoolean("NightMode", false);
        if(isNightModeOn){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}