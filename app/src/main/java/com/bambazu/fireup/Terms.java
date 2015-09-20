package com.bambazu.fireup;

import android.app.ProgressDialog;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class Terms extends AppCompatActivity {
    private TextView termDesc;
    private ProgressDialog loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);

        termDesc = (TextView) findViewById(R.id.term_desc);

        loader = new ProgressDialog(this);
        loader.setMessage(getResources().getString(R.string.loader_message));
        loader.setCancelable(false);

        loader.show();
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Terms");
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                loader.dismiss();

                if(e == null){
                    termDesc.setText(object.getString("description"));
                }
                else{
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_terms), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_terms, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        else if(id == R.id.btn_terms){
            NavUtils.navigateUpFromSameTask(this);
        }

        return super.onOptionsItemSelected(item);
    }
}
