package com.esprit.redcrescentapp.Fragments;

import android.app.ActionBar;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.esprit.redcrescentapp.Handlers.WebService;
import com.esprit.redcrescentapp.R;
import com.esprit.redcrescentapp.activites.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class SendmessageFragment extends Fragment implements View.OnClickListener, View.OnLongClickListener {
    Spinner SpAudience;
    ImageView IvMessage;
    EditText Title;
    EditText Description;
    EditText StartDate;
    EditText EndDate;
    EditText Audience;
    Button BtSend;
    Button BtCancel;

    public final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 105;
    public Uri fileUri;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public File file = null;
    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog fromDatePickerDialog1;

    private SimpleDateFormat dateFormatter;
    private OnFragmentInteractionListener mListener;

    public SendmessageFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sendmessage, container, false);

        SpAudience = (Spinner) v.findViewById(R.id.SpAudience);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.Audience, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SpAudience.setAdapter(adapter);
        IvMessage = (ImageView) v.findViewById(R.id.IvSendMessagePhoto);
        Title = (EditText) v.findViewById(R.id.TxTitleSendMessage);
        Description = (EditText) v.findViewById(R.id.TxDescriptionSendMessage);
        StartDate = (EditText) v.findViewById(R.id.TxStartDateSendMessage);
        EndDate = (EditText) v.findViewById(R.id.TxEndDateSendMessage);
        BtSend = (Button) v.findViewById(R.id.BtSendMessage);
        BtCancel = (Button) v.findViewById(R.id.BtCancelMessage);

        IvMessage.setOnLongClickListener(this);
        BtSend.setOnClickListener(this);

        EndDate.setOnClickListener(this);

        StartDate.setOnClickListener(this);
        BtCancel.setOnClickListener(this);
        dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.US);

        StartDate.setInputType(InputType.TYPE_NULL);
        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                StartDate.setText(dateFormatter.format(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        EndDate.setInputType(InputType.TYPE_NULL);
        Calendar newCalendar1 = Calendar.getInstance();
        fromDatePickerDialog1 = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                EndDate.setText(dateFormatter.format(newDate.getTime()));
            }

        }, newCalendar1.get(Calendar.YEAR), newCalendar1.get(Calendar.MONTH), newCalendar1.get(Calendar.DAY_OF_MONTH));
        if (MainActivity.user != null) {

            if (!MainActivity.user.getIsMember()) {
                SpAudience.setEnabled(false);
            }
        }
        return v;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {

        if (v.getId()==StartDate.getId())
        {
            fromDatePickerDialog.show();
        }
        if (v.getId()==EndDate.getId() )
        {
            fromDatePickerDialog1.show();
        }
        if (BtSend.getId() == v.getId()) {
            JSONObject locationJson = null;

            try {
                locationJson = new JSONObject("{\"Location\":{\"Longitude\":" + MainActivity.LastLocation.getLongitude() +
                        ",\"Latitude\":" + MainActivity.LastLocation.getLatitude() + ",\"Accuracy\":" + MainActivity.LastLocation.getAccuracy() +
                        ",\"Timestamp\":" + System.currentTimeMillis() + "}}");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            WebService.SendMessage(file, Description.getText().toString(), SpAudience.getSelectedItem().toString(), locationJson,
                    Title.getText().toString(), StartDate.getText().toString(), EndDate.getText().toString(), new WebService.SendMessageCallback() {
                        @Override
                        public void onSuccess() {
                            try {
                                Fragment fragment = null;
                                Class fragmentClass = null;
                                if (MainActivity.user.getIsAdmin())
                                    fragmentClass = NewsfeedFragment.class;
                                else
                                    fragmentClass = NewsFeedClientFragment.class;
                                fragmentClass.newInstance();
                                fragment = (Fragment) fragmentClass.newInstance();
                                MainActivity.manager.beginTransaction().replace(R.id.include_frame, fragment).commit();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFail() {
                    Log.e("Message", "error");
                        }
                    });

        }
        if (BtCancel.getId() == v.getId()) {
            try {
                Fragment fragment = null;
                Class fragmentClass = null;
                if (MainActivity.user.getIsAdmin())
                    fragmentClass = NewsfeedFragment.class;
                else
                    fragmentClass = NewsFeedClientFragment.class;
                fragmentClass.newInstance();
                fragment = (Fragment) fragmentClass.newInstance();
                MainActivity.manager.beginTransaction().replace(R.id.include_frame, fragment).commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (IvMessage.getId() == v.getId()) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            fileUri = ProfileFragment.getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
        return false;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == getActivity().RESULT_OK) {
                Bundle b = data.getExtras();
                Bitmap bitmap = (Bitmap) b.get("data");
                file = ProfileFragment.getOutputMediaFile(MEDIA_TYPE_IMAGE);
                if (bitmap != null) {
                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
                    IvMessage.setLayoutParams(lp);
                    IvMessage.setImageBitmap(bitmap);
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
                }
            }
        }


    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
