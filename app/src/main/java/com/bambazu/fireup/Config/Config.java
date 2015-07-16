package com.bambazu.fireup.Config;

import android.app.Application;

import com.bambazu.fireup.Helper.DistanceManager;
import com.bambazu.fireup.Model.Place;
import com.bambazu.fireup.R;
import com.parse.ParseObject;

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
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by blackxcorpio on 26/12/2014.
 */
public class Config extends Application {
    public static double currentLatitude = 0.0;
    public static double currentLongitude = 0.0;
    public static double distance = 0.0;
    public static final int MIN_TIME = 0;
    public static final int MIN_DISTANCE = 0;
    public static ArrayList<Place> currentPlaces;
    public static boolean isStartedInBackground = false;

    @Override
    public void onCreate() {
        super.onCreate();
        com.parse.Parse.initialize(this, "W8c7QBPJW1B2FBqCFwZPra6fHvIZcQncEl3USxBJ", "roS4gCRfShVZeQ8GDQgcrgOttWQ83tChFuYPLhqh");
    }

    public static int setPlaceColor(String type){
        int color = 0;

        if(type.equals("motel")){
            color = R.color.motel;
        }
        else if(type.equals("sexshop")){
            color = R.color.sexshop;
        }
        else if(type.equals("gaybar")){
            color = R.color.gaybar;
        }
        else if(type.equals("swingerbar")){
            color = R.color.swingerbar;
        }
        else if(type.equals("massagecenter")){
            color = R.color.massagecenter;
        }

        return color;
    }

    public static double getDistance(double sourceLatitude, double sourceLongitude, double destinationLatitude, double destinationLongitude){
        /*double theta = Math.abs(sourceLongitude - destinationLongitude);
        double dist = Math.sin(deg2rad(sourceLatitude)) * Math.sin(deg2rad(destinationLatitude)) + Math.cos(deg2rad(sourceLatitude)) * Math.cos(deg2rad(destinationLatitude)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        return Math.floor(dist * 100.0) / 100.0;*/

        float [] dist = new float[1];
        android.location.Location.distanceBetween(sourceLatitude, sourceLongitude, destinationLatitude, destinationLongitude, dist);

        return Math.round(dist[0] * 0.000621371192f);
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
}