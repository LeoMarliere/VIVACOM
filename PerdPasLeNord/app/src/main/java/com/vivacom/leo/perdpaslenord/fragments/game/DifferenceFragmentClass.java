package com.vivacom.leo.perdpaslenord.fragments.game;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vivacom.leo.perdpaslenord.R;
import com.vivacom.leo.perdpaslenord.ViewAnimations;
import com.vivacom.leo.perdpaslenord.constant.ConstantInfos;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Leo on 06/02/2018.
 */

public class DifferenceFragmentClass extends Fragment {

    private static final String TAG = "DifferenceGame";
    private static final String INFO = "INFO";
    ViewAnimations animator = new ViewAnimations();
    DifferenceFragmentClassCallBack differenceFragmentClassCallBack;

    // -------

    // Pour la sélection du joueur
    RelativeLayout lPlayerSelection;
    TextView tGameTitle,tGameName,tContinue;
    List<String> playersName;

    // -------

    private RelativeLayout lMainScreen;

    private RelativeLayout layout_Consigne;
    private TextView txtVConsigne, txtVNext;

    private LinearLayout lDif11, lDif12, lDif13, lDif14, lDif15;

    private List<LinearLayout> difCaseList_good = new ArrayList<>();

    private ImageView myImage;

    private ImageView correct1, correct2, correct3, correct4, correct5, error1, error2, error3;
    private List<ImageView> correctImageList = new ArrayList<>();
    private List<ImageView> errorImageList = new ArrayList<>();

    // -------

    private int nbCorrect = 0;
    private int nbError = 0;

    int nbRoatationPlayerSelection = 13;
    int numName= 0;

    // ------------------------------------------------------------------------------

    public DifferenceFragmentClass(){
        // Empty
    }

    public interface DifferenceFragmentClassCallBack{
        void whenGameIsValidate(Boolean correct);
        List<String> getPlayerName();
        String getGameTitle();
    }

    public static DifferenceFragmentClass newInstance(String spotName){
        DifferenceFragmentClass fragment = new DifferenceFragmentClass();
        Bundle args = new Bundle();
        args.putString(INFO, spotName);
        fragment.setArguments(args);
        return fragment;
    }

    // ------------------------------------------------------------------------------

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof DifferenceFragmentClassCallBack)
            differenceFragmentClassCallBack = (DifferenceFragmentClassCallBack) activity;
        Log.d(TAG, "Fragment onAttach");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        differenceFragmentClassCallBack = null;
        Log.d(TAG, "Fragment onDetach");
    }

    public void onDestroy(){
        super.onDestroy();
        Log.d(TAG, "Fragment onDestroy");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_difference_game,container,false);
        // Association des Elements graphiques
        associateElements(view);
        return view;
    }

    // ------------------------------------------------------------------------------


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "Fragment onStart");


        placeImageInList();

        setUpGame();
        createListenerForGame();
        setUpGamerSelection();


    }

    // ------------- Méthode de préparation du jeu ------------

    private void associateElements(View view){

        layout_Consigne = view.findViewById(R.id.dif_layoutConsigne);
        txtVConsigne = view.findViewById(R.id.dif_consigne);
        txtVNext = view.findViewById(R.id.dif_next);

        lMainScreen = view.findViewById(R.id.dif_ImageLimite);
        myImage = view.findViewById(R.id.dif_Image);

        lDif11 = view.findViewById(R.id.dif_dif1);
        lDif12 = view.findViewById(R.id.dif_dif2);
        lDif13 = view.findViewById(R.id.dif_dif3);
        lDif14 = view.findViewById(R.id.dif_dif4);
        lDif15 = view.findViewById(R.id.dif_dif5);


        // ---
        lPlayerSelection = view.findViewById(R.id.playerSelection);
        tGameName = view.findViewById(R.id.gamerName);
        tGameTitle = view.findViewById(R.id.gameTitle);
        tContinue = view.findViewById(R.id.blink_continue);

        // --

        correct1 = view.findViewById(R.id.dif_correct1);
        correct2 = view.findViewById(R.id.dif_correct2);
        correct3 = view.findViewById(R.id.dif_correct3);
        correct4 = view.findViewById(R.id.dif_correct4);
        correct5 = view.findViewById(R.id.dif_correct5);

        error1 = view.findViewById(R.id.dif_error1);
        error2 = view.findViewById(R.id.dif_error2);
        error3 = view.findViewById(R.id.dif_error3);

    }

    private void setUpGame(){
        if (getArguments() != null) {
            Bundle args = getArguments();
            if (args.containsKey(INFO)){
                if (args.getString(INFO).equals("Le Rang Du Beauregard")){setUpForBeauregard();}
                else if (args.getString(INFO).equals("")){

                } else if (args.getString(INFO).equals("")){

                }
            }
        }
    }

    private void setUpForBeauregard(){
        difCaseList_good.clear();
        difCaseList_good.add(lDif11);
        difCaseList_good.add(lDif12);
        difCaseList_good.add(lDif13);
        difCaseList_good.add(lDif14);
        difCaseList_good.add(lDif15);
    }

    private void placeImageInList(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                correctImageList.add(correct1);
                correctImageList.add(correct2);
                correctImageList.add(correct3);
                correctImageList.add(correct4);
                correctImageList.add(correct5);

                errorImageList.add(error1);
                errorImageList.add(error2);
                errorImageList.add(error3);
            }
        }).start();

        // On passe chaque image en INVISIBLE
        for (ImageView image : correctImageList){image.setVisibility(View.INVISIBLE);}
        for (ImageView image : errorImageList){image.setVisibility(View.INVISIBLE);}

    }


    // ------------- Méthode de fonctionnement du jeu ------------

        // Cette méthode créer les Listener pour nos "cases"
    private void createListenerForGame(){

        new Thread(new Runnable() {
            @Override
            public void run() {

                // Pour chaque layout de notre list
                for (final LinearLayout layout : difCaseList_good){
                    // On créer un Listener
                    layout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (layout.isClickable()) {

                                nbCorrect++;
                                if (checkIfWin()){showWinMessage();}
                                else {
                                    layout.setClickable(false);
                                    addCorrectImage();
                                }

                            }
                        }
                    });
                }

                // Pour le reste de l'écran
                lMainScreen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        nbError++;
                        if (checkIfLoose()){showLooseMessage();}
                        else {addErrorImage();}
                    }
                });

            }
        }).start();
    }

        // Cette méthode prépare l'écran de la consigne
    private void startGame(){
        layout_Consigne.setVisibility(View.VISIBLE);
        txtVConsigne.setVisibility(View.INVISIBLE);
        txtVNext.setVisibility(View.INVISIBLE);
        //txtVConsigne.setText(ConstantInfos.CONSIGNE_DIFFERENCE);

        animator.fadeInAnimation(txtVConsigne);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                txtVNext.setVisibility(View.VISIBLE);
                animator.classicBlink(tContinue);

                txtVConsigne.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        animator.fadeOutAnimation(txtVConsigne);
                    }
                });
            }
        }, 5000);




    }

        // Ces méthode vérifie si le nombre de réponse correct atteint 7
        // Ou si le nombre de réponse fausse atteint 3
    private boolean checkIfWin(){

        boolean result = false;
        if (nbCorrect == 5){
            result = true;
        }

        return result;

    }

    private boolean checkIfLoose(){

        boolean result = false;
        if (nbError > 3){
            result = true;
        }

        return result;
    }

        // Ces méthodes font apparaitre les Image "correct" ou "error" dans l'ordre
    private void addCorrectImage(){

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                boolean done = false;
                for (ImageView myImage : correctImageList){
                    if (myImage != null && myImage.getVisibility() == View.INVISIBLE && !done ){
                        myImage.setVisibility(View.VISIBLE);
                        done = true;
                        Log.d(TAG, "Image Correct add");
                    }
                }
            }
        });


    }

    private void addErrorImage(){

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                boolean done = false;
                for (ImageView myImage : errorImageList){
                    if (myImage != null && myImage.getVisibility() == View.INVISIBLE && !done){
                        myImage.setVisibility(View.VISIBLE);
                        done = true;
                        Log.d(TAG, "Image Error add");
                    }
                }

            }
        });
    }

        // Ces méthodes affiche le message de victoire/defaite
        // Et créer le Listener qui mets fin au jeu
    private void showWinMessage(){
        animator.fadeInAnimation(layout_Consigne);
        txtVConsigne.setText("Bravo !! \n Vous avez trouvé toutes les erreurs !! \n \n ");

        txtVConsigne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                differenceFragmentClassCallBack.whenGameIsValidate(true);
            }
        });

    }

    private void showLooseMessage(){
        animator.fadeInAnimation(layout_Consigne);
        txtVConsigne.setText("Dommmage !! \n Vous y étiez presque !!");

        txtVConsigne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                differenceFragmentClassCallBack.whenGameIsValidate(false);
            }
        });

    }


    // ------------- Méthode de séléction du joueur ------------

    /**
     * Cette méthode prépare l'écran de séléction d'un joueur
     */
    public void setUpGamerSelection(){
        lPlayerSelection.setVisibility(View.VISIBLE);
        tGameName.setVisibility(View.VISIBLE);
        tGameTitle.setVisibility(View.VISIBLE);
        tGameTitle.setText(differenceFragmentClassCallBack.getGameTitle());
        playersName = differenceFragmentClassCallBack.getPlayerName();

        Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                tGameTitle.animate().translationY(-200).withLayer();
            }
        },1500);

        Handler handler2 = new Handler();
        handler2.postDelayed(new Runnable() {
            @Override
            public void run() {
                tGameName.setVisibility(View.VISIBLE);
                tGameName.setText("C'est au tour de : \n " + playersName.get(numName));
                startNameRotation();
            }
        }, 2000);
    }

    /**
     * Cette méthode provoque une rotation des noms de la liste de joueurs
     */
    public void startNameRotation(){
        // On recupère la liste des joueurs
        final int listSize = playersName.size();

        final Handler nameRotation = new Handler();
        nameRotation.post(new Runnable() {
            int v = numName;
            int z = listSize;
            int nbRotation = 0;
            @Override
            public void run() {
                nbRotation++;
                tGameName.setText("C'est au tour de : \n " + playersName.get(v));
                v++;
                if (v == z) {v = 0;}
                // On défini le nombre de rotation
                if (nbRotation > nbRoatationPlayerSelection){
                    nameRotation.removeCallbacks(this);
                    whenRotationOver();}
                else {
                    // On continue la rotation
                    nameRotation.postDelayed(this, 100);
                }
            }
        });
    }

    /**
     * Cette méthode est appelé lorsque le joueur est séléctionné
     */
    private void whenRotationOver(){
        tContinue.setVisibility(View.VISIBLE);
        animator.classicBlink(tContinue);
        lPlayerSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animator.fadeOutAnimation(lPlayerSelection);
                startGame();
                lPlayerSelection.setClickable(false);
            }
        });
    }


}
