package com.bambazu.fireup;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.bambazu.fireup.Adapter.PlaceAdapter;
import com.bambazu.fireup.Config.Config;
import com.bambazu.fireup.Helper.DataManager;
import com.bambazu.fireup.Helper.LocalDataManager;
import com.bambazu.fireup.Helper.NetworkManager;
import com.bambazu.fireup.Interfaz.DataListener;
import com.bambazu.fireup.Model.Place;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Main extends AppCompatActivity implements DataListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

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
    private LocalDataManager localDataManager;

    private ProgressDialog loader = null;
    private FloatingActionButton btnMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Config.tracker.setScreenName(this.getClass().toString());

        localDataManager = new LocalDataManager(this);
        networkManager = new NetworkManager(this);

        loader = new ProgressDialog(this);
        loader.setCancelable(false);
        loader.setMessage(getResources().getString(R.string.getting_location));

        listPlaces = (ListView) findViewById(R.id.listPlaces);
        listPlaces.setEmptyView(findViewById(android.R.id.empty));

        btnMap = (FloatingActionButton) findViewById(R.id.btn_map);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Map.class);
                startActivity(i);
            }
        });

        if(checkPlayServices()){
            loader.show();

            buildGoogleApiClient();
            createLocationRequest();
        }
        else{
            Toast.makeText(this, getResources().getString(R.string.device_not_supported), Toast.LENGTH_SHORT).show();
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
        hideLoader();
        if(googleApiClient.isConnected()){
            stopLocationUpdates();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        hideLoader();
        googleApiClient.disconnect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        if(ParseUser.getCurrentUser() != null && ParseUser.getCurrentUser().isAuthenticated()){
            menu.getItem(5).setTitle(R.string.action_logout);
        }
        else{
            menu.getItem(5).setTitle(R.string.btn_login_text);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search) {
            startActivityForResult(new Intent(this, Search.class), SEARCH_REQUEST);
            return true;
        }
        else if(id == R.id.action_places_my_location){
            if(googleApiClient.isConnected()){
                listPlaces.setAdapter(null);
                getPlaces("List", null);
            }
            else{
                googleApiClient.connect();
            }
        }
        else if(id == R.id.action_share){
            shareApp();
        }
        else if(id == R.id.action_contact){
            contactTeam();
        }
        else if(id == R.id.action_logout){
            if(ParseUser.getCurrentUser() != null && ParseUser.getCurrentUser().isAuthenticated()){
                onLogout();
            }
            else{
                localDataManager.saveLocalData(null, null, null, false, false);
                startActivity(new Intent(Main.this, Login.class));
                finish();
            }
        }
        else if(id == R.id.action_offer){
            startActivity(new Intent(Main.this, Offers.class));
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

            listPlaces.setAdapter(null);
            Config.currentPlaces.clear();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        if(checkLocationSetting()){
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        hideLoader();
        lastLocation = location;
        getPlaces("List", null);

        stopLocationUpdates();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private boolean checkPlayServices(){
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if(resultCode != ConnectionResult.SUCCESS){
            if(GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                hideLoader();
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            else {
                hideLoader();
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
            placeAdapter.notifyDataSetChanged();

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

    private void shareApp(){
        Intent send = new Intent(Intent.ACTION_SEND);
        send.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.share_text));
        send.setType("text/plain");

        startActivity(Intent.createChooser(send, getResources().getString(R.string.share_chooser_title)));

        Config.tracker.send(new HitBuilders.EventBuilder()
                .setCategory("Main UI")
                .setAction("Click")
                .setLabel("Sharing the App")
                .build());
    }

    private boolean checkLocationSetting(){
        int locationMode = 0;
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(!lm.isProviderEnabled(LocationManager.GPS_PROVIDER) &&
                !lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

            showLocationDialog();
            return false;
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);
            }
            catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }

            if((locationMode == Settings.Secure.LOCATION_MODE_OFF) || (locationMode == Settings.Secure.LOCATION_MODE_SENSORS_ONLY)){
                showLocationDialog();
                return false;
            }

            return true;
        }

        return true;
    }

    private void showLocationDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.error_location_services_title));
        builder.setMessage(getResources().getString(R.string.error_location_services));
        builder.setPositiveButton(getResources().getString(R.string.ok_btn), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);

                finish();
            }
        });

        builder.create().show();
    }

    private void contactTeam(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.dialog_text));
        builder.setCancelable(true);
        builder.setItems(new CharSequence[]{
                getResources().getString(R.string.dialog_suggestion),
                getResources().getString(R.string.dialog_bug),
                getResources().getString(R.string.facebook_account),
                getResources().getString(R.string.instagram_account),
                getResources().getString(R.string.publish_business)}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String subject = "";
                String body = "";

                switch (which) {
                    case 0:
                        subject = getResources().getString(R.string.dialog_suggestion);
                        sendEmail(subject, body);
                        break;

                    case 1:
                        subject = getResources().getString(R.string.dialog_bug);
                        body = "<p><strong>Device Model</strong> " + Build.MANUFACTURER.toUpperCase() + " " + Build.MODEL + "<br/><strong>OS Version</strong> ANDROID " + Build.VERSION.RELEASE + "</p>";
                        sendEmail(subject, body);
                        break;

                    case 2:
                        Intent facebookIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.facebook_url)));
                        startActivity(facebookIntent);
                        break;

                    case 3:
                        Intent instagramIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.instagram_url)));
                        startActivity(instagramIntent);
                        break;

                    case 4:
                        subject = getResources().getString(R.string.dialog_business);
                        body = getResources().getString(R.string.publish_msg);
                        sendEmail(subject, body);
                        break;
                }
            }
        });
        builder.show();
    }

    private void sendEmail(String subject, String body){
        Intent email = new Intent(Intent.ACTION_SENDTO);
        email.setType("text/html");
        email.putExtra(Intent.EXTRA_EMAIL, new String[]{getResources().getString(R.string.support_email)});
        email.putExtra(Intent.EXTRA_SUBJECT, subject);
        email.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(body));
        email.setData(Uri.parse("mailto:"));

        startActivity(Intent.createChooser(email, getResources().getString(R.string.contact_chooser_title)));
    }

    private void hideLoader(){
        if(loader != null){
            loader.dismiss();
        }
    }

    private void onLogout(){
        ParseUser.logOutInBackground(new LogOutCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null){
                    LocalDataManager localDataManager = new LocalDataManager(getApplicationContext());
                    localDataManager.deleteLocalData(null);

                    ParsePush.unsubscribeInBackground("Offers");
                    ParsePush.unsubscribeInBackground("General");

                    startActivity(new Intent(Main.this, Login.class));
                    finish();
                }
                else{
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_logout), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}