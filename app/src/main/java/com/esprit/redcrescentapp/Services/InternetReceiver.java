package com.esprit.redcrescentapp.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.esprit.redcrescentapp.activites.MainActivity;

public class InternetReceiver extends BroadcastReceiver {
    public InternetReceiver() {
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().matches("android.net.conn.CONNECTIVITY_CHANGE")) {
            if (MainActivity.InternetDialog != null) {

                if (isOnline(context)) {
                    if (MainActivity.InternetDialog.isShowing()) {
                        MainActivity.InternetDialog.dismiss();
                        MainActivity.RefreshMap();
                    }
                } else {
                    if (!MainActivity.InternetDialog.isShowing())
                        MainActivity.InternetDialog.show();
                }
            }
            if (MyService.service != null) {

                if (isOnline(context)) {
                    {
                        //   MyService.Relogin();
                    }
                }
            }
        }
    }

    public boolean isOnline(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in air plan mode it will be null
        return (netInfo != null && netInfo.isConnected());

    }
}

