package edu.csulb.android.restofit.helpers;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.fence.AwarenessFence;
import com.google.android.gms.awareness.fence.DetectedActivityFence;
import com.google.android.gms.awareness.fence.FenceUpdateRequest;
import com.google.android.gms.awareness.fence.HeadphoneFence;
import com.google.android.gms.awareness.fence.LocationFence;
import com.google.android.gms.awareness.state.HeadphoneState;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.ResultCallbacks;
import com.google.android.gms.common.api.Status;

import edu.csulb.android.restofit.receivers.FenceBroadcastReceiver;

public class AwarenessHelper {

    private static GoogleApiClient mGoogleApiClient;
    private static PendingIntent mFencePendingIntent;
    private static AwarenessFence locationFence;
    private static FenceBroadcastReceiver receiver;

    public static void init(Context context) {

        mGoogleApiClient = new GoogleApiClient.Builder(context).addApi(Awareness.API).build();
        mGoogleApiClient.connect();

        Intent intent = new Intent(StaticMembers.IntentFlags.FENCE_RECEIVER_ACTION);
        mFencePendingIntent = PendingIntent.getBroadcast(context, 10001, intent, 0);
    }

    public static void registerFences(ResultCallback<Status> callback) {

        AwarenessFence headphoneFence = HeadphoneFence.during(HeadphoneState.PLUGGED_IN);
        AwarenessFence runningFence = HeadphoneFence.during(DetectedActivityFence.RUNNING);
        AwarenessFence runningWithHeadphones = AwarenessFence.and(headphoneFence, runningFence);

        AwarenessFence headphoneRunningOrPluggedIn = AwarenessFence.or(runningWithHeadphones, headphoneFence);

        AwarenessFence withDriving = AwarenessFence.or(headphoneRunningOrPluggedIn, DetectedActivityFence.during(DetectedActivityFence.IN_VEHICLE));

        Awareness.FenceApi.updateFences(
                mGoogleApiClient,
                new FenceUpdateRequest.Builder()
                        .addFence(StaticMembers.FenceKeys.HEADPHONES_RUNNING_OR_PLUGGED_IN, withDriving, mFencePendingIntent)
                        .build())
                .setResultCallback(callback);
    }

    public static void unregisterFences(ResultCallback<Status> callback) {
        Awareness.FenceApi.updateFences(
                mGoogleApiClient,
                new FenceUpdateRequest.Builder()
                        .removeFence(StaticMembers.FenceKeys.HEADPHONES_RUNNING_OR_PLUGGED_IN)
                        .build()).setResultCallback(callback);
    }

    public static void addLocationFense(double longitude, double lattitude) {
        if (locationFence == null) {
            locationFence = LocationFence.in(lattitude, longitude, 1000, 20000);
        } else {
            AwarenessFence newFence = LocationFence.in(lattitude, longitude, 1000, 20000);
            locationFence = AwarenessFence.and(locationFence, newFence);
        }

        // Remove all fences and register the new ones.
        unregisterLocationFences(new ResultCallbacks<Status>() {
            @Override
            public void onSuccess(@NonNull Status status) {
                registerLocationFences(null);
            }

            @Override
            public void onFailure(@NonNull Status status) {

            }
        });
    }

    private static void registerLocationFences(ResultCallback<? super Status> callback) {
        Awareness.FenceApi.updateFences(
                mGoogleApiClient,
                new FenceUpdateRequest.Builder()
                        .addFence(StaticMembers.FenceKeys.LOCATION, locationFence, mFencePendingIntent)
                        .build())
                .setResultCallback(callback);
    }

    public static void unregisterLocationFences(ResultCallback<Status> callback) {
        Awareness.FenceApi.updateFences(
                mGoogleApiClient,
                new FenceUpdateRequest.Builder()
                        .removeFence(StaticMembers.FenceKeys.LOCATION)
                        .build()).setResultCallback(callback);
    }
}
