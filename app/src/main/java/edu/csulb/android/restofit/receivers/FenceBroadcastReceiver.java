package edu.csulb.android.restofit.receivers;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.awareness.fence.FenceState;

import br.com.goncalves.pugnotification.notification.PugNotification;
import edu.csulb.android.restofit.R;
import edu.csulb.android.restofit.helpers.LocationHelper;
import edu.csulb.android.restofit.helpers.StaticMembers;
import edu.csulb.android.restofit.views.activities.RestaurantResultsActivity;

public class FenceBroadcastReceiver extends BroadcastReceiver {

    static String TAG = FenceBroadcastReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        FenceState fenceState = FenceState.extract(intent);

        Log.d(TAG, "Fence Receiver Received");

        if (TextUtils.equals(fenceState.getFenceKey(), StaticMembers.FenceKeys.HEADPHONES_RUNNING_OR_PLUGGED_IN)) {
            switch (fenceState.getCurrentState()) {
                case FenceState.TRUE:
                    Log.i(TAG, "Fence > Headphones are plugged in.");

                    Location l = LocationHelper.getLastKnownLocation(context);

                    Bundle bundle = new Bundle();
                    bundle.putString("q", "robeks jamba drinks");
                    bundle.putString("radius", "10000");
                    bundle.putString("lat", String.valueOf(l.getLatitude()));
                    bundle.putString("lon", String.valueOf(l.getLongitude()));

                    PugNotification.with(context)
                            .load()
                            .title("Going for a run?")
                            .message("Robecks and Jamba Juice near you.")
                            .bigTextStyle("Robecks and Jamba Juice near you.")
                            .smallIcon(R.mipmap.ic_launcher)
                            .largeIcon(R.mipmap.ic_launcher)
                            .flags(Notification.DEFAULT_ALL)
                            .click(RestaurantResultsActivity.class, bundle)
                            .simple()
                            .build();
                    break;
                case FenceState.FALSE:
                    Log.i(TAG, "Fence > Headphones are NOT plugged in.");
                    break;
                case FenceState.UNKNOWN:
                    Log.i(TAG, "Fence > The headphone fence is in an unknown state.");
                    break;
            }
        } else if (TextUtils.equals(fenceState.getFenceKey(), StaticMembers.FenceKeys.LOCATION)) {
            switch (fenceState.getCurrentState()) {
                case FenceState.TRUE:
                    Location l = LocationHelper.getLastKnownLocation(context);

                    Bundle bundle = new Bundle();
                    bundle.putString("q", "food");
                    bundle.putString("radius", "1000");
                    bundle.putString("lat", String.valueOf(l.getLatitude()));
                    bundle.putString("lon", String.valueOf(l.getLongitude()));

                    PugNotification.with(context)
                            .load()
                            .title("Thinking about food?")
                            .message("Robecks and Jamba Juice near you.")
                            .bigTextStyle("Robecks and Jamba Juice near you.")
                            .smallIcon(R.drawable.pugnotification_ic_launcher)
                            .largeIcon(R.drawable.pugnotification_ic_launcher)
                            .flags(Notification.DEFAULT_ALL)
                            .autoCancel(true)
                            .click(RestaurantResultsActivity.class, bundle)
                            .simple()
                            .build();
                    break;
                case FenceState.FALSE:
                    Log.i(TAG, "Fence > Headphones are NOT plugged in.");
                    break;
                case FenceState.UNKNOWN:
                    Log.i(TAG, "Fence > The headphone fence is in an unknown state.");
                    break;
            }
        }
    }
}