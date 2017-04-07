package edu.csulb.android.restofit.helpers;

import android.content.Context;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.common.api.GoogleApiClient;

public class AwarenessHelper {

    private static GoogleApiClient mGoogleApiClient;

    public static void init(Context context) {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(Awareness.API)
                .build();
        mGoogleApiClient.connect();
    }

}
