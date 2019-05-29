package com.emailandroidfront;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.emailandroidfront.dialog_fragment.LogoutDialog;
import com.emailandroidfront.model.AccountUser;

public class ProfileActivity extends AppCompatActivity {

    AccountUser accountUser;

    TextView tUsername;
    TextView tPassword;
    ImageView imageView;
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar myToolbar = findViewById(R.id.my_toolbar_profile);
        setSupportActionBar(myToolbar);

        PreferenceManager.setDefaultValues(this,R.xml.preferences,false);
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        boolean sortList=sharedPreferences.getBoolean("sorted_by",true);
        final SharedPreferences.Editor editor=sharedPreferences.edit();

        tUsername=findViewById(R.id.textview_profile_username);
        tPassword=findViewById(R.id.textview_profile_password);
        imageView=findViewById(R.id.item_profile_picture);

        accountUser=getUserFromLogin(sharedPreferences);
        tUsername.setText(accountUser.getUsername());
        tPassword.setText(accountUser.getPassword());
        //imageView.setImageURI();

        drawerLayout=findViewById(R.id.drawer_layout_profile);
        navigationView=findViewById(R.id.nav_view_profile);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                // set item as selected to persist highlight
                menuItem.setChecked(true);
                // close drawer when item is tapped
                drawerLayout.closeDrawers();

                switch (menuItem.getItemId()) {
                    case R.id.nav_profile_inbox:
                        Intent intent = new Intent(ProfileActivity.this, EmailsActivity.class);
                        startActivity(intent);
                        finish();
                        return true;

                    case R.id.nav_profile_outbox:
                        Intent intent1=new Intent(ProfileActivity.this, OutboxActivity.class);
                        startActivity(intent1);
                        finish();
                        return true;
                    default:
                        return true;
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.toolbar_profile,menu);
        MenuItem menuItem=menu.findItem(R.id.logout);
        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                openDialog();
                return false;
            }
        });
        return true;
    }
    public void openDialog(){
        LogoutDialog logoutDialog=new LogoutDialog();
        logoutDialog.show(getSupportFragmentManager(),"Logout dialog");
    }
    private AccountUser getUserFromLogin(SharedPreferences sharedPreferences) {
        AccountUser accountUser=new AccountUser();

        accountUser.setId(sharedPreferences.getLong("id",1));
        accountUser.setUsername(sharedPreferences.getString("username","user@example.com"));
        accountUser.setPassword(sharedPreferences.getString("password","123"));
        accountUser.setStmp(sharedPreferences.getString("stmp","stmp"));
        accountUser.setPop3(sharedPreferences.getString("pop3","pop3"));

        return accountUser;
    }
}
