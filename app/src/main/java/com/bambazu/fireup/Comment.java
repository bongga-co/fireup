package com.bambazu.fireup;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Toast;
import com.bambazu.fireup.Adapter.CommentsAdapter;
import com.bambazu.fireup.Config.Config;
import com.bambazu.fireup.Model.Comments;
import com.google.android.gms.analytics.HitBuilders;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Comment extends AppCompatActivity {
    private ListView listComment;
    private String objectId;
    private CommentsAdapter commentAdapter;
    private ArrayList<Comments> data;
    private ProgressDialog loader;
    private FloatingActionButton btnComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        Config.tracker.setScreenName(this.getClass().toString());

        loader = new ProgressDialog(this);
        loader.setMessage(getResources().getString(R.string.loader_message));
        loader.setCancelable(false);

        listComment = (ListView)findViewById(R.id.listComment);
        listComment.setEmptyView(findViewById(R.id.comments_empty));
        btnComment = (FloatingActionButton) findViewById(R.id.fab);
        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCommentBox();
            }
        });

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            objectId = (String)extras.get("objectId");
            getComments(objectId);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_comment, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
        }
        else if (id == R.id.action_add_comment) {
            showCommentBox();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getComments(String objectId){
        loader.show();

        ParseObject idPlace = ParseObject.createWithoutData("Places", objectId);
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Comments");
        query.include("idUser");
        query.include("idPlace");
        query.whereEqualTo("idPlace", idPlace);
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    loader.dismiss();
                    commentAdapter = null;
                    data = new ArrayList<Comments>();

                    for (int i = 0; i < objects.size(); i++) {
                        data.add(new Comments(objects.get(i).getParseObject("idUser").getString("username"), Config.formattedDate(objects.get(i).getCreatedAt()), objects.get(i).getString("post"), objects.get(i).getInt("rating")));
                    }

                    commentAdapter = new CommentsAdapter(getApplicationContext(), data);
                    listComment.setAdapter(commentAdapter);
                    commentAdapter.notifyDataSetChanged();
                } else {
                    loader.dismiss();
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_comment_data), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showCommentBox(){
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        final View comment_wrapper = inflater.inflate(R.layout.comments_dialog, null);

        AlertDialog.Builder dialog = new AlertDialog.Builder(Comment.this);
        dialog.setView(comment_wrapper);

        dialog.setPositiveButton(getResources().getString(R.string.ok_btn), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                final EditText post = (EditText) comment_wrapper.findViewById(R.id.place_comment_box);
                final RatingBar rating = (RatingBar) comment_wrapper.findViewById(R.id.comment_rating);

                ParseUser user = ParseUser.getCurrentUser();
                if(user != null && !user.getUsername().equals("")){
                    doComment(post, rating);
                }
                else{
                    Config.comingComment = true;
                    startActivity(new Intent(Comment.this, Login.class));
                }
            }
        });

        dialog.setNegativeButton(getResources().getString(R.string.cancel_btn), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        AlertDialog alertDialog = dialog.create();
        alertDialog.show();

        Config.tracker.send(new HitBuilders.EventBuilder()
                .setCategory("Comment UI")
                .setAction("Click")
                .setLabel("Button Comment")
                .build());
    }

    private void doComment(EditText post, RatingBar rating){
        if(post == null || post.getText().toString().equals("")){
            Toast.makeText(Comment.this, getResources().getString(R.string.comment_error_post), Toast.LENGTH_SHORT).show();
            return;
        }

        if(rating.getRating() == 0){
            Toast.makeText(Comment.this, getResources().getString(R.string.comment_error_rating), Toast.LENGTH_SHORT).show();
            return;
        }

        data.add(0, new Comments(ParseUser.getCurrentUser().getUsername(), Config.formattedDate(new Date()), post.getText().toString(), (int)rating.getRating()));
        commentAdapter.notifyDataSetChanged();

        final ParseObject newComment = new ParseObject("Comments");
        newComment.put("idUser", ParseUser.getCurrentUser());
        newComment.put("idPlace", ParseObject.createWithoutData("Places", objectId));
        newComment.put("post", post.getText().toString());
        newComment.put("rating", (int) rating.getRating());
        newComment.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null){
                    Config.tracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Comment UI")
                            .setAction("Submit")
                            .setLabel("New Comment")
                            .build());
                }
                else{
                    Toast.makeText(Comment.this, getResources().getString(R.string.comment_submit_error), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
