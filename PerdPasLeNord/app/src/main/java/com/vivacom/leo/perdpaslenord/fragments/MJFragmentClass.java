package com.vivacom.leo.perdpaslenord.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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

import java.util.List;

/**
 * Created by Leo on 02/02/2018.
 */

public class MJFragmentClass extends Fragment {

    RelativeLayout lMJ_Screen, lMJ_Bandeau, lMJ_textBox;

    TextView MJ_Texte;

    ImageView iMJ_Image;


    MJFragmentCallBack mjFragmentCallBack;

    String[] messageList;

    int currentMessage;

    boolean canChange = true;

    ViewAnimations animator = new ViewAnimations();

    public final String TAG= "MJ_Screen";


    // ------- CallBack -------

    public MJFragmentClass(){
        // Empty
    }

    public interface MJFragmentCallBack{
        void hideMJFragment();
    }

    public static MJFragmentClass newInstance(){
        MJFragmentClass fragment = new MJFragmentClass();
        return fragment;
    }


    // ------- Méthode onAttach / onDetach / onResume / onPause -------

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof MJFragmentClass.MJFragmentCallBack)
            mjFragmentCallBack = (MJFragmentClass.MJFragmentCallBack) activity;
        Log.d(TAG, "Activity onAttach");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mjFragmentCallBack = null;
        Log.d(TAG, "Activity onDetach");
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.d(TAG, "Activity onPause");
    }

    @Override
    public  void onResume(){
        super.onResume();
        Log.d(TAG, "Activity onResume");
    }


    // ----------


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_guide,container,false);
        lMJ_Screen = view.findViewById(R.id.MJ_screen);
        lMJ_Bandeau = view.findViewById(R.id.MJ_Bandeau);
        lMJ_textBox = view.findViewById(R.id.MJ_textBox);
        MJ_Texte = view.findViewById(R.id.MJ_consigne);
        iMJ_Image = view.findViewById(R.id.MJ_image);
        return view;
    }




    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d(TAG, "Activity onStart");


        lMJ_Screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Nothing
            }
        });


        lMJ_Bandeau.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(canChange){changeMessage();}
            }
        });


    }


        // Cette méthode affecte une liste de message au MJ
    public void setMessageList(String[] messages){
        this.messageList = messages;
    }

        // Cette méthode affiche le premier message de la liste
    public void showFirstMessage(){
        currentMessage = 0;
        lMJ_textBox.setVisibility(View.GONE);
        lMJ_Bandeau.setClickable(true);

        Handler handler = new Handler();
        handler.postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        animator.fadeInAnimation(lMJ_textBox);
                        MJ_Texte.setText(messageList[currentMessage]);
                    }
                }, 700);
    }

        // Cette méthode déclanche le changement de message lors d'un clic
    private void changeMessage(){
        currentMessage++;

        // Si tous les messages ont été affiché, on fait disaraitre la bandeau
        if(currentMessage == messageList.length){
            mjFragmentCallBack.hideMJFragment();
            lMJ_Bandeau.setClickable(false);
        }
        else {

            changeMJExpression(2);
            if(currentMessage == messageList.length -1){changeMJExpression(1);}

            animator.fadeOutAnimation(MJ_Texte);
            canChange = false;

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    MJ_Texte.setText(messageList[currentMessage]);
                    animator.fadeInAnimation(MJ_Texte);
                }
            }, 1000);

            Handler handler2 = new Handler();
            handler2.postDelayed(new Runnable() {
                @Override
                public void run() {
                    canChange = true;
                }
            }, 2000);
        }






    }

        // Cette méthode change le design du guide en fonction du numéro en paramètre
    public void changeMJExpression(int num){

        switch (num){
            case 1 :
                iMJ_Image.setImageResource(R.drawable.guide_happy);
                break;
            case 2 :
                iMJ_Image.setImageResource(R.drawable.guide_neutre);
                break;
            case 3 :
                iMJ_Image.setImageResource(R.drawable.guide_surpris);
                break;

        }

    }










}
