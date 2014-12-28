package com.bambazu.fireup.Helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by blackxcorpio on 8/28/14.
 */
public class NetworkManager {

    private static Context context;

    private static ConnectivityManager cm;
    private static NetworkInfo netInfo;

    public NetworkManager(Context context) {
        this.context = context;
    };

    public boolean isNetworkOnline() {
        boolean status = false;

        try{
            cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            netInfo = cm.getNetworkInfo(0);

            if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED) {
                status = true;
            }
            else {
                netInfo = cm.getNetworkInfo(1);

                if(netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED) {
                    status = true;
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }

        return status;
    }
}
