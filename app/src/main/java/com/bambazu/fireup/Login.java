package com.bambazu.fireup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import com.bambazu.fireup.Config.Config;
import com.bambazu.fireup.Helper.LocalDataManager;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import org.json.JSONObject;
import java.util.Arrays;
import java.util.List;

public class Login extends AppCompatActivity implements View.OnClickListener {
    private Button fbButton;
    private ImageButton skipButton;
    private Button loginButton;
    private Button signUpButton;
    private Button forgotButton;
    private ProgressDialog dialog;
    LocalDataManager localDataManager;
    private ParseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        localDataManager = new LocalDataManager(this);
        currentUser = ParseUser.getCurrentUser();

        if(!Config.comingComment){
            if (currentUser != null || localDataManager.isSkipped()) {
                startActivity(new Intent(Login.this, Main.class));
                finish();
            }
        }

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

        skipButton = (ImageButton) findViewById(R.id.btn_skip);
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
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.btn_login_login:
                startActivity(new Intent(Login.this, SignIn.class));
                break;

            case R.id.btn_login_signup:
                startActivity(new Intent(Login.this, SignUp.class));
                break;

            case R.id.btn_login_forgot:
                startActivity(new Intent(Login.this, Forgot.class));
                break;

            case R.id.login_button:
                onLogin();
                break;

            case R.id.btn_skip:
                if (Config.comingComment) {
                    setResult(RESULT_OK, new Intent());
                    Config.comingComment = false;
                }
                else{
                    localDataManager.saveLocalData(null, null, null, false, true);
                    startActivity(new Intent(Login.this, Main.class));
                }

                finish();
                break;
        }
    }

    private void onLogin(){
        dialog.show();

        List<String> permissions = Arrays.asList("email", "public_profile");
        ParseFacebookUtils.logInWithReadPermissionsInBackground(this, permissions, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                dialog.dismiss();

                if (user == null) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_login_facebook), Toast.LENGTH_SHORT).show();
                } else if (user.isNew()) {
                    if (Config.comingComment) {
                        setResult(RESULT_OK, new Intent());
                        Config.comingComment = false;
                        finish();
                    } else {
                        showFbUsername();
                        startActivity(new Intent(Login.this, Main.class));
                        finish();
                    }
                } else {
                    if (Config.comingComment) {
                        setResult(RESULT_OK, new Intent());
                        Config.comingComment = false;
                        finish();
                    } else {
                        showFbUsername();
                        startActivity(new Intent(Login.this, Main.class));
                        finish();
                    }
                }
            }
        });
    }

    private void showFbUsername(){
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                ParseUser.getCurrentUser().setUsername(object.optString("first_name"));
                ParseUser.getCurrentUser().setEmail(object.optString("email"));
                ParseUser.getCurrentUser().saveEventually();
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "first_name, email");
        request.setParameters(parameters);
        request.executeAsync();
    }
}
