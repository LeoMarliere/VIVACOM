package com.vivacom.leo.perdpaslenord.fragments.game;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vivacom.leo.perdpaslenord.R;
import com.vivacom.leo.perdpaslenord.ViewAnimations;
import com.vivacom.leo.perdpaslenord.constant.ConstantInfos;

import java.util.List;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

import static com.vivacom.leo.perdpaslenord.R.string.playerSelection_subtitle;

/**
 * Created by Leo on 18/09/2017.
 */

public class DragNDropAssociationFragmentClass extends Fragment {


    // ------------------------------------------------------------------
    // -------------------- Elements graphiques -------------------------
    TextView txtVConsigne;
    TextView selection_title;
    LinearLayout layoutForGame;

    LinearLayout layoutOneTop,layoutTwoTop,layoutThreeTop,layoutFourTop;
    LinearLayout layoutOneBot,layoutTwoBot,layoutThreeBot,layoutFourBot;
    ImageView imageOne,imageTwo,imageThree,imageFour;
    TextView txtVOne,txtVTwo,txtVThree,txtVFour;

    View ligne;
    Button btnValid;

    boolean gamefinish = false;

    // Pour la sélection du joueur
    RelativeLayout lPlayerSelection;
    TextView tGameTitle,tGameName,tContinue;
    List<String> playersName;

    int nbRoatationPlayerSelection, numName= 0;


    // -------------------- Elements diverts -------------------------

    public static final String TXT_INFOS = "TXT";

    DragNDropAssociationFragmentClassCallBack dragNDropAssociationFragmentClassCallBack;

    public View goodImageForOne;
    public View goodImageForTwo;
    public View goodImageForThree;
    public View goodImageForFour;

    public int nbReponseCorrectes = 0;
    public int nbReponseFausses = 0;

    public Boolean gameWin;

    String spotActuel;

    ViewAnimations animator = new ViewAnimations();

    public final String TAG = "ASSO";



    // ------------------------------------------------------------------------------


    public DragNDropAssociationFragmentClass(){
        // Require empty public constructor
    }

    public interface DragNDropAssociationFragmentClassCallBack{
        void whenGameIsValidate(Boolean correct);
        List<String> getPlayerName();
        String getGameTitle();
        void handleBtnClick(boolean canClick);
    }

    public static DragNDropAssociationFragmentClass newInstance(String spotName){
        DragNDropAssociationFragmentClass fragment = new DragNDropAssociationFragmentClass();
        Bundle args = new Bundle();
        args.putString(TXT_INFOS, spotName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof DragNDropAssociationFragmentClassCallBack)
            dragNDropAssociationFragmentClassCallBack = (DragNDropAssociationFragmentClassCallBack) activity;
        Log.d(TAG, "Fragment onAttach");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        dragNDropAssociationFragmentClassCallBack = null;
        Log.d(TAG, "Fragment onDetach");
    }

    public void onDestroy(){
        super.onDestroy();
        System.gc();
        Log.d(TAG, "Fragment onDestroy");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_drag_n_drop_association,container,false);
        // Association des Elements graphiques
        associateElements(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d(TAG, "Fragment onStart");

        //  Affectation des Events
        associateDragNDropAction();

        // Affectation des Images et des Textes
        selectQuestion();

        // Sélection du joueur
        setUpGamerSelection();




    }

    // ----------------------------------------------------------------------------
    // -------------------- Initialisation -------------------------

    /**
     * Méthode associant les éléments à leur composant graphique
     * @param view
     */
    public void associateElements(View view){
        txtVConsigne = view.findViewById(R.id.dndAsso_txtVConsigne);
        layoutForGame =  view.findViewById(R.id.dndAsso_GameLayout);

        layoutOneTop =  view.findViewById(R.id.dndAsso_oneTop);
        layoutTwoTop =  view.findViewById(R.id.dndAsso_twoTop);
        layoutThreeTop = view.findViewById(R.id.dndAsso_threeTop);
        layoutFourTop =  view.findViewById(R.id.dndAsso_fourTop);

        layoutOneBot =  view.findViewById(R.id.dndAsso_oneBot);
        layoutTwoBot =  view.findViewById(R.id.dndAsso_twoBot);
        layoutThreeBot =  view.findViewById(R.id.dndAsso_threeBot);
        layoutFourBot =  view.findViewById(R.id.dndAsso_fourBot);

        imageOne =  view.findViewById(R.id.dndAsso_imageOne);
        imageTwo =  view.findViewById(R.id.dndAsso_imageTwo);
        imageThree =  view.findViewById(R.id.dndAsso_imageThree);
        imageFour =  view.findViewById(R.id.dndAsso_imageFour);

        txtVOne = view.findViewById(R.id.dndAsso_txtVone);
        txtVTwo = view.findViewById(R.id.dndAsso_txtVtwo);
        txtVThree = view.findViewById(R.id.dndAsso_txtVthree);
        txtVFour = view.findViewById(R.id.dndAsso_txtVfour);

        btnValid =  view.findViewById(R.id.dndAsso_btnValidate);
        ligne = view.findViewById(R.id.dndAsso_ligne);

        // ---
        lPlayerSelection = view.findViewById(R.id.playerSelection);
        tGameName = view.findViewById(R.id.gamerName);
        tGameTitle = view.findViewById(R.id.gameTitle);
        tContinue = view.findViewById(R.id.blink_continue);

        selection_title = view.findViewById(R.id.selection_title);
        Typeface type = Typeface.createFromAsset(getActivity().getAssets(),"fonts/steinem.ttf");
        selection_title.setTypeface(type);
    }

    /**
     * Méthode créant les OnTouchListener et les OnDragListener
     */
    @SuppressLint("ClickableViewAccessibility")
    public void associateDragNDropAction(){
        imageOne.setOnTouchListener(new MyTouchListener());
        imageTwo.setOnTouchListener(new MyTouchListener());
        imageThree.setOnTouchListener(new MyTouchListener());
        imageFour.setOnTouchListener(new MyTouchListener());

        layoutOneBot.setOnDragListener(new MyDragListener());
        layoutTwoBot.setOnDragListener(new MyDragListener());
        layoutThreeBot.setOnDragListener(new MyDragListener());
        layoutFourBot.setOnDragListener(new MyDragListener());

        layoutOneTop.setOnDragListener(new MyDragListener());
        layoutTwoTop.setOnDragListener(new MyDragListener());
        layoutThreeTop.setOnDragListener(new MyDragListener());
        layoutFourTop.setOnDragListener(new MyDragListener());

        layoutForGame.setOnDragListener(new MyDragListener());
    }

    /**
     * Méthode récupérant le nom du spot passé en paramètre
     * Preapare les images et les réponses en fonction
     */
    public void selectQuestion(){
        if (getArguments() != null) {
            Bundle args = getArguments();
            if (args.containsKey(TXT_INFOS)){
                if (Objects.equals(args.getString(TXT_INFOS), ConstantInfos.NAME_COLONNE)){
                    affectImageAndAnswerForSpotColonneDeLaDeesse();
                    nbRoatationPlayerSelection = 17;
                } else if (Objects.equals(args.getString(TXT_INFOS), ConstantInfos.NAME_NATIO)){
                    affectImageAndAnswerForSpotRueNationale();
                    nbRoatationPlayerSelection = 18;
                } else if (Objects.equals(args.getString(TXT_INFOS), ConstantInfos.NAME_HOSPICE)){
                    affectImageAndAnswerForSpotHospice();
                    nbRoatationPlayerSelection = 19;
                } else if (Objects.equals(args.getString(TXT_INFOS), ConstantInfos.NAME_VOIXDUNORD)){
                    affectImageAndAnswerForSpotVoixDuNord();
                    nbRoatationPlayerSelection = 20;
                }
            }
        }
    }

    /**
     * Cette méthode crée nos différents listener
     */
    public void createListener(){
        txtVConsigne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!gamefinish) {
                    dragNDropAssociationFragmentClassCallBack.handleBtnClick(false);
                    animator.fadeOutFadeInAnimation(txtVConsigne,layoutForGame);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            animator.fadeInAnimation(ligne);
                        }
                    }, 1500);
                }
            }
        });


        btnValid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!gamefinish){
                    if(checkIfAllImageArePlaced()){
                        checkIfAnswerIsCorrect();
                    } else {
                        Toasty.error(getActivity().getApplicationContext(), "Il faut d'abord associer toutes les images à leur titre.", Toast.LENGTH_SHORT, true).show();
                    }
                } else {
                    dragNDropAssociationFragmentClassCallBack.whenGameIsValidate(gameWin);
                }
            }
        });

    }


    public void startGame(){
        View whiteView = new View(getContext());
        layoutForGame.addView(whiteView);

        txtVConsigne.setVisibility(View.VISIBLE);
        layoutForGame.setVisibility(View.GONE);
        ligne.setVisibility(View.GONE);

        createListener();
    }

    // ----------------------------------------------------------------------------
    // -------------------- Méthodes de SET UP du jeu en fonction du spot -------------------------

    /**
     * Méthode affectant les textes, les images et les bonnes réponses
     * Dans le cas ou le spot est : LA COLONNE DE LA DEESSE
     */
    public void affectImageAndAnswerForSpotColonneDeLaDeesse(){
        imageOne.setImageResource(R.drawable.photo_statue_colonnedeladeesse);
        imageTwo.setImageResource(R.drawable.photo_statue_louisedebettignies);
        imageThree.setImageResource(R.drawable.photo_statue_ptitquinquin);
        imageFour.setImageResource(R.drawable.photo_statue_pigeonvoyageur);

        txtVConsigne.setText("Ce jeu est très simple. \n  \n Vous devez associer chaque statue à son nom. \n Une fois cela fait, appuyer sur le bouton TERMINER, mais attention, vous n'avez pas le droit a l'erreur. \n \n Toucher l'écran pour continuer");

        txtVOne.setText("La Statue Du Ptit Quinquin");
        txtVTwo.setText("La Colonne De La Déesse");
        txtVThree.setText("La Statue Des pigeons Voyageurs");
        txtVFour.setText("La Statue De Louise De Bettignies");

        // On stockes les bonnes réponses
        goodImageForOne = layoutThreeTop.getChildAt(0);
        goodImageForTwo = layoutOneTop.getChildAt(0);
        goodImageForThree = layoutFourTop.getChildAt(0);
        goodImageForFour = layoutTwoTop.getChildAt(0);

        spotActuel = "COLONNE";

    }

    /**
     * Méthode affectant les textes, les images et les bonnes réponses
     * Dans le cas ou le spot est : RUE NATIONALE
     */
    public void affectImageAndAnswerForSpotRueNationale(){
        imageOne.setImageResource(R.drawable.photo_fete_enduropal);
        imageTwo.setImageResource(R.drawable.photo_fete_braderie);
        imageThree.setImageResource(R.drawable.photo_fete_ducasse);
        imageFour.setImageResource(R.drawable.photo_fete_carnaval);

        txtVConsigne.setText("Comme tout le monde le sait, le Nord organise de nombreuses fêtes populaires. \n  \n Vous devez associer chaque photo d'un de ses fêtes à son nom. \n Une fois cela fait, appuyer sur le bouton TERMINER, mais attention, vous n'avez pas le droit a l'erreur. \n \n Toucher l'écran pour continuer");

        txtVOne.setText("La Braderie de Lille");
        txtVTwo.setText("Le Carnaval de Dunkerque");
        txtVThree.setText("L'Enduropale");
        txtVFour.setText("Les Ducasses du Nord");

        // On stockes les bonnes réponses
        goodImageForOne = layoutTwoTop.getChildAt(0);
        goodImageForTwo = layoutFourTop.getChildAt(0);
        goodImageForThree = layoutOneTop.getChildAt(0);
        goodImageForFour = layoutThreeTop.getChildAt(0);

        spotActuel = "RUE NATIONALE";
    }

    /**
     * Méthode affectant les textes, les images et les bonnes réponses
     * Dans le cas ou le spot est : LE RANG BEAUXREGARD
     */
    public void affectImageAndAnswerForSpotHospice(){
        imageOne.setImageResource(R.drawable.photo_biere_jenlain);//jeanlin
        imageTwo.setImageResource(R.drawable.photo_biere_3monts);//3mont
        imageThree.setImageResource(R.drawable.photo_biere_chti);//chti
        imageFour.setImageResource(R.drawable.photo_biere_goudale);//goudale

        txtVConsigne.setText("Le Nord et la bière, toute une histoire !! \n \n Vous devez associer chaque bière à son nom. \n Une fois cela fait, appuyer sur le bouton TERMINER, mais attention, vous n'avez pas le droit a l'erreur. \n \n Toucher l'écran pour continuer");

        txtVOne.setText("La Ch'ti");
        txtVTwo.setText("La Goudale");
        txtVThree.setText("La Jenlain");
        txtVFour.setText("La 3 Monts");

        // On stocke les bonnes réponses
        goodImageForOne = layoutThreeTop.getChildAt(0);
        goodImageForTwo = layoutFourTop.getChildAt(0);
        goodImageForThree = layoutOneTop.getChildAt(0);
        goodImageForFour = layoutTwoTop.getChildAt(0);

        spotActuel = "RANG BEAUXREGARD";
    }

    /**
     * Méthode affectant les textes, les images et les bonnes réponses
     * Dans le cas ou le spot est : LE RANG BEAUXREGARD
     */
    public void affectImageAndAnswerForSpotVoixDuNord(){
        imageOne.setImageResource(R.drawable.jeu_asso_blason_douai);
        imageTwo.setImageResource(R.drawable.jeu_asso_blason_amiens);
        imageThree.setImageResource(R.drawable.jeu_asso_blason_saintomer);
        imageFour.setImageResource(R.drawable.jeu_asso_blason_tourcoin);


        txtVConsigne.setText("La Voix du Nord est publiée dans de nombreuses communes de la métropole Lilloise. \n Vous devez associer chaque blason à sa commune. \n Vous trouverez des indices sur la façade du bâtiment. \n \n Une fois cela fait, appuyer sur le bouton TERMINER, mais attention, vous n'avez pas le droit a l'erreur. \n \n Toucher l'écran pour continuer");

        txtVOne.setText("Douai");
        txtVTwo.setText("Saint-Omer");
        txtVThree.setText("Tourcoin");
        txtVFour.setText("Amiens");

        // On stocke les bonnes réponses
        goodImageForOne = layoutOneTop.getChildAt(0);
        goodImageForTwo = layoutThreeTop.getChildAt(0);
        goodImageForThree = layoutFourTop.getChildAt(0);
        goodImageForFour = layoutTwoTop.getChildAt(0);

        spotActuel = "LA VOIX DU NORD";
    }

    // ----------------------------------------------------------------------------
    // -------------------- Méthodes de gestion de la réponse -------------------------

    /**
     * Méthode renvoyant TRUE si toutes les images sont affectées
     * Dans le cas contraire, renvoie FALSE
     * @return
     */
    public boolean checkIfAllImageArePlaced(){
        boolean alright = true;
        if (layoutOneBot.getChildAt(0) == null){alright = false;}
        if (layoutTwoBot.getChildAt(0) == null){alright = false;}
        if (layoutThreeBot.getChildAt(0) == null){alright = false;}
        if (layoutFourBot.getChildAt(0) == null){alright = false;}
        return  alright;
    }

    /**
     * Méthode vérifiant si toutes les réponses sont correctes
     */
    public void checkIfAnswerIsCorrect(){
        if (goodImageForOne == layoutOneBot.getChildAt(0)){nbReponseCorrectes++;}
        else{
            nbReponseFausses++;
            layoutOneBot.setBackgroundDrawable(getResources().getDrawable(R.drawable.drag_n_drop_layoutselected_style));
        }

        if (goodImageForTwo == layoutTwoBot.getChildAt(0)){nbReponseCorrectes++;}
        else{
            nbReponseFausses++;
            layoutTwoBot.setBackgroundDrawable(getResources().getDrawable(R.drawable.drag_n_drop_layoutselected_style));
        }

        if (goodImageForThree == layoutThreeBot.getChildAt(0)){nbReponseCorrectes++;}
        else{
            nbReponseFausses++;
            layoutThreeBot.setBackgroundDrawable(getResources().getDrawable(R.drawable.drag_n_drop_layoutselected_style));
        }

        if (goodImageForFour == layoutFourBot.getChildAt(0)){nbReponseCorrectes++;}
        else{
            nbReponseFausses++;
            layoutFourBot.setBackgroundDrawable(getResources().getDrawable(R.drawable.drag_n_drop_layoutselected_style));
        }

        gameWin = nbReponseCorrectes == 4;
        showEndMessage(gameWin);
    }

    /**
     * Affiche le message de fin et affecte les bonnes réponses
     * @param gameWin
     */
    public void showEndMessage(boolean gameWin){

        final Animation in = new AlphaAnimation(0.0f, 1.0f);
        in.setDuration(2000);

        txtVConsigne.startAnimation(in);
        txtVConsigne.animate().translationY(-180);
        txtVConsigne.setBackgroundColor(Color.TRANSPARENT);

        fadeAnimationForLayout();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {changeVisibility();}
        }, 2000);


        if (gameWin){
            switch (spotActuel){
                case "COLONNE":
                    txtVConsigne.setText(ConstantInfos.ASSO_WINMESSAGE_STATUE);
                    break;
                case "RUE NATIONALE":
                    txtVConsigne.setText(ConstantInfos.ASSO_WINMESSAGE_FETE);
                    break;
                case "RANG BEAUXREGARD":
                    txtVConsigne.setText(ConstantInfos.ASSO_WINMESSAGE_BIERE);
                    break;
                case "LA VOIX DU NORD":
                    txtVConsigne.setText(ConstantInfos.ASSO_WINMESSAGE_BLASON);
                    break;
            }
        }  else {
            txtVConsigne.setText(ConstantInfos.ASSO_LOOSEMESSAGE);
            affectGoodImageToGoodText();
        }

        gamefinish = true;

    }

    /**
     * Méthode associant chaque image à sa patisserie correspondante
     */
    public void affectGoodImageToGoodText(){
        layoutOneBot.removeAllViews();
        layoutTwoBot.removeAllViews();
        layoutThreeBot.removeAllViews();
        layoutFourBot.removeAllViews();

        switch (spotActuel){
            case "COLONNE":
                layoutOneBot.addView(imageThree);
                layoutTwoBot.addView(imageOne);
                layoutThreeBot.addView(imageFour);
                layoutFourBot.addView(imageTwo);
                break;
            case "RUE NATIONALE":
                layoutOneBot.addView(imageTwo);
                layoutTwoBot.addView(imageFour);
                layoutThreeBot.addView(imageOne);
                layoutFourBot.addView(imageThree);
                break;
            case "RANG BEAUXREGARD":
                layoutOneBot.addView(imageThree);
                layoutTwoBot.addView(imageFour);
                layoutThreeBot.addView(imageOne);
                layoutFourBot.addView(imageTwo);
                break;
            case "LA VOIX DU NORD":
                layoutOneBot.addView(imageOne);
                layoutTwoBot.addView(imageThree);
                layoutThreeBot.addView(imageFour);
                layoutFourBot.addView(imageTwo);
                break;
        }

    }

    /**
     * Méthode gérant la disparition des layout top
     */
    public void fadeAnimationForLayout(){
        final Animation out = new AlphaAnimation(1.0f, 0.0f);
        out.setDuration(2000);

        layoutOneTop.startAnimation(out);
        layoutTwoTop.startAnimation(out);
        layoutThreeTop.startAnimation(out);
        layoutFourTop.startAnimation(out);
    }

    /**
     * Méthode qui passe le textView en VISIBLE et les layoutTop en INVISIBLE
     */
    public void changeVisibility(){
        layoutOneTop.setVisibility(View.INVISIBLE);
        layoutTwoTop.setVisibility(View.INVISIBLE);
        layoutThreeTop.setVisibility(View.INVISIBLE);
        layoutFourTop.setVisibility(View.INVISIBLE);

        txtVConsigne.setVisibility(View.VISIBLE);
    }

    // ------------- Méthode de fonctionnement du jeu ------------

    /**
     * Cette méthode prépare l'écran de séléction d'un joueur
     */
    public void setUpGamerSelection(){
        lPlayerSelection.setVisibility(View.VISIBLE);
        tGameName.setVisibility(View.VISIBLE);
        tGameTitle.setVisibility(View.VISIBLE);
        tGameTitle.setPaintFlags(tGameTitle.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tGameTitle.setText(dragNDropAssociationFragmentClassCallBack.getGameTitle());

        playersName = dragNDropAssociationFragmentClassCallBack.getPlayerName();

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
                tGameName.setText(getText(R.string.playerSelection_subtitle) + "\n" + playersName.get(v));
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



    // ----------------------------------------------------------------------------
    // -------------------- Class De Gestion D'Event-------------------------

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
    class MyDragListener implements View.OnDragListener {
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
                    // Dropped, reassign View to ViewGroup
                    View view = (View) event.getLocalState();
                    ViewGroup owner = (ViewGroup) view.getParent();
                    owner.removeView(view);
                    LinearLayout container = (LinearLayout) v;
                    if (container.getChildAt(0) == null) {
                        container.addView(view);
                        view.setVisibility(View.VISIBLE);
                    } else {
                        owner.addView(view);
                        view.setVisibility(View.VISIBLE);
                    }
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
