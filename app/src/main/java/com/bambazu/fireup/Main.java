package com.bambazu.fireup;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
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
import com.bambazu.fireup.Interfaz.DataListener;
import com.bambazu.fireup.Model.Place;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Main extends ActionBarActivity implements DataListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private Location lastLocation;
    private GoogleApiClient googleApiClient;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private boolean isRequestLocationUpdates = false;
    private LocationRequest locationRequest;

    private static int UPDATE_INTERVAL = 10000;
    private static int FASTEST_INTERVAL = 5000;
    private static int DISPLACEMENT = 10;

    private ListView listPlaces;
    private ArrayList<Place> places;
    private static final int SEARCH_REQUEST = 1;

    private HashMap<String, Object> queryData;

    private DataManager dataManager;
    private NetworkManager networkManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        networkManager = new NetworkManager(this);

        listPlaces = (ListView) findViewById(R.id.listPlaces);
        listPlaces.setEmptyView(findViewById(android.R.id.empty));

        if(checkPlayServices()){
            buildGoogleApiClient();
            createLocationRequest();
        }
        else{
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!isRequestLocationUpdates){
            isRequestLocationUpdates = true;
            googleApiClient.connect();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(googleApiClient.isConnected()){
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(googleApiClient.isConnected()){
            stopLocationUpdates();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        googleApiClient.disconnect();
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
            getPlaces("Search", searchData);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;
        getPlaces("List", null);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private boolean checkPlayServices(){
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if(resultCode != ConnectionResult.SUCCESS){
            if(GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            else {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.device_not_supported), Toast.LENGTH_LONG).show();
            }

            return false;
        }

        return true;
    }

    protected synchronized void buildGoogleApiClient(){
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    protected void createLocationRequest(){
        locationRequest = new LocationRequest();

        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    private void startLocationUpdates(){
        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient, locationRequest, this);
    }

    private void stopLocationUpdates(){
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
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
                    places.add(
                            new Place(
                                 placeObject.getObjectId(),
                                 placeObject.getString("name"),
                                 placeObject.getParseFile("preview_one").getUrl(),
                                 placeObject.getParseFile("preview_two").getUrl(),
                                 placeObject.getParseFile("preview_three").getUrl(),
                                 placeObject.getParseFile("preview_four").getUrl(),
                                 placeObject.getParseFile("preview_five").getUrl(),
                                 placeObject.getString("category"),
                                 placeObject.getNumber("ranking"),
                                 placeObject.getParseGeoPoint("position").getLatitude(),
                                 placeObject.getParseGeoPoint("position").getLongitude(),
                                 placeObject.getNumber("rooms").intValue(),
                                 placeObject.getBoolean("visible"),
                                 placeObject.getString("address"),
                                 placeObject.getString("city"),
                                 placeObject.getString("depto"),
                                 placeObject.getString("country"),
                                 placeObject.getString("description"),
                                 placeObject.getNumber("lowprice"),
                                 placeObject.getNumber("highprice"),
                                 placeObject.getString("phone")
                            )
                    );
                }
            }

            Config.currentPlaces = places;

            final PlaceAdapter placeAdapter = new PlaceAdapter(this, places);
            listPlaces.setAdapter(placeAdapter);

            listPlaces.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getApplicationContext(), Detail.class);
                    intent.putExtra("placePosition", position);
                    startActivity(intent);
                }
            });
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

        //lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if(lastLocation != null){
            Config.currentLatitude = lastLocation.getLatitude();
            Config.currentLongitude = lastLocation.getLongitude();

            dataManager = new DataManager(this);
            dataManager.setDataListener(this);
            dataManager.execute(queryData);
        }
        else{
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_get_location), Toast.LENGTH_SHORT);
        }
    }
}