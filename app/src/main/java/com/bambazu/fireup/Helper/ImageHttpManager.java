package com.bambazu.fireup.Helper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by blackxcorpio on 12/05/2015.
 */
public class ImageHttpManager extends AsyncTask<String, Void, Bitmap> {
    private ImageView placeMap;
    Bitmap bMap;

    public ImageHttpManager(ImageView placeMap){
        this.placeMap = placeMap;
    }

    @Override
    protected Bitmap doInBackground(String... urlImage) {
        InputStream in;
        try {
            in = new URL(urlImage[0]).openStream();
            bMap = BitmapFactory.decodeStream(in);

            if (in != null) {
                in.close();
            }
        }
        catch (Exception e) {}

        return bMap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        placeMap.setImageBitmap(bitmap);
    }
}
