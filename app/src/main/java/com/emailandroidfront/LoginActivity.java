package com.emailandroidfront;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.emailandroidfront.model.AccountUser;
import com.emailandroidfront.remote.ApiUtils;
import com.emailandroidfront.remote.UserService;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    EditText usernameLogin;
    EditText passwordLogin;
    Button submitLogin;
    UserService userService;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar_login);
        setSupportActionBar(myToolbar);

        PreferenceManager.setDefaultValues(this,R.xml.preferences,false);
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor editor=sharedPreferences.edit();

        //brisemo sve posatke o ranije ulogovanom korisniku
        removeLoginUser(editor);

        usernameLogin = findViewById(R.id.usernameLogin);
        passwordLogin = findViewById(R.id.passwordLogin);
        submitLogin = findViewById(R.id.submitLogin);
        button= findViewById(R.id.button_bypass);

        submitLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userService= ApiUtils.getUserService();
                String username=usernameLogin.getText().toString();
                String password=passwordLogin.getText().toString();

                if(validateLogin(username,password)){
                    doLogin(username,password,editor);
                }
            }
        });

        //metoda kojom preskacemo logovanje i treba je obrisati kasnije
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AccountUser resObj=new AccountUser();
                resObj.setId(1);
                resObj.setUsername("user@example.com");
                resObj.setPassword("123");
                resObj.setStmp("stmp");
                resObj.setPop3("pop3");

                insertLoginUser(resObj,editor);

                Intent intent=new Intent(LoginActivity.this,EmailsActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private boolean validateLogin(String username, String password) {
        if(username == null || username.trim().length() == 0){
            Toast.makeText(this, "Username is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(password == null || password.trim().length() == 0){
            Toast.makeText(this, "Password is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    private void doLogin(String username, String password, final SharedPreferences.Editor editor){

        Call<AccountUser> call=userService.login(username,password);

        call.enqueue(new Callback<AccountUser>() {
            @Override
            public void onResponse(Call<AccountUser> call, Response<AccountUser> response) {
                if(response.isSuccessful()){
                    Log.d("filter", response.body().toString());
                    // mapiramo response na AccountUser klasu koja je ustvari response.body
                    AccountUser resObj=response.body();
                    //upisujemo sve podatke za logovanog korisnika u sharedPreference
                    insertLoginUser( resObj,editor);

                    Intent intent=new Intent(LoginActivity.this,EmailsActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Toast.makeText(LoginActivity.this,"Error , please try again",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AccountUser> call, Throwable t) {
                Toast.makeText(LoginActivity.this,t.toString(),Toast.LENGTH_LONG).show();
            }
        });
    }

    //upisujemo sve podatke za logovanog korisnika u sharedPreference
    private void insertLoginUser(AccountUser resObj, SharedPreferences.Editor editor) {
        editor.putLong("id",resObj.getId()).apply();
        editor.putString("username",resObj.getUsername()).apply();
        editor.putString("password",resObj.getPassword()).apply();
        editor.putString("stmp",resObj.getStmp()).apply();
        editor.putString("pop3",resObj.getPop3()).apply();
    }

    //brisemo sve posatke o ranije ulogovanom korisniku
    private void removeLoginUser( final SharedPreferences.Editor editor) {
        editor.remove("username").apply();
        editor.remove("password").apply();
        editor.remove("id").apply();
        editor.remove("stmp").apply();
        editor.remove("pop3").apply();
    }

}
