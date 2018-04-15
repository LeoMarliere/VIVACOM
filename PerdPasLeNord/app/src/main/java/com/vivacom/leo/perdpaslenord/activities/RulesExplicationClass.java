package com.vivacom.leo.perdpaslenord.activities;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vivacom.leo.perdpaslenord.R;
import com.vivacom.leo.perdpaslenord.ViewAnimations;
import com.vivacom.leo.perdpaslenord.objects.TeamClass;

/**
 * Created by Leo on 20/02/2018.
 */

public class RulesExplicationClass extends Activity {

    static final String TAG = "RulesExplication";

    // Objects
    ViewAnimations animator = new ViewAnimations();

    // Graphique
    LinearLayout  explicationLayout_Map, explicationLayout_UI, explicationLayout_Game;
    Button backButton, nextButton;

    // Variable
    int numLayout = 1;

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

    // ----------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.slide_to_right, R.anim.slide_from_left);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rules_explication);

        // ------- On associe nos élements graphiques -------
        associateElements();

        // ------- On prépare les layout et les boutons -------
        explicationLayout_Map.setVisibility(View.VISIBLE);
        explicationLayout_UI.setVisibility(View.GONE);
        explicationLayout_Game.setVisibility(View.GONE);

        backButton.setVisibility(View.GONE);
        nextButton.setVisibility(View.VISIBLE);

        // ------- On créer nos Listener -------
        createBtnAction();
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
                    Intent goToInGameActivity = new Intent (getApplicationContext(), InGameActivityClass.class);
                    startActivity(goToInGameActivity);
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
