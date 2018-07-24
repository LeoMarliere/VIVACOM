package com.vivacom.leo.perdpaslenord.activities;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.leakcanary.LeakCanary;
import com.vivacom.leo.perdpaslenord.R;
import com.vivacom.leo.perdpaslenord.ViewAnimations;
import com.vivacom.leo.perdpaslenord.objects.TeamClass;

import java.util.Objects;

import static android.view.View.GONE;

/**
 * Created by Leo on 20/02/2018.
 * CodeRevue 14/05
 */

public class RulesExplicationClass extends Activity {

    // -----------------------------------------

    // Variable
    int numLayout = 1;
    static final String TAG = "RulesExplication";

    // Objects
    ViewAnimations animator = new ViewAnimations();

    // Graphique
    RelativeLayout lBlckScreen;
    LinearLayout  explicationLayout_Map, explicationLayout_UI, explicationLayout_Game, explicationLayout_Party;
    Button backButton, nextButton;
    TextView title;

    // -----------------------------------------

    @Override
    public void onBackPressed() { }

    @Override
    public  void onResume(){
        super.onResume();
        Log.d(TAG, "Activity onResume");
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.d(TAG, "Activity onPause");
    }

    // -----------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rules_explication);


        // ------- On associe nos élements graphiques -------
        associateElements();

        // ------- On prépare les layout et les boutons -------
        explicationLayout_Map.setVisibility(View.VISIBLE);
        explicationLayout_UI.setVisibility(GONE);
        explicationLayout_Game.setVisibility(GONE);
        explicationLayout_Party.setVisibility(GONE);
        backButton.setVisibility(GONE);
        nextButton.setVisibility(View.VISIBLE);
        lBlckScreen.setVisibility(View.VISIBLE);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                animator.fadeOutAnimation(lBlckScreen);
            }
        },1000);

        // ------- On créer nos Listener -------
        createBtnAction();

        // ----- On place notre Font -----
        Typeface type = Typeface.createFromAsset(getApplication().getAssets(),"fonts/steinem.ttf");
        title.setTypeface(type);
    }


    /**
     * Cette méthode associe nos éléments graphiques avec nos composants
     */
    private void associateElements(){
        nextButton = findViewById(R.id.btn_suivant);
        backButton = findViewById(R.id.btn_precedent);

        explicationLayout_Game = findViewById(R.id.rules_Jeu);
        explicationLayout_Map = findViewById(R.id.rules_Carte);
        explicationLayout_UI = findViewById(R.id.rules_Interface);
        explicationLayout_Party  = findViewById(R.id.rules_Party);

        title = findViewById(R.id.rulesExplication_Title);
        lBlckScreen= findViewById(R.id.rules_blackScreen);
    }

    /**
     * Cette méthode va créer les OnClickListener pour nos deux boutons
     */
    private void createBtnAction(){
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableBtnFor3Sec();
                if(numLayout == 2){
                    animator.fadeInAnimation(explicationLayout_Map);
                    animator.fadeOutAnimation(explicationLayout_UI);
                    animator.fadeOutAnimation(backButton);
                    numLayout = 1;
                } else if(numLayout == 3){
                    animator.fadeInAnimation(explicationLayout_UI);
                    animator.fadeOutAnimation(explicationLayout_Game);
                    numLayout = 2;
                } else if(numLayout == 4){
                    animator.fadeInAnimation(explicationLayout_Game);
                    animator.fadeOutAnimation(explicationLayout_Party);
                    numLayout = 3;
                }
            }
        });


        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableBtnFor3Sec();
                if(numLayout == 1){
                    animator.fadeInAnimation(explicationLayout_UI);
                    animator.fadeOutAnimation(explicationLayout_Map);
                    animator.fadeInAnimation(backButton);
                    numLayout = 2;
                } else if(numLayout == 2){
                    animator.fadeInAnimation(explicationLayout_Game);
                    animator.fadeOutAnimation(explicationLayout_UI);
                    numLayout = 3;
                } else if(numLayout == 3){
                    animator.fadeInAnimation(explicationLayout_Party);
                    animator.fadeOutAnimation(explicationLayout_Game);
                    numLayout = 4;
                } else if (numLayout == 4){


                    AlertDialog.Builder builder = new AlertDialog.Builder(RulesExplicationClass.this);

                    builder.setMessage("Êtes-vous sur de vouloir commencer ?")
                            .setTitle("Lancement du jeu :");

                    builder.setPositiveButton("Oui, allons-y !!", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent goToInGameActivity = new Intent (getApplicationContext(), InGameActivityClass.class);
                                    startActivity(goToInGameActivity);
                                }
                            });

                    builder.setNegativeButton("Peu être plus tard ...", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();

                }
            }
        });

    }

    /**
     * Cette méthode va désactiver les boutons pendant 3 secondes
     */
    private void disableBtnFor3Sec(){
        backButton.setEnabled(false);
        backButton.setClickable(false);
        nextButton.setEnabled(false);
        nextButton.setClickable(false);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                backButton.setEnabled(true);
                backButton.setClickable(true);
                nextButton.setEnabled(true);
                nextButton.setClickable(true);
            }
        }, 3000);
    }



}
