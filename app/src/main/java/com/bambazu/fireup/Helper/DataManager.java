package com.bambazu.fireup.Helper;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
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

        ParseQuery<ParseObject> query = ParseQuery.getQuery(objectType);

        if(queryType.equals("List")){
            query.whereWithinKilometers("position", new ParseGeoPoint(Config.currentLatitude, Config.currentLongitude), 5);

            try{
                responseObject = query.find();
            }
            catch (ParseException e){
                Toast.makeText(context, context.getResources().getString(R.string.error_get_places), Toast.LENGTH_SHORT).show();
            }
        }
        else{
            JSONObject searchData = null;

            try{
                searchData = new JSONObject((String) data[0].get("search"));

                //LowPrice
                if(searchData.getLong("lowprice") > 0){
                    query.whereGreaterThanOrEqualTo("lowprice", searchData.getLong("lowprice"));
                }

                //HighPrice
                if(searchData.getLong("highprice") > 0){
                    query.whereLessThanOrEqualTo("highprice", searchData.getLong("highprice"));
                }

                //City
                if(!searchData.getString("city").equals("")){
                    query.whereEqualTo("city", searchData.getString("city"));
                }

                //Category
                if(!searchData.getString("category").equals("")){
                    query.whereEqualTo("category", searchData.getString("category"));
                }

                //Rating
                if(searchData.getInt("rating") != 0){
                    query.whereEqualTo("ranking", searchData.getInt("rating"));
                }

                //Distance
                if(searchData.getInt("distance") != 0){
                    query.whereWithinKilometers("position", new ParseGeoPoint(Config.currentLatitude, Config.currentLongitude), searchData.getInt("distance"));
                }

                try{
                    responseObject = query.find();
                }
                catch (ParseException e){
                    Toast.makeText(context, context.getResources().getString(R.string.error_get_places), Toast.LENGTH_SHORT).show();
                }
            }
            catch (JSONException e){
                Toast.makeText(context, context.getResources().getString(R.string.error_get_places), Toast.LENGTH_SHORT).show();
            }
        }

        return responseObject;
    }

    @Override
    protected void onPostExecute(List<ParseObject> parseObjects) {
        super.onPostExecute(parseObjects);
        hideLoading();

        if(parseObjects != null){
            dataListener.retrieveData(parseObjects);
        }
        else {
            Toast.makeText(context, context.getResources().getString(R.string.error_get_places), Toast.LENGTH_SHORT).show();
        }
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
