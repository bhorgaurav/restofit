package edu.csulb.android.restofit.helpers;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.fence.AwarenessFence;
import com.google.android.gms.awareness.fence.DetectedActivityFence;
import com.google.android.gms.awareness.fence.FenceUpdateRequest;
import com.google.android.gms.awareness.fence.HeadphoneFence;
import com.google.android.gms.awareness.state.HeadphoneState;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

public class AwarenessHelper {

    private static GoogleApiClient mGoogleApiClient;
    private static PendingIntent mFencePendingIntent;

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
}
