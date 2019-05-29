package com.emailandroidfront;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {

    private long ms=0 , splashtime=1500;
    private boolean splashActive=true , paused=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setStatusBarColor(R.color.colorPrimary);

        final ConstraintLayout cl=findViewById(R.id.cl);

        Thread thread= new Thread(){
            public void run(){
                try{
                    while(splashActive&& ms<splashtime){
                        if(!paused){
                            ms=ms+100;
                            sleep(100);
                        }
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                finally {
                    if(!isOnLine()){
                        Snackbar snackbar=Snackbar.make(cl,"No internet access",Snackbar.LENGTH_INDEFINITE)
                                .setAction("Retry", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        recreate();
                                    }
                                });
                        snackbar.show();
                    }
                    else {
                        goMain();
                    }
                }
            }
        };
        thread.start();
    }
    private boolean isOnLine(){
        ConnectivityManager connectivityManager= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo()!=null&& connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    private void setStatusBarColor(@ColorRes int statusBarColor) {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            int color= ContextCompat.getColor(this,statusBarColor);
            Window window=getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        }
    }

    private void goMain() {
        Intent i = new Intent(MainActivity.this,LoginActivity.class);
        startActivity(i);
        finish();
    }
}
