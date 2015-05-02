package com.bambazu.fireup.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.androidquery.AQuery;
import com.bambazu.fireup.Model.Service;
import com.bambazu.fireup.R;
import java.util.ArrayList;

/**
 * Created by blackxcorpio on 02/05/2015.
 */
public class ServiceAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Service> listServices;
    private ViewHolder holder;
    private AQuery imgLoader;

    public ServiceAdapter(Context context, ArrayList<Service> listServices) {
        this.context = context;
        this.listServices = listServices;

        imgLoader = new AQuery(context);
    }

    @Override
    public int getCount() {
        return listServices.size();
    }

    @Override
    public Object getItem(int position) {
        return listServices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Service service = listServices.get(position);

        if(convertView == null){
            holder = new ViewHolder();

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_service, null);

            holder.serviceName = (TextView) convertView.findViewById(R.id.service_name);
            holder.serviceIcon = (ImageView) convertView.findViewById(R.id.service_icon);

            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.serviceName.setText(service.getServiceName());

        AQuery asyncLoader = imgLoader.recycle(convertView);
        asyncLoader.id(holder.serviceIcon).progress(R.drawable.ic_loader).image(service.getServiceIcon(), true, true, 0, 0, null, 0, 1.0f);

        return convertView;
    }

    static class ViewHolder {
        ImageView serviceIcon;
        TextView serviceName;
    }
}
