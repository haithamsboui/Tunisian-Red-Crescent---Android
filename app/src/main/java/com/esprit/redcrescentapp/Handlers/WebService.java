package com.esprit.redcrescentapp.Handlers;

import android.app.Activity;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

import com.esprit.redcrescentapp.activites.MainActivity;
import com.esprit.redcrescentapp.entities.FeedItem;
import com.esprit.redcrescentapp.entities.MessageItem;
import com.esprit.redcrescentapp.entities.User;
import com.facebook.AccessToken;
import com.facebook.AccessTokenSource;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by iBicha on 23/11/2015.
 */
public class WebService {
    private static final String BASE_URL = "https://crt-server-ibicha.c9users.io/api/v1";

    private static final OkHttpClient client = new OkHttpClient();

    public static String UserID;
    public static String Token;

    public interface IAuthCallback {
        void onAuth(boolean success, String message);
    }

    static void Authenticate(final Activity Main, String username, String password, final IAuthCallback callback) {
        JSONObject jReq = new JSONObject();
        try {
            jReq.put("Email", username);
            jReq.put("Password", password);
        } catch (JSONException e) {
            Main.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    callback.onAuth(false, "Oups. Something went wrong. Sorry.");
                }
            });
        }
        try {
            post(BASE_URL + "/authenticate", jReq.toString(), new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    Main.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onAuth(false, "Could not connect to server. Sorry.");
                        }
                    });
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    JSONObject jsonObj = null;
                    try {
                        jsonObj = new JSONObject(response.body().string());
                        if (jsonObj.getBoolean("success")) {
                            Token = jsonObj.getString("token");
                            UserID = jsonObj.getString("UserID");
                            Main.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    callback.onAuth(true, "");
                                }
                            });
                        } else {
                            final String message = jsonObj.getString("message");
                            Main.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    callback.onAuth(false, message);
                                }
                            });
                        }
                    } catch (JSONException e) {
                        Main.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                callback.onAuth(false, "Error Logging in.");
                            }
                        });
                    }
                }
            });
        } catch (Exception e) {
            Main.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    callback.onAuth(false, "Oups. Something went wrong. Sorry.");
                }
            });
        }
    }

    public interface IGetUserCallback {
        void onUser(User user);
    }

    static void GetUserMe(final Activity Main, final IGetUserCallback callback) {
        try {
            get(BASE_URL + "/user/me?access_token=" + Token, new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    Main.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onUser(null);
                        }
                    });
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    try {
                        JSONObject jsonObj = new JSONObject(response.body().string());
                        if (jsonObj.getBoolean("success")) {
                            JSONObject jUser = jsonObj.getJSONObject("user");
                            final User user = new User(jUser);
                            Main.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    callback.onUser(user);
                                }
                            });
                        } else {
                            Main.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    callback.onUser(null);
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        } catch (Exception e) {
            Main.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    callback.onUser(null);
                }
            });
        }
    }


    public static void GetUser(final IGetUserCallback Callback) throws JSONException {
        final User[] us = new User[1];
        String Token = MainActivity.sharedpreferences.getString("access_token", "");
        String UserId = MainActivity.sharedpreferences.getString("UserID", "");
        String json = "{\"access_token\":\"" + Token + "\"}";
        String url = "https://crt-server-ibicha.c9users.io/api/v1/user/" + UserId + "?access_token=" + Token;
        try {
            get(url, new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    try {
                        Boolean succes;
                        String r = response.body().string();
                        JSONObject jsonObj = new JSONObject(r);
                        succes = jsonObj.getBoolean("success");
                        if (succes) {
                            us[0] = new User(jsonObj.getJSONObject("user"));
                            MainActivity.Main.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Callback.onUser(us[0]);
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


    public static void post(String url, String json, Callback callback) throws Exception {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder().url(url).post(body).build();
        client.newCall(request).enqueue(callback);

    }

    public static void postImage(String url, String json, Callback callback) throws Exception {
        MediaType JSON = MediaType.parse("image/jpg; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder().url(url).post(body).build();
        client.newCall(request).enqueue(callback);

    }

    public static void get(String url, Callback callback) throws Exception {
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(callback);
    }

    public static void UpdateUser(User user, String Token) throws JSONException {

        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        String userJson;
        if (user.getBirthDate() != null) {
            userJson = "{" +
                    "\"access_token\":\"" + Token + "\"," +
                    "\"FirstName\":\"" + user.getFirstName() + "\"," +
                    "\"LastName\":\"" + user.getLastName() + "\"," +
                    "\"Email\":\"" + user.getEmail() + "\"," +
                    "\"BirthDate\":\"" + dateFormatter.format(user.getBirthDate().getTime()) + "\"," +
                    "\"NationalId\":\"" + user.getNationalId() + "\"," +
                    "\"PhoneNumber\":\"" + user.getPhoneNumber() + "\"," +
                    "\"Address\":\"" + user.getAdress() + "\"," +
                    "\"FacebookId\":\"" + user.getFacebookId() + "\"," +
                    "\"Username\":\"" + user.getUsername() + "\"," +
                    "\"ImageFile\":\"" + user.getImageFile() + "\"" + "}";
        } else {
            userJson = "{" +
                    "\"access_token\":\"" + Token + "\"," +
                    "\"FirstName\":\"" + user.getFirstName() + "\"," +
                    "\"LastName\":\"" + user.getLastName() + "\"," +
                    "\"Email\":\"" + user.getEmail() + "\"," +
                    "\"NationalId\":\"" + user.getNationalId() + "\"," +
                    "\"PhoneNumber\":\"" + user.getPhoneNumber() + "\"," +
                    "\"Address\":\"" + user.getAdress() + "\"," +
                    "\"FacebookId\":\"" + user.getFacebookId() + "\"," +
                    "\"Username\":\"" + user.getUsername() + "\"," +
                    "\"ImageFile\":\"" + user.getImageFile() + "\"" + "}";
        }
        try {
            post(BASE_URL + "/user/edit", userJson, new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    String r = response.body().string();
                    Log.d("OK", r);

                    try {
                        JSONObject jsonObj = new JSONObject(r);
                        if (jsonObj.getBoolean("success")) {

                            MainActivity.Main.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.Main, "Update successfull", Toast.LENGTH_LONG).show();
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


    public static JSONObject uploadImage(String token, String sourceImageFile, String URL, Bitmap bitmap) {
        final JSONObject[] jobj = {null};
        RequestBody requestBody;
        try {
            File sourceFile = new File(sourceImageFile);
            final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
            final MediaType MEDIA_TYPE_JSON = MediaType.parse("json/plain");
            if (bitmap == null) {
                requestBody = new MultipartBuilder().type(MultipartBuilder.FORM)
                        .addFormDataPart("access_token", MainActivity.Token)
                        .addFormDataPart("ImageFile", sourceFile.getName(), RequestBody.create(MediaType.parse("image/png"), sourceFile))
                        .build();
            } else {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                requestBody = new MultipartBuilder().type(MultipartBuilder.FORM)
                        .addFormDataPart("access_token", MainActivity.Token)
                        .addFormDataPart("ImageFile", sourceFile.getName(), RequestBody.create(MediaType.parse("image/png"), byteArray))
                        .build();
            }
            Request request = new Request.Builder()
                    .addHeader("access_token", MainActivity.Token)
                    .url(URL)
                    .post(requestBody)
                    .build();

            OkHttpClient client = new OkHttpClient();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {

                }

                @Override
                public void onResponse(Response response) throws IOException {
                    try {

                        Log.d("Update img", response.body().string());

                        jobj[0] = new JSONObject(response.body().string());
                        Log.d("Update img", jobj[0].toString());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (Exception e) {
            Log.d("Error", "Other Error: " + e.getLocalizedMessage());
        }
        return jobj[0];
    }

    public interface IReportAccident {
        void OnSucces(JSONObject res);

        void Onfail(JSONObject res);
    }

    public static void ReportAccident(File file, String Description, Location location, final IReportAccident callback) {
        final JSONObject[] jobj = {null};

        File sourceFile = file;
        RequestBody requestBody = null;
        final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
        final MediaType MEDIA_TYPE_JSON = MediaType.parse("json/plain");
        JSONObject jsLocation = null;
        try {
            jsLocation = new JSONObject("{\"Longitude\":" + location.getLongitude() +
                    ",\"Latitude\":" + location.getLatitude() + ",\"Accuracy\":" + location.getAccuracy() +
                    ",\"Timestamp\":" + System.currentTimeMillis() + "}");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        if (file != null) {
            requestBody = new MultipartBuilder().type(MultipartBuilder.FORM)
                    .addFormDataPart("ImageFile", sourceFile.getName(), RequestBody.create(MediaType.parse("image/png"), sourceFile))
                    .addFormDataPart("Description", Description)
                    .addFormDataPart("Location", jsLocation.toString())
                    .addFormDataPart("ReporterID", MainActivity.user.getId())
                    .build();
        }
        if (file == null) {
            if (MainActivity.user == null) {

                requestBody = new MultipartBuilder().type(MultipartBuilder.FORM)
                        .addFormDataPart("Description", Description)
                        .addFormDataPart("Location", jsLocation.toString())
                        .build();
            } else {
                requestBody = new MultipartBuilder().type(MultipartBuilder.FORM)
                        .addFormDataPart("Description", Description)
                        .addFormDataPart("Location", jsLocation.toString())
                        .addFormDataPart("ReporterID", MainActivity.user.getId())
                        .build();
            }
        }

        Request request = new Request.Builder()
                .addHeader("access_token", MainActivity.Token)
                .url("https://crt-server-ibicha.c9users.io/api/v1/accident/report")
                .post(requestBody)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                try {
                    jobj[0] = new JSONObject(response.body().string());
                    if (jobj[0].getBoolean("success"))
                        callback.OnSucces(jobj[0]);
                    else
                        callback.Onfail(jobj[0]);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    public interface GetAccidentCallback {
        void Received(JSONObject Accidents) throws JSONException;
    }

    public static void GetAccidents(final GetAccidentCallback callback) throws Exception {
        String URL = "https://crt-server-ibicha.c9users.io/api/v1/accidents?access_token=" + MainActivity.Token;
        get(URL, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                JSONObject jsonObject = null;
                String res = response.body().string();

                try {
                    jsonObject = new JSONObject(res);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    if (jsonObject != null) {
                        if (jsonObject.getBoolean("success"))
                            callback.Received(jsonObject);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public interface GetAccidentConfirmCallback {
        void Sucess();

        void Fail();

    }

    public static void ConfirmAccident(String Id, final GetAccidentConfirmCallback callback) throws Exception {
        String URL = "https://crt-server-ibicha.c9users.io/api/v1/accident/handle/" + Id + "?access_token=" + MainActivity.Token;
        get(URL, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                JSONObject jsonObject = null;
                String res = response.body().string();
                try {
                    jsonObject = new JSONObject(res);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    if (jsonObject.getBoolean("success"))
                        callback.Sucess();
                    else
                        callback.Fail();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public interface getMessageCallback {
        void onSuccess(ArrayList<MessageItem> messages);

        void onFail();
    }

    public static void getMessage(final getMessageCallback callback) throws Exception {
        final ArrayList<MessageItem> messages = new ArrayList<>();
        final SimpleDateFormat[] dateFormatter = {new SimpleDateFormat("yyyy-MM-dd HH:mm")};
        String URL = "https://crt-server-ibicha.c9users.io/api/v1/messages?access_token=" + MainActivity.Token;
        get(URL, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                JSONObject jsonObject = null;
                String res = response.body().string();
                try {
                    jsonObject = new JSONObject(res);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    if (jsonObject.getBoolean("success")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("messages");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            MessageItem msg = new MessageItem(jsonArray.getJSONObject(i));
                          /*
                            msg.setTitle(jsonArray.getJSONObject(i).getString("Title"));
                            if (jsonArray.getJSONObject(i).has("Description"))
                            msg.setContent(jsonArray.getJSONObject(i).getString("Description"));
                            msg.setID(jsonArray.getJSONObject(i).getString("id"));

                            if (jsonArray.getJSONObject(i).has("StartDate")) {
                                msg.setStartdate(dateFormatter[0].parse(jsonArray.getJSONObject(i).getString("StartDate").replace("T", " ").replace("Z", "")));

                            }
                            if (jsonArray.getJSONObject(i).has("EndDate")) {
                                msg.setStartdate(dateFormatter[0].parse(jsonArray.getJSONObject(i).getString("EndDate").replace("T", " ").replace("Z", "")));
                            }
                            if (jsonArray.getJSONObject(i).has("ImageFile")) {
                              //  String ImageFile = jsonArray.getJSONObject(i).getString("ImageFile");

                               // java.net.URL url = new URL(ImageFile);
                               // Bitmap Image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
//                                Bitmap Image = BitmapFactory.decodeResource(MainActivity.Main.getResources(),R.drawable.accident);
                               // msg.setImage(Image);
                            }
                          */
                            messages.add(i, msg);

                        }
                        callback.onSuccess(messages);
                    } else {
                        callback.onFail();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public interface AddUserCallback {
        void Success();

        void Fail();
    }

    public static void CreateUser(String firstname, String Lastname, String Password, String email, String Birthdate, final AddUserCallback callback) throws Exception {
        String url = "https://crt-server-ibicha.c9users.io/api/v1/user/add";
        final String json = "{\"FirstName\":\"" + firstname + "\",\"LastName\":\"" + Lastname +
                "\",\"Password\":\"" + Password + "\",\"Email\":\"" + email + "\",\"BirthDate\":\"" + Birthdate + "\"}";
        Log.d("User", json);
        post(url, json, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                String r = response.body().string();
                Log.d("Response", r);
                try {
                    JSONObject jsonResponse = new JSONObject(r);
                    if (jsonResponse.getBoolean("success")) {
                        callback.Success();
                    } else {
                        callback.Fail();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });
    }

    public interface getCRTLocationCallback {
        void onSuccess(JSONArray jsonArray);
    }

    public static void getCRTLocation(final getCRTLocationCallback callback) throws Exception {
        final List<LatLng> positions = new ArrayList();
        final JSONArray jsonArray = new JSONArray();
        String URL = "https://crt-server-ibicha.c9users.io/api/v1/crtplaces";
        get(URL, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                String body = response.body().string();
                Log.e("LocCRT", body);
                try {
                    JSONObject jsonObject = new JSONObject(body);
                    if (jsonObject.getBoolean("success")) {
                        JSONArray crtplaces = jsonObject.getJSONArray("crtplaces");
                        callback.onSuccess(crtplaces);

                       /* for (int i = 0; i < crtplaces.length(); i++) {
                            if (crtplaces.getJSONObject(i).has("Location")) {
                                String jsonString="{"
                                JSONObject loc = crtplaces.getJSONObject(i).getJSONObject("Location");
                                if (loc.has("Longitude") && loc.has("Latitude")) {
                                    jsonArray.
                                    LatLng latlng = new LatLng(loc.getDouble("Longitude"), loc.getDouble("Latitude"));

                                    positions.add(latlng);
                                }

                            }
                        }*/
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    public interface FacebookFeedCallback {
        void onSucces(ArrayList<FeedItem> messages);
    }

    public static void getFacebookFeed(final FacebookFeedCallback callback) {
        final ArrayList<FeedItem> FeedItems = new ArrayList<>();
        AccessToken accessToken = new AccessToken("521461201342140|-3AwdZRFhOXUbfd0BRuih8mTtL0", "521461201342140", "818056918259152",
                null, null, AccessTokenSource.WEB_VIEW, null, null);
        Log.d("TOKK", accessToken.getToken());
     /*   AccessToken.ACCESS_TOKEN_KEY="521461201342140|-3AwdZRFhOXUbfd0BRuih8mTtL0";
        AccessToken.createFromNativeLinkingIntent(MainActivity.Main.getIntent(), "521461201342140", new AccessToken.AccessTokenCreationCallback() {
            @Override
            public void onSuccess(AccessToken token) {
                Log.d("TOKKK",token.getToken());
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("TOKKKK", "onError: "+error.toString());
            }
        });*/
        GraphRequest request = GraphRequest.newGraphPathRequest(
                accessToken,
                "/CRTunisien",
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {


                        JSONObject responseJSON = response.getJSONObject();
                        try {
                            JSONArray responseArray = responseJSON.getJSONObject("posts").getJSONArray("data");
                            for (int i = 0; i < responseArray.length(); i++) {
                                FeedItem feedItem = new FeedItem(responseArray.getJSONObject(i));
                                FeedItems.add(feedItem);
                            }
                            callback.onSucces(FeedItems);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "posts{message,caption,description,name,link,picture}");
        request.setParameters(parameters);
        request.executeAsync();
    }

    public interface ReportAccidentWidgetCallback {
        void OnSucces();

        void OnFail();
    }

    public static void ReportAccidentWidget(String Description, Location location, final ReportAccidentWidgetCallback callback) {
        String dataJson;
        dataJson = "{\"Description\" :\"" + Description + "\"" + ",\"Location\":{\"Longitude\":" + location.getLongitude() +
                ",\"Latitude\":" + location.getLatitude() + ",\"Accuracy\":" + location.getAccuracy() +
                ",\"Timestamp\":" + System.currentTimeMillis() + "}}";


        try {

            post("https://crt-server-ibicha.c9users.io/api/v1/accident/report", dataJson, new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    String r = response.body().string();
                    Log.d("Rs", r);
                    try {
                        JSONObject jsonObj = new JSONObject(r);
                        if (jsonObj.getBoolean("success")) {

                            callback.OnSucces();
                        } else
                            callback.OnFail();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface SendMessageCallback {
        void onSuccess();

        void onFail();
    }

    public static void SendMessage(File file, String Description, String Audience, JSONObject Location, String Title, String StartDate, String EndDate, final SendMessageCallback callback) {

        final JSONObject[] jobj = {null};

        File sourceFile = file;
        RequestBody requestBody = null;
        final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
        final MediaType MEDIA_TYPE_JSON = MediaType.parse("json/plain");

        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(System.currentTimeMillis());
        String SubmitDate = DateFormat.format("dd-MM-yyyy", cal).toString();
        Log.e("AUD",Audience);
        if (file != null) {
            requestBody = new MultipartBuilder().type(MultipartBuilder.FORM)
                    .addFormDataPart("ImageFile", sourceFile.getName(), RequestBody.create(MEDIA_TYPE_PNG, sourceFile))
                    .addFormDataPart("Description", Description)
                    .addFormDataPart("Location", Location.toString())
                    .addFormDataPart("ReporterID", MainActivity.user.getId())
                    .addFormDataPart("Title", Title)
                    .addFormDataPart("EndDate", EndDate)
                    .addFormDataPart("StartDate", StartDate)
                    .addFormDataPart("SubmitDate", SubmitDate)
                    .addFormDataPart("Audience", "[\""+Audience+"\"]")
                    .build();
        }
        if (file == null) {
            requestBody = new MultipartBuilder().type(MultipartBuilder.FORM)
                    .addFormDataPart("Description", Description)
                    .addFormDataPart("Location", Location.toString())
                    .addFormDataPart("ReporterID", MainActivity.user.getId())
                    .addFormDataPart("Title", Title)
                    .addFormDataPart("EndDate", EndDate)
                    .addFormDataPart("StartDate", StartDate)
                    .addFormDataPart("SubmitDate", SubmitDate)
                    .addFormDataPart("Audience", "[\""+Audience+"\"]")
                    .build();

        }

        Request request = new Request.Builder()
                .addHeader("access_token", MainActivity.Token)
                .url("https://crt-server-ibicha.c9users.io/api/v1/message/send")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
            }

            @Override
            public void onResponse(Response response) throws IOException {
                try {

                    jobj[0] = new JSONObject(response.body().string());
                    Log.e("Error",jobj[0].toString());

                    if (jobj[0].getBoolean("success"))
                        callback.onSuccess();
                    else
                        callback.onFail();


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


    }
}