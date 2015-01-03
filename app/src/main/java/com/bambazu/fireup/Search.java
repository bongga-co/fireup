package com.bambazu.fireup;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Search extends ActionBarActivity {

    private String cities;

    private EditText lowPrice;
    private EditText highPrice;
    private Spinner cityName;
    private static ArrayList listCity;
    private RatingBar ratingPlace;
    private SeekBar placeDistance;
    private TextView placeDistanceValue;
    private Toast notifier;

    private Button btnSearch;

    private String validateNumber = "^[^\\\\d]*";
    JSONObject searchFields;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        lowPrice = (EditText) findViewById(R.id.sr_low_price);
        highPrice = (EditText) findViewById(R.id.sr_high_price);
        cityName = (Spinner) findViewById(R.id.sr_city);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.cities, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cityName.setAdapter(adapter);

        ratingPlace = (RatingBar) findViewById(R.id.sr_rating);
        placeDistanceValue = (TextView) findViewById(R.id.sr_distance_value);
        placeDistance = (SeekBar) findViewById(R.id.sr_distance);
        placeDistance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                placeDistanceValue.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        btnSearch = (Button) findViewById(R.id.btn_search);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lowPrice.getText().toString().length() == 0 && highPrice.getText().toString().length() == 0
                        && cityName.getSelectedItemPosition() == 0
                        && placeDistanceValue.getText().toString().equals("0") && ratingPlace.getRating() == 0.0) {

                    if(notifier == null){
                        notifier = Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_search), Toast.LENGTH_SHORT);
                    }

                    notifier.show();
                    return;
                }

                searchFields = new JSONObject();

                try{
                    //Price
                    if((lowPrice.getText().toString().length() != 0 && lowPrice.getText().toString().matches(validateNumber))
                            && highPrice.getText().length() == 0){
                        searchFields.put("lowprice", lowPrice.getText().toString());
                    }
                    else if(lowPrice.getText().toString().length() == 0 && highPrice.getText().length() != 0
                            && highPrice.getText().toString().matches(validateNumber)){
                        searchFields.put("highprice", highPrice.getText().toString());
                    }
                    else if(lowPrice.getText().toString().length() != 0 && highPrice.getText().length() != 0
                            && lowPrice.getText().toString().matches(validateNumber) && highPrice.getText().toString().matches(validateNumber)){
                        searchFields.put("lowprice", lowPrice.getText().toString());
                        searchFields.put("highprice", highPrice .getText().toString());
                    }

                    //City
                    searchFields.put("city", cityName.getSelectedItem());
                    //Rating
                    searchFields.put("rating", (int)ratingPlace.getRating());
                    //Distance
                    searchFields.put("distance", Integer.parseInt(placeDistanceValue.getText().toString()));

                    Intent intent = new Intent();
                    intent.putExtra("searchResult", searchFields.toString());
                    setResult(RESULT_OK, intent);
                }
                catch (JSONException e){
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_search_exception), Toast.LENGTH_SHORT).show();
                }

                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        else if(id == android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
        }
        else if(id == R.id.action_reset){
            lowPrice.setText(null);
            highPrice.setText(null);
            cityName.setSelection(0, true);
            ratingPlace.setRating(0);
            placeDistance.setProgress(0);
            placeDistanceValue.setText("0");
        }

        return super.onOptionsItemSelected(item);
    }
}
