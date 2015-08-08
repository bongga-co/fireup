package com.bambazu.fireup;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.bambazu.fireup.Config.Config;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class Comment extends ActionBarActivity {
    private ListView listComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        Config.tracker.setScreenName(this.getClass().toString());

        listComment = (ListView)findViewById(R.id.listComment);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            getComments((String)extras.get("objectId"));
        }

        onLogin();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_comment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
        }
        else if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getComments(String objectId){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Places");
        query.getInBackground(objectId, new GetCallback<ParseObject>() {
            public void done(ParseObject place, ParseException e) {
                if (e == null) {
                    //place.increment("ranking", 1);
                    //place.saveInBackground();
                }
            }
        });
    }

    private void onLogin(){
        ParseUser user = ParseUser.getCurrentUser();
        Toast.makeText(this, user.getUsername().toString(), Toast.LENGTH_LONG).show();
    }
}
