package com.emailandroidfront;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //postavljamo fragment umesto layouta sa ovim FragmentManager-om
        //postaviti style appTheme u styles.xml i u manifest.xml dodati u aktivnost SettingsActivity
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}
