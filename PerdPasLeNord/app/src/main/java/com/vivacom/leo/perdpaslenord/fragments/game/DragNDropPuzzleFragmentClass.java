package com.vivacom.leo.perdpaslenord.fragments.game;

import android.app.Activity;
import android.content.ClipData;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ThemedSpinnerAdapter;

import com.vivacom.leo.perdpaslenord.R;
import com.vivacom.leo.perdpaslenord.ViewAnimations;
import com.vivacom.leo.perdpaslenord.constant.ConstantInfos;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Leo on 21/09/2017.
 */

public class DragNDropPuzzleFragmentClass extends Fragment {

    // ------------------- Elements Graphiques ---------------------
    TextView txtVConsigne, selection_title;
    LinearLayout layoutForGame;
    LinearLayout layoutOneOne,layoutOneTwo,layoutOneThree,layoutOneFour;
    LinearLayout layoutTwoOne,layoutTwoTwo,layoutTwoThree,layoutTwoFour;
    LinearLayout layoutThreeOne,layoutThreeTwo,layoutThreeThree,layoutThreeFour;
    ImageView image1,image2,image3,image4,image5,image6,image7,image8,image9,image10,image11;
    View goodImageFor2, goodImageFor3,goodImageFor4,goodImageFor5,goodImageFor6,goodImageFor7,goodImageFor8,goodImageFor9, goodImageFor10,goodImageFor11,goodImageFor12;

    // -------

    RelativeLayout lPlayerSelection;
    TextView tGameTitle,tGameName,tContinue;

    // ------------------------

    List<LinearLayout> layoutList;
    List<String> playersName;

    int numName = 0, nbBonneReponse = 0;
    int nbRotationAFaire;
    public String startMessage,endMessage;


    public boolean gameFinish = false;

    public final String TAG = "PUZZLE";
    public static final String TXT_INFOS = "TXT";

    // ------------------- Elements diverts ---------------------

    DragNDropPuzzleFragmentClassCallBack dragNDropPuzzleFragmentClassCallBack;
    ViewAnimations animator = new ViewAnimations();

    // ------------------

    public DragNDropPuzzleFragmentClass(){
        // Require empty public constructor
    }

    public interface DragNDropPuzzleFragmentClassCallBack{
        void whenGameIsValidate(Boolean correct);
        List<String> getPlayerName();
        String getGameTitle();
        void handleBtnClick(boolean canClick);
    }

    public static DragNDropPuzzleFragmentClass newInstance(String spotName){
        DragNDropPuzzleFragmentClass fragment = new DragNDropPuzzleFragmentClass();
        Bundle args = new Bundle();
        args.putString(TXT_INFOS, spotName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof DragNDropPuzzleFragmentClassCallBack)
            dragNDropPuzzleFragmentClassCallBack = (DragNDropPuzzleFragmentClassCallBack) activity;
        Log.i(TAG, "Fragment onAttach");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        dragNDropPuzzleFragmentClassCallBack = null;
        Log.i(TAG, "Fragment onDetach");
    }

    public void onDestroy(){
        super.onDestroy();
        recycleAllBitmap();
        Log.i(TAG, "Fragment onDestroy");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_drag_n_drop_puzzle,container,false);
        // Association des Elements graphiques
        associateElements(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.i(TAG, "Fragment onStart");

        // ----------------- Affectation des Events  -----------------------
        createListener();
        createLayoutList();

        // ------- Sélection de la question -------
        setUpQuestion();

        // ----- Lancement de la séléction du joueur -----
        setUpGamerSelection();

    }

    /**
     * Cette méthode associe nos elements a leurs composants graphiques
     * @param view
     */
    public void associateElements(final View view){
        // ---------------- Association des Elements graphiques -----------
        // ----------------------------- Layout ---------------------------
        txtVConsigne =  view.findViewById(R.id.dndPuzzle_txtVConsigne);
        //---
        layoutForGame = view.findViewById(R.id.dndPuzzle_GameLayout);
        //---
        layoutOneOne = view.findViewById(R.id.dndPuzzle_OneOne);
        layoutOneTwo = view.findViewById(R.id.dndPuzzle_OneTwo);
        layoutOneThree = view.findViewById(R.id.dndPuzzle_OneThree);
        layoutOneFour = view.findViewById(R.id.dndPuzzle_OneFour);
        layoutTwoOne = view.findViewById(R.id.dndPuzzle_TwoOne);
        layoutTwoTwo = view.findViewById(R.id.dndPuzzle_TwoTwo);
        layoutTwoThree = view.findViewById(R.id.dndPuzzle_TwoThree);
        layoutTwoFour = view.findViewById(R.id.dndPuzzle_TwoFour);
        layoutThreeOne = view.findViewById(R.id.dndPuzzle_ThreeOne);
        layoutThreeTwo = view.findViewById(R.id.dndPuzzle_ThreeTwo);
        layoutThreeThree = view.findViewById(R.id.dndPuzzle_ThreeThree);
        layoutThreeFour = view.findViewById(R.id.dndPuzzle_ThreeFour);
        // ---
        lPlayerSelection = view.findViewById(R.id.playerSelection);
        tGameName = view.findViewById(R.id.gamerName);
        tGameTitle = view.findViewById(R.id.gameTitle);
        tContinue = view.findViewById(R.id.blink_continue);
        // ----------------------------- Image ----------------------------
        image1 = view.findViewById(R.id.dndPuzzle_image1);
        image2 = view.findViewById(R.id.dndPuzzle_image2);
        image3 = view.findViewById(R.id.dndPuzzle_image3);
        image4 = view.findViewById(R.id.dndPuzzle_image4);
        image5 = view.findViewById(R.id.dndPuzzle_image5);
        image6 = view.findViewById(R.id.dndPuzzle_image6);
        image7 = view.findViewById(R.id.dndPuzzle_image7);
        image8 = view.findViewById(R.id.dndPuzzle_image8);
        image9 = view.findViewById(R.id.dndPuzzle_image9);
        image10 = view.findViewById(R.id.dndPuzzle_image10);
        image11 = view.findViewById(R.id.dndPuzzle_image11);

        selection_title = view.findViewById(R.id.selection_title);
        Typeface type = Typeface.createFromAsset(getActivity().getAssets(),"fonts/steinem.ttf");
        selection_title.setTypeface(type);
    }

    /**
     * Cette méthode créer des listener pour nos images et nos layouts
     */
    public void createListener(){
        // ----------------- Affectation des Events  -----------------------
        // -------------------- Events OnDrag ------------------------------
        layoutOneOne.setOnDragListener(new MyDragListener());
        layoutOneTwo.setOnDragListener(new MyDragListener());
        layoutOneThree.setOnDragListener(new MyDragListener());
        layoutOneFour.setOnDragListener(new MyDragListener());
        layoutTwoOne.setOnDragListener(new MyDragListener());
        layoutTwoTwo.setOnDragListener(new MyDragListener());
        layoutTwoThree.setOnDragListener(new MyDragListener());
        layoutTwoFour.setOnDragListener(new MyDragListener());
        layoutThreeOne.setOnDragListener(new MyDragListener());
        layoutThreeTwo.setOnDragListener(new MyDragListener());
        layoutThreeThree.setOnDragListener(new MyDragListener());
        layoutThreeFour.setOnDragListener(new MyDragListener());
        layoutForGame.setOnDragListener(new MyDragListener());
        //----------------------

        txtVConsigne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gameFinish){
                    dragNDropPuzzleFragmentClassCallBack.whenGameIsValidate(true);
                } else {
                    dragNDropPuzzleFragmentClassCallBack.handleBtnClick(false);
                    animator.fadeOutFadeInAnimation(txtVConsigne,layoutForGame);
                    selectLayoutWhitchCanBeClickable();
                }
            }
        });
    }

    /**
     * Cette méthode créer nos OnDragListener au lancement du fragment
     */
    public void createLayoutList(){
        layoutList = new ArrayList<>();

        layoutList.add(layoutOneOne);
        layoutList.add(layoutOneTwo);
        layoutList.add(layoutOneThree);
        layoutList.add(layoutOneFour);

        layoutList.add(layoutTwoOne);
        layoutList.add(layoutTwoTwo);
        layoutList.add(layoutTwoThree);
        layoutList.add(layoutTwoFour);

        layoutList.add(layoutThreeOne);
        layoutList.add(layoutThreeTwo);
        layoutList.add(layoutThreeThree);
        layoutList.add(layoutThreeFour);
    }

    /**
     * Cette méthode vérifie le spot concerné et prépare la question en fonction
     */
    public void setUpQuestion(){
        if (getArguments() != null) {
            Bundle args = getArguments();
            if (args.containsKey(TXT_INFOS)) {
                if (Objects.equals(args.getString(TXT_INFOS), ConstantInfos.NAME_OPERA)) {
                    setUpForOperaDeLille();
                    nbRotationAFaire = 14;
                } else if (Objects.equals(args.getString(TXT_INFOS), ConstantInfos.NAME_GRANDGARDE)) {
                    setUpForGrandGarde();
                    nbRotationAFaire = 15;
                } else if (Objects.equals(args.getString(TXT_INFOS), ConstantInfos.NAME_TREILLE)) {
                    setUpForNotreDameDeLaTreille();
                    nbRotationAFaire = 16;
                }
            }
        }
    }
    // ----------------- Méthode de préparation du puzzle ---------------------------------

    /**
     * Méthode affectant les images pour le cas du spot OPERA DE LILLE
     */
    private void setUpForOperaDeLille(){
        // On affecte les images a leur elements
        /*
        image1.setImageResource(R.drawable.puzzle_opera_p6);
        image2.setImageResource(R.drawable.puzzle_opera_p11);
        image3.setImageResource(R.drawable.puzzle_opera_p5);
        image4.setImageResource(R.drawable.puzzle_opera_p9);
        image5.setImageResource(R.drawable.puzzle_opera_p2);
        image6.setImageResource(R.drawable.puzzle_opera_p8);
        image7.setImageResource(R.drawable.puzzle_opera_p12);
        image8.setImageResource(R.drawable.puzzle_opera_p3);
        image9.setImageResource(R.drawable.puzzle_opera_p4);
        image10.setImageResource(R.drawable.puzzle_opera_p10);
        image11.setImageResource(R.drawable.puzzle_opera_p7);
        */

        image1.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.drawable.puzzle_opera_p6, 150, 150));
        image2.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.drawable.puzzle_opera_p11, 150, 150));
        image3.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.drawable.puzzle_opera_p5, 150, 150));
        image4.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.drawable.puzzle_opera_p9, 150, 150));
        image5.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.drawable.puzzle_opera_p2, 150, 150));
        image6.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.drawable.puzzle_opera_p8, 150, 150));
        image7.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.drawable.puzzle_opera_p12, 150, 150));
        image8.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.drawable.puzzle_opera_p3, 150, 150));
        image9.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.drawable.puzzle_opera_p4, 150, 150));
        image10.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.drawable.puzzle_opera_p10, 150, 150));
        image11.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.drawable.puzzle_opera_p7, 150, 150));


        // pas de 12


        // On affecte chaque bonne image à sa bonne place
        // pas de 1
        goodImageFor2 = layoutTwoOne.getChildAt(0);
        goodImageFor3 = layoutTwoFour.getChildAt(0);
        goodImageFor4 = layoutThreeOne.getChildAt(0);
        goodImageFor5 = layoutOneThree.getChildAt(0);
        goodImageFor6 = layoutOneOne.getChildAt(0);
        goodImageFor7 = layoutThreeThree.getChildAt(0);
        goodImageFor8 = layoutTwoTwo.getChildAt(0);
        goodImageFor9 = layoutOneFour.getChildAt(0);
        goodImageFor10 = layoutThreeTwo.getChildAt(0);
        goodImageFor11 = layoutOneTwo.getChildAt(0);
        goodImageFor12 = layoutTwoThree.getChildAt(0);

        startMessage = "Oh Non !!! \n Notre magnifique puzzle de l'Opéra c'est mélangé ... \n \n A vous de le recomposer !!";
        endMessage = "Félicitation !!!  \n Vous êtes vraiment très doué pour les puzzles. \n \n Cliquer sur l'écran pour débloquer un mot !!";
    }

    /**
     * Méthode affectant les images pour le cas du spot VIEILLE BOURSE
     */
    private void setUpForGrandGarde(){
        // On affecte les images a leur elements
        /*
        image1.setImageResource(R.drawable.puzzle_grandgarde_p6);
        image2.setImageResource(R.drawable.puzzle_grandgarde_p11);
        image3.setImageResource(R.drawable.puzzle_grandgarde_p5);
        image4.setImageResource(R.drawable.puzzle_grandgarde_p9);
        image5.setImageResource(R.drawable.puzzle_grandgarde_p2);
        image6.setImageResource(R.drawable.puzzle_grandgarde_p8);
        image7.setImageResource(R.drawable.puzzle_grandgarde_p12);
        image8.setImageResource(R.drawable.puzzle_grandgarde_p3);
        image9.setImageResource(R.drawable.puzzle_grandgarde_p4);
        image10.setImageResource(R.drawable.puzzle_grandgarde_p10);
        image11.setImageResource(R.drawable.puzzle_grandgarde_p7);
        */
        // pas de 12

        image1.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.drawable.puzzle_opera_p6, 150, 150));
        image2.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.drawable.puzzle_opera_p11, 150, 150));
        image3.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.drawable.puzzle_opera_p5, 150, 150));
        image4.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.drawable.puzzle_opera_p9, 150, 150));
        image5.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.drawable.puzzle_opera_p2, 150, 150));
        image6.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.drawable.puzzle_opera_p8, 150, 150));
        image7.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.drawable.puzzle_opera_p12, 150, 150));
        image8.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.drawable.puzzle_opera_p3, 150, 150));
        image9.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.drawable.puzzle_opera_p4, 150, 150));
        image10.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.drawable.puzzle_opera_p10, 150, 150));
        image11.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.drawable.puzzle_opera_p7, 150, 150));

        // On affecte chaque bonne image à sa bonne place
        // pas de 1
        goodImageFor2 = layoutTwoOne.getChildAt(0);
        goodImageFor3 = layoutTwoFour.getChildAt(0);
        goodImageFor4 = layoutThreeOne.getChildAt(0);
        goodImageFor5 = layoutOneThree.getChildAt(0);
        goodImageFor6 = layoutOneOne.getChildAt(0);
        goodImageFor7 = layoutThreeThree.getChildAt(0);
        goodImageFor8 = layoutTwoTwo.getChildAt(0);
        goodImageFor9 = layoutOneFour.getChildAt(0);
        goodImageFor10 = layoutThreeTwo.getChildAt(0);
        goodImageFor11 = layoutOneTwo.getChildAt(0);
        goodImageFor12 = layoutTwoThree.getChildAt(0);

        startMessage = "Oh Non !!! \n C'est le bazar au niveau de la Grand'Garde ... \n \n Aider moi a recomposer le puzzle !!";
        endMessage = "Félicitation !!!  \n Vous êtes vraiment très doué pour les puzzles. \n \n Cliquer sur l'écran pour débloquer un mot.";
    }

    /**
     * Méthode affectant les images pour le cas du spot NOTRE DAME DE LA TREILLE
     */
    private void setUpForNotreDameDeLaTreille(){
        // On affecte les images a leur elements
        /*
        image1.setImageResource(R.drawable.puzzle_treille_p7);
        image2.setImageResource(R.drawable.puzzle_treille_p3);
        image3.setImageResource(R.drawable.puzzle_treille_p2);
        image4.setImageResource(R.drawable.puzzle_treille_p9);
        image5.setImageResource(R.drawable.puzzle_treille_p12);
        image6.setImageResource(R.drawable.puzzle_treille_p6);
        image7.setImageResource(R.drawable.puzzle_treille_p5);
        image8.setImageResource(R.drawable.puzzle_treille_p11);
        image9.setImageResource(R.drawable.puzzle_treille_p8);
        image10.setImageResource(R.drawable.puzzle_treille_p4);
        image11.setImageResource(R.drawable.puzzle_treille_p10);
        */

        image1.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.drawable.puzzle_opera_p7, 150, 150));
        image2.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.drawable.puzzle_opera_p3, 150, 150));
        image3.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.drawable.puzzle_opera_p2, 150, 150));
        image4.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.drawable.puzzle_opera_p9, 150, 150));
        image5.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.drawable.puzzle_opera_p12, 150, 150));
        image6.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.drawable.puzzle_opera_p6, 150, 150));
        image7.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.drawable.puzzle_opera_p5, 150, 150));
        image8.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.drawable.puzzle_opera_p11, 150, 150));
        image9.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.drawable.puzzle_opera_p8, 150, 150));
        image10.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.drawable.puzzle_opera_p4, 150, 150));
        image11.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.drawable.puzzle_opera_p10, 150, 150));


        // On affecte chaque bonne image à sa bonne place
        // pas de 1
        goodImageFor2 = layoutOneThree.getChildAt(0);
        goodImageFor3 = layoutOneTwo.getChildAt(0);
        goodImageFor4 = layoutThreeTwo.getChildAt(0);
        goodImageFor5 = layoutTwoThree.getChildAt(0);
        goodImageFor6 = layoutTwoTwo.getChildAt(0);
        goodImageFor7 = layoutOneOne.getChildAt(0);
        goodImageFor8 = layoutThreeOne.getChildAt(0);
        goodImageFor9 = layoutOneFour.getChildAt(0);
        goodImageFor10 = layoutThreeThree.getChildAt(0);
        goodImageFor11 = layoutTwoFour.getChildAt(0);
        goodImageFor12 = layoutTwoOne.getChildAt(0);

        startMessage = "Oh Non !!! \n La Cathédrale est sans dessus dessous... \n \n Vite, remettez moi un peu d'ordre !!";
        endMessage = "Félicitation !!! \n Vous êtes vraiment très doué pour les puzzles. \n \n Cliquer sur l'écran pour continuer";

    }

    // ----------------------------------------------------------------------------------

    /**
     * Méthode vérifiant si chaque image est à sa bonne place
     * Si c'est le cas, on renvoi TRUE, sinon, on renvoi FALSE
     * @return
     */
    public boolean checkIfCorrect(){
        nbBonneReponse = 0;
        if(layoutOneTwo.getChildAt(0) != null){if (layoutOneTwo.getChildAt(0)==(goodImageFor2)){nbBonneReponse++;}}
        if(layoutOneThree.getChildAt(0) != null){if (layoutOneThree.getChildAt(0)==(goodImageFor3)){nbBonneReponse++;}}
        if(layoutOneFour.getChildAt(0) != null){if (layoutOneFour.getChildAt(0)==(goodImageFor4)){nbBonneReponse++;}}
        if(layoutTwoOne.getChildAt(0) != null){if (layoutTwoOne.getChildAt(0)==(goodImageFor5)){nbBonneReponse++;}}
        if(layoutTwoTwo.getChildAt(0) != null){if (layoutTwoTwo.getChildAt(0)==(goodImageFor6)){nbBonneReponse++;}}
        if(layoutTwoThree.getChildAt(0) != null){if (layoutTwoThree.getChildAt(0)==(goodImageFor7)){nbBonneReponse++;}}
        if(layoutTwoFour.getChildAt(0) != null){if (layoutTwoFour.getChildAt(0)==(goodImageFor8)){nbBonneReponse++;}}
        if(layoutThreeOne.getChildAt(0) != null){if (layoutThreeOne.getChildAt(0)==(goodImageFor9)){nbBonneReponse++;}}
        if(layoutThreeTwo.getChildAt(0) != null){if (layoutThreeTwo.getChildAt(0)==(goodImageFor10)){nbBonneReponse++;}}
        if(layoutThreeThree.getChildAt(0) != null){if (layoutThreeThree.getChildAt(0)==(goodImageFor11)){nbBonneReponse++;}}
        if(layoutThreeFour.getChildAt(0) != null){if (layoutThreeFour.getChildAt(0)==(goodImageFor12)){nbBonneReponse++;}}
        Log.d("PUZZLE CORRECTION", "Nb bonnes reponses : "+nbBonneReponse);
        return nbBonneReponse == 11;
    }

    // Cette méthode va recycler nos Bitmap
    private void recycleAllBitmap(){
        if(image1.getDrawable() != null){
            ((BitmapDrawable)image1.getDrawable()).getBitmap().recycle();
            image1.setImageDrawable(null);
        }
        if(image2.getDrawable() != null){
            ((BitmapDrawable)image2.getDrawable()).getBitmap().recycle();
            image2.setImageDrawable(null);
        }
        if(image3.getDrawable() != null){
            ((BitmapDrawable)image3.getDrawable()).getBitmap().recycle();
            image3.setImageDrawable(null);
        }
        if(image4.getDrawable() != null){
            ((BitmapDrawable)image4.getDrawable()).getBitmap().recycle();
            image4.setImageDrawable(null);
        }
        if(image5.getDrawable() != null){
            ((BitmapDrawable)image5.getDrawable()).getBitmap().recycle();
            image5.setImageDrawable(null);
        }
        if(image6.getDrawable() != null){
            ((BitmapDrawable)image6.getDrawable()).getBitmap().recycle();
            image6.setImageDrawable(null);
        }
        if(image7.getDrawable() != null){
            ((BitmapDrawable)image7.getDrawable()).getBitmap().recycle();
            image7.setImageDrawable(null);
        }
        if(image8.getDrawable() != null){
            ((BitmapDrawable)image8.getDrawable()).getBitmap().recycle();
            image8.setImageDrawable(null);
        }
        if(image9.getDrawable() != null){
            ((BitmapDrawable)image9.getDrawable()).getBitmap().recycle();
            image9.setImageDrawable(null);
        }
        if(image10.getDrawable() != null){
            ((BitmapDrawable)image10.getDrawable()).getBitmap().recycle();
            image10.setImageDrawable(null);
        }
        if(image11.getDrawable() != null){
            ((BitmapDrawable)image11.getDrawable()).getBitmap().recycle();
            image11.setImageDrawable(null);
        }

        System.gc();
    }

    // ------------- Méthode de fonctionnement du jeu ------------

    /**
     * Cette méthode prépare l'écran de séléction d'un joueur
     */
    private void setUpGamerSelection(){
        tGameTitle.setText(dragNDropPuzzleFragmentClassCallBack.getGameTitle());
        playersName = dragNDropPuzzleFragmentClassCallBack.getPlayerName();

        lPlayerSelection.setVisibility(View.VISIBLE);
        tGameTitle.setPaintFlags(tGameTitle.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);


        Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                tGameTitle.animate().translationY(-150).withLayer();
            }
        },1500);

        Handler handler2 = new Handler();
        handler2.postDelayed(new Runnable() {
            @Override
            public void run() {
                animator.fadeInAnimation(tGameName);
                tGameName.setText(getText(R.string.playerSelection_subtitle) + "\n" + playersName.get(numName));
                startNameRotation();
            }
        }, 2000);
    }

    /**
     * Cette méthode provoque une rotation des noms de la liste de joueurs
     */
    private void startNameRotation(){
        // On recupère la liste des joueurs
        final int listSize = dragNDropPuzzleFragmentClassCallBack.getPlayerName().size();

        final Handler nameRotation = new Handler();
        nameRotation.post(new Runnable() {
            int v = numName;
            int z = listSize;
            int nbRotation = 0;
            @Override
            public void run() {
                nbRotation++;
                tGameName.setText(getText(R.string.playerSelection_subtitle) + "\n" + playersName.get(v));
                v++;
                if (v == z) {v = 0;}
                // On défini le nombre de rotation
                if (nbRotation > nbRotationAFaire){
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
            }
        });
    }

    // ------------

    /**
     * Méthode qui prépare le lancement du jeu
     */
    public void startGame(){
        txtVConsigne.setVisibility(View.VISIBLE);
        txtVConsigne.setText(startMessage);
        layoutForGame.setVisibility(View.INVISIBLE);
    }

    /**
     * Cette méthode defini les image qui peuvent bouger en fonction de l'image vide
     * Appelé a chaque mouvement
     */
    public void selectLayoutWhitchCanBeClickable(){

        for (LinearLayout monLayout : layoutList) {
            if (monLayout.getChildAt(0) != null){
                monLayout.getChildAt(0).setOnTouchListener(new MyTouchListener());
            }
        }

        // --------------- Création d'une view "vide" pour l'ecran en entier -----------------------------
        View whiteView = new View(getContext());
        layoutForGame.addView(whiteView);
    }

    // ---------

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }


    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    // -----------------------------------------------------------------------------
    // -------------------- Class De Gestion D'Event--------------------------------

    /**
     * Class gérant le drag d'une image d'un layout
     */
    private final class MyTouchListener implements View.OnTouchListener {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                view.startDrag(data, shadowBuilder, view, 0);
                view.setVisibility(View.INVISIBLE);
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * Class gérant le drop d'une image sur un layout
     */
    private final class MyDragListener implements View.OnDragListener {
        Drawable enterShape = getResources().getDrawable(R.drawable.drag_n_drop_layoutselected_style);
        Drawable normalShape = getResources().getDrawable(R.drawable.drag_n_drop_layout_style);

        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    // do nothing
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    if (v != layoutForGame){v.setBackgroundDrawable(enterShape);}
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    if (v != layoutForGame){v.setBackgroundDrawable(normalShape);}
                    break;
                case DragEvent.ACTION_DROP:
                    try{
                        // Dropped, reassign View to ViewGroup
                        View view = (View) event.getLocalState();
                        ViewGroup owner = (ViewGroup) view.getParent();
                        owner.removeView(view);
                        LinearLayout container = (LinearLayout) v;
                        if (container.getChildAt(0) == null) {
                            container.addView(view);
                            view.setVisibility(View.VISIBLE);
                            selectLayoutWhitchCanBeClickable();
                            if(checkIfCorrect()){
                                animator.fadeOutFadeInAnimation(layoutForGame, txtVConsigne);
                                txtVConsigne.setText(endMessage);
                                gameFinish = true;
                            }
                        } else {
                            owner.addView(view);
                            view.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception e){System.out.print(e.getMessage());}

                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    v.setBackgroundDrawable(normalShape);
                default:
                    break;
            }
            return true;
        }
    }


    // ----------------------------------------------------------------------------


}
