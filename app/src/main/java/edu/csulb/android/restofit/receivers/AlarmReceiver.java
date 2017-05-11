package edu.csulb.android.restofit.receivers;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import br.com.goncalves.pugnotification.notification.PugNotification;
import edu.csulb.android.restofit.R;
import edu.csulb.android.restofit.helpers.LocationHelper;
import edu.csulb.android.restofit.helpers.StaticMembers;
import edu.csulb.android.restofit.views.activities.RestaurantResultsActivity;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        handleIntent(context);
    }

    public static void handleIntent(Context context) {

        String title, message, query;
        int currentHour = StaticMembers.getCurrentHour();
        if (currentHour >= 5 && currentHour < 10) {
            // Just had breakfast
            title = "Awesome breakfast near you!";
            message = "These places server good breakfast.";
        } else if (currentHour >= 10 && currentHour <= 15) {
            // Just had lunch
            title = "Get your lunch soon!";
            message = "These places server good lunch.";
        } else {
            // Dinner
            title = "Don't skip dinner today!";
            message = "These places server good dinner.";
        }

        query = "food";

        Location l = LocationHelper.getLastKnownLocation(context);
        Bundle bundle = new Bundle();
        bundle.putString("q", query);
        bundle.putString("radius", "10000");
        bundle.putString("lat", String.valueOf(l.getLatitude()));
        bundle.putString("lon", String.valueOf(l.getLongitude()));

        PugNotification.with(context)
                .load()
                .title(title)
                .message(message)
                .autoCancel(true)
                .bigTextStyle(message)
                .smallIcon(R.mipmap.ic_launcher)
                .largeIcon(R.mipmap.ic_launcher)
                .flags(Notification.DEFAULT_ALL)
                .click(RestaurantResultsActivity.class, bundle)
                .simple()
                .build();
    }
}