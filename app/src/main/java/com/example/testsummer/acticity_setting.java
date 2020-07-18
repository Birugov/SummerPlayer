package com.example.testsummer;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentManager;


import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import net.protyposis.android.mediaplayer.MediaPlayer;



public class acticity_setting extends AppCompatActivity {

    static MediaPlayer mediaPlayer;

    SharedPreferences appSettingPrefs;
    static SharedPreferences blackList;
    SharedPreferences shakeFunStatus;



    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acticity_setting);
        Bundle arguments = getIntent().getExtras();
        assert arguments != null;



        shakeFunStatus = getSharedPreferences("shakeFunStatus", MODE_PRIVATE);
        appSettingPrefs = getSharedPreferences("AppSettingPrefs", MODE_PRIVATE);
        blackList = getSharedPreferences("songBlackList", MODE_PRIVATE);
        final SharedPreferences.Editor sharedPrefEdit= appSettingPrefs.edit();
        final boolean isNightModeOn = appSettingPrefs.getBoolean("NightMode", false);



        final Button switch_button = (Button)findViewById(R.id.switch_button);
        final Button about_button = (Button)findViewById(R.id.about_button);
        final Button blackList_button = (Button)findViewById(R.id.blackList_button);
        final Button refresh_button = (Button)findViewById(R.id.refresh_button);
        final Button shake_button = (Button)findViewById(R.id.shake_button);

        if(shakeFunStatus.getBoolean("status", false)){
            shake_button.setText("SHAKE ON");
        } else {
            shake_button.setText("SHAKE OFF");
        }


        switch_button.setOnClickListener(new View.OnClickListener() { //

            @Override
            public void onClick(View v) {

                if(isNightModeOn){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    sharedPrefEdit.putBoolean("NightMode", false);
                    sharedPrefEdit.apply();

                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    sharedPrefEdit.putBoolean("NightMode", true);
                    sharedPrefEdit.apply();

                }


            }
        });

        shake_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean check = shakeFunStatus.getBoolean("status", false);
                if(!check){
                    shakeFunStatus.edit().putBoolean("status", true).apply();
                    shake_button.setText("SHAKE ON");

                } else {
                    shakeFunStatus.edit().putBoolean("status", false).apply();
                    shake_button.setText("SHAKE OFF");
                }
            }
        });

        about_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    FragmentManager manager = getSupportFragmentManager();
                    AboutDialogFragment about_DialogFragment = new AboutDialogFragment();
                    about_DialogFragment.show(manager, "about_Dialog");
            }
        });

        blackList_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentManager manager = getSupportFragmentManager();
                SongBlackListDialogFragment blackList_DialogFragment = new SongBlackListDialogFragment();
                blackList_DialogFragment.show(manager, " blackList_Dialog");
            }
        });

        refresh_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acticity_setting.this.recreate();
            }
        });

    }

    @Override public void onBackPressed(){
        Intent intent = new Intent(acticity_setting.this, activity_main.class);
        startActivity (intent);
        finish();
    }

}
