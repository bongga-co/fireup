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
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class SignIn extends AppCompatActivity {
    private EditText username;
    private EditText password;
    private Button btn_login;
    private Toast toast;
    private ProgressDialog loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        loader = new ProgressDialog(this);
        loader.setMessage(getResources().getString(R.string.loader_message));
        loader.setCancelable(false);

        username = (EditText) findViewById(R.id.login_email);
        password = (EditText) findViewById(R.id.login_password);
        btn_login = (Button) findViewById(R.id.btn_send_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toast = null;

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

                loader.show();

                ParseUser.logInInBackground(username.getText().toString(), password.getText().toString(), new LogInCallback() {
                    public void done(ParseUser user, ParseException e) {
                        if (user != null) {
                            loader.dismiss();
                            startActivity(new Intent(SignIn.this, Main.class));
                            finish();
                        }
                        else {
                            loader.dismiss();
                            toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_msg_login), Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sign_in, menu);
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
