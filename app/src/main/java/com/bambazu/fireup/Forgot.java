package com.bambazu.fireup;

import android.app.ProgressDialog;
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
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

public class Forgot extends AppCompatActivity {

    private EditText email;
    private Button btn_forgot;
    private Toast toast;
    private ProgressDialog loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);

        loader = new ProgressDialog(this);
        loader.setMessage(getResources().getString(R.string.loader_message));
        loader.setCancelable(false);

        email = (EditText) findViewById(R.id.forgot_email);
        btn_forgot = (Button) findViewById(R.id.btn_forgot_send);
        btn_forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toast = null;

                if(TextUtils.isEmpty(email.getText().toString()) && !android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()){
                    toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_email_signup), Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }

                loader.show();
                ParseUser.requestPasswordResetInBackground(email.getText().toString(), new RequestPasswordResetCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            loader.dismiss();
                            toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.success_msg_forgot), Toast.LENGTH_SHORT);
                            toast.show();

                            finish();
                        }
                        else {
                            loader.dismiss();
                            toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_msg_forgot), Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_forgot, menu);
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
