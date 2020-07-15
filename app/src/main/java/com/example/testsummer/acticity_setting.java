package com.example.testsummer;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.FragmentManager;


import android.content.SharedPreferences;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class acticity_setting extends AppCompatActivity {

    SharedPreferences appSettingPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acticity_setting);



        appSettingPrefs = getSharedPreferences("AppSettingPrefs", MODE_PRIVATE);
        final SharedPreferences.Editor sharedPrefEdit= appSettingPrefs.edit();
        final boolean isNightModeOn = appSettingPrefs.getBoolean("NightMode", false);

        final Button switch_button = (Button)findViewById(R.id.switch_button);
        final Button about_button = (Button)findViewById(R.id.about_button);




        switch_button.setOnClickListener(new View.OnClickListener() {

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

        about_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    FragmentManager manager = getSupportFragmentManager();
                    MyDialogFragment myDialogFragment = new MyDialogFragment();
                    myDialogFragment.show(manager, "myDialog");
            }
        });

    }

}
