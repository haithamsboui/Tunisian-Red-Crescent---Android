package com.esprit.redcrescentapp.Services;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.esprit.redcrescentapp.Handlers.SocketHandler;
import com.esprit.redcrescentapp.R;
import com.esprit.redcrescentapp.activites.MainActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MyService extends Service implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    Socket socket;
    public static Service service = null;
    static String Token;
    public static SharedPreferences sharedpreferences;
    private static LocationRequest mLocationRequest;
    public GoogleApiClient mGoogleApiClient;

    private boolean IsShared = false;
    boolean shared = false;

    public MyService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        service = this;
        Token = sharedpreferences.getString("access_token", "");
        socket.emit("access_token", Token);
        socket.on("Message", OnMessage);
        socket.on("Accident", OnAccident);

        return START_STICKY;
    }

    @Override
    public void onCreate() {

        super.onCreate();

        sharedpreferences = getSharedPreferences("RedCrecent", Context.MODE_PRIVATE);
        IsShared = sharedpreferences.getBoolean("Sharing", false);
        if (IsShared) {
            buildGoogleApiClient();
            mGoogleApiClient.connect();
        }

        socket = SocketHandler.getSocketInstance();
    }

    @Override
    public void onDestroy() {
        socket.emit("SharingOFF");
        super.onDestroy();

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private Emitter.Listener OnMessage = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            long[] vibraPattern = {0, 500, 250, 500};
            Notification n = null;
            Intent intent = new Intent(service, MainActivity.class);
            PendingIntent activity = PendingIntent.getActivity(getApplication(), 0, intent, 0);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                n = new Notification.Builder(service)
                        .setContentTitle("Alert message !")
                        .setSmallIcon(R.drawable.headinjuryicon)
                        .setAutoCancel(false)
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .build();

            }

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(0, n);
        }


    };
    private Emitter.Listener OnAccident = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            long[] vibraPattern = {0, 500, 250, 500};
            Notification n = null;
            Intent intent = new Intent(service, MainActivity.class);
            PendingIntent activity = PendingIntent.getActivity(getApplication(), 0, intent, 0);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                n = new Notification.Builder(service)
                        .setContentTitle("A nearby accident !")
                        .setSmallIcon(R.drawable.headinjuryicon)
                        .setAutoCancel(false)
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .build();

            }

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(0, n);
        }


    };

    @Override
    public void onLocationChanged(Location location) {
        JSONObject jsonObject = null;
        if (IsShared && !shared) {
            try {
                jsonObject = new JSONObject("{\"Location\":{\"Longitude\":" + location.getLongitude() +
                        ",\"Latitude\":" + location.getLatitude() + ",\"Accuracy\":" + location.getAccuracy() +
                        ",\"Timestamp\":" + System.currentTimeMillis() + "}}");
                socket.emit("SharingON", jsonObject);
                shared = true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (IsShared) {
            try {
                jsonObject = new JSONObject("{\"Location\":{\"Longitude\":" + location.getLongitude() +
                        ",\"Latitude\":" + location.getLatitude() + ",\"Accuracy\":" + location.getAccuracy() +
                        ",\"Timestamp\":" + System.currentTimeMillis() + "}}");
                socket.emit("Location", jsonObject);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdates();
        Log.d("Service", "Conn");

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("Service", "Conn sus");

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("Service", "Conn fail");

    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(25000);
        mLocationRequest.setFastestInterval(25000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {
        createLocationRequest();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    /*public static void Relogin() {
        Token = sharedpreferences.getString("access_token", "");
        socket = SocketHandler.getSocketInstance();
        socket.emit("access_token", Token);

    }*/
/*
   @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }*/


}
