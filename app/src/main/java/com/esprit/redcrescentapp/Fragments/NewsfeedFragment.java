package com.esprit.redcrescentapp.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.esprit.redcrescentapp.Adapters.CustomListAdapter;
import com.esprit.redcrescentapp.Handlers.WebService;
import com.esprit.redcrescentapp.R;
import com.esprit.redcrescentapp.activites.MainActivity;
import com.esprit.redcrescentapp.entities.MessageItem;

import java.util.ArrayList;


public class NewsfeedFragment extends Fragment implements View.OnClickListener {


    private OnFragmentInteractionListener mListener;
    private ImageView IvSendMessage;

    public NewsfeedFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_newsfeed, container, false);
        IvSendMessage = (ImageView) view.findViewById(R.id.IvSendMessagePhoto);
        IvSendMessage.setOnClickListener(this);


        try {
            final ProgressDialog dialog = new ProgressDialog(getActivity());
            dialog.setMessage("Loading ...");
            dialog.show();
            WebService.getMessage(new WebService.getMessageCallback() {
                @Override
                public void onSuccess(ArrayList<MessageItem> messages) {
                    final CustomListAdapter adapter = new CustomListAdapter(getContext(), messages);
                    final ListView listView = (ListView) view.findViewById(R.id.LvMessage);
                    MainActivity.Main.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listView.setAdapter(adapter);
                            dialog.dismiss();
                        }
                    });

                }

                @Override
                public void onFail() {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    @Override
    public void onClick(View v) {
        if (v.getId() == IvSendMessage.getId()) {
            try {
                Fragment fragment = null;
                Class fragmentClass = null;
                fragmentClass = SendmessageFragment.class;
                fragmentClass.newInstance();
                fragment = (Fragment) fragmentClass.newInstance();
                MainActivity.manager.beginTransaction().replace(R.id.include_frame, fragment).commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
