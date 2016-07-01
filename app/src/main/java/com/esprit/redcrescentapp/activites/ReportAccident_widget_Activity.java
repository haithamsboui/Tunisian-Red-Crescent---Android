package com.esprit.redcrescentapp.activites;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.esprit.redcrescentapp.Handlers.WebService;
import com.esprit.redcrescentapp.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class ReportAccident_widget_Activity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener, com.google.android.gms.location.LocationListener {

    EditText TxDescription;
    Button BtSend;
    boolean IsOnline = true;
    AppCompatActivity THIS = this;
    public GoogleApiClient mGoogleApiClient;
    public Location mLastLocation;
    private LocationRequest mLocationRequest;
    public static AlertDialog LocationDialog;

    public static String BROADCAST_ACTION =
            "android.location.PROVIDERS_CHANGED";
    BroadcastReceiver br = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {
                if (LocationDialog != null) {
                    if (isGpsEnabled()) {
                        if (LocationDialog.isShowing())
                            LocationDialog.dismiss();
                    } else {
                        if (!LocationDialog.isShowing())
                            LocationDialog.show();
                    }
                }
            }
        }
    };

    public void sendBroadcast() {
        Intent broadcast = new Intent();
        broadcast.setAction(BROADCAST_ACTION);
        broadcast.addCategory(Intent.CATEGORY_DEFAULT);
        sendBroadcast(broadcast);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IntentFilter filter = new IntentFilter();
        filter.addAction(BROADCAST_ACTION);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(br, filter);

        setContentView(R.layout.activity_report_accident_widget_);
        AlertDialog.Builder LocationDialogBuild = new AlertDialog.Builder(
                this);
        LocationDialogBuild.setTitle("Info");


        LocationDialogBuild.setMessage("Please enable location sharing")
                .setCancelable(false);
        LocationDialog = LocationDialogBuild.create();

        if (!isGpsEnabled()) {
            LocationDialog = LocationDialogBuild.create();
            LocationDialog.show();
        }

        TxDescription = (EditText) findViewById(R.id.TxDescriptionWidget);
        BtSend = (Button) findViewById(R.id.BtSendWidget);
        BtSend.setOnClickListener(this);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                IsOnline = hasInternetAccess();
            }
        });
        buildGoogleApiClient();
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == BtSend.getId()) {

            if (mLastLocation != null) {
               /* if (IsOnline) {
                    WebService.ReportAccidentWidget(TxDescription.getText().toString(), mLastLocation, new WebService.ReportAccidentWidgetCallback() {
                        @Override
                        public void OnSucces() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplication(), "Thank you for the help :)", Toast.LENGTH_LONG).show();
                                }
                            });
                        }

                        @Override
                        public void OnFail() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplication(), "Sorry we couldn't report that :(", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    });
                } else {*/
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            this);
                    alertDialogBuilder.setTitle("Confirm");
                    alertDialogBuilder
                            .setMessage("Extra charges are applied (0.15DT) .Confirm ?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    if (sendSMSMessage()) {
                                        Toast.makeText(getApplicationContext(), "Thank you for the help :)", Toast.LENGTH_LONG);
                                        THIS.finish();
                                    } else
                                        Toast.makeText(getApplicationContext(), "Sorry we couldn't report that :(", Toast.LENGTH_LONG);

                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();


            }
        }
    }


    public boolean hasInternetAccess() {
        if (isNetworkAvailable()) {
            try {
                HttpURLConnection urlc = (HttpURLConnection)
                        (new URL("http://clients3.google.com/generate_204")
                                .openConnection());
                urlc.setRequestProperty("User-Agent", "Android");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1500);
                urlc.connect();
                return (urlc.getResponseCode() == 204 &&
                        urlc.getContentLength() == 0);
            } catch (IOException e) {
                Log.e("Internet", "Error checking internet connection", e);
            }
        } else {
            Log.d("Internet", "No network available!");
        }
        return false;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    private boolean isGpsEnabled() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
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

    protected boolean sendSMSMessage() {
        Log.i("Send SMS", "");
        String phoneNo = "+18704556288";
        String message;
        if (mLastLocation != null)
            message = "\"d\":\"" + TxDescription.getText().toString() + ",\"x\":\"" + mLastLocation.getLongitude()
                    + ",\"y\":\"" + mLastLocation.getLatitude() + ",\"t\":\"" + System.currentTimeMillis() + "\"";
        else
            return false;
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, message, null, null);
            Toast.makeText(this, "Accident reported !", Toast.LENGTH_LONG).show();
            return true;
        } catch (Exception e) {
            Toast.makeText(this, "An error has occured ", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {
        createLocationRequest();
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
    }

    /*@Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
*/

}







