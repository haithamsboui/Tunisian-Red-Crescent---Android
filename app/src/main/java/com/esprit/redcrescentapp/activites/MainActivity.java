package com.esprit.redcrescentapp.activites;


import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.esprit.redcrescentapp.Fragments.EmergencyguideFagment;
import com.esprit.redcrescentapp.Fragments.LoginFragment;
import com.esprit.redcrescentapp.Fragments.MapFragment;
import com.esprit.redcrescentapp.Fragments.NewsFeedClientFragment;
import com.esprit.redcrescentapp.Fragments.NewsfeedFragment;
import com.esprit.redcrescentapp.Fragments.ProfileFragment;
import com.esprit.redcrescentapp.Fragments.ReportAccidentFragment;
import com.esprit.redcrescentapp.Fragments.UpdateGooglePlay_Fragement;
import com.esprit.redcrescentapp.Handlers.SocketHandler;
import com.esprit.redcrescentapp.Handlers.WebService;
import com.esprit.redcrescentapp.R;
import com.esprit.redcrescentapp.Services.MyService;
import com.esprit.redcrescentapp.entities.User;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Bundle myState;
    public static Activity Main;
    private final OkHttpClient client = new OkHttpClient();
    public static CallbackManager callbackManager;
    public static FragmentManager manager;

    public static String Token;
    public static Switch switchLoc;
    private String UserId;
    String url;
    String json;

    private EditText TxUsername;
    private EditText TxPassword;
    public static LoginButton loginButton;
    public static ImageView UserImage;
    private DrawerLayout mDrawerLayout;
    NavigationView nvDrawer;
    public static TextView Username_Txt;
    Toolbar toolbar;

    public static SharedPreferences sharedpreferences;
    public static SharedPreferences.Editor editor;
    Socket socket;
    public static User user;

    public static Location LastLocation;
    boolean Loaded = false;

    public static AlertDialog LocationDialog;
    public static AlertDialog InternetDialog;
    public static AlertDialog DisconnectDialog;
    public static AlertDialog SocketDialog;
    private boolean IsOnMap = true;


    public void launchService() {
        if (user != null) {
            if (user.getIsMember()) {
                Intent i = new Intent(this, MyService.class);
                startService(i);
            }
        }
    }

    public void StopService() {
        if (isMyServiceRunning(MyService.class)) {
            Intent i = new Intent(this, MyService.class);
            stopService(i);
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        myState = savedInstanceState;
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getLayoutInflater().setFactory(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StopService();

        AlertDialog.Builder InternetDialogBuild = new AlertDialog.Builder(
                this);
        InternetDialogBuild.setTitle("Info");
        InternetDialogBuild.setMessage("You are Offline ! You can use our widget to report an accident (via sms)")
                .setCancelable(false);
        InternetDialog = InternetDialogBuild.create();

        if (!isOnline(this)) {
            InternetDialog = InternetDialogBuild.create();
            InternetDialog.show();
        }

        AlertDialog.Builder DisconnectDialogBuild = new AlertDialog.Builder(
                this);
        DisconnectDialogBuild.setTitle("Info");


        DisconnectDialogBuild.setMessage("Couldn't connect to server ! please hold on")
                .setCancelable(false);
        DisconnectDialog = DisconnectDialogBuild.create();

        AlertDialog.Builder SocketDialogBuild = new AlertDialog.Builder(
                this);
        SocketDialogBuild.setTitle("Info");


        SocketDialogBuild.setMessage("Connecting to server ...")
                .setCancelable(false);
        SocketDialog = SocketDialogBuild.create();
        if (socket != null) {
            if (!socket.connected())
                SocketDialog.show();
        }

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


        Main = this;
        socket = SocketHandler.getSocketInstance();

        Initialise();

        switchLoc = (Switch) findViewById(R.id.switch1);
        switchLoc.setChecked(false);
        switchLoc.setVisibility(View.INVISIBLE);

        Fragment fragment = null;
        Class fragmentClass;
        if (checkGoogleService())
            fragmentClass= MapFragment.class;
        else
             fragmentClass = UpdateGooglePlay_Fragement.class;

        try {
            fragmentClass.newInstance();
            fragment = (Fragment) fragmentClass.newInstance();

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        manager.beginTransaction().add(R.id.include_frame, fragment).commit();

        socket.on("Accident", OnAccident);
        socket.on("Message", OnMessage);
        socket.on("disconnect", OnDisconnect);
        socket.on("connect", OnConnect);

    }

    private Emitter.Listener OnAccident = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            final JSONObject item = (JSONObject) args[0];
            if (!IsOnMap) {

                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        Main);
                alertDialogBuilder.setTitle("A new Accident");

                alertDialogBuilder
                        .setMessage("Switch to map ?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, int id) {
                                try {
                                    Fragment fragment = null;
                                    Class fragmentClass ;
                                    if (checkGoogleService())
                                        fragmentClass= MapFragment.class;
                                    else
                                        fragmentClass = UpdateGooglePlay_Fragement.class;
                                    fragmentClass.newInstance();
                                    fragment = (Fragment) fragmentClass.newInstance();
                                    dialog.dismiss();
                                    manager.beginTransaction().replace(R.id.include_frame, fragment).commit();
                                    IsOnMap = true;


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
                Main.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                });

            }
        }

    };

    private Emitter.Listener OnMessage = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    Main);
            alertDialogBuilder.setTitle("New message");

            alertDialogBuilder
                    .setMessage("Check Inbox ?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, int id) {
                            try {
                                IsOnMap = false;

                                Fragment fragment = null;
                                Class fragmentClass = NewsfeedFragment.class;
                                fragmentClass.newInstance();
                                fragment = (Fragment) fragmentClass.newInstance();
                                dialog.dismiss();
                                manager.beginTransaction().replace(R.id.include_frame, fragment).commit();
                            } catch (Exception e) {
                            }
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            Main.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            });

        }


    };

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        Fragment fragment = null;
        Class fragmentClass = null;
        switch (menuItem.getItemId()) {
            case R.id.nav_home: {
                if (checkGoogleService())
                    fragmentClass= MapFragment.class;
                else
                    fragmentClass = UpdateGooglePlay_Fragement.class;
                IsOnMap = true;
            }
            break;
            case R.id.nav_Login: {
                IsOnMap = false;

                fragmentClass = LoginFragment.class;
            }
            break;
            case R.id.nav_Profile: {
                IsOnMap = false;

                fragmentClass = ProfileFragment.class;
            }

            break;
            case R.id.news_feed: {
                IsOnMap = false;

                if (user != null) {
                    if (user.getIsMember())
                        fragmentClass = NewsfeedFragment.class;
                    else
                        fragmentClass = NewsFeedClientFragment.class;

                }
            }

            break;
            case R.id.nav_sos: {
                IsOnMap = false;

                fragmentClass = EmergencyguideFagment.class;
            }
            break;
            case R.id.nav_report_accident: {
                IsOnMap = false;

                fragmentClass = ReportAccidentFragment.class;
            }

            break;
            case R.id.logout: {
                IsOnMap = true;
                Logout();
                nvDrawer.getMenu().setGroupVisible(R.id.drawer_anonym, true);
                nvDrawer.getMenu().setGroupVisible(R.id.drawer_conneted, false);
                if (checkGoogleService())
                    fragmentClass= MapFragment.class;
                else
                    fragmentClass = UpdateGooglePlay_Fragement.class;
            }
            break;

        }
        try {
            fragmentClass.newInstance();
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        manager.beginTransaction().replace(R.id.include_frame, fragment).commit();
        menuItem.setChecked(true);
        //setTitle(menuItem.getTitle());
        mDrawerLayout.closeDrawers();

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                if (!Loaded) {
                    UserImage = (ImageView) findViewById(R.id.UserImage);
                    Username_Txt = (TextView) findViewById(R.id.UserName);
                    SetProfileImage();
                    Loaded = true;
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static void SetProfileImage() {
        if (UserImage != null) {
            if (user != null) {
                Picasso.with(Main).load(user.getImageFile()).into(UserImage);
                Username_Txt.setText(user.getFirstName() + " " + user.getLastName());
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onClick(View v) {
        final ProgressDialog dialog = new ProgressDialog(MainActivity.this);
        dialog.setMessage("Please wait!");
        dialog.show();
        switch (v.getId()) {
            case R.id.BtLogin: {
                url = "https://crt-server-ibicha.c9users.io/api/v1/authenticate";
                TxUsername = (EditText) findViewById(R.id.TxUsername);
                TxPassword = (EditText) findViewById(R.id.TxPassword);
                json = "{\"Email\" : \"" + TxUsername.getText().toString() + "\",\"Password\" :\"" + TxPassword.getText().toString() + "\"}";
                try {
                    post(url, json, new Callback() {
                        @Override
                        public void onFailure(Request request, IOException e) {
                            Main.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(Main, "Error Connecting to server,please try again later", Toast.LENGTH_LONG).show();
                                    dialog.dismiss();

                                }
                            });
                        }

                        @Override
                        public void onResponse(Response response) throws IOException {
                            try {
                                JSONObject jsonObj = new JSONObject(response.body().string());

                                if (jsonObj.getBoolean("success")) {
                                    Token = jsonObj.getString("token");
                                    editor.putString("access_token", Token);
                                    editor.putString("UserID", jsonObj.getString("UserID"));
                                    editor.commit();
                                    socket.emit("access_token", Token);
                                    Log.d("Token", Token);


                                    Main.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                WebService.GetUser(new WebService.IGetUserCallback() {
                                                    @Override
                                                    public void onUser(User user1) {
                                                        user = user1;
                                                        if (user.getIsMember()) {
                                                            switchLoc.setChecked(true);
                                                            switchLoc.setVisibility(View.VISIBLE);
                                                        }
                                                        SetProfileImage();

                                                    }
                                                });
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                            nvDrawer.getMenu().setGroupVisible(R.id.drawer_anonym, false);
                                            nvDrawer.getMenu().setGroupVisible(R.id.drawer_conneted, true);
                                            try {
                                                Fragment fragment = null;
                                                Class fragmentClass ;
                                                if (checkGoogleService())
                                                    fragmentClass= MapFragment.class;
                                                else
                                                    fragmentClass = UpdateGooglePlay_Fragement.class;
                                                fragmentClass.newInstance();
                                                fragment = (Fragment) fragmentClass.newInstance();
                                                dialog.dismiss();
                                                manager.beginTransaction().replace(R.id.include_frame, fragment).commit();
                                            } catch (Exception e) {
                                            }
                                        }
                                    });
                                } else {
                                    Main.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(Main, "Invalid ID/Password !", Toast.LENGTH_LONG).show();
                                            dialog.dismiss();

                                        }
                                    });
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Main.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(Main, "Error Connecting to server,please try again later", Toast.LENGTH_LONG).show();
                                        dialog.dismiss();

                                    }
                                });
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            break;
            case R.id.BtLoginFacebook: {

                loginButton = (LoginButton) findViewById(R.id.BtLoginFacebook);
                loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        try {
                            url = "https://crt-server-ibicha.c9users.io/api/v1/facebookauth?access_token=" + loginResult.getAccessToken().getToken();
                            get(url, new Callback() {
                                @Override
                                public void onFailure(Request request, IOException e) {
                                }

                                @Override
                                public void onResponse(Response response) throws IOException {
                                    String r = response.body().string();
                                    try {
                                        JSONObject jsonObj = new JSONObject(r);
                                        if (jsonObj.getBoolean("success")) {
                                            Token = jsonObj.getString("token");
                                            editor.putString("access_token", Token);
                                            Log.d("Token", Token);
                                            editor.putString("UserID", jsonObj.getString("UserID"));
                                            editor.commit();
                                            socket.emit("access_token", Token);

                                            Main.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        WebService.GetUser(new WebService.IGetUserCallback() {
                                                            @Override
                                                            public void onUser(User user1) {
                                                                user = user1;
                                                                if (user.getIsMember()) {
                                                                    switchLoc.setChecked(true);
                                                                    switchLoc.setVisibility(View.VISIBLE);
                                                                }
                                                                SetProfileImage();

                                                            }
                                                        });
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }


                                                    nvDrawer.getMenu().setGroupVisible(R.id.drawer_anonym, false);
                                                    nvDrawer.getMenu().setGroupVisible(R.id.drawer_conneted, true);
                                                    try {
                                                        Fragment fragment = null;
                                                        Class fragmentClass ;
                                                        if (checkGoogleService())
                                                            fragmentClass= MapFragment.class;
                                                        else
                                                            fragmentClass = UpdateGooglePlay_Fragement.class;
                                                        fragmentClass.newInstance();
                                                        fragment = (Fragment) fragmentClass.newInstance();
                                                        dialog.dismiss();
                                                        manager.beginTransaction().replace(R.id.include_frame, fragment).commit();
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            });
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onError(FacebookException e) {
                    }
                });
            }
        }
    }

    void post(String url, String json, Callback callback) throws Exception {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder().url(url).post(body).build();
        client.newCall(request).enqueue(callback);

    }

    void get(String url, Callback callback) throws Exception {
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(callback);
    }

    public void Logout() {
        if (AccessToken.getCurrentAccessToken() != null)
            LoginManager.getInstance().logOut();
        editor.putString("access_token", "");
        editor.commit();
        MainActivity.editor.putBoolean("Sharing", false);
        MainActivity.editor.commit();
        socket.emit("SharingOFF");
        user = null;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switchLoc.setVisibility(View.INVISIBLE);
                switchLoc.setChecked(false);

                if (Username_Txt != null)
                    Username_Txt.setText("Welcome");
                if (UserImage != null)

                    Picasso.with(Main).load(R.drawable.aid).into(UserImage);
            }
        });
    }

    void Initialise() {

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        sharedpreferences = getSharedPreferences("RedCrecent", Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();

        toolbar = (Toolbar) findViewById(R.id.include);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        this.setupDrawerContent(nvDrawer);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        manager = getSupportFragmentManager();
        Token = sharedpreferences.getString("access_token", "");
        Log.d("Token", Token);

        UserId = sharedpreferences.getString("UserID", "");
        json = "{\"access_token\":\"" + Token + "\"}";
        url = "https://crt-server-ibicha.c9users.io/api/v1/user/" + UserId + "?access_token=" + Token;
        try {
            get(url, new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    Logout();
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    try {
                        Boolean succes;
                        String r = response.body().string();

                        JSONObject jsonObj = new JSONObject(r);

                        succes = jsonObj.getBoolean("success");
                        if (succes) {
                            socket.emit("access_token", Token);
                            user = new User(jsonObj.getJSONObject("user"));

                            Main.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (user.getIsMember()) {
                                        switchLoc.setVisibility(View.VISIBLE);
                                        switchLoc.setChecked(true);
                                        MainActivity.editor.putBoolean("Sharing", MainActivity.switchLoc.isChecked());
                                        MainActivity.editor.commit();
                                    }
                                    // SetProfileImage();
                                    nvDrawer.getMenu().setGroupVisible(R.id.drawer_anonym, false);
                                    nvDrawer.getMenu().setGroupVisible(R.id.drawer_conneted, true);
                                }
                            });
                        } else
                            Logout();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        socket.emit("SharingOFF", "");
        super.onDestroy();
    }

    @Override
    public void onStop() {
        socket.emit("SharingOFF");
        launchService();
        super.onStop();

    }

    @Override
    protected void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this);
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
        return gps_enabled;
    }

    public boolean isOnline(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in air plan mode it will be null
        return (netInfo != null && netInfo.isConnected());

    }

    @Override
    protected void onResume() {
        super.onResume();
        StopService();
        AppEventsLogger.activateApp(this);
    }

    public static void RefreshMap() {
        Fragment fragment = null;
        Class fragmentClass ;
        if (checkGoogleService())
            fragmentClass= MapFragment.class;
        else
            fragmentClass = UpdateGooglePlay_Fragement.class;
        try {
            fragmentClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        manager.beginTransaction().add(R.id.include_frame, fragment).commit();
    }

    private Emitter.Listener OnDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("Socket", "DisConnect");

                    if (DisconnectDialog != null) {
                        if (!DisconnectDialog.isShowing())
                            DisconnectDialog.show();
                    }
                }
            });

        }

    };
    private Emitter.Listener OnConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("Socket", "Connect");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (DisconnectDialog != null) {
                        if (DisconnectDialog.isShowing())
                            DisconnectDialog.dismiss();
                        if (!Token.isEmpty())
                            socket.emit("access_token", Token);

                    }
                    if (SocketDialog.isShowing()) {
                        SocketDialog.dismiss();
                    }
                }
            });

        }

    };

    @Override
    protected void onRestart() {

        StopService();
        super.onRestart();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                Main);
        alertDialogBuilder.setTitle("Quit");

        alertDialogBuilder
                .setMessage("Confirm exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, int id) {

                        moveTaskToBack(true);
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(1);
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

    public static boolean checkGoogleService() {
        int status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(Main);
        if (status == ConnectionResult.SUCCESS) {
            return true;
        } else {
            GoogleApiAvailability.getInstance().getErrorDialog( Main, status,status);
            return false;
        }
    }

    public void SetGoogleServiceAlert() {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                Main);
        alertDialogBuilder.setTitle("Quit");

        alertDialogBuilder
                .setMessage("Please Update/install Google play service")
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, int id) {

                        moveTaskToBack(true);
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(1);
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

