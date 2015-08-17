package com.bambazu.fireup.Adapter;

/**
 * Created by Blackxcorpio on 20/10/2014.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.androidquery.AQuery;

import java.util.ArrayList;

import com.bambazu.fireup.R;

public class ViewPagerAdapter extends PagerAdapter {
    Context context;
    ArrayList<String> place_image;
    int totalImages;
    LayoutInflater inflater;
    private AQuery imgLoader;
    private Bitmap loader;

    public ViewPagerAdapter(Context context, ArrayList<String> place_image, int totalImages) {
        this.context = context;
        this.place_image = place_image;
        this.totalImages = totalImages;

        imgLoader = new AQuery(context);
        loader = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_loader);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public int getCount() {
        return totalImages;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.viewpager_item, container, false);

        ImageView imgPlace = (ImageView) itemView.findViewById(R.id.place_image);
        AQuery asyncLoader = imgLoader.recycle(itemView);
        asyncLoader.id(imgPlace).image(place_image.get(position), true, true, 0, 0, loader, 0, 1.0f);

        ((ViewPager) container).addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);
    }
}