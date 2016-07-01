package com.esprit.redcrescentapp.Fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.esprit.redcrescentapp.Handlers.WebService;
import com.esprit.redcrescentapp.R;
import com.esprit.redcrescentapp.activites.MainActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SignupFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SignupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignupFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    EditText TxFirstname;
    EditText TxLastname;
    EditText TxPassword;
    EditText Txemail;
    EditText TxBirthDate;

    private DatePickerDialog fromDatePickerDialog;
    private SimpleDateFormat dateFormatter;

    Button Submit;

    public SignupFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SignupFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SignupFragment newInstance(String param1, String param2) {
        SignupFragment fragment = new SignupFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_signup, container, false);
        TxFirstname = (EditText) view.findViewById(R.id.TxFirstnameSign);
        TxLastname = (EditText) view.findViewById(R.id.TxLastnameSign);
        Txemail = (EditText) view.findViewById(R.id.TxemailSign);
        TxPassword = (EditText) view.findViewById(R.id.TxPasswordSign);
        TxBirthDate = (EditText) view.findViewById(R.id.TxBirthdate);
        Submit = (Button) view.findViewById(R.id.BtSubmitSign);
        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TxFirstname.getText().toString().isEmpty() && !TxLastname.getText().toString().isEmpty() &&
                        !Txemail.getText().toString().isEmpty() && !TxPassword.getText().toString().isEmpty() &&
                        !TxBirthDate.getText().toString().isEmpty()) {
                    try {
                        WebService.CreateUser(TxFirstname.getText().toString(), TxLastname.getText().toString(), TxPassword.getText().toString(),
                                Txemail.getText().toString(), TxBirthDate.getText().toString(), new WebService.AddUserCallback() {
                                    @Override
                                    public void Success() {
                                        MainActivity.Main.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(MainActivity.Main, "Welcome :)", Toast.LENGTH_SHORT).show();
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
                                                MainActivity.manager.beginTransaction().replace(R.id.include_frame, fragment).commit();

                                            }
                                        });
                                    }

                                    @Override
                                    public void Fail() {
                                        MainActivity.Main.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(MainActivity.Main, " User already exist ! ", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    }
                                });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    MainActivity.Main.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.Main, "Sorry :( We need some info", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });
        dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.US);

        TxBirthDate.setInputType(InputType.TYPE_NULL);
        Calendar newCalendar = Calendar.getInstance();

        fromDatePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                TxBirthDate.setText(dateFormatter.format(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        TxBirthDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fromDatePickerDialog.show();
            }
        });
        return view;
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
