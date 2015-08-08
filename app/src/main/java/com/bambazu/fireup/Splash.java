package com.bambazu.fireup;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;


public class Splash extends ActionBarActivity {
    private static int SPLASH_TIMER = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                ParseUser currentUser = ParseUser.getCurrentUser();

                if ((currentUser != null) && ParseFacebookUtils.isLinked(currentUser)) {
                    startActivity(new Intent(Splash.this, Main.class));
                }
                else{
                    startActivity(new Intent(Splash.this, Login.class));
                }

                finish();
            }
        }, SPLASH_TIMER);
    }
}
