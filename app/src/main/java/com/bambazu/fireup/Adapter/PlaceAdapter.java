package com.bambazu.fireup.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.androidquery.AQuery;
import java.util.ArrayList;
import com.bambazu.fireup.Config.Config;
import com.bambazu.fireup.R;
import com.bambazu.fireup.Model.Place;

/**
 * Created by blackxcorpio on 10/15/14.
 */
public class PlaceAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Place> listPlaces;
    private ViewHolder holder;
    private AQuery imgLoader;
    private int loader = R.drawable.ic_loader;

    public PlaceAdapter(Context context, ArrayList<Place> listPlaces) {
        this.context = context;
        this.listPlaces = listPlaces;

        imgLoader = new AQuery(context);
    }

    @Override
    public int getCount() {
        return listPlaces.size();
    }

    @Override
    public Object getItem(int position) {
        return listPlaces.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Place place = listPlaces.get(position);

        if(convertView == null){
            holder = new ViewHolder();

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_places, null);

            holder.placeName = (TextView) convertView.findViewById(R.id.place_name);
            holder.placeCategory = (TextView) convertView.findViewById(R.id.place_category);
            holder.placeIcon = (ImageView) convertView.findViewById(R.id.place_icon);
            holder.placeDistance = (TextView) convertView.findViewById(R.id.place_distance);
            holder.placeAvailableRoom = (TextView) convertView.findViewById(R.id.place_available_room);
            holder.lineWrapper = (LinearLayout) convertView.findViewById(R.id.line_icon_wrapper);
            holder.lineInnerWrapper = (LinearLayout) convertView.findViewById(R.id.line_icon_inner_wrapper);

            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.placeName.setText(place.getPlaceName());
        holder.placeCategory.setText(context.getResources().getIdentifier(place.getPlaceCategory().toLowerCase(), "string", context.getPackageName()));

        holder.lineWrapper.setBackgroundColor(context.getResources().getColor(Config.setPlaceColor(place.getPlaceCategory().toLowerCase())));
        holder.lineInnerWrapper.setBackgroundColor(context.getResources().getColor(Config.setPlaceColor(place.getPlaceCategory().toLowerCase())));

        holder.placeDistance.setText(String.valueOf(Config.getDistance(Config.currentLatitude, Config.currentLongitude, place.getLatitude(), place.getLongitude())) + " Kms");

        if(place.getPlaceCategory().toLowerCase().equals("motel") && place.getRooms() > 0) {
            holder.placeAvailableRoom.setText(place.getRooms() + " " + context.getResources().getString(R.string.available_rooms));
        }
        else{
            holder.placeAvailableRoom.setText("");
        }

        AQuery asyncLoader = imgLoader.recycle(convertView);
        asyncLoader.id(holder.placeIcon).progress(R.drawable.ic_loader).image(place.getPlaceIcon(), true, true, 0, 0, null, 0, 1.0f);

        return convertView;
    }

    static class ViewHolder {
        TextView placeName;
        TextView placeCategory;
        ImageView placeIcon;
        TextView placeDistance;
        TextView placeAvailableRoom;
        LinearLayout lineWrapper;
        LinearLayout lineInnerWrapper;
    }
}
