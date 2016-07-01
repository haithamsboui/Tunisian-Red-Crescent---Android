package com.esprit.redcrescentapp.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.util.Log;

import com.esprit.redcrescentapp.activites.MainActivity;

/**
 * Created by haith on 27/12/2015.
 */
public class GpsLocationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {
            if (MainActivity.LocationDialog != null) {
                if (isGpsEnabled(context)) {
                    if (MainActivity.LocationDialog.isShowing())
                        MainActivity.LocationDialog.dismiss();
                } else {
                    if (!MainActivity.LocationDialog.isShowing())
                        MainActivity.LocationDialog.show();
                }
            }
        }
    }

    private boolean isGpsEnabled(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Log.d("GPS", "GPS : " + gps_enabled + " Network : " + network_enabled);
        return gps_enabled;
    }
}