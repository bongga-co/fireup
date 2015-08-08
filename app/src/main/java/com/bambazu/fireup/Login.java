package com.bambazu.fireup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bambazu.fireup.Config.Config;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import java.util.Arrays;
import java.util.List;

public class Login extends ActionBarActivity implements View.OnClickListener {
    private Button fbButton;
    private Button skipButton;
    private Button loginButton;
    private Button signUpButton;
    private Button forgotButton;
    private EditText username;
    private EditText password;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setMessage(getResources().getString(R.string.loader_message));

        loginButton = (Button) findViewById(R.id.btn_login_login);
        loginButton.setOnClickListener(this);

        signUpButton = (Button) findViewById(R.id.btn_login_signup);
        signUpButton.setOnClickListener(this);

        forgotButton = (Button) findViewById(R.id.btn_login_forgot);
        forgotButton.setOnClickListener(this);

        fbButton = (Button) findViewById(R.id.login_button);
        fbButton.setOnClickListener(this);

        skipButton = (Button) findViewById(R.id.btn_skip);
        skipButton.setOnClickListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.btn_login_login:
                break;

            case R.id.btn_login_signup:
                break;

            case R.id.btn_login_forgot:
                break;

            case R.id.login_button:
                onLogin();
                break;

            case R.id.btn_skip:
                startActivity(new Intent(Login.this, Main.class));
                finish();
                break;
        }
    }

    private void onLogin(){
        dialog.show();

        List<String> permissions = Arrays.asList("email", "public_profile");
        ParseFacebookUtils.logIn(permissions, this, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                dialog.dismiss();

                if (user == null) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_login_facebook), Toast.LENGTH_SHORT).show();
                }
                else if (user.isNew()) {
                    if(Config.comingComment){
                        Config.comingComment = false;
                        finish();
                    }
                    else{
                        startActivity(new Intent(Login.this, Main.class));
                        finish();
                    }
                }
                else {
                    if(Config.comingComment){
                        Config.comingComment = false;
                        finish();
                    }
                    else{
                        startActivity(new Intent(Login.this, Main.class));
                        finish();
                    }
                }
            }
        });
    }
}
