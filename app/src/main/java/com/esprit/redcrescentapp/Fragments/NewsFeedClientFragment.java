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

import com.esprit.redcrescentapp.Adapters.CustomFeedClientAdapter;
import com.esprit.redcrescentapp.Handlers.WebService;
import com.esprit.redcrescentapp.R;
import com.esprit.redcrescentapp.activites.MainActivity;
import com.esprit.redcrescentapp.entities.FeedItem;

import java.util.ArrayList;


public class NewsFeedClientFragment extends Fragment implements View.OnClickListener {


    private OnFragmentInteractionListener mListener;
    private ImageView IvSendMessage;


    public NewsFeedClientFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_news_feed_client, container, false);
        final ListView listView = (ListView) view.findViewById(R.id.LvFacebookFeed);
        IvSendMessage = (ImageView) view.findViewById(R.id.IvClientSendMessage);
        IvSendMessage.setOnClickListener(this);
        try {
            final ProgressDialog dialog = new ProgressDialog(getActivity());
            dialog.setMessage("Loading ...");
            dialog.show();
            WebService.getFacebookFeed(new WebService.FacebookFeedCallback() {
                @Override
                public void onSucces(ArrayList<FeedItem> messages) {
                    final CustomFeedClientAdapter adapter = new CustomFeedClientAdapter(getContext(), messages);
                    MainActivity.Main.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listView.setAdapter(adapter);
                            dialog.dismiss();
                        }
                    });
                }
            });
        /*
                public void onSuccess (ArrayList < FeedItem > messages) {
                    final CustomFeedClientAdapter adapter = new CustomFeedClientAdapter(getContext(), messages);
                    final ListView listView = (ListView) view.findViewById(R.id.LvMessage);
                    MainActivity.Main.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listView.setAdapter(adapter);
                            dialog.dismiss();
                        }
                    });

                }*/


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


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
