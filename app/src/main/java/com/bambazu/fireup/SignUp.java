package com.bambazu.fireup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bambazu.fireup.Config.Config;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignUp extends AppCompatActivity {

    private EditText email;
    private EditText username;
    private EditText password;
    private EditText verify;
    private Button btn_signup;
    private Toast toast;
    private ParseUser user;
    private ProgressDialog loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Config.tracker.setScreenName(this.getClass().toString());

        loader = new ProgressDialog(this);
        loader.setMessage(getResources().getString(R.string.loader_message));
        loader.setCancelable(false);

        email = (EditText) findViewById(R.id.signup_email);
        username = (EditText) findViewById(R.id.signup_username);
        password = (EditText) findViewById(R.id.signup_password);
        verify = (EditText) findViewById(R.id.verify_password);
        btn_signup = (Button) findViewById(R.id.btn_signup_signup);
        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toast = null;

                if(TextUtils.isEmpty(email.getText().toString()) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()){
                    toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_email_signup), Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }

                if(TextUtils.isEmpty(username.getText().toString())){
                    toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_username_signup), Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }

                if(TextUtils.isEmpty(password.getText().toString())){
                    toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_password_signup), Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }

                if(password.getText().toString().length() < 6){
                    toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_password_length_signup), Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }

                if(!password.getText().toString().equals(verify.getText().toString())){
                    toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_password_match_signup), Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }

                loader.show();

                user = new ParseUser();
                user.setUsername(username.getText().toString());
                user.setEmail(email.getText().toString());
                user.setPassword(password.getText().toString());
                user.signUpInBackground(new SignUpCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            loader.dismiss();
                            startActivity(new Intent(SignUp.this, Main.class));
                            finish();
                        }
                        else {
                            loader.dismiss();
                            toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_msg_signup), Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sign_up, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
