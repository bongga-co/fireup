package com.bambazu.fireup;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.bambazu.fireup.Config.Config;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.HashMap;

public class Map extends AppCompatActivity {
    private GoogleMap map;
    private java.util.Map<Marker, String[]> idMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Config.tracker.setScreenName(this.getClass().toString());

        idMarker = new HashMap<Marker, String[]>();
        setUpMap();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return false;
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

        return super.onOptionsItemSelected(item);
    }

    private void setUpMap(){
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        if (map != null) {
            map.setMyLocationEnabled(true);
            map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                Intent intent = new Intent(Map.this, Detail.class);
                intent.putExtra("placePosition", Integer.parseInt(idMarker.get(marker)[0]));
                startActivity(intent);
                }
            });

            if(Config.currentLatitude != 0.0 && Config.currentLongitude != 0.0){
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Config.currentLatitude, Config.currentLongitude), 11));

                if(Config.currentPlaces != null){
                    map.clear();
                    for(int i=0, l=Config.currentPlaces.size(); i<l; i++){
                        if(Config.currentPlaces.get(i).getVisible()) {
                            Marker marker = map.addMarker(new MarkerOptions()
                                    .title(Config.currentPlaces.get(i).getPlaceName())
                                    .snippet(getResources().getString(getResources().getIdentifier(Config.currentPlaces.get(i).getPlaceCategory().toLowerCase(), "string", getPackageName())))
                                    .position(new LatLng(Config.currentPlaces.get(i).getLatitude(), Config.currentPlaces.get(i).getLongitude()))
                                    .icon(BitmapDescriptorFactory.fromResource(getResources().getIdentifier(setPlaceIcon(Config.currentPlaces.get(i).getPlaceCategory()), "drawable", getApplicationContext().getPackageName()))));

                            idMarker.put(marker, new String[]{
                                    String.valueOf(i)
                            });
                        }
                    }

                    String resultText = (Config.currentPlaces.size() != 1) ? getResources().getString(R.string.places_size) : getResources().getString(R.string.places_size_singular);
                    Toast.makeText(this, String.valueOf(Config.currentPlaces.size()) + " " + resultText, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private String setPlaceIcon(String category){
        String c = null;
        switch (category.toLowerCase()){
            case "motel":
                c = "ic_motel";
                break;

            case "gaybar":
                c = "ic_gaybar";
                break;

            case "swingerbar":
                c = "ic_swingerbar";
                break;

            case "massagecenter":
                c = "ic_massagecenter";
                break;

            case "sexshop":
                c = "ic_sexshop";
                break;
        }

        return c;
    }
}
