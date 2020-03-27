package com.hypertrack.live.utils;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class OnDeviceGeofence extends BroadcastReceiver {
    private static final String TAG = "OnDeviceGeofence";
    public static final float GEOFENCE_RADIUS = 100.0f;

    private static GeofencingRequest  getGeofencingRequest(double latitude, double longitude) {
        Log.d(TAG, String.format("addGeofence: lat %f, long %f", latitude, longitude));
        Geofence geofence = new Geofence.Builder()
                .setRequestId("home")
                .setCircularRegion(latitude, longitude, GEOFENCE_RADIUS)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build();

        GeofencingRequest request = new GeofencingRequest.Builder()
                .addGeofence(geofence)
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER | GeofencingRequest.INITIAL_TRIGGER_EXIT)
                .build();
        return request;

    }

    private static PendingIntent getGeofencePendingIntent(Context context) {
        // Reuse the PendingIntent if we already have it.
        PendingIntent mGeofencePendingIntent;

        Intent intent = new Intent(context, OnDeviceGeofence.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        mGeofencePendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
        return mGeofencePendingIntent;
    }

    public static void addGeofence(@NonNull final Context context, double latitude, double longitude) {

        GeofencingClient geofencingClient = LocationServices.getGeofencingClient(context);

        geofencingClient.addGeofences(getGeofencingRequest(latitude, longitude), getGeofencePendingIntent(context))
                .addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "onSuccess: geofence setup complete");
                                showToast(context, null);
                            }
                        }
                )
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "onFailure: geofence setup failed with error", e);
                        showToast(context, e);
                    }
                });

    }

    private static void showToast(@NonNull Context context, @Nullable Exception e) {
        String message = "Geofence was created successfully";
        if (e != null) {
            message = String.format("Can't add geofence due to error %s", e.getLocalizedMessage());
        }
        Toast.makeText(context, message, Toast.LENGTH_LONG)
                .show();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: ");

    }
}
