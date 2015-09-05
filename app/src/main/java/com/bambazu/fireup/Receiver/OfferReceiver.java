package com.bambazu.fireup.Receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;

import com.bambazu.fireup.Main;
import com.bambazu.fireup.Offers;
import com.bambazu.fireup.R;
import com.parse.ParsePushBroadcastReceiver;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Sil on 26/08/2015.
 */
public class OfferReceiver extends ParsePushBroadcastReceiver {
    private final static String GROUP_KEY = "group_offers";
    private Intent parseIntent;

    public OfferReceiver() {
        super();
    }

    @Override
    protected void onPushReceive(Context context, Intent intent) {
        super.onPushReceive(context, intent);

        if (intent == null)
            return;

        try {
            JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));
            parseIntent = intent;

            parsePushJson(context, json);
        }
        catch (JSONException e) {}
    }

    @Override
    protected void onPushDismiss(Context context, Intent intent) {
        super.onPushDismiss(context, intent);
    }

    @Override
    protected void onPushOpen(Context context, Intent intent) {
        super.onPushOpen(context, intent);
    }

    /**
     * Parses the push notification json
     *
     * @param context
     * @param json
     */
    private void parsePushJson(Context context, JSONObject json) {
        try {
            JSONObject data = json.getJSONObject("data");
            String title = data.getString("title");
            String message = data.getString("message");
            String typePush = data.getString("type_push");
            Intent resultIntent = null;

            switch (typePush){
                case "Offer":
                    resultIntent = new Intent(context, Offers.class);
                    break;

                case "General":
                    resultIntent = new Intent(context, Main.class);
                    break;
            }

            if(resultIntent != null){
                showNotificationMessage(context, title, message, resultIntent);
            }
        }
        catch (JSONException e) {}
    }

    /**
     * Shows the notification message in the notification bar
     * If the app is in background, launches the app
     *
     * @param context
     * @param title
     * @param message
     * @param intent
     */
    private void showNotificationMessage(Context context, String title, String message, Intent intent) {
        // Check for empty push message
        if (TextUtils.isEmpty(message))
            return;

        // notification icon
        int icon = R.drawable.ic_launcher;
        int mNotificationId = 0001;

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        intent,
                        PendingIntent.FLAG_CANCEL_CURRENT
                );

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        Notification notification = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setStyle(inboxStyle)
                .setContentIntent(resultPendingIntent)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), icon))
                .setContentText(message)
                .build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(mNotificationId, notification);
    }
}
