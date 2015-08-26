package com.bambazu.fireup.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.androidquery.AQuery;
import com.bambazu.fireup.Model.Discount;
import com.bambazu.fireup.R;
import java.util.ArrayList;

/**
 * Created by Sil on 25/08/2015.
 */
public class DiscountAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Discount> listOffer;
    private AQuery imgLoader;
    private Bitmap loader;

    public DiscountAdapter(Context context, ArrayList<Discount> listOffer){
        this.context = context;
        this.listOffer = listOffer;

        imgLoader = new AQuery(context);
        loader = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_loader);
    }

    @Override
    public int getCount() {
        return listOffer.size();
    }

    @Override
    public Object getItem(int position) {
        return listOffer.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        View convertView = null;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(convertView == null){
            convertView = inflater.inflate(R.layout.item_offer, parent, false);

            ViewHolder holder = new ViewHolder();
            holder.offerIcon = (ImageView) convertView.findViewById(R.id.offerIcon);
            holder.offerDiscount = (TextView) convertView.findViewById(R.id.offerDiscount);
            holder.offerPlace = (TextView) convertView.findViewById(R.id.offerPlace);

            convertView.setTag(holder);
        }

        ViewHolder holder = (ViewHolder) convertView.getTag();

        AQuery asyncLoader = imgLoader.recycle(convertView);
        asyncLoader.id(holder.offerIcon).image(listOffer.get(position).getOfferIcon(), true, true, 0, 0, loader, 0, 1.0f);

        holder.offerDiscount.setText(listOffer.get(position).getOfferDiscount());
        holder.offerPlace.setText(listOffer.get(position).getOfferPlace());

        return convertView;
    }

    static class ViewHolder {
        ImageView offerIcon;
        TextView offerDiscount;
        TextView offerPlace;
    }
}
