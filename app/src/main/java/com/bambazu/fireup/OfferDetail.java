package com.bambazu.fireup;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.androidquery.AQuery;
import com.bambazu.fireup.Config.Config;
import com.bambazu.fireup.Helper.Contents;
import com.bambazu.fireup.Helper.QRCodeEncoder;
import com.google.android.gms.analytics.HitBuilders;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

public class OfferDetail extends AppCompatActivity implements View.OnClickListener {
    private ImageView offerIcon;
    private TextView offerDiscount;
    private TextView offerPlace;
    private TextView offerDesc;
    private Button claimOffer;
    private Button goPlace;
    private static String place;
    private int index;
    private static String qrData;

    private AQuery imgLoader;
    private Bitmap loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_detail);

        Config.tracker.setScreenName(this.getClass().toString());

        imgLoader = new AQuery(this);
        loader = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_loader);

        final Bundle extras = getIntent().getExtras();
        if(extras == null){
            Toast.makeText(this, getResources().getString(R.string.error_offer), Toast.LENGTH_SHORT).show();
            finish();
        }

        index = extras.getInt("placePosition");
        qrData = getResources().getString(R.string.offer_qr_code) + " " + extras.getString("offerID");

        offerIcon = (ImageView) findViewById(R.id.detail_offer_icon);
        offerDiscount = (TextView) findViewById(R.id.detail_offer_discount);
        offerPlace = (TextView) findViewById(R.id.detail_offer_place);
        offerDesc = (TextView) findViewById(R.id.detail_offer_desc);
        claimOffer = (Button) findViewById(R.id.claim_offer);
        claimOffer.setOnClickListener(this);

        goPlace = (Button) findViewById(R.id.go_place_offer);
        goPlace.setOnClickListener(this);

        showOfferDetail(extras);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_offer_detail, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
        }
        else if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showOfferDetail(Bundle data){
        AQuery asyncLoader = imgLoader.recycle(offerIcon);
        asyncLoader.id(offerIcon).image(data.getString("offerIcon"), true, true, 0, 0, loader, 0, 1.0f);

        offerDiscount.setText(data.getString("offerDiscount"));
        offerPlace.setText(data.getString("offerPlace"));
        offerDesc.setText(Html.fromHtml(data.getString("offerDesc")));
        offerDesc.setMovementMethod(LinkMovementMethod.getInstance());
        place = data.getString("offerPlace");

        Config.tracker.send(new HitBuilders.EventBuilder()
                .setCategory("Offer Detail UI")
                .setAction("Visit")
                .setLabel("Offer For: " + place)
                .build());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.claim_offer:
                showQROffer();
                break;

            case R.id.go_place_offer:
                Intent intent = new Intent(OfferDetail.this, Detail.class);
                intent.putExtra("placePosition", index);
                startActivity(intent);
                break;
        }
    }

    private void showQROffer(){
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        View view = inflater.inflate(R.layout.offer_qr_dialog, null);

        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int width = point.x;
        int height = point.y;
        int smallerDimension = width < height ? width : height;
        smallerDimension = smallerDimension * 3/4;

        QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(qrData,
                null,
                Contents.Type.TEXT,
                BarcodeFormat.QR_CODE.toString(),
                smallerDimension);
        try {
            Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();
            ImageView qrWrapper = (ImageView) view.findViewById(R.id.qr_offer_wrapper);
            qrWrapper.setImageBitmap(bitmap);

        }
        catch (WriterException e) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.qr_error), Toast.LENGTH_SHORT).show();
        }

        AlertDialog.Builder dialog = new AlertDialog.Builder(OfferDetail.this);
        dialog.setView(view);

        dialog.setPositiveButton(getResources().getString(R.string.ok_btn), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {}
        });

        AlertDialog alertDialog = dialog.create();
        alertDialog.show();

        Config.tracker.send(new HitBuilders.EventBuilder()
                .setCategory("Offer Detail UI")
                .setAction("Click")
                .setLabel("Offer Claimed For: " + place)
                .build());
    }
}
