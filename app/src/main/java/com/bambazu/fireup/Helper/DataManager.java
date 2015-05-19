package com.bambazu.fireup.Helper;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.bambazu.fireup.Config.Config;
import com.bambazu.fireup.R;
import com.bambazu.fireup.Interfaz.DataListener;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ubiquity on 9/19/14.
 */
public class DataManager extends AsyncTask<HashMap, Void, List<ParseObject>> {

    private ProgressDialog loader;
    private Context context;
    private List<ParseObject> responseObject = null;
    private String objectType;
    private String objectId;
    private String queryType;
    private DataListener dataListener;

    public DataManager(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if(loader == null){
            loader = new ProgressDialog(context);
            loader.setMessage(context.getResources().getString(R.string.loader_message));
            loader.setCancelable(false);
            loader.show();
        }
    }

    @Override
    protected List<ParseObject> doInBackground(HashMap... data) {
        objectType = (String) data[0].get("class");
        objectId = (String) data[0].get("objectId");
        queryType = (String) data[0].get("queryType");

        if(objectId == null){
            ParseQuery<ParseObject> query = ParseQuery.getQuery(objectType);

            if(queryType.equals("List")){
                query.whereWithinKilometers("position", new ParseGeoPoint(Config.currentLatitude, Config.currentLongitude), 3);
            }
            else{
                JSONObject searchData = null;

                try{
                    searchData = new JSONObject((String) data[0].get("search"));

                    //City
                    if(!searchData.getString("city").equals("Choose City") || !searchData.getString("city").equals("Seleccione Ciudad")){
                        query.whereEqualTo("city", searchData.getString("city"));
                    }

                    //LowPrice
                    /*if(searchData.getLong("lowprice") >= 0){
                        query.whereGreaterThanOrEqualTo("lowprice", searchData.getLong("lowprice"));
                    }*/

                    //HighPrice
                    /*if(searchData.getLong("highprice") >= 0){
                        Log.i("FireUp-High", String.valueOf(searchData.getLong("highprice")));
                        query.whereLessThanOrEqualTo("highprice", searchData.getLong("highprice"));
                    }*/

                    //Rating
                    /*if(searchData.getInt("rating") != 0){
                        query.whereEqualTo("ranking", searchData.getInt("rating"));
                    }*/

                    //Distance
                    /*if(searchData.getInt("distance") != 0){
                        query.whereWithinKilometers("position", new ParseGeoPoint(Config.currentLatitude, Config.currentLongitude), searchData.getInt("distance"));
                    }*/
                }
                catch (JSONException e){}
            }

            try{
                responseObject = query.find();
                hideLoading();
            }
            catch (ParseException e){
                Toast.makeText(context, context.getResources().getString(R.string.error_get_places), Toast.LENGTH_SHORT).show();
            }
        }
        else {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Places");
            query.getInBackground(objectId, new GetCallback<ParseObject>() {
                public void done(ParseObject object, ParseException e) {
                    if (e == null) {
                        ArrayList<ParseObject> parseObjects = new ArrayList<ParseObject>();
                        parseObjects.add(object);

                        dataListener.retrieveData(parseObjects);
                    } else {
                        Toast.makeText(context, context.getResources().getString(R.string.error_place_detail_data), Toast.LENGTH_SHORT).show();
                    }

                    hideLoading();
                }
            });
        }

        return responseObject;
    }

    @Override
    protected void onPostExecute(List<ParseObject> parseObjects) {
        super.onPostExecute(parseObjects);
        dataListener.retrieveData(parseObjects);
    }

    public void setDataListener(DataListener listener) {
        this.dataListener = listener;
    }

    private void hideLoading(){
        if (loader != null) {
            loader.dismiss();
            loader = null;
        }
    }
}
