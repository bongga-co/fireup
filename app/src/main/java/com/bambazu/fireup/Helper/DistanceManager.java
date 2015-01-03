package com.bambazu.fireup.Helper;

import android.os.AsyncTask;

import com.bambazu.fireup.Config.Config;
import com.bambazu.fireup.Interfaz.CalculateDistanceListener;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

/**
 * Created by blackxcorpio on 02/01/2015.
 */
public class DistanceManager extends AsyncTask<HashMap, Void, Double> {
    private StringBuilder stringBuilder;
    private double destinationLatitude;
    private double destinationLongitude;
    private String dist = "";
    private JSONObject jsonObject;
    private CalculateDistanceListener calculateDistanceListener;

    @Override
    protected Double doInBackground(HashMap... locationData) {
        stringBuilder = new StringBuilder();

        destinationLatitude = (Double) locationData[0].get("destinationLatitude");
        destinationLongitude = (Double) locationData[0].get("destinationLongitude");

        try {
            String url = "http://maps.googleapis.com/maps/api/directions/json?origin=" + Config.currentLatitude + "," + Config.currentLongitude + "&destination=" + destinationLatitude + "," + destinationLongitude + "&mode=driving&sensor=false";

            HttpPost httppost = new HttpPost(url);
            HttpClient client = new DefaultHttpClient();
            HttpResponse response;
            stringBuilder = new StringBuilder();

            response = client.execute(httppost);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            int b;

            while ((b = stream.read()) != -1) {
                stringBuilder.append((char) b);
            }
        }
        catch (ClientProtocolException e) {}
        catch (IOException e) {}

        try {
            jsonObject = new JSONObject(stringBuilder.toString());
            if(jsonObject.getString("status").equals("OK")){
                JSONArray array = jsonObject.getJSONArray("routes");
                JSONObject routes = array.getJSONObject(0);
                JSONArray legs = routes.getJSONArray("legs");
                JSONObject steps = legs.getJSONObject(0);
                JSONObject distance = steps.getJSONObject("distance");

                dist = distance.getString("text");
                calculateDistanceListener.calculateDistance(dist);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void setCalculateDistanceListener(CalculateDistanceListener calculateDistanceListener){
        this.calculateDistanceListener = calculateDistanceListener;
    }
}
