package com.bambazu.fireup;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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
    private EditText lowPrice;
    private EditText highPrice;
    private Spinner cityName;
    private Spinner placeCategory;
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
        placeCategory = (Spinner) findViewById(R.id.sr_category);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.cities, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cityName.setAdapter(adapter);

        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(this,
                R.array.categories, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        placeCategory.setAdapter(categoryAdapter);

        ratingPlace = (RatingBar) findViewById(R.id.sr_rating);
        ratingPlace.setRating(0);
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
                        && placeCategory.getSelectedItemPosition() == 0
                        && placeDistanceValue.getText().toString().equals("0") && ratingPlace.getRating() == 0.0) {

                    if(notifier == null){
                        notifier = Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_search), Toast.LENGTH_SHORT);
                    }

                    notifier.show();
                    return;
                }

                searchFields = new JSONObject();

                try{
                    //Prices
                    if(lowPrice.getText().length() > 0){
                        searchFields.put("lowprice", Long.parseLong(lowPrice.getText().toString()));
                    }
                    else{
                        searchFields.put("lowprice", 0);
                    }

                    if(highPrice.getText().length() > 0){
                        searchFields.put("highprice", Long.parseLong(highPrice.getText().toString()));
                    }
                    else{
                        searchFields.put("highprice", 0);
                    }

                    //City
                    if(!cityName.getSelectedItem().equals(getResources().getString(R.string.first_city))){
                        searchFields.put("city", cityName.getSelectedItem());
                    }
                    else{
                        searchFields.put("city", "");
                    }

                    //Category
                    if(!placeCategory.getSelectedItem().equals(getResources().getString(R.string.first_category))){
                        String category = null;
                        switch (placeCategory.getSelectedItem().toString()){
                            case "Motel":
                                category = "Motel";
                                break;

                            case "Sex Shop":
                            case "Tienda Er\u00f3tica":
                                category = "SexShop";
                                break;

                            case "Massage Center":
                            case "Centro d Masajes":
                                category = "MassageCenter";
                                break;

                            case "Gay Bar":
                            case "Bar Gay":
                                category = "GayBar";
                                break;

                            case "Swinger Bar":
                            case "Bar Swinger":
                                category = "SwingerBar";
                                break;
                        }

                        searchFields.put("category", category);
                    }
                    else{
                        searchFields.put("category", "");
                    }

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
