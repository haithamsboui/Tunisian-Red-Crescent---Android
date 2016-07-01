package com.esprit.redcrescentapp.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.esprit.redcrescentapp.Handlers.WebService;
import com.esprit.redcrescentapp.R;
import com.esprit.redcrescentapp.activites.MainActivity;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ReportAccidentFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ReportAccidentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReportAccidentFragment extends Fragment implements View.OnLongClickListener, View.OnClickListener, View.OnFocusChangeListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ImageView IvAccident;
   // private ImageView IvCamera;

    private Button BtSubmit;
    private EditText TxDescription;

    public final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 101;
    public Uri fileUri;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public File file = null;
    private OnFragmentInteractionListener mListener;


    public ReportAccidentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReportAccidentFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReportAccidentFragment newInstance(String param1, String param2) {
        ReportAccidentFragment fragment = new ReportAccidentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View v = inflater.inflate(R.layout.fragment_report__accident, container, false);

        IvAccident = (ImageView) v.findViewById(R.id.IvAccident);
        IvAccident.setOnLongClickListener(this);

       // IvCamera = (ImageView) v.findViewById(R.id.IvCamera);
       // IvCamera.setOnClickListener(this);


        BtSubmit = (Button) v.findViewById(R.id.BtSubmitAccident);
        BtSubmit.setOnClickListener(this);
        TxDescription = (EditText) v.findViewById(R.id.TxDescriptionAccident);
        TxDescription.setOnFocusChangeListener(this);
        IvAccident.setImageResource(R.drawable.accident);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = ProfileFragment.getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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

        switch (v.getId()) {
            case R.id.BtSubmitAccident: {
                if (TxDescription.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), "Invalid Content", Toast.LENGTH_LONG).show();
                } else {

                    final ProgressDialog dialog = new ProgressDialog(MainActivity.Main);
                    dialog.setMessage("Please wait!");
                    dialog.show();
                    WebService.ReportAccident(file, TxDescription.getText().toString(), MainActivity.LastLocation, new WebService.IReportAccident() {
                        @Override
                        public void OnSucces(JSONObject res) {
                            Fragment fragment = null;
                            Class fragmentClass = MapFragment.class;
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
                            dialog.dismiss();
                            MainActivity.manager.beginTransaction().replace(R.id.include_frame, fragment).commit();
                        }

                        @Override
                        public void Onfail(JSONObject res) {
                            dialog.dismiss();
                            Toast.makeText(getActivity(), "Something is broken :(", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
            break;
            case R.id.BtCamera: {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                fileUri = ProfileFragment.getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                // file = ProfileFragment.getOutputMediaFile(MEDIA_TYPE_IMAGE);
                //intent.putExtra("Path", file.getAbsoluteFile());
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
            break;

        }


    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
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
    public boolean onLongClick(View v) {

        if (v.getId() == R.id.IvAccident) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            fileUri = ProfileFragment.getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
        return false;
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == getActivity().RESULT_OK) {
                Bundle b = data.getExtras();
                Bitmap bitmap = (Bitmap) b.get("data");
                file = ProfileFragment.getOutputMediaFile(MEDIA_TYPE_IMAGE);
                if (bitmap != null) {
                    IvAccident.setImageBitmap(bitmap);
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


}
