package com.vivacom.leo.perdpaslenord.fragments;

import android.app.Activity;

import android.graphics.Typeface;
import android.media.Image;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vivacom.leo.perdpaslenord.R;
import com.vivacom.leo.perdpaslenord.ViewAnimations;


import java.util.ArrayList;



/**
 * Created by Leo on 08/09/2017.
 */

public class InformationFragmentClass extends Fragment {

    TextView informations, spotName, moreInformation, moreInfo1,  moreInfo2, lessInformation;
    RelativeLayout layout1, layout2;
    View mView, ligne;


    // -----------------

    String listInfos[];

    ViewAnimations animator = new ViewAnimations();

    InformationFragmentClassCallBack informationFragmentClassCallBack;
    static final String SPOT_INFO = "SPOT_INFO";
    static final String TAG = "INFOS";

    // --------------------------------------------------------------------------------------

    public InformationFragmentClass(){
        // Require empty public constructor
    }

    public interface InformationFragmentClassCallBack{
        void checkIfInformationsCompleted();
    }

    public static InformationFragmentClass newInstance(String spotInfos[]){
        InformationFragmentClass fragment = new InformationFragmentClass();
        Bundle args = new Bundle();
        args.putStringArray(SPOT_INFO, spotInfos);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof InformationFragmentClass.InformationFragmentClassCallBack)
            informationFragmentClassCallBack = (InformationFragmentClass.InformationFragmentClassCallBack) activity;
        Log.d(TAG, "Fragment onAttach");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        informationFragmentClassCallBack = null;
        Log.d(TAG, "Fragment onDetach");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Fragment onDestroy");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_information,container,false);

        layout1 = mView.findViewById(R.id.informations1);
        layout2 = mView.findViewById(R.id.informations2);
        informations = mView.findViewById(R.id.info_txtInfos);
        spotName = mView.findViewById(R.id.info_spotName);
        moreInformation = mView.findViewById(R.id.info_plus);
        moreInfo1 = mView.findViewById(R.id.info_more1);
        moreInfo2 = mView.findViewById(R.id.info_more2);
        lessInformation = mView.findViewById(R.id.info_moin);
        ligne = mView.findViewById(R.id.ligneVerticale);

        return mView;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d(TAG, "Fragment onStart");

        if (getArguments() != null) {
            Bundle args = getArguments();
            if (args.containsKey(SPOT_INFO))
                listInfos = (args.getStringArray(SPOT_INFO));
        }


        if (listInfos != null & listInfos.length != 0) {getInfosFromList();}

        animator.classicBlink(moreInformation);
        animator.classicBlink(lessInformation);

        layout1.setVisibility(View.VISIBLE);
        layout2.setVisibility(View.VISIBLE);

        layout2.animate().translationY(1000).withLayer().setDuration(10);

        moreInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout1.animate().translationY(-1000).withLayer().setDuration(500);
                layout2.animate().translationY(0).withLayer().setDuration(500);
                informationFragmentClassCallBack.checkIfInformationsCompleted();
            }
        });

        lessInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout1.animate().translationY(0).withLayer().setDuration(500);
                layout2.animate().translationY(1000).withLayer().setDuration(500);
            }
        });

        if(listInfos[1] != null && listInfos[1].equalsIgnoreCase("Vous devez vous rapprochez du spot pour pouvoir accéder au jeux.") || listInfos[1].equalsIgnoreCase("Pas encore de jeu")  ){
            moreInformation.setVisibility(View.GONE);
        }
    }


    /**
     * Cette méthode va vérifier le nombre d'élément dans la liste
     * En fonction, va les attribuer aux elements du fragment
     */
    private void getInfosFromList(){
        Log.d(TAG, "List size = "+ listInfos.length);
        if (listInfos.length == 2){
           setUpForCulture();
        }  else if (listInfos.length == 4){
           setUpForPassage();
        }

        Typeface type = Typeface.createFromAsset(getActivity().getAssets(),"fonts/steinem.ttf");
        spotName.setTypeface(type);
    }

    private void setUpForPassage(){
        Log.i(TAG, "SetUp pour Point de Passage");
        spotName.setText(listInfos[0].toUpperCase());
        informations.setText(listInfos[1]);
        moreInfo1.setText(listInfos[2]);
        moreInfo2.setText(listInfos[3]);
        informations.setTextSize(32);
        moreInfo1.setTextSize(28);
        moreInfo2.setTextSize(28);

        moreInformation.setVisibility(View.VISIBLE);
    }


    private void setUpForCulture(){
        Log.i(TAG, "SetUp pour Point Culture");
        spotName.setText(listInfos[0].toUpperCase());
        informations.setText(listInfos[1]);
        informations.setTextSize(28);

        moreInformation.setVisibility(View.GONE);
        informationFragmentClassCallBack.checkIfInformationsCompleted();
    }



}
