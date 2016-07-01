package com.esprit.redcrescentapp.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.esprit.redcrescentapp.R;
import com.esprit.redcrescentapp.activites.MainActivity;
import com.google.android.youtube.player.YouTubeApiServiceUtil;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;


public class EmergencyguideFagment extends Fragment implements View.OnClickListener, TabHost.OnTabChangeListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    TextView txBleedingmore;
    RelativeLayout BleedingmoreLayout;
    Boolean BleedingmoreVisibilty = false;

    TextView txchokingmore;
    RelativeLayout ChokingmoreLayout;
    Boolean ChokingmoreVisibilty = false;

    TextView txStrokegmore;
    TextView txStrokegmore1;
    RelativeLayout StrokemoreLayout;
    RelativeLayout StrokemoreLayout1;
    Boolean StrokemoreVisibilty = false;
    Boolean StrokemoreVisibilty1 = false;
    TabHost tabHost;


    TextView txHeadmore;
    private RelativeLayout HeadmoreLayout;
    Boolean HeadmoreVisibilty = false;

    TextView txHeadmore1;
    private RelativeLayout HeadmoreLayout1;
    Boolean HeadmoreVisibilty1 = false;

    TextView txHeartAttackmore;
    private RelativeLayout HeartAttackmoreLayout;
    Boolean HeartAttackvisibilty = false;

    TextView txHeartAttackmore1;
    private RelativeLayout HeartAttackmoreLayout1;
    Boolean HeartAttackvisibilty1 = false;

    TextView txHeartAttackmore2;
    private RelativeLayout HeartAttackmoreLayout2;
    Boolean HeartAttackvisibilty2 = false;


    TextView txBrokenBone;
    private RelativeLayout BrokenBoneLayout;
    Boolean BrokenBoneVisibilty = false;


    private ImageButton CallPolice;
    private ImageButton CallAmbulance;
    private ImageButton CallFire;
    private ImageButton CallAntiPoison;
    private ImageButton CallCrt;


    YouTubePlayer YPlayer;
    YouTubePlayerSupportFragment youTubePlayerFragment;
    FragmentTransaction transaction;

    FrameLayout FlBleeding;
    FrameLayout FlBStroke;
    FrameLayout FlChoking;
    FrameLayout FlHead;
    FrameLayout FlHeart;
    FrameLayout FlBone;

    public EmergencyguideFagment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        final YouTubeInitializationResult result = YouTubeApiServiceUtil.isYouTubeApiServiceAvailable(getContext());

        if (result != YouTubeInitializationResult.SUCCESS) {
            //If there are any issues we can show an error dialog.
            result.getErrorDialog(getActivity(), 0).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_emergencyguide_fagment, container, false);

        tabHost = (TabHost) view.findViewById(R.id.sostab);
        tabHost.setup();
        tabHost.setOnTabChangedListener(this);
        TabHost.TabSpec spec1 = tabHost.newTabSpec("Emergency call");
        spec1.setContent(R.id.sosHome);
        spec1.setIndicator("Emergency call");
        TabHost.TabSpec spec2 = tabHost.newTabSpec("Bleeding");
        spec2.setIndicator("Bleeding");
        spec2.setContent(R.id.sosBleeding);
        TabHost.TabSpec spec3 = tabHost.newTabSpec("Stroke");
        spec3.setIndicator("Stroke");
        spec3.setContent(R.id.sosStroke);
        TabHost.TabSpec spec4 = tabHost.newTabSpec("choking");
        spec4.setIndicator("choking");
        spec4.setContent(R.id.soschoking);
        TabHost.TabSpec spec5 = tabHost.newTabSpec("Head injury");
        spec5.setIndicator("Head injury");
        spec5.setContent(R.id.sosheadinjury);
        TabHost.TabSpec spec6 = tabHost.newTabSpec("Heart attack");
        spec6.setIndicator("Heart attack");
        spec6.setContent(R.id.sosheartattack);
        TabHost.TabSpec spec7 = tabHost.newTabSpec("Broken bone");
        spec7.setIndicator("Broken bone");
        spec7.setContent(R.id.sosbrokenbone);

        FlBleeding = (FrameLayout) view.findViewById(R.id.YoutubeBleeding);
        FlBStroke = (FrameLayout) view.findViewById(R.id.youtube_fragment);
        FlChoking = (FrameLayout) view.findViewById(R.id.youtubechoking);
        FlHead = (FrameLayout) view.findViewById(R.id.youtubeheadinjury);
        FlHeart = (FrameLayout) view.findViewById(R.id.youtubeheart);
        FlBone = (FrameLayout) view.findViewById(R.id.Brokenboneyoutube);

        tabHost.addTab(spec1);
        tabHost.addTab(spec2);
        tabHost.addTab(spec3);
        tabHost.addTab(spec4);
        tabHost.addTab(spec5);
        tabHost.addTab(spec6);
        tabHost.addTab(spec7);


        for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
            TextView tv = (TextView) tabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            tv.setTextColor(Color.WHITE);
        }
        txBleedingmore = (TextView) view.findViewById(R.id.TxBleedingmore);
        txBleedingmore.setOnClickListener(this);
        BleedingmoreLayout = (RelativeLayout) view.findViewById(R.id.TxBleedingmorelayout);


        txchokingmore = (TextView) view.findViewById(R.id.txchokingmore);
        txchokingmore.setOnClickListener(this);
        ChokingmoreLayout = (RelativeLayout) view.findViewById(R.id.TxChokingmorelayout);


        txStrokegmore = (TextView) view.findViewById(R.id.TxStrokemore);
        txStrokegmore.setOnClickListener(this);
        StrokemoreLayout = (RelativeLayout) view.findViewById(R.id.StrokemoreLayout);

        txStrokegmore1 = (TextView) view.findViewById(R.id.TxStrokemore1);
        txStrokegmore1.setOnClickListener(this);
        StrokemoreLayout1 = (RelativeLayout) view.findViewById(R.id.StrokemoreLayout1);

        txHeadmore = (TextView) view.findViewById(R.id.TxMoreHead);
        txHeadmore.setOnClickListener(this);
        HeadmoreLayout = (RelativeLayout) view.findViewById(R.id.headinjurymore);

        txHeadmore1 = (TextView) view.findViewById(R.id.TxMoreHead1);
        txHeadmore1.setOnClickListener(this);
        HeadmoreLayout1 = (RelativeLayout) view.findViewById(R.id.headinjurymore1);

        txStrokegmore1 = (TextView) view.findViewById(R.id.TxStrokemore1);
        txStrokegmore1.setOnClickListener(this);
        StrokemoreLayout1 = (RelativeLayout) view.findViewById(R.id.StrokemoreLayout1);

        txHeartAttackmore = (TextView) view.findViewById(R.id.TxHeartmore);
        txHeartAttackmore.setOnClickListener(this);
        HeartAttackmoreLayout = (RelativeLayout) view.findViewById(R.id.HeartmoreLayout);

        txHeartAttackmore1 = (TextView) view.findViewById(R.id.TxHeartmore1);
        txHeartAttackmore1.setOnClickListener(this);
        HeartAttackmoreLayout1 = (RelativeLayout) view.findViewById(R.id.HeartmoreLayout1);

        txHeartAttackmore2 = (TextView) view.findViewById(R.id.TxHeartmore2);
        txHeartAttackmore2.setOnClickListener(this);
        HeartAttackmoreLayout2 = (RelativeLayout) view.findViewById(R.id.HeartmoreLayout2);

        txBrokenBone = (TextView) view.findViewById(R.id.TxBrokenBone);
        txBrokenBone.setOnClickListener(this);
        BrokenBoneLayout = (RelativeLayout) view.findViewById(R.id.BrokenBoneLayout);


        CallPolice = (ImageButton) view.findViewById(R.id.BtCalPolice);
        CallAmbulance = (ImageButton) view.findViewById(R.id.BtcallAmbulance);
        CallFire = (ImageButton) view.findViewById(R.id.BtCallFire);
        CallAntiPoison = (ImageButton) view.findViewById(R.id.BtCallAntiPoison);
        CallCrt = (ImageButton) view.findViewById(R.id.BtCallCrt);

        CallAmbulance.setOnClickListener(this);
        CallPolice.setOnClickListener(this);
        CallFire.setOnClickListener(this);
        CallAntiPoison.setOnClickListener(this);
        CallCrt.setOnClickListener(this);




      /*  youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance();
        transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.YoutubeBleeding, youTubePlayerFragment).commit();
        youTubePlayerFragment.initialize("AIzaSyDOtk9pozJcejF9qRMaiDWRzmfIaRAN7b4", new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider arg0, YouTubePlayer youTubePlayer, boolean b) {
                if (!b) {
                    YPlayer = youTubePlayer;
                    YPlayer.loadVideo("BQRqUxB5pn0");
                    // YPlayer.play();
                }
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider arg0, YouTubeInitializationResult arg1) {
            }
        });

        youTubePlayerFragment1 = YouTubePlayerSupportFragment.newInstance();
        transaction1 = getChildFragmentManager().beginTransaction();
        transaction1.add(R.id.youtube_fragment, youTubePlayerFragment1).commit();

        youTubePlayerFragment1.initialize("AIzaSyDOtk9pozJcejF9qRMaiDWRzmfIaRAN7b4", new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider arg0, YouTubePlayer youTubePlayer, boolean b) {
                if (!b) {
                    YPlayer1 = youTubePlayer;
                    YPlayer1.loadVideo("PCNTMIcOMpE");
                    //  YPlayer.play();
                }
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider arg0, YouTubeInitializationResult arg1) {
            }
        });*/


        return view;
    }


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
            case R.id.TxBleedingmore:
                if (BleedingmoreVisibilty == false)
                    BleedingmoreLayout.setVisibility(View.VISIBLE);
                else
                    BleedingmoreLayout.setVisibility(View.GONE);
                BleedingmoreVisibilty = !BleedingmoreVisibilty;
                break;
            case R.id.TxStrokemore:
                if (StrokemoreVisibilty == false)
                    StrokemoreLayout.setVisibility(View.VISIBLE);
                else
                    StrokemoreLayout.setVisibility(View.GONE);
                StrokemoreVisibilty = !StrokemoreVisibilty;
                break;
            case R.id.TxStrokemore1:
                if (StrokemoreVisibilty1 == false)
                    StrokemoreLayout1.setVisibility(View.VISIBLE);
                else
                    StrokemoreLayout1.setVisibility(View.GONE);
                StrokemoreVisibilty1 = !StrokemoreVisibilty1;
                break;
            case R.id.txchokingmore:
                if (ChokingmoreVisibilty == false)
                    ChokingmoreLayout.setVisibility(View.VISIBLE);
                else
                    ChokingmoreLayout.setVisibility(View.GONE);
                ChokingmoreVisibilty = !ChokingmoreVisibilty;
                break;
            case R.id.TxMoreHead: {
                if (HeadmoreVisibilty == false)
                    HeadmoreLayout.setVisibility(View.VISIBLE);
                else
                    HeadmoreLayout.setVisibility(View.GONE);
                HeadmoreVisibilty = !HeadmoreVisibilty;
            }
            break;
            case R.id.TxMoreHead1: {
                if (HeadmoreVisibilty1 == false)
                    HeadmoreLayout1.setVisibility(View.VISIBLE);
                else
                    HeadmoreLayout1.setVisibility(View.GONE);
                HeadmoreVisibilty1 = !HeadmoreVisibilty1;
            }
            break;

            case R.id.TxHeartmore: {
                if (HeartAttackvisibilty == false)
                    HeartAttackmoreLayout.setVisibility(View.VISIBLE);
                else
                    HeartAttackmoreLayout.setVisibility(View.GONE);
                HeartAttackvisibilty = !HeartAttackvisibilty;
            }
            break;

            case R.id.TxHeartmore1: {
                if (HeartAttackvisibilty1 == false)
                    HeartAttackmoreLayout1.setVisibility(View.VISIBLE);
                else
                    HeartAttackmoreLayout1.setVisibility(View.GONE);
                HeartAttackvisibilty1 = !HeartAttackvisibilty1;
            }
            break;

            case R.id.TxHeartmore2: {
                if (HeartAttackvisibilty2 == false)
                    HeartAttackmoreLayout2.setVisibility(View.VISIBLE);
                else
                    HeartAttackmoreLayout2.setVisibility(View.GONE);
                HeartAttackvisibilty2 = !HeartAttackvisibilty2;
            }
            case R.id.TxBrokenBone: {
                if (BrokenBoneVisibilty == false)
                    BrokenBoneLayout.setVisibility(View.VISIBLE);
                else
                    BrokenBoneLayout.setVisibility(View.GONE);
                BrokenBoneVisibilty = !BrokenBoneVisibilty;
            }
            break;
            case R.id.BtCallAntiPoison: {
                callNumber("+21671335500");
            }
            break;
            case R.id.BtCallCrt: {
                callNumber("+21671320630");
            }
            break;
            case R.id.BtCallFire: {
                callNumber("198");
            }
            break;
            case R.id.BtCalPolice: {
                callNumber("197");
            }
            break;
            case R.id.BtcallAmbulance: {
                callNumber("190");
            }
            break;
        }
    }

    @Override
    public void onTabChanged(String tabId) {

        MainActivity.Main.runOnUiThread(new Runnable() {
            @Override
            public void run() {

            }
        });
        if (YPlayer != null) {
            YPlayer.release();
        }
        switch (tabId) {
            case "Bleeding": {
                FlBleeding.setVisibility(View.VISIBLE);
                youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance();
                transaction = getChildFragmentManager().beginTransaction();
                transaction.replace(R.id.YoutubeBleeding, youTubePlayerFragment).commit();
                youTubePlayerFragment.initialize("AIzaSyDOtk9pozJcejF9qRMaiDWRzmfIaRAN7b4", new YouTubePlayer.OnInitializedListener() {
                    @Override
                    public void onInitializationSuccess(YouTubePlayer.Provider arg0, YouTubePlayer youTubePlayer, boolean b) {
                        if (!b) {

                            YPlayer = youTubePlayer;
                            YPlayer.loadVideo("BQRqUxB5pn0");
                            YPlayer.setShowFullscreenButton(false);

                            YPlayer.play();
                        }
                    }

                    @Override
                    public void onInitializationFailure(YouTubePlayer.Provider arg0, YouTubeInitializationResult arg1) {
                    }
                });
            }
            break;
            case "Stroke": {
                FlBStroke.setVisibility(View.VISIBLE);

                youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance();
                transaction = getChildFragmentManager().beginTransaction();
                transaction.replace(R.id.youtube_fragment, youTubePlayerFragment).commit();

                youTubePlayerFragment.initialize("AIzaSyDOtk9pozJcejF9qRMaiDWRzmfIaRAN7b4", new YouTubePlayer.OnInitializedListener() {
                    @Override
                    public void onInitializationSuccess(YouTubePlayer.Provider arg0, YouTubePlayer youTubePlayer, boolean b) {
                        if (!b) {
                            YPlayer = youTubePlayer;
                            YPlayer.loadVideo("PCNTMIcOMpE");
                            YPlayer.setShowFullscreenButton(false);

                            YPlayer.play();
                        }
                    }

                    @Override
                    public void onInitializationFailure(YouTubePlayer.Provider arg0, YouTubeInitializationResult arg1) {
                    }
                });
            }
            break;
            case "choking": {
                FlChoking.setVisibility(View.VISIBLE);

                youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance();
                transaction = getChildFragmentManager().beginTransaction();
                transaction.replace(R.id.youtubechoking, youTubePlayerFragment).commit();
                youTubePlayerFragment.initialize("AIzaSyDOtk9pozJcejF9qRMaiDWRzmfIaRAN7b4", new YouTubePlayer.OnInitializedListener() {
                    @Override
                    public void onInitializationSuccess(YouTubePlayer.Provider arg0, YouTubePlayer youTubePlayer, boolean b) {
                        if (!b) {
                            YPlayer = youTubePlayer;
                            YPlayer.loadVideo("6xbQdKXXXlY");
                            YPlayer.play();
                            YPlayer.setShowFullscreenButton(false);

                        }
                    }

                    @Override
                    public void onInitializationFailure(YouTubePlayer.Provider arg0, YouTubeInitializationResult arg1) {
                    }
                });
            }
            break;
            case "Head injury": {
                FlHead.setVisibility(View.VISIBLE);

                youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance();
                transaction = getChildFragmentManager().beginTransaction();
                transaction.replace(R.id.youtubeheadinjury, youTubePlayerFragment).commit();

                youTubePlayerFragment.initialize("AIzaSyDOtk9pozJcejF9qRMaiDWRzmfIaRAN7b4", new YouTubePlayer.OnInitializedListener() {
                    @Override
                    public void onInitializationSuccess(YouTubePlayer.Provider arg0, YouTubePlayer youTubePlayer, boolean b) {
                        if (!b) {
                            YPlayer = youTubePlayer;
                            YPlayer.loadVideo("OqDdLUi7kkA");
                            YPlayer.play();
                            YPlayer.setShowFullscreenButton(false);

                        }
                    }

                    @Override
                    public void onInitializationFailure(YouTubePlayer.Provider arg0, YouTubeInitializationResult arg1) {
                    }
                });
            }
            break;
            case "Heart attack": {
                FlHeart.setVisibility(View.VISIBLE);

                youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance();
                transaction = getChildFragmentManager().beginTransaction();
                transaction.replace(R.id.youtubeheart, youTubePlayerFragment).commit();

                youTubePlayerFragment.initialize("AIzaSyDOtk9pozJcejF9qRMaiDWRzmfIaRAN7b4", new YouTubePlayer.OnInitializedListener() {
                    @Override
                    public void onInitializationSuccess(YouTubePlayer.Provider arg0, YouTubePlayer youTubePlayer, boolean b) {
                        if (!b) {
                            YPlayer = youTubePlayer;
                            YPlayer.loadVideo("1qje-XlNowI");
                            YPlayer.play();
                            YPlayer.setShowFullscreenButton(false);
                        }
                    }

                    @Override
                    public void onInitializationFailure(YouTubePlayer.Provider arg0, YouTubeInitializationResult arg1) {
                    }
                });
            }
            break;
            case "Broken bone": {
                FlBone.setVisibility(View.VISIBLE);

                youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance();
                transaction = getChildFragmentManager().beginTransaction();
                transaction.replace(R.id.Brokenboneyoutube, youTubePlayerFragment).commit();
                youTubePlayerFragment.initialize("AIzaSyDOtk9pozJcejF9qRMaiDWRzmfIaRAN7b4", new YouTubePlayer.OnInitializedListener() {
                    @Override
                    public void onInitializationSuccess(YouTubePlayer.Provider arg0, YouTubePlayer youTubePlayer, boolean b) {
                        if (!b) {
                            YPlayer = youTubePlayer;
                            YPlayer.loadVideo("dVqhZTBV3vI");
                            YPlayer.setShowFullscreenButton(false);

                            YPlayer.play();
                        }
                    }

                    @Override
                    public void onInitializationFailure(YouTubePlayer.Provider arg0, YouTubeInitializationResult arg1) {
                    }
                });
            }
            break;
        }
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void callNumber(String number) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + number));
        startActivity(callIntent);
    }

    @Override
    public void onResume() {

        super.onResume();

    }

    @Override
    public void onStop() {

        super.onStop();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
