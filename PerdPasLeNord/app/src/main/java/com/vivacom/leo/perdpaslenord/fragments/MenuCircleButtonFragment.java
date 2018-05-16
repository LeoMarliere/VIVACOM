package com.vivacom.leo.perdpaslenord.fragments;

import android.app.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.vivacom.leo.perdpaslenord.R;

import java.util.Objects;

import es.dmoral.toasty.Toasty;


/**
 * Created by Leo on 05/12/2017.
 */

public class MenuCircleButtonFragment extends android.support.v4.app.Fragment {

    // ---- Object -----
    View mView;
    Button btn_Infos, btn_Photos, btn_Jeu, btn_Carnet, btn_Carte;

    // ---- Parametre -----
    boolean infosChecked, galleryChecked, gameFinish;
    boolean carnetVisible = false, menuOpen = false, carnetAnimationOver = true, actionPossible = true;
    int currentFragment = 1;

    // ---- CallBack -----
    MenuCircleButtonFragmentCallBack menuCircleButtonFragmentCallBack;
    static final String TAG = "MENU";

    // ------------------------------------------------------------

    // Interface CallBack
    public interface MenuCircleButtonFragmentCallBack {
        void slideOutLeftAndInRight(final int currentFragment);
        void hideInGameInterface();
        void transitionRoadBookToGame();
        void transitionGameToRoadBook();
    }

    // Instance
    public static MenuCircleButtonFragment newInstance(Boolean infosChecked, Boolean galleryChecked, Boolean gameChecked, int currentFragment){
        MenuCircleButtonFragment fragment = new MenuCircleButtonFragment();
        Bundle args = new Bundle();
        args.putBoolean("1", infosChecked);
        args.putBoolean("2", galleryChecked);
        args.putBoolean("3", gameChecked);
        args.putInt("4", currentFragment);
        fragment.setArguments(args);
        return fragment;
    }

    // Require empty public constructor
    public MenuCircleButtonFragment() {

    }

    // ------------------------------------------------------------

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof MenuCircleButtonFragment.MenuCircleButtonFragmentCallBack)
            menuCircleButtonFragmentCallBack = (MenuCircleButtonFragment.MenuCircleButtonFragmentCallBack) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        menuCircleButtonFragmentCallBack = null;
    }


    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView =  inflater.inflate(R.layout.fragment_menu_circlebutton, container, false);
        btn_Carnet = mView.findViewById(R.id.menuBtn_Carnet);
        btn_Carte = mView.findViewById(R.id.menuBtn_Carte);
        btn_Infos = mView.findViewById(R.id.menuBtn_Infos);
        btn_Photos = mView.findViewById(R.id.menuBtn_Photos);
        btn_Jeu = mView.findViewById(R.id.menuBtn_Jeu);
        return mView;
    }

    // ------------------------------------------------------------

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // On récupère nos arguments
        if (getArguments() != null) {
            Bundle args = getArguments();
            infosChecked = args.getBoolean("1");
            galleryChecked = args.getBoolean("2");
            gameFinish = args.getBoolean("3");
            currentFragment = args.getInt("4");
        }

        setBtnClickable(true);
        createBtnListener();

    }

    // ----------- Méthodes de SETUP --------------

    /**
     * Cette méthode va créer nos OnClickListener nde nos 5 boutons
     */
    private void createBtnListener(){

        // Bouton Info
        btn_Infos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentFragment != 1 && actionPossible){
                    setActionPossibleAfterTime();
                    currentFragment = 1;
                    menuCircleButtonFragmentCallBack.slideOutLeftAndInRight(currentFragment);
                    if (carnetVisible){carnetVisible=false;}
                }
            }
        });

        // Bouton Photo
        btn_Photos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentFragment != 2 && actionPossible){
                    setActionPossibleAfterTime();
                    currentFragment = 2;
                    menuCircleButtonFragmentCallBack.slideOutLeftAndInRight(currentFragment);
                    if (carnetVisible){carnetVisible=false;}

                }
            }
        });

        // Bouton Jeu
        btn_Jeu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Point Principaux
                if (btn_Jeu.getTag() == "PP" && currentFragment != 3 && actionPossible) {
                    if (gameFinish) {
                        Toasty.error(Objects.requireNonNull(getContext()), "Vous avez déja terminé ce jeu.", Toast.LENGTH_SHORT, true).show();
                    } else {
                        setActionPossibleAfterTime();
                        currentFragment = 3;
                        menuCircleButtonFragmentCallBack.slideOutLeftAndInRight(currentFragment);
                        if (carnetVisible) {
                            carnetVisible = false;
                        }
                    }
                }

                // Point Secondaire
                if (btn_Jeu.getTag() == "PS" && currentFragment != 3 && actionPossible) {
                    setActionPossibleAfterTime();
                    currentFragment = 3;
                    menuCircleButtonFragmentCallBack.slideOutLeftAndInRight(currentFragment);
                }

            }
        });

        // Bouton Carte
        btn_Carte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (actionPossible) {
                    setActionPossibleAfterTime();
                    menuCircleButtonFragmentCallBack.hideInGameInterface();
                    setBtnClickable(false);
                }
            }
        });

        // Bouton Carnet
        btn_Carnet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(actionPossible){
                    setActionPossibleAfterTime();
                    if (carnetVisible){
                        menuCircleButtonFragmentCallBack.transitionRoadBookToGame();
                        carnetVisible = false;
                    } else {
                        menuCircleButtonFragmentCallBack.transitionGameToRoadBook();
                        carnetVisible = true;
                        currentFragment = 10; // On modifie la valeur du currentFragment
                    }
                }
            }
        });

    }

    /**
     * Méthode modifiant le background des boutons
     * En fonction de si les actions ont déja été réalisé
     */
    private void setUpBackgroundBtn(){

        // Si les photos ont déja été regardé, le boutons passe au vert
        if (galleryChecked){btn_Photos.setBackgroundResource(R.drawable.btn_photo_on);}
        else{btn_Photos.setBackgroundResource(R.drawable.btn_photo_off);}

        // Si les infos ont déja été regardé, le boutons passe au vert
        if (infosChecked){btn_Infos.setBackgroundResource(R.drawable.btn_info_on);}
        else{btn_Infos.setBackgroundResource(R.drawable.btn_info_off);}

        // Si le jeu a déja été fait, le boutons passe au vert
        if (gameFinish){
            if(btn_Jeu.getTag() == "PP"){ btn_Jeu.setBackgroundResource(R.drawable.btn_jeu_on);}
            if(btn_Jeu.getTag() == "PS"){ btn_Jeu.setBackgroundResource(R.drawable.btn_prisephoto);}
        } else{
            if(btn_Jeu.getTag() == "PP"){ btn_Jeu.setBackgroundResource(R.drawable.btn_jeu_off);}
            if(btn_Jeu.getTag() == "PS"){ btn_Jeu.setBackgroundResource(R.drawable.btn_prisephoto_off);}
        }
    }


    // ------------- Animation ---------------

    /**
     * Cette méthode anime les différents boutons
     * Elle les fait "sortir"
     */
    public void openMenu(){
        if(!menuOpen) {
            currentFragment = 1;
            btn_Carnet.animate().translationY(-100).withLayer();
            btn_Jeu.animate().translationY(-300).withLayer();
            btn_Photos.animate().translationY(-400).withLayer();
            btn_Infos.animate().translationY(-500).withLayer();
            menuOpen = true;
        }
    }

    /**
     * Cette méthode anime les différents boutons
     * Elle les fait "rentrer"
     */
    public void closeMenu(){
        if (menuOpen) {
            btn_Carnet.animate().translationY(0).withLayer();
            btn_Jeu.animate().translationY(0).withLayer();
            btn_Photos.animate().translationY(0).withLayer();
            btn_Infos.animate().translationY(0).withLayer();
            menuOpen = false;
        } else {
            btn_Carnet.animate().translationY(0).withLayer();
            btn_Jeu.animate().translationY(0).withLayer();
            btn_Photos.animate().translationY(0).withLayer();
            btn_Infos.animate().translationY(0).withLayer();
            menuOpen = false;
            Log.w(TAG, "Erreur de close/open menu");
        }
    }

    // ------------- Modification des boutons ---------------

    /**
     * Cette méthode change le background du bouton Info
     */
    public void setInfoBtnValidate(){
        btn_Infos.setBackgroundResource(R.drawable.btn_info_on);
        infosChecked = true;
    }

    /**
     * Cette méthode change le background du bouton Photo
     */
    public void setPhotoBtnValidate(){
        btn_Photos.setBackgroundResource(R.drawable.btn_photo_on);
        galleryChecked = true;
    }

    /**
     * Cette méthode change le background du bouton Jeu
     */
    public void setGameBtnValidate(int type) {
        currentFragment = 1;
        if(type == 1 ){
            btn_Jeu.setBackgroundResource(R.drawable.btn_jeu_on);
            gameFinish = true;
        } else if (type == 2){
            btn_Jeu.setBackgroundResource(R.drawable.btn_prisephoto);
        }
    }

    /**
     * Cette méthode va bloquer ou déblquer le click sur les boutons
     * @param canClick
     */
    public void setBtnClickable(boolean canClick){

        btn_Carnet.setEnabled(canClick);
        btn_Carnet.setClickable(canClick);

        btn_Carte.setEnabled(canClick);
        btn_Carte.setClickable(canClick);

        btn_Infos.setEnabled(canClick);
        btn_Infos.setClickable(canClick);

        btn_Jeu.setEnabled(canClick);
        btn_Jeu.setClickable(canClick);

        btn_Photos.setEnabled(canClick);
        btn_Photos.setClickable(canClick);

    }

    // ---------------

    /**
     * Cette méthode renvoie un boolean en fonction e si le CarnetDeBord est visible ou non
     * @return
     */
    public boolean isCarnetVisible(){
       return carnetVisible;
    }

    // passe a TRUE la variable actionPossible
    private void setActionPossibleAfterTime(){

        actionPossible = false;

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                actionPossible = true;
            }
        }, 2000);

    }

    /**
     * Cette méthode repasse la variable carnetAnimationOver a TRUE
     * Appelé après l'animation du carnet
     */
    public void setCarnetAnimationOverTrue(){
        carnetAnimationOver = true;
    }


    // Affecte l'image et le tag correspondant - au type de spot - au btn_jeu
    public void changeGameBtnImage(int typeSpot){

        if (typeSpot == 1){
            btn_Jeu.setBackgroundResource(R.drawable.btn_jeu_off);
            btn_Jeu.setTag("PP");
        }

        else if (typeSpot == 2) {
            btn_Jeu.setBackgroundResource(R.drawable.btn_prisephoto_off);
            btn_Jeu.setTag("PS");
        }

        setUpBackgroundBtn();
    }


}








