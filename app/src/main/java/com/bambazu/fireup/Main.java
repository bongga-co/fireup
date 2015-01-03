package com.bambazu.fireup;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.bambazu.fireup.Adapter.PlaceAdapter;
import com.bambazu.fireup.Config.Config;
import com.bambazu.fireup.Helper.DataManager;
import com.bambazu.fireup.Helper.NetworkManager;
import com.bambazu.fireup.Interfaz.CalculateDistanceListener;
import com.bambazu.fireup.Interfaz.DataListener;
import com.bambazu.fireup.Model.Place;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main extends ActionBarActivity implements DataListener, LocationListener {

    private ListView listPlaces;
    private ArrayList<Place> places;
    private static final int SEARCH_REQUEST = 1;

    private HashMap<String, Object> queryData;

    private LocationManager locationManager;
    private Location bestLocation;

    private static final long ONE_MIN = 60 * 1000;
    private static final long TWO_MIN = ONE_MIN * 2;
    private static final long FIVE_MIN = ONE_MIN * 5;
    private static final long MEASURE_TIME = 1000 * 30;
    private static final long POLLING_FREQ = 1000 * 10;
    private static final float MIN_ACCURACY = 25.0f;
    private static final float MIN_LAST_READ_ACCURACY = 500.0f;
    private static final float MIN_DISTANCE = 10.0f;

    private DataManager dataManager;
    private NetworkManager networkManager;
    private HashMap<String, Double> locationData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        networkManager = new NetworkManager(this);

        listPlaces = (ListView) findViewById(R.id.listPlaces);
        listPlaces.setEmptyView(findViewById(android.R.id.empty));

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        bestLocation = bestKnownLocation(MIN_LAST_READ_ACCURACY, FIVE_MIN);

        if(bestLocation != null){
            getPlaces("List", null);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if(bestLocation == null || bestLocation.getAccuracy() > MIN_LAST_READ_ACCURACY || bestLocation.getTime() < System.currentTimeMillis() - TWO_MIN){
            if(locationManager.getProvider(LocationManager.NETWORK_PROVIDER) != null){
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, POLLING_FREQ, MIN_DISTANCE, this);
            }

            if(locationManager.getProvider(LocationManager.GPS_PROVIDER) != null){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, POLLING_FREQ, MIN_DISTANCE, this);
            }

            Executors.newScheduledThreadPool(1).schedule(new Runnable() {
                @Override
                public void run() {
                    locationManager.removeUpdates(Main.this);
                }
            }, MEASURE_TIME, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        locationManager.removeUpdates(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search) {
            startActivityForResult(new Intent(this, Search.class), SEARCH_REQUEST);
            return true;
        }
        else if(id == R.id.action_map){
            Intent i = new Intent(this, Map.class);
            startActivity(i);

            return true;
        }
        else if(id == R.id.action_places_my_location){
            listPlaces.setAdapter(null);
            getPlaces("List", null);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String searchData = null;
        if(requestCode == SEARCH_REQUEST && resultCode == RESULT_OK){
            searchData = data.getStringExtra("searchResult");
        }

        getPlaces("Search", searchData);
    }

    @Override
    public void onLocationChanged(Location location) {
        if(bestLocation == null || location.getAccuracy() < bestLocation.getAccuracy()){
            Config.currentLatitude = location.getLatitude();
            Config.currentLongitude = location.getLongitude();

            bestLocation = location;
            getPlaces("List", null);

            if(bestLocation.getAccuracy() < MIN_ACCURACY){
                locationManager.removeUpdates(this);
            }
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void retrieveData(List<ParseObject> data) {
        if (data.size() > 0) {
            places = null;
            places = new ArrayList<Place>();
            places.clear();

            for (int i = 0; i < data.size(); i++) {
                ParseObject placeObject = data.get(i);

                if(placeObject.getBoolean("visible")){
                    places.add(new Place(placeObject.getObjectId(), placeObject.getString("name"), placeObject.getParseFile("preview_one").getUrl(), placeObject.getString("category"), Float.parseFloat(placeObject.getString("ranking")), placeObject.getParseGeoPoint("position").getLatitude(), placeObject.getParseGeoPoint("position").getLongitude(), placeObject.getNumber("rooms").intValue(), placeObject.getBoolean("visible")));
                }
            }

            Config.currentPlaces = places;

            final PlaceAdapter placeAdapter = new PlaceAdapter(this, places);
            listPlaces.setAdapter(placeAdapter);

            listPlaces.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getApplicationContext(), Detail.class);
                    intent.putExtra("objectId", places.get(position).getPlaceCode());
                    intent.putExtra("placeName", places.get(position).getPlaceName());

                    startActivity(intent);
                }
            });
        }
    }

    private Location bestKnownLocation(float minAccuracy, float maxAge){
        Location bestResult = null;
        float bestAccuracy = Float.MAX_VALUE;
        long bestTime = Long.MIN_VALUE;

        List<String> allProviders = locationManager.getAllProviders();
        for(String provider: allProviders){
            Location location = locationManager.getLastKnownLocation(provider);
            if(location != null){
                float accuracy = location.getAccuracy();
                long time = location.getTime();

                if(accuracy < bestAccuracy){
                    bestResult = location;
                    bestAccuracy = accuracy;
                    bestTime = time;
                }
            }
        }

        if(bestAccuracy > minAccuracy || (System.currentTimeMillis() - bestTime) > maxAge){
            return null;
        }
        else{
            return bestResult;
        }
    }

    private void getPlaces(String queryType, String data){
        queryData = null;
        dataManager = null;

        queryData = new HashMap<String, Object>();
        queryData.put("class", "Places");
        queryData.put("objectId", null);
        queryData.put("queryType", queryType);

        if(data != null){
            queryData.put("search", data);
        }

        if(!networkManager.isNetworkOnline()){
            Toast.makeText(this, getResources().getString(R.string.error_network), Toast.LENGTH_SHORT).show();
            return;
        }

        dataManager = new DataManager(this);
        dataManager.setDataListener(this);
        dataManager.execute(queryData);
    }
}
