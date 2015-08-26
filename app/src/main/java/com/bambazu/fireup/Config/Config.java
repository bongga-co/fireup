package com.bambazu.fireup.Config;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;

import com.bambazu.fireup.Model.Place;
import com.bambazu.fireup.R;
import com.facebook.FacebookSdk;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by blackxcorpio on 26/12/2014.
 */
public class Config extends Application {
    public static double currentLatitude = 0.0;
    public static double currentLongitude = 0.0;
    public static ArrayList<Place> currentPlaces;
    public static ArrayList<Place> offersPlaces = null;

    public static GoogleAnalytics analytics;
    public static Tracker tracker;

    public static boolean comingComment = false;

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, "W8c7QBPJW1B2FBqCFwZPra6fHvIZcQncEl3USxBJ", "roS4gCRfShVZeQ8GDQgcrgOttWQ83tChFuYPLhqh");
        FacebookSdk.sdkInitialize(getApplicationContext());
        ParseFacebookUtils.initialize(getApplicationContext());
        printHashKey();

        analytics = GoogleAnalytics.getInstance(this);
        analytics.setLocalDispatchPeriod(1800);

        tracker = analytics.newTracker("UA-55361973-3");
        tracker.enableExceptionReporting(true);
        tracker.enableAdvertisingIdCollection(true);
        tracker.enableAutoActivityTracking(false);
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

    public void printHashKey(){
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.bambazu.fireup",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        }
        catch (PackageManager.NameNotFoundException e) {}
        catch (NoSuchAlgorithmException e) {}
    }

    public static String formattedDate(Date date){
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date d = date;
        return dateFormat.format(d);
    }
}