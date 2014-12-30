package com.bambazu.fireup;

import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import com.bambazu.fireup.Adapter.SpinnerAdapter;
import com.bambazu.fireup.Model.SpinnerModel;
import java.util.ArrayList;

public class Search extends ActionBarActivity {

    private String cities;

    private EditText lowPrice;
    private EditText highPrice;
    private Spinner cityName;
    private static ArrayList listCity;
    private RatingBar ratingPlace;
    private SeekBar placeDistance;

    private Button btnSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        lowPrice = (EditText) findViewById(R.id.sr_low_price);
        highPrice = (EditText) findViewById(R.id.sr_high_price);
        cityName = (Spinner) findViewById(R.id.sr_city);

        btnSearch = (Button) findViewById(R.id.btn_search);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        setDataCities();
        cityName.setAdapter(new SpinnerAdapter(this, R.layout.spinner_item, listCity));

        ratingPlace = (RatingBar) findViewById(R.id.sr_rating);
        placeDistance = (SeekBar) findViewById(R.id.sr_distance);
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
        }

        return super.onOptionsItemSelected(item);
    }

    private void setDataCities(){
        String[] cityArray = getResources().getStringArray(R.array.cities);
        listCity = new ArrayList();

        for(int i=0; i<cityArray.length; i++){
            listCity.add(new SpinnerModel(cityArray[i]));
        }
    }
}
