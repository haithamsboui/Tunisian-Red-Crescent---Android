package com.esprit.redcrescentapp.Fragments;


import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.esprit.redcrescentapp.Handlers.SocketHandler;
import com.esprit.redcrescentapp.Handlers.WebService;
import com.esprit.redcrescentapp.R;
import com.esprit.redcrescentapp.Services.CrtGeoFenceService;
import com.esprit.redcrescentapp.activites.MainActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class MapFragment extends Fragment implements ResultCallback, GoogleMap.OnCameraChangeListener, GoogleMap.OnMarkerClickListener, OnMapReadyCallback, com.google.android.gms.location.LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static LocationRequest mLocationRequest;
    private static MapView mMapView;
    private static MarkerOptions markerOptions;
    private Marker Mymarker;
    private static GoogleMap googleMap;
    public GoogleApiClient mGoogleApiClient;
    private static Socket socket;

    private static HashMap<String, JSONObject> Members;
    private static HashMap<String, JSONObject> AccidentsMap;
    private static HashMap<String, Marker> MembersMarkers;
    private static HashMap<String, Marker> AccidentsMarkers;

    private static Fragment Main;
    private boolean IsShared = false;
    private ArrayList<Geofence> mGeofenceList = new ArrayList<>();
    private PendingIntent mGeofencePendingIntent;
    Bundle mBundle;
    AlertDialog.Builder alertDialogBuilderServer;
    AlertDialog alertDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBundle = savedInstanceState;

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map, container,
                false);

        Main = this;
        buildGoogleApiClient();
        socket = SocketHandler.getSocketInstance();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMapView.onCreate(mBundle);
        markerOptions = new MarkerOptions();

        Members = new HashMap<>();
        AccidentsMap = new HashMap<>();
        MembersMarkers = new HashMap<>();
        AccidentsMarkers = new HashMap<>();

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMaps) {
                googleMap = googleMaps;
                googleMap.setOnMarkerClickListener((GoogleMap.OnMarkerClickListener) Main);
                googleMap.setOnCameraChangeListener((GoogleMap.OnCameraChangeListener) Main);

                try {
                    LoadAccidentsMarkers();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    LoadCrtLocations();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //socket.emit("Members");

        socket.on("Members", onMembers);
        socket.on("Location", onLocation);
        socket.on("SharingON", onSharingON);
        socket.on("SharingOFF", onSharingOFF);
        socket.on("Accident", OnAccident);
        socket.on("disconnect", OnDisconnect);
        socket.on("connect", OnConnect);


        socket.on("AccidentHandled", onAccidentHandled);

        MainActivity.switchLoc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && (MainActivity.LastLocation != null)) {
                    try {
                        JSONObject jsonObject = new JSONObject("{\"Location\":{\"Longitude\":" + MainActivity.LastLocation.getLongitude() +
                                ",\"Latitude\":" + MainActivity.LastLocation.getLatitude() + ",\"Accuracy\":" + MainActivity.LastLocation.getAccuracy() +
                                ",\"Timestamp\":" + System.currentTimeMillis() + "}}");
                        socket.emit("SharingON", jsonObject);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                if (!isChecked) {
                    socket.emit("SharingOFF");
                    MainActivity.editor.putBoolean("Sharing", MainActivity.switchLoc.isChecked());
                    MainActivity.editor.commit();


                }
            }
        });
        alertDialogBuilderServer = new AlertDialog.Builder(
                MainActivity.Main);
        alertDialogBuilderServer.setTitle("Waiting for server ");

        alertDialogBuilderServer
                .setMessage("We're trying to connect to the server,if this is taking too long the server may be down.\nPlease try again later")
                .setCancelable(false);
        alertDialog = alertDialogBuilderServer.create();
        CheckServerStatus();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        mGoogleApiClient.connect();
        socket.emit("Members");


    }

    @Override
    public void onPause() {
        super.onPause();
          mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        MainActivity.Main.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.Main, "Couldn't not retrieve your location .", Toast.LENGTH_LONG).show();
            }
        });
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

        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
        MainActivity.LastLocation = location;
        JSONObject jsonObject = null;

        if (!IsShared && MainActivity.switchLoc.isChecked() && MainActivity.user != null) {
            try {
                if (MainActivity.user.getIsMember()) {
                    jsonObject = new JSONObject("{\"Location\":{\"Longitude\":" + location.getLongitude() +
                            ",\"Latitude\":" + location.getLatitude() + ",\"Accuracy\":" + location.getAccuracy() +
                            ",\"Timestamp\":" + System.currentTimeMillis() + "}}");
                    socket.emit("SharingON", jsonObject);
                    IsShared = true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        markerOptions.position(new LatLng(latitude, longitude)).title("Me");
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.male_shadow));

        if (Mymarker == null) {
            Mymarker = googleMap.addMarker(markerOptions);
            Mymarker.setPosition(new LatLng(latitude, longitude));
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(latitude, longitude)).zoom(9).build();
            googleMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));
        } else {
            Mymarker.setPosition(new LatLng(latitude, longitude));
        }

        if (MainActivity.switchLoc.isChecked()) {
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

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }


    //TODO
    @Override
    public void onMapReady(GoogleMap googleMap) {
    }

    private Emitter.Listener OnConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (alertDialog.isShowing()) {
                MainActivity.Main.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        alertDialog.dismiss();
                    }
                });
            }
        }

    };
    private Emitter.Listener OnDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (!alertDialog.isShowing()) {
                MainActivity.Main.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        alertDialog.show();
                    }
                });
            }        }

    };

    private Emitter.Listener onAccidentHandled = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            final JSONObject jsonObject = (JSONObject) args[0];
            MainActivity.Main.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (AccidentsMarkers.containsKey(jsonObject.getString("id"))) {
                            if (AccidentsMarkers.get(jsonObject.getString("id")) != null) {
                                AccidentsMarkers.get(jsonObject.getString("id")).remove();
                            }
                            AccidentsMarkers.remove(jsonObject.getString("id"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        }

    };


    private Emitter.Listener OnAccident = new Emitter.Listener() {
        @Override
        public void call(Object... args) {


            final JSONObject item = (JSONObject) args[0];

            if (googleMap != null) {
                try {
                    AccidentsMap.put(item.getString("id"), item);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                final MarkerOptions accMarkerOpt = new MarkerOptions();
                try {
                    accMarkerOpt.position(new LatLng(item.getJSONObject("Location").getDouble("Latitude"), item.getJSONObject("Location").getDouble("Longitude")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    accMarkerOpt.title(item.getString("id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                accMarkerOpt.icon(BitmapDescriptorFactory.fromResource(R.drawable.headinjuryicon));
                MainActivity.Main.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Marker acc = googleMap.addMarker(accMarkerOpt);
                        try {
                            AccidentsMarkers.put(item.getString("id"), acc);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }


            final AlertDialog.Builder alertDialogBuilderAcc = new AlertDialog.Builder(
                    MainActivity.Main);
            alertDialogBuilderAcc.setTitle("A new Accident");

            alertDialogBuilderAcc
                    .setMessage("Check it ?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, int id) {
                            try {

                                CameraPosition cameraPosition = new CameraPosition.Builder()
                                        .target(new LatLng(item.getJSONObject("Location").getDouble("Latitude"), item.getJSONObject("Location").getDouble("Longitude"))).zoom(12).build();
                                googleMap.animateCamera(CameraUpdateFactory
                                        .newCameraPosition(cameraPosition));

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            MainActivity.Main.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    AlertDialog alertDialogAcc = alertDialogBuilderAcc.create();
                    alertDialogAcc.show();
                }
            });
        }
    };

    private static Emitter.Listener onMembers = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            final JSONArray jsonArray = (JSONArray) args[0];

            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        final JSONObject item = jsonArray.getJSONObject(i);

                        if (MainActivity.user != null) {
                            Log.d("mm",item.getString("id")+" "+socket.id());
                            Log.d("mm",String.valueOf(item.getString("id").equals(socket.id())));
                            if (!item.getString("id").equals(socket.id()))
                                Members.put(item.getString("id"), item.getJSONObject("Location"));
                        }
                        else
                            Members.put(item.getString("id"), item.getJSONObject("Location"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
            try {
                Log.d("mm",Members.size()+" Array "+jsonArray.length());
                LoadMembersMarkers();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private static Emitter.Listener onSharingON = new Emitter.Listener() {

        @Override
        public void call(Object... args) {

            try {
                final JSONObject jsonObject = (JSONObject) args[0];
                LatLng LL = new LatLng(jsonObject.getJSONObject("Location").getDouble("Latitude"), jsonObject.getJSONObject("Location").getDouble("Latitude"));
                final MarkerOptions mo = new MarkerOptions();
                mo.position(LL);
                mo.icon(BitmapDescriptorFactory.fromResource(R.drawable.member_shadow));
                mo.title("RCT member");
                MainActivity.Main.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (!SocketHandler.getSocketInstance().id().equals(jsonObject.getString("id"))) {
                                if (MainActivity.user != null) {
                                    if (!jsonObject.getString("id").equals(socket.id()))
                                        MembersMarkers.put(jsonObject.getString("id"), googleMap.addMarker(mo));
                                } else {
                                    MembersMarkers.put(jsonObject.getString("id"), googleMap.addMarker(mo));

                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };
    private static Emitter.Listener onSharingOFF = new Emitter.Listener() {

        @Override
        public void call(Object... args) {

            final JSONObject jsonObject = (JSONObject) args[0];
            try {
                if (Members.containsKey(jsonObject.getString("id"))) {
                   JSONObject o = Members.remove(jsonObject.getString("id"));

                }
                MainActivity.Main.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (MembersMarkers.containsKey(jsonObject.getString("id"))) {

                                if (MembersMarkers.get(jsonObject.getString("id")) != null) {
                                    Log.d("SharingOFF", String.valueOf(MembersMarkers.get(jsonObject.getString("id"))==null)+" "+jsonObject.getString("id"));
                                    MembersMarkers.get(jsonObject.getString("id")).remove();
                                }
                                Marker m=MembersMarkers.remove(jsonObject.getString("id"));
                                m.remove();


                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
    private static Emitter.Listener onLocation = new Emitter.Listener() {

        @Override
        public void call(Object... args) {

            final JSONObject jsonObject = (JSONObject) args[0];
            MainActivity.Main.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {


                        if (MembersMarkers.get(jsonObject.getString("id")) != null) {
                            if (MembersMarkers.containsKey(jsonObject.getString("id"))) {
                               /* MembersMarkers.get(jsonObject.getString("id")).remove();
                                MembersMarkers.remove(jsonObject.getString("id"));
                                final MarkerOptions mo = new MarkerOptions();
                                mo.position(new LatLng(jsonObject.getJSONObject("Location").getDouble("Latitude"), jsonObject.getJSONObject("Location").getDouble("Longitude")));
                                mo.title("RCT member");
                                mo.icon(BitmapDescriptorFactory.fromResource(R.drawable.member_shadow));
                                MembersMarkers.put(jsonObject.getString("id"), googleMap.addMarker(mo));*/
                                MembersMarkers.get(jsonObject.getString("id")).setPosition(new LatLng(jsonObject.getJSONObject("Location").getDouble("Latitude"), jsonObject.getJSONObject("Location").getDouble("Longitude")));
                            }
                        }
                        Members.put(jsonObject.getString("id"), jsonObject.getJSONObject("Location"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    @Override
    public boolean onMarkerClick(final Marker marker) {

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(marker.getPosition()).zoom(12).build();
        googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
        if (AccidentsMarkers.containsValue(marker)) {


            // custom dialog
            final Dialog dialogA = new Dialog(MainActivity.Main);
            dialogA.setContentView(R.layout.custom_dialog);
            dialogA.setTitle("Confirm help ?");
            dialogA.setCancelable(false);
            // set the custom dialog components - text, image and button
            TextView text = (TextView) dialogA.findViewById(R.id.text);
            try {
                text.setText(AccidentsMap.get(marker.getTitle()).getString("Description"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            final ImageView[] image = {(ImageView) dialogA.findViewById(R.id.image)};

            if (AccidentsMap.get(marker.getTitle()).has("ImageFile")) {
                String ImageUrl = null;
                try {
                    ImageUrl = AccidentsMap.get(marker.getTitle()).getString("ImageFile").replace("\\/", "\\");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                URL url = null;
                try {
                    url = new URL(ImageUrl);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                final URL finalUrl = url;
                final Bitmap[] bi = new Bitmap[1];
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                      bi[0] =BitmapFactory.decodeStream(finalUrl.openConnection().getInputStream());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                            MainActivity.Main.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    image[0].setImageBitmap(bi[0]);

                                }
                            });

                    }
                });

            }
            else
            image[0].setVisibility(View.GONE);


            Button dialogButtonOK = (Button) dialogA.findViewById(R.id.dialogButtonOK);
            Button dialogButtonNo = (Button) dialogA.findViewById(R.id.dialogButtonNo);
            // if button is clicked, close the custom dialog
            dialogButtonNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogA.dismiss();
                }
            });
            dialogButtonOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {
                        WebService.ConfirmAccident(marker.getTitle(), new WebService.GetAccidentConfirmCallback() {
                            @Override
                            public void Sucess() {

                                MainActivity.Main.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(MainActivity.Main, "Thank you :)", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                dialogA.dismiss();

                            }

                            @Override
                            public void Fail() {
                                MainActivity.Main.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(MainActivity.Main, "Sorry we couldn't handle this !", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                dialogA.dismiss();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });

            dialogA.show();



           /* final AlertDialog.Builder alertDialogBuilderHanAcc = new AlertDialog.Builder(
                    MainActivity.Main);
            alertDialogBuilderHanAcc.setTitle("Confirm help ?");

            try {
                alertDialogBuilderHanAcc
                        .setMessage(AccidentsMap.get(marker.getTitle()).getString("Description"))
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, int id) {
                                try {
                                    WebService.ConfirmAccident(marker.getTitle(), new WebService.GetAccidentConfirmCallback() {
                                        @Override
                                        public void Sucess() {

                                            MainActivity.Main.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(MainActivity.Main, "Thank you :)", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                            dialog.cancel();

                                        }

                                        @Override
                                        public void Fail() {
                                            MainActivity.Main.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(MainActivity.Main, "Sorry we couldn't handle this !", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                            dialog.cancel();
                                        }
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }

                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
            } catch (JSONException e) {
                e.printStackTrace();
            }
            MainActivity.Main.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    AlertDialog alertDialog = alertDialogBuilderHanAcc.create();
                    alertDialog.show();
                }
            });*/
        } else
            marker.showInfoWindow();
        return true;
    }


    @Override
    public void onStart() {
        super.onStart();
    }
/*
    @Override
    public void onMapLongClick(LatLng latLng) {
        if (MainActivity.user == null)
            Toast.makeText(getActivity(), "Please log in first ! ", Toast.LENGTH_LONG).show();
        else {
            Fragment fragment = null;
            Class fragmentClass = ReportAccidentFragment.class;
            try {
                fragmentClass.newInstance();
            } catch (java.lang.InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (java.lang.InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            MainActivity.manager.beginTransaction().replace(R.id.include_frame, fragment).commit();

        }
    }*/

    public static void LoadMembersMarkers() throws JSONException {
        Iterator i = Members.entrySet().iterator();
        for (final Map.Entry<String, JSONObject> item : Members.entrySet()) {
            final MarkerOptions mo = new MarkerOptions();
            mo.position(new LatLng(item.getValue().getDouble("Latitude"), item.getValue().getDouble("Longitude")));
        mo.title("RCT member");
        mo.icon(BitmapDescriptorFactory.fromResource(R.drawable.member_shadow));
            Main.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    MembersMarkers.put(item.getKey(), googleMap.addMarker(mo));
                    Log.d("Member Added",String.valueOf(MembersMarkers.get(item.getKey())== null)+ item.getKey());

                }
            });
        }

    }

    public void LoadAccidentsMarkers() throws Exception {
        WebService.GetAccidents(new WebService.GetAccidentCallback() {
            @Override
            public void Received(final JSONObject Accidents) throws JSONException {
                Log.d("Acc",Accidents.toString());

                for (int i = 0; i < Accidents.length(); i++) {
                    if (!Accidents.getJSONArray("accidents").getJSONObject(i).getBoolean("IsHandled")) {

                        AccidentsMap.put(Accidents.getJSONArray("accidents").getJSONObject(i).getString("id"), Accidents.getJSONArray("accidents").getJSONObject(i));

                        final MarkerOptions mo = new MarkerOptions();
                        mo.position(new LatLng(Accidents.getJSONArray("accidents").getJSONObject(i).getJSONObject("Location").getDouble("Latitude"),
                                Accidents.getJSONArray("accidents").getJSONObject(i).getJSONObject("Location").getDouble("Longitude")));

                        mo.title(Accidents.getJSONArray("accidents").getJSONObject(i).getString("id"));
                        mo.icon(BitmapDescriptorFactory.fromResource(R.drawable.headinjuryicon));
                        final int index = i;
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    AccidentsMarkers.put(Accidents.getJSONArray("accidents").getJSONObject(index).getString("id"), googleMap.addMarker(mo));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    public void LoadCrtLocations() throws Exception {
        //   mGoogleApiClient.connect();
        final ResultCallback rc = this;
        WebService.getCRTLocation(new WebService.getCRTLocationCallback() {
            @Override
            public void onSuccess(JSONArray jsonArray) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    //mGeofenceList.add(getGeofence(loc.latitude, loc.longitude));
                    final MarkerOptions mo = new MarkerOptions();
                    LatLng latlng = null;
                    try {
                        latlng = new LatLng(jsonArray.getJSONObject(i).getJSONObject("Location").getDouble("Latitude"), jsonArray.getJSONObject(i).getJSONObject("Location").getDouble("Longitude"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mo.position(latlng);
                    try {
                        mo.title(jsonArray.getJSONObject(i).getString("Title"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    mo.icon(BitmapDescriptorFactory.fromResource(R.drawable.redcres1));
                    MainActivity.Main.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            googleMap.addMarker(mo);
                        }
                    });
                }
                /* mGoogleApiClient.connect();
                mGeofencePendingIntent = getGeofencePendingIntent();
                LocationServices.GeofencingApi.addGeofences(
                        mGoogleApiClient,
                        getGeofencingRequest(),
                        mGeofencePendingIntent
                ).setResultCallback(rc);*/
            }


        });
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
    }

    public Geofence getGeofence(double latitude, double longitude) {
        String id = UUID.randomUUID().toString();
        return new Geofence.Builder()

                .setRequestId(id)
                .setCircularRegion(
                        latitude,
                        longitude,
                        5000
                )
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .build();
    }

    public GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(getActivity(), CrtGeoFenceService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        return PendingIntent.getService(getActivity(), 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onResult(Result result) {

    }

    public void CheckServerStatus() {

        if (!socket.connected() && !alertDialog.isShowing()) {
            MainActivity.Main.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    alertDialog.show();
                }
            });
        }

    }
}




