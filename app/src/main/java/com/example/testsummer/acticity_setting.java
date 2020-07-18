package com.example.testsummer;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentManager;



import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.view.View;

import android.widget.Button;
import android.widget.Toast;


import net.protyposis.android.mediaplayer.MediaPlayer;



public class acticity_setting extends AppCompatActivity {

    static MediaPlayer mediaPlayer;

    SharedPreferences appSettingPrefs;
    static SharedPreferences blackList;
    static SharedPreferences shakeFunStatus;



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
        final Button not_button = (Button)findViewById(R.id.not_button);
        final Button shake_button = (Button)findViewById(R.id.shake_button);



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
                FragmentManager manager = getSupportFragmentManager();
                ShakeModeDialogFragment shake_Mode = new ShakeModeDialogFragment();
                shake_Mode.show(manager, "shake_Mode");
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

        not_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Всего хорошего! Русские идут домой!",
                        Toast.LENGTH_SHORT);
                toast.show();

            }
        });

    }

    @Override public void onBackPressed(){
        Intent intent = new Intent(acticity_setting.this, activity_main.class);
        startActivity (intent);
        finish();
    }

}
