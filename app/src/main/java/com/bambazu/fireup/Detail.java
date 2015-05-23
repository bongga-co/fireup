package com.bambazu.fireup;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bambazu.fireup.Adapter.ServiceAdapter;
import com.bambazu.fireup.Adapter.ViewPagerAdapter;
import com.bambazu.fireup.Config.Config;
import com.bambazu.fireup.Helper.DistanceManager;
import com.bambazu.fireup.Helper.NetworkManager;
import com.bambazu.fireup.Interfaz.CalculateDistanceListener;
import com.bambazu.fireup.Model.Place;
import com.bambazu.fireup.Model.Service;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.viewpagerindicator.CirclePageIndicator;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;

public class Detail extends ActionBarActivity implements View.OnClickListener, CalculateDistanceListener {
    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    private ArrayList<String> place_image;
    private int totalImages = 1;
    private CirclePageIndicator indicator;
    private TextView placeName;
    private RatingBar placeRating;
    private TextView placeCategory;
    private TextView placeLowPrice;
    private TextView placeHighPrice;
    private TextView placeDescription;
    private TextView placeAddress;
    private TextView placePhone;
    private TextView placeCity;
    private TextView placeDistance;
    private TextView placeAvailableRooms;

    private Button gridServices;

    private Button btnShowDescription;
    private static String desc;
    private LinearLayout linearLayoutForMap;
    private RelativeLayout mapWrapper;
    private String objectId;
    private ListView listServices;
    private View serviceWrapper;
    private String phone;
    private double lat;
    private double lng;
    private NetworkManager networkManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        networkManager = new NetworkManager(this);

        viewPager = (ViewPager) findViewById(R.id.place_pager);

        placeName = (TextView) findViewById(R.id.detail_place_name);
        placeRating = (RatingBar) findViewById(R.id.detail_place_rating);
        placeCategory = (TextView) findViewById(R.id.detail_place_category);
        placeLowPrice = (TextView) findViewById(R.id.detail_low_price);
        placeHighPrice = (TextView) findViewById(R.id.detail_high_price);

        mapWrapper = (RelativeLayout) findViewById(R.id.map_wrapper);
        mapWrapper.setOnClickListener(this);

        linearLayoutForMap = (LinearLayout) findViewById(R.id.map_info_wrapper);
        linearLayoutForMap.setOnClickListener(this);

        placeAddress = (TextView) findViewById(R.id.detail_place_address);
        placePhone = (TextView) findViewById(R.id.detail_place_phone);
        placeCity = (TextView) findViewById(R.id.detail_place_city);
        placeDistance = (TextView) findViewById(R.id.detail_place_distance);
        placeAvailableRooms = (TextView) findViewById(R.id.detail_place_available_rooms);

        gridServices = (Button) findViewById(R.id.gridServices);
        gridServices.setOnClickListener(this);

        placeDescription = (TextView) findViewById(R.id.detail_description);

        btnShowDescription = (Button) findViewById(R.id.btn_show_more_desc);
        btnShowDescription.setOnClickListener(this);

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            Place placeData = Config.currentPlaces.get(extras.getInt("placePosition"));
            setTitle(placeData.getPlaceName());
            objectId = placeData.getPlaceCode();
            showPlaceData(placeData);

            //Calculate distance
            final HashMap<String, Object> queryData = new HashMap<String, Object>();
            queryData.put("destinationLatitude", placeData.getLatitude());
            queryData.put("destinationLongitude", placeData.getLongitude());

            if(networkManager.isNetworkOnline()){
                placeDistance.setText(getResources().getString(R.string.calculate_distance));

                final DistanceManager distance = new DistanceManager();
                distance.setCalculateDistanceListener(this);
                distance.execute(queryData);
            }
            else{
                placeDistance.setText("--");
            }
        }
        else{
            Toast.makeText(this, getResources().getString(R.string.error_place_detail_data), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_call) {
            callPlace();
            return true;
        }
        /*else if (id == R.id.action_comments) {
            openComments();
            return true;
        }*/
        else if(id == android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_show_more_desc:
                showModalDescription();
                break;

            case R.id.gridServices:
                showServices();
                break;

            case R.id.map_wrapper:
            case R.id.map_info_wrapper:
                takeMeToThePlace();
                break;
        }
    }

    @Override
    public void calculateDistance(String distance) {
        String d = (distance != null) ? distance : "--";
        placeDistance.setText(d);
    }

    private void showPlaceData(Place placeData){
        place_image = new ArrayList<String>();

        if(placeData.getPlaceIcon() != null){
            place_image.add(placeData.getPlaceIcon());
        }

        if(placeData.getPlaceIcon2() != null){
            place_image.add(placeData.getPlaceIcon2());
        }

        if(placeData.getPlaceIcon3() != null){
            place_image.add(placeData.getPlaceIcon3());
        }

        if(placeData.getPlaceIcon4() != null){
            place_image.add(placeData.getPlaceIcon4());
        }

        if(placeData.getPlaceIcon5() != null){
            place_image.add(placeData.getPlaceIcon5());
        }

        totalImages = place_image.size();
        pagerAdapter = new ViewPagerAdapter(Detail.this, place_image, totalImages);
        viewPager.setAdapter(pagerAdapter);

        indicator = (CirclePageIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(viewPager);

        placeName.setText(placeData.getPlaceName());
        placeCategory.setText(getApplicationContext().getResources().getIdentifier(placeData.getPlaceCategory().toLowerCase(), "string", getApplicationContext().getPackageName()));
        placeRating.setRating(placeData.getPlaceRanking().floatValue());

        if(placeCategory.getText().toString().toLowerCase().equals("motel") && placeData.getRooms() > 0){
            placeAvailableRooms.setText(placeData.getRooms() + " " + getResources().getString(R.string.available_rooms));
        }

        placeLowPrice.setText(formatCurrency(placeData.getLowprice()));
        placeHighPrice.setText(formatCurrency(placeData.getHighprice()));

        placeAddress.setText(placeData.getAddress());
        placePhone.setText(placeData.getPhone().toString().substring(3));
        placeCity.setText(placeData.getCity().toString() + " - " + placeData.getDepto().toString());

        placeDescription.setText(showPlaceDescription(placeData.getDescription()));
        phone = placeData.getPhone();
        lat = placeData.getLatitude();
        lng = placeData.getLongitude();
    }

    private String showPlaceDescription(String placeDescription){
        desc = placeDescription;
        String placeDesc = null;

        if(placeDescription.length() > 150){
            placeDesc = placeDescription.substring(0, 150) + "...";
            btnShowDescription.setVisibility(View.VISIBLE);
        }
        else{
            placeDesc = placeDescription;
            btnShowDescription.setVisibility(View.INVISIBLE);
        }

        return placeDesc;
    }

    private void showModalDescription(){
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        View comment_wrapper = inflater.inflate(R.layout.description_dialog, null);

        AlertDialog.Builder dialog = new AlertDialog.Builder(Detail.this);
        dialog.setView(comment_wrapper);

        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        final TextView comment_value = (TextView) comment_wrapper.findViewById(R.id.descrption_text);
        comment_value.setText(desc);

        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }

    private String formatCurrency(Number price){
        String sign = "$ ";
        NumberFormat defaultFormat = NumberFormat.getInstance();
        Currency currency = Currency.getInstance("COP");
        defaultFormat.setCurrency(currency);

        return sign + defaultFormat.format(price);
    }

    private void showServices(){
        final ParseQuery<ParseObject> innerQuery = ParseQuery.getQuery("Places");
        innerQuery.whereEqualTo("objectId", objectId);

        final ParseQuery<ParseObject> query = ParseQuery.getQuery("ServicesByPlaces");
        query.whereMatchesQuery("idPlace", innerQuery);
        query.include("idService");

        if(!networkManager.isNetworkOnline()){
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_network), Toast.LENGTH_SHORT).show();
            return;
        }

        gridServices.setEnabled(false);
        gridServices.setText(getResources().getString(R.string.loader_message));

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        ArrayList<Service> arrayServices = null;
                        arrayServices = new ArrayList<Service>();

                        for (int i = 0; i < list.size(); i++) {
                            ParseObject services = list.get(i);
                            arrayServices.add(
                                    new Service(
                                            services.getParseObject("idService").getParseFile("icon").getUrl(),
                                            services.getParseObject("idService").getString("name")
                                    )
                            );
                        }

                        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                        serviceWrapper = inflater.inflate(R.layout.place_services, null);

                        listServices = (ListView) serviceWrapper.findViewById(R.id.listServices);
                        listServices.setAdapter(new ServiceAdapter(getApplicationContext(), arrayServices));

                        AlertDialog.Builder dialog = new AlertDialog.Builder(Detail.this);
                        dialog.setView(serviceWrapper);

                        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                gridServices.setEnabled(true);
                                gridServices.setText(getResources().getString(R.string.btn_show_services));
                            }
                        });

                        AlertDialog alertDialog = dialog.create();
                        alertDialog.show();
                    } else {
                        gridServices.setEnabled(true);
                        gridServices.setText(getResources().getString(R.string.btn_show_services));
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_services_available), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    gridServices.setEnabled(true);
                    gridServices.setText(getResources().getString(R.string.btn_show_services));
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_services), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void callPlace(){
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phone));
        startActivity(intent);
    }

    private void openComments(){
        Intent intent = new Intent(getApplicationContext(), Comment.class);
        intent.putExtra("objectId", objectId);
        startActivity(intent);
    }

    private void takeMeToThePlace(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(Detail.this);
        dialog.setTitle(getResources().getString(R.string.show_route_title));
        dialog.setMessage(getResources().getString(R.string.show_route_msg));
        dialog.setPositiveButton(getResources().getString(R.string.take_me), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?saddr="+Config.currentLatitude+","+Config.currentLongitude+"&daddr="+lat+","+lng+"&mode=driving"));
                startActivity(intent);
            }
        });

        dialog.setNegativeButton(getResources().getString(R.string.cancel_btn), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }
}
