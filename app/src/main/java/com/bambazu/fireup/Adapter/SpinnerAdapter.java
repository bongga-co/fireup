package com.bambazu.fireup.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bambazu.fireup.Model.SpinnerModel;
import com.bambazu.fireup.R;

import java.util.ArrayList;

/**
 * Created by blackxcorpio on 26/12/2014.
 */
public class SpinnerAdapter extends ArrayAdapter<String> {
    private Context context;
    private ArrayList rows;
    private int layout;
    private SpinnerModel spinnerModel;

    public SpinnerAdapter(Context context, int layout, ArrayList rows){
        super(context, layout, rows);

        this.context = context;
        this.rows = rows;
        this.layout = layout;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    private View getCustomView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(layout, parent, false);

        spinnerModel = null;
        spinnerModel = (SpinnerModel) rows.get(position);

        TextView row =(TextView) view.findViewById(R.id.spinner_row);

        if(position == 0){
            row.setText(context.getResources().getString(R.string.first_city));
        }
        else{
            row.setText(spinnerModel.getRow());
        }

        return view;
    }
}
