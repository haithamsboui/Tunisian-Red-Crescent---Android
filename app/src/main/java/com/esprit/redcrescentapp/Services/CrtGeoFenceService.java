package com.esprit.redcrescentapp.Services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.util.Log;

import com.esprit.redcrescentapp.R;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class CrtGeoFenceService extends IntentService {

    public static final String TRANSITION_INTENT_SERVICE = "ReceiveTransitionsIntentService";

    public CrtGeoFenceService() {
        super("TRANSITION_INTENT_SERVICE");
    }


    protected void onHandleIntent(Intent intent) {
        Log.d("GeoFence HANDLE", "E");
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            Log.e("Error Geo", "GEO");
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            Log.d("geofenceTransition", "GEOFENCE_TRANSITION_ENTER");

            List triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            /*
            // Get the transition details as a String.
            String geofenceTransitionDetails = getGeofenceTransitionDetails(this,
                    geofenceTransition,
                    triggeringGeofences
            );*/

            Notification n = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                n = new Notification.Builder(this)
                        .setContentTitle("Red Cresant nearby ! ")
                        .setContentText("Consider donating blood ?")
                        .setSmallIcon(R.drawable.accidenicon)
                        .setAutoCancel(false)
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .build();

            }

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(0, n);
        }
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL) {
            Log.d("geofenceTransition", "GEOFENCE_TRANSITION_DWELL");

        }
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            Log.d("geofenceTransition", "GEOFENCE_TRANSITION_EXIT");

        }
        Notification n = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            n = new Notification.Builder(this)
                    .setContentTitle("Red Cresant ! ")
                    .setContentText("Consider donating blood ?")
                    .setSmallIcon(R.drawable.accidenicon)
                    .setAutoCancel(false)
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .build();

        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, n);
    }

}
