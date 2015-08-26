package com.bambazu.fireup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.bambazu.fireup.Adapter.DiscountAdapter;
import com.bambazu.fireup.Config.Config;
import com.bambazu.fireup.Helper.NetworkManager;
import com.bambazu.fireup.Model.Discount;
import com.bambazu.fireup.Model.Place;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class Offers extends AppCompatActivity {
    private ProgressDialog loader;
    private NetworkManager networkManager;
    private ListView listOffer;
    private DiscountAdapter discountAdapter;
    private ArrayList<Discount> offerData;
    private ArrayList<Place> dataPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers);

        Config.tracker.setScreenName(this.getClass().toString());

        networkManager = new NetworkManager(this);

        loader = new ProgressDialog(this);
        loader.setCancelable(false);
        loader.setMessage(getResources().getString(R.string.loader_message));

        listOffer = (ListView) findViewById(R.id.listOffer);
        listOffer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Offers.this, OfferDetail.class);

                Discount discount = (Discount) listOffer.getItemAtPosition(position);
                intent.putExtra("offerID", discount.getOfferID());
                intent.putExtra("offerIcon", discount.getOfferIcon());
                intent.putExtra("offerDiscount", discount.getOfferDiscount());
                intent.putExtra("offerPlace", discount.getOfferPlace());
                intent.putExtra("offerDesc", discount.getOfferDesc());
                intent.putExtra("placePosition", position);

                startActivity(intent);
            }
        });

        listOffer.setEmptyView(findViewById(R.id.empty_offer));

        getDiscounts();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_offers, menu);
        return false;
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

    private void getDiscounts(){
        if(!networkManager.isNetworkOnline()){
            Toast.makeText(this, getResources().getString(R.string.error_network), Toast.LENGTH_SHORT).show();
            return;
        }

        loader.show();

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Offers");
        query.include("idPlace");
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null){
                    loader.dismiss();

                    offerData = new ArrayList<Discount>();
                    dataPlace = new ArrayList<Place>();

                    for(int i=0; i<objects.size(); i++){
                        if(objects.get(i).getBoolean("visible")){
                            offerData.add(new Discount(objects.get(i).getObjectId(), objects.get(i).getParseFile("picture").getUrl(), objects.get(i).getString("discount"), objects.get(i).getParseObject("idPlace").getString("category") + " " + objects.get(i).getParseObject("idPlace").getString("name"), objects.get(i).getString("description")));
                            dataPlace.add(new Place(
                                    objects.get(i).getParseObject("idPlace").getObjectId(),
                                    objects.get(i).getParseObject("idPlace").getString("name"),
                                    objects.get(i).getParseObject("idPlace").getParseFile("preview_one").getUrl(),
                                    objects.get(i).getParseObject("idPlace").getParseFile("preview_two").getUrl(),
                                    objects.get(i).getParseObject("idPlace").getParseFile("preview_three").getUrl(),
                                    objects.get(i).getParseObject("idPlace").getParseFile("preview_four").getUrl(),
                                    objects.get(i).getParseObject("idPlace").getParseFile("preview_five").getUrl(),
                                    objects.get(i).getParseObject("idPlace").getString("category"),
                                    objects.get(i).getParseObject("idPlace").getNumber("ranking"),
                                    objects.get(i).getParseObject("idPlace").getParseGeoPoint("position").getLatitude(),
                                    objects.get(i).getParseObject("idPlace").getParseGeoPoint("position").getLongitude(),
                                    objects.get(i).getParseObject("idPlace").getNumber("rooms").intValue(),
                                    objects.get(i).getParseObject("idPlace").getBoolean("visible"),
                                    objects.get(i).getParseObject("idPlace").getString("address"),
                                    objects.get(i).getParseObject("idPlace").getString("city"),
                                    objects.get(i).getParseObject("idPlace").getString("depto"),
                                    objects.get(i).getParseObject("idPlace").getString("country"),
                                    objects.get(i).getParseObject("idPlace").getString("description"),
                                    objects.get(i).getParseObject("idPlace").getNumber("lowprice"),
                                    objects.get(i).getParseObject("idPlace").getNumber("highprice"),
                                    objects.get(i).getParseObject("idPlace").getString("phone")
                            ));
                        }
                    }

                    Config.offersPlaces = dataPlace;

                    discountAdapter = new DiscountAdapter(getApplicationContext(), offerData);
                    listOffer.setAdapter(discountAdapter);
                    discountAdapter.notifyDataSetChanged();
                }
                else{
                    loader.dismiss();
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_offer), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
