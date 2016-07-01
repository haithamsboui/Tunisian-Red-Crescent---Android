package com.esprit.redcrescentapp.Fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.Toast;

import com.esprit.redcrescentapp.Handlers.WebService;
import com.esprit.redcrescentapp.R;
import com.esprit.redcrescentapp.activites.MainActivity;
import com.esprit.redcrescentapp.entities.User;
import com.facebook.AccessToken;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class ProfileFragment extends Fragment implements View.OnFocusChangeListener, View.OnClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private Boolean IsPofileEdited = false;
    private OnFragmentInteractionListener mListener;
    private static EditText TxFirstname;
    private static EditText TxLastname;
    private static EditText TxEmail;
    private static EditText TxDate;
    private static EditText TxNationalId;
    private static EditText TxBloodType;
    private static EditText TxPhone;
    private static EditText TxAdresse;

    private static LoginButton BtFbLink;
    private static ImageView IvProfile;
    private static Button BtUpload;
    private static Button BtCamera;
    private static Button BtmemberShip;
    private DatePickerDialog fromDatePickerDialog;
    private SimpleDateFormat dateFormatter;
    private User CURRENT_USER;

    public final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 101;
    public final int PICKFILE_REQUEST_CODE = 102;
    public Uri fileUri;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public File file;
    public Bitmap Bit;

    private Boolean HasChangedContent = false;
    private Boolean HasChangedImage = false;
    private Boolean HasChangedImageFacebook = false;

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            HasChangedContent = true;
            Log.d("Text changed", s.toString());
        }
    };


    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public ProfileFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        CURRENT_USER = MainActivity.user;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        final TabHost tabHost = (TabHost) view.findViewById(R.id.tabHost);
        tabHost.setup();
        TabHost.TabSpec spec1 = tabHost.newTabSpec("Basic");
        spec1.setContent(R.id.basic);
        spec1.setIndicator("Basic");
        TabHost.TabSpec spec2 = tabHost.newTabSpec("Extended");
        spec2.setIndicator("Extended");
        spec2.setContent(R.id.extended);
        TabHost.TabSpec spec3 = tabHost.newTabSpec("Sharing");
        spec3.setIndicator("Sharing");
        spec3.setContent(R.id.sharing);
        tabHost.addTab(spec1);
        tabHost.addTab(spec2);
        tabHost.addTab(spec3);

        TxFirstname = (EditText) view.findViewById(R.id.TxFirstname);
        TxLastname = (EditText) view.findViewById(R.id.Txlastname);
        TxEmail = (EditText) view.findViewById(R.id.TxEmail);
        TxDate = (EditText) view.findViewById(R.id.TxDate);
        TxNationalId = (EditText) view.findViewById(R.id.TxNationalId);
        TxBloodType = (EditText) view.findViewById(R.id.TxBloodType);
        TxAdresse = (EditText) view.findViewById(R.id.TxAdresse);

        TxPhone = (EditText) view.findViewById(R.id.TxPhone);
        BtFbLink = (LoginButton) view.findViewById(R.id.BtLoginFacebookProfile);
        IvProfile = (ImageView) view.findViewById(R.id.IvProfile);
        BtCamera = (Button) view.findViewById(R.id.BtCamera);
        BtUpload = (Button) view.findViewById(R.id.BtUpload);
        BtmemberShip = (Button) view.findViewById(R.id.BtMembership);

        TxFirstname.setOnFocusChangeListener(this);
        TxLastname.setOnFocusChangeListener(this);
        TxEmail.setOnFocusChangeListener(this);
        TxDate.setOnFocusChangeListener(this);
        TxNationalId.setOnFocusChangeListener(this);
        TxBloodType.setOnFocusChangeListener(this);
        TxPhone.setOnFocusChangeListener(this);
        BtFbLink.setOnFocusChangeListener(this);

        BtCamera.setOnClickListener(this);
        BtUpload.setOnClickListener(this);
        BtmemberShip.setOnClickListener(this);
        BtFbLink.setOnClickListener(this);

        if (CURRENT_USER.getFirstName() != null)
            TxFirstname.setText(CURRENT_USER.getFirstName());
        if (CURRENT_USER.getLastName() != null)
            TxLastname.setText(CURRENT_USER.getLastName());
        if (CURRENT_USER.getEmail() != null)
            TxEmail.setText(CURRENT_USER.getEmail());
        dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        if (CURRENT_USER.getBirthDate() != null)
            TxDate.setText(dateFormatter.format(CURRENT_USER.getBirthDate().getTime()));
        if (CURRENT_USER.getNationalId() != null)
            TxEmail.setText(CURRENT_USER.getNationalId());
        if (CURRENT_USER.getPhoneNumber() != null)

            TxPhone.setText(CURRENT_USER.getPhoneNumber());

        BtFbLink.setLoginBehavior(LoginManager.getInstance().getLoginBehavior());

        TxDate.setInputType(InputType.TYPE_NULL);
        Calendar newCalendar = Calendar.getInstance();
        if (CURRENT_USER.getBirthDate() != null)
        newCalendar.setTime(CURRENT_USER.getBirthDate());

        fromDatePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                TxDate.setText(dateFormatter.format(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        TxFirstname.addTextChangedListener(textWatcher);
        TxLastname.addTextChangedListener(textWatcher);
        TxEmail.addTextChangedListener(textWatcher);
        TxDate.addTextChangedListener(textWatcher);
        TxNationalId.addTextChangedListener(textWatcher);
        TxBloodType.addTextChangedListener(textWatcher);
        TxPhone.addTextChangedListener(textWatcher);

        TxFirstname.clearFocus();

        if (MainActivity.user.getImage() != null)
            IvProfile.setImageBitmap(MainActivity.user.getImage());

        if (MainActivity.user.getIsMember())
            BtmemberShip.setVisibility(View.GONE);
        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        if (HasChangedContent) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    getActivity());
            alertDialogBuilder.setTitle("Confirm");
            alertDialogBuilder
                    .setMessage("Save changes ?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if (HasChangedImage) {
                                String URL = "https://crt-server-ibicha.c9users.io/api/v1/user/submitimage";
                                if (file != null) {

                                    WebService.uploadImage(MainActivity.Token, file.getAbsolutePath(), URL, null);
                                }//  else
                                //    WebService.uploadImage(MainActivity.Token, null, URL, Bit);
                            }
                            CURRENT_USER.setAdress(TxAdresse.getText().toString());
                            CURRENT_USER.setBloodType(TxBloodType.getText().toString());
                            CURRENT_USER.setFirstName(TxFirstname.getText().toString());
                            CURRENT_USER.setLastName(TxLastname.getText().toString());
                            CURRENT_USER.setNationalId(TxNationalId.getText().toString());
                            CURRENT_USER.setPhoneNumber(TxPhone.getText().toString());
                            CURRENT_USER.setEmail(TxEmail.getText().toString());

                            if (AccessToken.getCurrentAccessToken() != null)
                                CURRENT_USER.setFacebookId(AccessToken.getCurrentAccessToken().getUserId());

                            try {
                                CURRENT_USER.setBirthDate(dateFormatter.parse(TxDate.getText().toString()));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            MainActivity.user = CURRENT_USER;
                            MainActivity.SetProfileImage();
                            try {

                                WebService.UpdateUser(CURRENT_USER, MainActivity.Token);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
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


    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (v == TxDate && hasFocus) {
            fromDatePickerDialog.show();
        }
        if (hasFocus) {
            v.setBackgroundColor(Color.parseColor("#F44336"));
            ((EditText) v).setTextColor(Color.parseColor("#FFFFFF"));
        }
        if (!v.hasFocus()) {
            v.setBackgroundColor(Color.parseColor("#FFFFFF"));
            ((EditText) v).setTextColor(Color.parseColor("#000000"));
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == BtCamera.getId()) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
            file = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            intent.putExtra("Path", file.getAbsoluteFile());
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
        if (v.getId() == BtUpload.getId()) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("file/png");
            startActivityForResult(intent, PICKFILE_REQUEST_CODE);

        }
        if (v.getId() == BtmemberShip.getId()) {
            MainActivity.Main.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.Main, "Request sent . We will send you a confirmation mail soon !", Toast.LENGTH_LONG).show();
                }
            });
        }
        if (v.getId() == BtFbLink.getId()) {


            BtFbLink.registerCallback(MainActivity.callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(final LoginResult loginResult) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            getActivity());
                    alertDialogBuilder.setTitle("Info");
                    alertDialogBuilder
                            .setMessage("Update your profile ?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    GraphRequest request = GraphRequest.newMeRequest(
                                            loginResult.getAccessToken(),
                                            new GraphRequest.GraphJSONObjectCallback() {
                                                @Override
                                                public void onCompleted(
                                                        JSONObject object,
                                                        GraphResponse response) {

                                                    if (response.getJSONObject().has("first_name")) {
                                                        try {
                                                            TxFirstname.setText(response.getJSONObject().getString("first_name"));
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                    if (response.getJSONObject().has("last_name")) {
                                                        try {
                                                            TxLastname.setText(response.getJSONObject().getString("last_name"));
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                    if (response.getJSONObject().has("birthday")) {
                                                        try {
                                                            TxDate.setText(response.getJSONObject().getString("birthday"));
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                    if (response.getJSONObject().has("email")) {
                                                        try {
                                                            TxEmail.setText(response.getJSONObject().getString("email"));
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                    if (response.getJSONObject().has("id")) {
                                                        try {
                                                            CURRENT_USER.setFacebookId(response.getJSONObject().getString("id"));
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                    if (response.getJSONObject().has("picture")) {
                                                        try {
                                                            String URLimg = response.getJSONObject().getJSONObject("picture").getJSONObject("data").getString("url").replace("\\/", "\\");
                                                            final URL url = new URL(URLimg);
                                                            Log.d("URL", url.toString());
                                                            new DownloadImageTask().execute(url);

                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        } catch (IOException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }
                                            });
                                    Bundle parameters = new Bundle();
                                    parameters.putString("fields", "id,first_name,last_name,email,birthday,picture.type(large)");
                                    request.setParameters(parameters);
                                    request.executeAsync();

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

                @Override
                public void onCancel() {

                }

                @Override
                public void onError(FacebookException error) {

                }
            });
        }
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }


    public static Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    public static File getOutputMediaFile(int type) {

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCamera");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCamera", "failed to create directory");
                return null;
            }
        }
        java.util.Date date = new java.util.Date();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(date.getTime());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == getActivity().RESULT_OK) {
                HasChangedContent = true;
                HasChangedImage = true;
                Bundle b = data.getExtras();
                Bitmap bitmap = (Bitmap) b.get("data");
                Bit = bitmap;
                if (bitmap != null) {
                    IvProfile.setImageBitmap(bitmap);
                    CURRENT_USER.setImage(bitmap);
                    MainActivity.user.setImage(bitmap);
                    FileOutputStream out = null;
                    try {
                        out = new FileOutputStream(file);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                    try {
                        out.flush();
                        out.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    IvProfile.setImageDrawable(getResources().getDrawable(R.drawable.aid));
                }
                IvProfile.setImageBitmap(bitmap);
            } else {
            }
        }
        if (requestCode == PICKFILE_REQUEST_CODE) {

            if (resultCode == getActivity().RESULT_OK) {

                HasChangedContent = true;
                HasChangedImage = true;
                Uri uri = data.getData();
                String path = null;
                try {
                    path = getPath(getActivity(), uri);
                    file = new File(path);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }

            }
        }


    }

    public static String getPath(Context context, Uri uri) throws URISyntaxException {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {"_data"};
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                // Eat it
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    private class DownloadImageTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {

            final URL url1 = (URL) params[0];
            Bitmap bit = null;
            try {
                bit = BitmapFactory.decodeStream(url1.openConnection().getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("URL", "BACK ERROR");

            }
            return bit;
        }

        @Override
        protected void onPostExecute(Object o) {
            Log.d("URL", "IMAGE");

            Bitmap result = (Bitmap) o;
            Bit = result;
            IvProfile.setImageBitmap(result);
            CURRENT_USER.setImage(result);

            HasChangedImage = true;
            HasChangedContent = true;
            file = null;
        }
    }
}
