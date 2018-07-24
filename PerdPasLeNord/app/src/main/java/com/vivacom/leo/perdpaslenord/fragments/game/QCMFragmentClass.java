package com.vivacom.leo.perdpaslenord.fragments.game;


import android.app.Activity;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.leakcanary.RefWatcher;
import com.vivacom.leo.perdpaslenord.R;
import com.vivacom.leo.perdpaslenord.ViewAnimations;
import com.vivacom.leo.perdpaslenord.activities.InGameActivityClass;
import com.vivacom.leo.perdpaslenord.constant.ConstantInfos;

import java.util.List;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

/**
 * Created by Leo on 14/09/2017.
 */

public class QCMFragmentClass extends Fragment {

    // ------- Element graphiques -------

    // Pour la consigne
    TextView txtVConsigne, selection_title;

    // Pour le jeu
    LinearLayout gameLayout;
    TextView txtVQuestion, txtQuestionNumber;
    CheckBox chkBReponse1,chkBReponse2,chkBReponse3,chkBReponse4,chkBReponse5;
    CheckBox chkBSelected1,chkBSelected2,chkBSelected3;
    Button btnValider;

    // Pour la sélection du joueur
    RelativeLayout lPlayerSelection;
    TextView tGameTitle,tGameName,tContinue;
    List<String> playersName;

    // ------- Variables pour le jeu -------

    String startMessage, succesMessage, failMessage = "";
    String question1,question2,question3;
    String possibility1for1,possibility2for1,possibility3for1,possibility4for1,possibility5for1;
    String possibility1for2, possibility2for2,possibility3for2,possibility4for2,possibility5for2;
    String possibility1for3,possibility2for3,possibility3for3,possibility4for3,possibility5for3;
    String reponse1,reponse2,reponse3;
    String bonneReponse1,bonneReponse2,bonneReponse3;

    boolean question1IsCorrect, question2IsCorrect, question3IsCorrect = false;
    boolean gameFinish, succes, firstclic = false;

    int currentQuestion = 1;
    int nbBonneReponse = 0;
    int nbRoatationPlayerSelection;

    int spotTag, numName= 0;
    ViewAnimations animator = new ViewAnimations();

    public static final String SPOT_NAME = "Spot Name";
    QCMFragmentClassCallBack qcmFragmentClassCallBack;


    public static final String TAG = "QCM";


    // ---------------------------------------------------

    public QCMFragmentClass(){
        // Require empty public constructor
    }

    public static QCMFragmentClass newInstance(String spotName){
        QCMFragmentClass fragment = new QCMFragmentClass();
        Bundle args = new Bundle();
        args.putString(SPOT_NAME, spotName);
        fragment.setArguments(args);
        return fragment;
    }

    public interface QCMFragmentClassCallBack{
        void whenGameIsValidate(Boolean correct);
        List<String> getPlayerName();
        String getGameTitle();
        void handleBtnClick(boolean canClick);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof QCMFragmentClassCallBack)
            qcmFragmentClassCallBack = (QCMFragmentClassCallBack) activity;
        Log.d(TAG, "Fragment onAttach");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        qcmFragmentClassCallBack = null;
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
        View view =  inflater.inflate(R.layout.fragment_qcm,container,false);
        associateElements(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d(TAG, "Fragment onStart");

        // ------- Préparation des différents éléments -------
        // On séléctionne la question en fonction du point de passage
        selectQuestion();

        // On créer les listeners
        createOnClickListener();

        // On prépare la séléction du joueur
        setUpGamerSelection();

    }

    //------------------------------------------------------------------

    /**
     * Méthode associant les éléments avec leurs composant graphiques
     * @param view
     */
    public void associateElements(View view){
        gameLayout = view.findViewById(R.id.qcm_gameLayout);

        txtVConsigne = view.findViewById(R.id.qcm_consigne1);
        txtQuestionNumber = view.findViewById(R.id.question_number);

        txtVQuestion = view.findViewById(R.id.qcm_question);
        chkBReponse1 =view.findViewById(R.id.qcm_reponse1);
        chkBReponse2 = view.findViewById(R.id.qcm_reponse2);
        chkBReponse3 =  view.findViewById(R.id.qcm_reponse3);
        chkBReponse4 = view.findViewById(R.id.qcm_reponse4);
        chkBReponse5 =  view.findViewById(R.id.qcm_reponse5);
        btnValider = view.findViewById(R.id.qcm_btnValider);

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
     * Cette méthode créer les event OnCickListener sur les CheckBox
     */
    public void createOnClickListener(){
        // CheckBox
        chkBReponse1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chkBReponse1.setChecked(true);
                chkBReponse2.setChecked(false);
                chkBReponse3.setChecked(false);
                chkBReponse4.setChecked(false);
                chkBReponse5.setChecked(false);
            }
        });

        chkBReponse2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chkBReponse1.setChecked(false);
                chkBReponse2.setChecked(true);
                chkBReponse3.setChecked(false);
                chkBReponse4.setChecked(false);
                chkBReponse5.setChecked(false);
            }
        });

        chkBReponse3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chkBReponse1.setChecked(false);
                chkBReponse2.setChecked(false);
                chkBReponse3.setChecked(true);
                chkBReponse4.setChecked(false);
                chkBReponse5.setChecked(false);
            }
        });

        chkBReponse4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chkBReponse1.setChecked(false);
                chkBReponse2.setChecked(false);
                chkBReponse3.setChecked(false);
                chkBReponse4.setChecked(true);
                chkBReponse5.setChecked(false);
            }
        });
        chkBReponse5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chkBReponse1.setChecked(false);
                chkBReponse2.setChecked(false);
                chkBReponse3.setChecked(false);
                chkBReponse4.setChecked(false);
                chkBReponse5.setChecked(true);
            }
        });

        // TextView
        txtVConsigne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!gameFinish) {
                    if (!firstclic){
                        qcmFragmentClassCallBack.handleBtnClick(false);
                        animator.fadeOutFadeInAnimation(txtVConsigne, gameLayout);
                        currentQuestion = 1;
                        setUpQuestion();
                        firstclic = true;
                    }
                } else {qcmFragmentClassCallBack.whenGameIsValidate(succes);}
            }
        });

        // Bouton
        btnValider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tag = (int)btnValider.getTag();
                if (tag == 1){
                    if (!chkBReponse1.isChecked() & !chkBReponse2.isChecked() & !chkBReponse3.isChecked() & !chkBReponse4.isChecked() & !chkBReponse5.isChecked()){
                        Toasty.error(getContext(), "Vous devez d'abord séléctionner une réponse",  Toast.LENGTH_SHORT ).show();
                    } else {
                        registerAnswer();
                    }
                } if (tag == 2){
                    currentQuestion++;
                    setUpQuestion();

                }

            }
        });


    }


    //-------------- Méthodes d'affichage ------------------------------

    /**
     * Cette méthode séléctionne les questions à afficher
     */
    public void selectQuestion(){

        if (getArguments() != null) {
            Bundle args = getArguments();
            if (args.containsKey(SPOT_NAME)){
                if (args.getString(SPOT_NAME).equals(ConstantInfos.NAME_VIEILLEBOURSE)){
                    spotTag = 1;
                    nbRoatationPlayerSelection = 23;
                    setUpQuestionAndAnswerForVieilleBourse();
                } else if (args.getString(SPOT_NAME).equals(ConstantInfos.NAME_NOUVEAUSIECLE)){
                    spotTag = 3;
                    nbRoatationPlayerSelection = 24;
                    setUpQuestionAndAnswerForLeNouveauSiecle();
                } else if (args.getString(SPOT_NAME).equals(ConstantInfos.NAME_RUECHAUSSEE)){
                    spotTag = 4;
                    nbRoatationPlayerSelection = 25;
                    setUpQuestionAndAnswerForRueGrandeChaussee();
                } else if (args.getString(SPOT_NAME).equals(ConstantInfos.NAME_PLACEAUXOIGNONS)){
                    spotTag = 2;
                    nbRoatationPlayerSelection = 26;
                    setUpQuestionAndAnswerForPlaceOignons();
                }
            }
        }

    }

    /**
     * Affiche la question et les réponses du QCM 'Le Nouveau Siecle' -- 3
     */
    public void setUpQuestionAndAnswerForLeNouveauSiecle(){

        question1 = "Quel célèbre grand magasin trouve t'on rue Nationale ?";
        possibility1for1 = "La Fnac";
        possibility2for1 = "Abercrombie & Fitch";
        possibility3for1 = "Le Printemps";
        possibility4for1 = "Apple Store";
        possibility5for1 = "GUCCI";
        bonneReponse1 = "Le Printemps";

        question2 = "De quelle couleur sont les vélos V'Lille ?";
        possibility1for2 = "Bleu et blanc";
        possibility2for2 = "Rouge et Noir";
        possibility3for2 = "Noir et Bleu";
        possibility4for2 = "Gris et Vert";
        possibility5for2 = "Vert et Noir";
        bonneReponse2 = "Rouge et Noir";

        question3 = "De quel type d'établissements est rempli la place du Nouveau Siècle ?";
        possibility1for3 = "Des magasins de vêtements";
        possibility2for3 = "Des écoles";
        possibility3for3 = "Des appartements privés";
        possibility4for3 = "Des restaurants";
        possibility5for3 = "Des bars";
        bonneReponse3 = "Des bars";

        succesMessage= "Bravo !!  \n On peut dire que vous maitrisez le quartier de Lille Centre, je vous en félicite. \n \n Cliquer sur l'écran pour continuer et débloquer un nouveau mot ";
        failMessage = "Aïe Aïe Aïe !!  \n Dommage, j'ai l'impression que vous n'avez pas été assez attentif ... \n \n Cliquer sur l'écran pour continuer";


    }   // DONE

    /**
     * Affiche la question et les réponses du QCM 'Rue Grande Chaussee' -- 4
     */
    public void setUpQuestionAndAnswerForRueGrandeChaussee(){

        question1 = "Il y a deux gares de voyageurs à Lille. La gare \"Lille-Flandre\" et :";
        possibility1for1 = "La Gare Lille-France";
        possibility2for1 = "La Gare Lille-Nord";
        possibility3for1 = "La Gare Lille-Europe";
        possibility4for1 = "La Grande Gare";
        possibility5for1 = "La Gare de Lille";
        bonneReponse1 = possibility3for1;

        question2 = "Les deux géants de la Lille se nomment :";
        possibility1for2 = "Lydéric et Phinaert";
        possibility2for2 = "Phydéric et Lynaert";
        possibility3for2 = "Lydie et Phinaert";
        possibility4for2 = "Lydéric et Lydiaert";
        possibility5for2 = "Phydéric et Phinaert";
        bonneReponse2 = possibility1for2;

        question3 = "Traditionnellement, on sert lors de la braderie de Lille :";
        possibility1for3 = "Du poulet frites";
        possibility2for3 = "Du porc au Maroille";
        possibility3for3 = "Du fish and chips";
        possibility4for3 = "Des moules frites";
        possibility5for3 = "Du Welsh";
        bonneReponse3 = possibility4for3;

        succesMessage= "Bravo !!  \n La ville n'a plus de secret pour vous.. \n \n Cliquer sur l'écran pour continuer et un mot.";
        failMessage = "Aïe Aïe Aïe !! \n Dommage, j'ai l'impression que Lille vous est toujours inconnue ... \n \n Cliquer sur l'écran pour continuer";


    }  // DONE

    /**
     * Affiche la question et les réponses du QCM 'La Place aux Oignons' -- 2
     */
    public void setUpQuestionAndAnswerForPlaceOignons(){

        question1 = "A quel endroit se situe cette place ?";
        possibility1for1 ="Sur un ancien cimetière indien (ayant habité a Lille il y a fort fort longtemps).";
        possibility2for1 ="Sur un site préhistorique.";
        possibility3for1 = "Sur un ancien Donjon.";
        possibility4for1 = "Sur la coline la plus haute de l'époque.";
        possibility5for1 = "Sur les ruines d'une ancienne écurie très célèbre.";
        bonneReponse1 = possibility3for1;


        question2 = "Quelle célèbre brasserie de Lille, ouverte de 10h à l'aube, trouve t'on sur la place de Rihour ?";
        possibility1for2 = "Les 3 Brasseurs";
        possibility2for2 = "La Chicorée";
        possibility3for2 = "André";
        possibility4for2 = "Le Bettigny";
        possibility5for2 = "Au Moulin D'Or";
        bonneReponse2 = possibility2for2;


        question3 = "Quel fameux restaurant se situe sur cette place ?";
        possibility1for3 = "Le Vieux de la Veille";
        possibility2for3 = "La Chicorée";
        possibility3for3 = "Au Moulin D'Or";
        possibility4for3 = "Le Bettigny";
        possibility5for3 = "Le Bon Ch'ti";
        bonneReponse3 = possibility1for3;

        succesMessage= "Clap Clap Clap !! \n Je vous félicite, vous avez réussi ce jeu !! \n \n Ciquer sur l'écran pour continuer.";
        failMessage = "Dommage !! \n Tous les autres ont pourtant réussi ..... \n \n Ciquer sur l'écran pour continuer.";

    } // DONE

    /**
     * Affiche la question et les réponses du QCM 'La Vieille Bourse' -- 1
     */
    public void setUpQuestionAndAnswerForVieilleBourse(){
        question1 = "Combien il a t'y d'armoirie sur la façade de La Voix Du Nord?";
        possibility1for1 ="21";
        possibility2for1 ="22";
        possibility3for1 = "24";
        possibility4for1 = "28";
        possibility5for1 = "32";
        bonneReponse1 = possibility4for1;

        question2 = "Qui dit commerce dit calcul : \n 3 - 2 * 4 + 5";
        possibility1for2 ="12";
        possibility2for2 ="9";
        possibility3for2 = "0";
        possibility4for2 = "-9";
        possibility5for2 = "-12";
        bonneReponse2 = possibility3for2 ;

        question3 = "Combien y a t'il d'acces a la chambre des Commerces .";
        possibility1for3 ="1";
        possibility2for3 ="2";
        possibility3for3 = "3";
        possibility4for3 = "4";
        possibility5for3 = "5";
        bonneReponse3 = possibility4for3;

        succesMessage= "Tu as réussi la quête de la Chambre des Secrets Harry !! Tu es un ... Heu Non ... Bravo à vous !! Cliquer pour débloquer un mot.";
        failMessage = "Dommage pour vous, cela n'etais pourtant pas très compliqué ... Cliquer sur l'écran pour continuer";

    } // DONE



    //------------------------------------------------------------------
    //--------------- Méthode de gestion de la réponse -----------------

    /**
     * Méthode s'occupant d'enregistrer la reponse selectionner
     * Affiche ensuite la question suivante
     */
    public void registerAnswer(){
        switch (currentQuestion){
            case 1 :
                reponse1 = getAnswerChecked();
                currentQuestion++;
                setUpQuestion();
                break;
            case 2 :
                reponse2 = getAnswerChecked();
                currentQuestion++;
                setUpQuestion();
                break;
            case 3 :
                reponse3 = getAnswerChecked();
                getNbGoodAnswer();
                setUpAfterLastQuestion();
                break;
        }
    }

    /**
     * Méthode retournant un String contenant la valeur selectionner par le joueur
     * @return
     */
    public String getAnswerChecked() {
        String result = "";
        switch (currentQuestion) {
            case 1:
                if (chkBReponse1.isChecked()) {
                    result = chkBReponse1.getText().toString().toUpperCase();
                    chkBSelected1 = chkBReponse1;}
                if (chkBReponse2.isChecked()) {
                    result = chkBReponse2.getText().toString().toUpperCase();
                    chkBSelected1 = chkBReponse2;}
                if (chkBReponse3.isChecked()) {
                    result = chkBReponse3.getText().toString().toUpperCase();
                    chkBSelected1 = chkBReponse3;}
                if (chkBReponse4.isChecked()) {
                    result = chkBReponse4.getText().toString().toUpperCase();
                    chkBSelected1 = chkBReponse4;}
                if (chkBReponse5.isChecked()) {
                    result = chkBReponse5.getText().toString().toUpperCase();
                    chkBSelected1 = chkBReponse5;}
                break;

            case 2:
                if (chkBReponse1.isChecked()) {
                    result = chkBReponse1.getText().toString().toUpperCase();
                    chkBSelected2 = chkBReponse1;}
                if (chkBReponse2.isChecked()) {
                    result = chkBReponse2.getText().toString().toUpperCase();
                    chkBSelected2 = chkBReponse2;}
                if (chkBReponse3.isChecked()) {
                    result = chkBReponse3.getText().toString().toUpperCase();
                    chkBSelected2 = chkBReponse3;}
                if (chkBReponse4.isChecked()) {
                    result = chkBReponse4.getText().toString().toUpperCase();
                    chkBSelected2 = chkBReponse4;}
                if (chkBReponse5.isChecked()) {
                    result = chkBReponse5.getText().toString().toUpperCase();
                    chkBSelected2 = chkBReponse5;}
                break;

            case 3:
                if (chkBReponse1.isChecked()) {
                    result = chkBReponse1.getText().toString().toUpperCase();
                    chkBSelected3 = chkBReponse1;}
                if (chkBReponse2.isChecked()) {
                    result = chkBReponse2.getText().toString().toUpperCase();
                    chkBSelected3 = chkBReponse2;}
                if (chkBReponse3.isChecked()) {
                    result = chkBReponse3.getText().toString().toUpperCase();
                    chkBSelected3 = chkBReponse3;}
                if (chkBReponse4.isChecked()) {
                    result = chkBReponse4.getText().toString().toUpperCase();
                    chkBSelected3 = chkBReponse4;}
                if (chkBReponse5.isChecked()) {
                    result = chkBReponse5.getText().toString().toUpperCase();
                    chkBSelected3 = chkBReponse5;}
                break;
        }
        return result;
    }

    /**
     * Méthode affichant la question et les différents choix
     * En fonction de la variable : currentQuestion
     */
    public void setUpQuestion(){
        switch (currentQuestion){
            case 1 :
                txtQuestionNumber.setText(R.string.qcm_txtv_question1);
                txtVQuestion.setText(question1);
                chkBReponse1.setText(possibility1for1);
                chkBReponse2.setText(possibility2for1);
                chkBReponse3.setText(possibility3for1);
                chkBReponse4.setText(possibility4for1);
                chkBReponse5.setText(possibility5for1);
                btnValider.setBackgroundResource(R.drawable.btn_action_question_suivante);

                break;
            case 2 :
                txtQuestionNumber.setText(R.string.qcm_txtv_question2);
                animator.slideOutAndInWithMessage(txtVQuestion, question2);
                fadeInOutAllCheckBox(2);
                break;
            case 3 :
                txtQuestionNumber.setText(R.string.qcm_txtv_question3);
                animator.slideOutAndInWithMessage(txtVQuestion, question3);
                fadeInOutAllCheckBox(3);
                btnValider.setBackgroundResource(R.drawable.btn_action_terminer);
                break;
            case 4 :
                animator.fadeOutFadeInAnimation(gameLayout, txtVConsigne);
                gameFinish = true;
                if (nbBonneReponse >= 2){
                    txtVConsigne.setText(succesMessage);
                    succes = true;
                } else {
                    txtVConsigne.setText(failMessage);
                    succes = false;
                }
                break;
        }
        uncheckedCheckBox();
    }

    /**
     * Méthode décochant toute les checkBox
     * Appelé lors du changement de question
     */
    public void uncheckedCheckBox(){
        chkBReponse1.setChecked(false);
        chkBReponse2.setChecked(false);
        chkBReponse3.setChecked(false);
        chkBReponse4.setChecked(false);
        chkBReponse5.setChecked(false);
    }

    /**
     * Cette méthode s'occupe de l'animation des CheckBox
     * @param nbState
     */
    public void fadeInOutAllCheckBox(final int nbState){
        animator.fadeOutAnimation(chkBReponse1);
        animator.fadeOutAnimation(chkBReponse2);
        animator.fadeOutAnimation(chkBReponse3);
        animator.fadeOutAnimation(chkBReponse4);
        animator.fadeOutAnimation(chkBReponse5);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                animator.fadeInAnimation(chkBReponse1);
                animator.fadeInAnimation(chkBReponse2);
                animator.fadeInAnimation(chkBReponse3);
                animator.fadeInAnimation(chkBReponse4);
                animator.fadeInAnimation(chkBReponse5);

                if(nbState == 2){
                    chkBReponse1.setText(possibility1for2);
                    chkBReponse2.setText(possibility2for2);
                    chkBReponse3.setText(possibility3for2);
                    chkBReponse4.setText(possibility4for2);
                    chkBReponse5.setText(possibility5for2);
                }

                if(nbState == 3){
                    chkBReponse1.setText(possibility1for3);
                    chkBReponse2.setText(possibility2for3);
                    chkBReponse3.setText(possibility3for3);
                    chkBReponse4.setText(possibility4for3);
                    chkBReponse5.setText(possibility5for3);
                }

                if(gameFinish){colorCheckBox();}
            }
        },750);
    }

    /**
     * Méthode comparant les réponses obtenu
     * et permet d'avoir le nombre de bonne reponse obtenue
     */
    public void getNbGoodAnswer(){
        if (bonneReponse1.toUpperCase().equalsIgnoreCase(reponse1.toUpperCase())){nbBonneReponse++;question1IsCorrect = true;}
        if (bonneReponse2.toUpperCase().equalsIgnoreCase(reponse2.toUpperCase())){nbBonneReponse++;question2IsCorrect = true;}
        if (bonneReponse3.toUpperCase().equalsIgnoreCase(reponse3.toUpperCase())){nbBonneReponse++;question3IsCorrect = true;}
    }

    /**
     * Cette méthode color en rouge la réponse sélectionner
     * Puis color en vert la bonne réponse
     */
    public void colorCheckBox(){

        chkBReponse1.setBackground(getResources().getDrawable(R.drawable.checkbox_qcm_style));
        chkBReponse2.setBackground(getResources().getDrawable(R.drawable.checkbox_qcm_style));
        chkBReponse3.setBackground(getResources().getDrawable(R.drawable.checkbox_qcm_style));
        chkBReponse4.setBackground(getResources().getDrawable(R.drawable.checkbox_qcm_style));
        chkBReponse5.setBackground(getResources().getDrawable(R.drawable.checkbox_qcm_style));

        switch (currentQuestion){
            case 1:
                chkBSelected1.setBackground(getResources().getDrawable(R.drawable.checkbox_qcm_uncorrect));
                if (spotTag == 1){chkBReponse4.setBackground(getResources().getDrawable(R.drawable.checkbox_qcm_correct));}
                else if (spotTag == 2){chkBReponse3.setBackground(getResources().getDrawable(R.drawable.checkbox_qcm_correct));}
                else if (spotTag == 3){chkBReponse3.setBackground(getResources().getDrawable(R.drawable.checkbox_qcm_correct));}
                else if (spotTag == 4){chkBReponse3.setBackground(getResources().getDrawable(R.drawable.checkbox_qcm_correct));}
                break;

            case 2:
                chkBSelected2.setBackground(getResources().getDrawable(R.drawable.checkbox_qcm_uncorrect));
                if (spotTag == 1){chkBReponse3.setBackground(getResources().getDrawable(R.drawable.checkbox_qcm_correct));}
                else if (spotTag == 2){chkBReponse2.setBackground(getResources().getDrawable(R.drawable.checkbox_qcm_correct));}
                else if (spotTag == 3){chkBReponse2.setBackground(getResources().getDrawable(R.drawable.checkbox_qcm_correct));}
                else if (spotTag == 4){chkBReponse1.setBackground(getResources().getDrawable(R.drawable.checkbox_qcm_correct));}
                break;

            case 3:
                chkBSelected3.setBackground(getResources().getDrawable(R.drawable.checkbox_qcm_uncorrect));
                if (spotTag == 1){chkBReponse4.setBackground(getResources().getDrawable(R.drawable.checkbox_qcm_correct));}
                else if (spotTag == 2){chkBReponse1.setBackground(getResources().getDrawable(R.drawable.checkbox_qcm_correct));}
                else if (spotTag == 3){chkBReponse5.setBackground(getResources().getDrawable(R.drawable.checkbox_qcm_correct));}
                else if (spotTag == 4){chkBReponse4.setBackground(getResources().getDrawable(R.drawable.checkbox_qcm_correct));}
                break;
        }

    }

    //------------------------------------------------------------------
    // ------------- Méthode de fonctionnement du jeu ------------

    /**
     * Cette méthode prépare l'écran de séléction d'un joueur
     */
    public void setUpGamerSelection(){
        lPlayerSelection.setVisibility(View.VISIBLE);
        tGameTitle.setText(qcmFragmentClassCallBack.getGameTitle());
        playersName = qcmFragmentClassCallBack.getPlayerName();
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
                setUpBeforeGame();
                lPlayerSelection.setClickable(false);
            }
        });
    }


    // ----------------------

    /**
     * Cette méthode affiche la consigne et prepare les éléments pour la partie
     */
    public void setUpBeforeGame(){
        txtVConsigne.setVisibility(View.VISIBLE);
        gameLayout.setVisibility(View.GONE);
        txtVConsigne.setText(ConstantInfos.CONSIGNE_QCM);


        btnValider.setTag(1);
    }

    /**
     * Méthode affichant le layout de fin de jeu
     */
    public void setUpAfterLastQuestion(){
        txtVConsigne.setText(R.string.qcm_txtv_reponse);
        animator.fadeOutFadeInAnimation(gameLayout, txtVConsigne);

        chkBReponse1.setClickable(false);
        chkBReponse2.setClickable(false);
        chkBReponse3.setClickable(false);
        chkBReponse4.setClickable(false);
        chkBReponse5.setClickable(false);

        currentQuestion = 1;
        gameFinish = true;

        txtVConsigne.setClickable(false);
        txtVConsigne.setFocusable(false);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                animator.fadeOutFadeInAnimation(txtVConsigne,gameLayout);
                setUpQuestion();
                btnValider.setBackgroundResource(R.drawable.btn_action_question_suivante);
                btnValider.setTag(2);
                colorCheckBox();
            }
        }, 2000);

        // On réactive le click sur le TextViewConsigne
        final Handler handler2 = new Handler();
        handler2.postDelayed(new Runnable() {
            @Override
            public void run() {
                txtVConsigne.setClickable(true);
                txtVConsigne.setFocusable(true);
            }
        }, 4000);


    }



}
