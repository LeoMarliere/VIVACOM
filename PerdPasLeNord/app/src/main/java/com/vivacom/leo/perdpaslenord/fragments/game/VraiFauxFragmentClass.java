package com.vivacom.leo.perdpaslenord.fragments.game;

import android.app.Activity;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vivacom.leo.perdpaslenord.R;
import com.vivacom.leo.perdpaslenord.ViewAnimations;
import com.vivacom.leo.perdpaslenord.constant.ConstantInfos;

import org.w3c.dom.Text;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Created by Leo on 25/09/2017.
 */

public class VraiFauxFragmentClass extends Fragment  {

    LinearLayout layoutForGame,layoutResultat;
    TextView txtVConsigne,txtVNbQuestion, txtVQuestion, selection_title;
    TextView txtVResultQuestion, txtVResultAnswer, timer, isCorrectMessage;
    ImageView imageAnswer;
    CountDownTimer countDown;
    Button btnVrai, btnFaux;

    ViewAnimations animator = new ViewAnimations();

    // Pour la sélection du joueur
    RelativeLayout lPlayerSelection;
    TextView tGameTitle,tGameName,tContinue;
    List<String> playersName;

    // ---------------------------
    String question1, question2, question3;
    String infos1, infos2, infos3;
    Boolean reponse1, reponse2, reponse3;
    Boolean bonneReponse1, bonneReponse2, bonneReponse3, currentBonneReponse;
    Boolean correct1 = false, correct2= false, correct3= false;
    // -------------------------
    int currentQuestion = 1;
    int nbReponseCorrect = 0;
    String winMessage, looseMessage = "";
    boolean resultat;

    int nbRoatationPlayerSelection;
    int  numName= 0;

    public static final String SPOT_NAME = "Spot Name";
    VraiFauxFragmentClassCallBack vraiFauxFragmentClassCallBack;

    private static final String FORMAT = "%02d";

    public final String TAG = "VF";

    // ---------------------------------------------------------------

    public VraiFauxFragmentClass(){
        // Require empty public constructor
    }

    public static VraiFauxFragmentClass newInstance(String spotName){
        VraiFauxFragmentClass fragment = new VraiFauxFragmentClass();
        Bundle args = new Bundle();
        args.putString(SPOT_NAME, spotName);
        fragment.setArguments(args);
        return fragment;
    }

    public interface VraiFauxFragmentClassCallBack{
        void whenGameIsValidate(Boolean coorect);
        List<String> getPlayerName();
        String getGameTitle();
        void handleBtnClick(boolean canClick);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof VraiFauxFragmentClass.VraiFauxFragmentClassCallBack)
            vraiFauxFragmentClassCallBack = (VraiFauxFragmentClass.VraiFauxFragmentClassCallBack) activity;
        Log.d(TAG, "Fragment onAttach");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        vraiFauxFragmentClassCallBack = null;
        Log.d(TAG, "Fragment onDetach");
    }

    public void onDestroy(){
        super.onDestroy();
        Log.d(TAG, "Fragment onDestroy");
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vrai_faux_game,container,false);


        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d(TAG, "Fragment onStart");

        associateElements(view);

        // --------------- On affiche en premier la selection du joueur -----------------------
        setUpGamerSelection();

        // ---------------- SetUp en fonction du Spot ---------------------

        if (getArguments() != null) {
            Bundle args = getArguments();
            if (args.containsKey(SPOT_NAME)){
                if (Objects.equals(args.getString(SPOT_NAME), ConstantInfos.NAME_PALAIS)){
                    setUpForPalaisRihour();
                    nbRoatationPlayerSelection = 17;
                } else if (Objects.equals(args.getString(SPOT_NAME), ConstantInfos.NAME_PLACELOUISE)){
                    setUpForLouiseDeBettignies();
                    nbRoatationPlayerSelection = 18;
                } else if (Objects.equals(args.getString(SPOT_NAME), ConstantInfos.NAME_BEFFROI)){
                    setUpForBeffroi();
                    nbRoatationPlayerSelection = 19;
                } else if (Objects.equals(args.getString(SPOT_NAME), ConstantInfos.NAME_FURET)){
                    setUpForFuretDuNord();
                    nbRoatationPlayerSelection = 20;
                } else if (Objects.equals(args.getString(SPOT_NAME), ConstantInfos.NAME_RUEESQUERMOISE)){
                    setUpForRueEsquermoise();
                    nbRoatationPlayerSelection = 21;
                } else if (Objects.equals(args.getString(SPOT_NAME), ConstantInfos.NAME_QUINQUIN)){
                    setUpForQuinquin();
                    nbRoatationPlayerSelection = 22;
                }


            }
        }



        // ------------- Gestion du click sur les CheckBox ---------------
        btnVrai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentBonneReponse = true;
                setUpQuestion(currentQuestion);
            }
        });

        btnFaux.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentBonneReponse = false;
                setUpQuestion(currentQuestion);
            }
        });


    }

    private void associateElements(View view){

        layoutForGame = view.findViewById(R.id.VF_layoutGame);
        layoutResultat = view.findViewById(R.id.VF_layoutResultat);
        txtVConsigne =  view.findViewById(R.id.VF_consigne);
        txtVNbQuestion = view.findViewById(R.id.VF_numeroQuestion);
        txtVQuestion =  view.findViewById(R.id.VF_question);
        txtVResultAnswer = view.findViewById(R.id.VF_resultAnswer);
        txtVResultQuestion =view.findViewById(R.id.VF_resultQuestion);
        btnVrai = view.findViewById(R.id.VF_btnVrai);
        btnFaux = view.findViewById(R.id.VF_btnFaux);
        imageAnswer = view.findViewById(R.id.VF_ImageAnswer);
        timer = view.findViewById(R.id.VF_chrono);
        lPlayerSelection = view.findViewById(R.id.playerSelection);
        tGameName = view.findViewById(R.id.gamerName);
        tGameTitle = view.findViewById(R.id.gameTitle);
        isCorrectMessage = view.findViewById(R.id.VF_isCorrectMessage);
        tContinue = view.findViewById(R.id.blink_continue);

        selection_title = view.findViewById(R.id.selection_title);
        Typeface type = Typeface.createFromAsset(getActivity().getAssets(),"fonts/steinem.ttf");
        selection_title.setTypeface(type);

    }

    // ------------- Différents setup en fonction du spot ---------------------

    private void setUpForPalaisRihour(){
        question1 = "Le Palais a souffert de 4 incendies ?";
        question2 = "La construction du bâtiment est antérieur à 1395 ?";
        question3 = "Le Palais fait parti des Monuments Historiques ?";

        bonneReponse1 = true;
        bonneReponse2 = false;
        bonneReponse3 = true;

        infos1 = "Le Palais de Rihour a subit 3 violent incendies avant le XVIIème siècle ainsi qu'un quatrième en 1918.";
        infos2 = "La construction du Palais c'est finie en 1473, sous Charles le Téméraire.";
        infos3 = "Le Palais de Rihour est inscrit aux Monuments Historiques depuis 1998";
    } // DONE

    private void setUpForFuretDuNord(){
        question1 = "Jean de la Fontaine est né à Lille ?";
        question2 = "Marguerite Yourcenar est une la première femme élue à l'Académie Francaise ?";
        question3 = "Paul Callens ( premier propriaitaire du Furet du Nord en temps que librairie ) a put acheter la boutique grâce aux économies de ses parents ?";

        bonneReponse1 = false;
        bonneReponse2 = true;
        bonneReponse3 = false;

        infos1 = "Jean de la Fontaine est né en 1621 à Château-Thierry, dans l'Aisne";
        infos2 = "Elle fut la première femme élue à l'Académie française, le 6 mars 1980.";
        infos3 = "Paul Callens a put acheter la boutique grâce aux économies d'une de ses amies.";
    } // DONE

    private void setUpForBeffroi(){
        question1 = "Le Beffroi de Lille mesure plus de 100 mètres ?";
        question2 = "Le Beffroi donne sur le Boulevard Vauban ?";
        question3 = "Les Beffrois sont des symboles de richesses ?";

        bonneReponse1 = false;
        bonneReponse2 = false;
        bonneReponse3 = true;

        infos1 = "Le Beffroi de Lille mesure 76 mètres.";
        infos2 = "Le Beffroi ne donne pas sur le boulevard Vauban.";
        infos3 = "Les beffrois sont les symbole de la reconnaissance du roi et de la richesse de la ville.";
    } // DONE

    private void setUpForLouiseDeBettignies(){
        question1 = "Depuis cette place, on peut voir un bar nommé L'Imaginaire ?";
        question2 = "La Place Louise De Bettignies donne accès à la rue Nationale ?";
        question3 = "Sur cette place ce situe la célèbre statue de Louise de Bettignies ?";

        bonneReponse1 = true;
        bonneReponse2 = false;
        bonneReponse3 = false;

        infos1 = "Effectivement, si vous observez bien, vous appercevrez un bar nommé \"L'Imaginaire\", au Sud de la place. ";
        infos2 = "La rue Nationale n'est pas du tout proche de cette place. \n Regardez votre carte.";
        infos3 = "Vous voyez une statue sur cette place ? \n La statue ce trouve en réalité à l'entrée du boulevard Carnot.";
    } // DONE

    private void setUpForRueEsquermoise(){
        question1 = "La rue Esquermoise fût construite après la Première Guerre Mondiale ?";
        question2 = "L'Opéra de Lille se situe \"Place de l'Opéra\" ?";
        question3 = "Pendant la Première Guerre Mondiale, la ville de Lille est occupé de 1914 à 1918 ?";

        bonneReponse1 = false;
        bonneReponse2 = false;
        bonneReponse3 = true;

        infos1 = "On retrouve des traces de l'existance de cette rue au XI siècle. Elle est donc bien antérieure à la remière Guerre Mondiale.";
        infos2 = "L'Opéra de Lille se situe \"Place du Théatre\". C'était un petit piège, il est vrai.";
        infos3 = "La ville de Lille fût occupée par les Allemands d’octobre 1914 à octobre 1918.";
    } // DONE

    private void setUpForQuinquin(){
        question1 = "Le nom \"P'tit Quinquin\" est le nom original de la chanson ?";
        question2 = "Il existe une film nommé \"La Statue du Quinquin\" ?";
        question3 = "Les Lillois entendent régulièrement cette musique.";

        bonneReponse1 = false;
        bonneReponse2 = false;
        bonneReponse3 = true;

        infos1 = "Le titre original de la chanson est \"L'canchon Dormoire\" (La chanson pour dormir). Une berceuse destinée à endormir les enfants.";
        infos2 = "Il existe cependant un film nommé \"P'tit Quinquin\" réalisé par Bruno Dumont en 2014.";
        infos3 = "Ahah !! La mélodie du \"P'tit quinquin\" est régulièrement sonné toutes les heures par le carillon du beffroi de la Chambre de commerce de Lille.";
    }

    // ------------ Méthode de gestion du jeu ---------------------------------


    /**
     * Cette méthode prépare les différents éméments pour le lancement du jeu
     */
    public void setUpBeforeGame(){
        txtVConsigne.setVisibility(View.VISIBLE);

        layoutForGame.setVisibility(View.GONE);
        layoutResultat.setVisibility(View.GONE);

        txtVConsigne.setText(ConstantInfos.CONSIGNE_VRAIFAUX);
        txtVConsigne.setTextSize(25);
        txtVConsigne.setTextColor(getResources().getColor(R.color.noir));

        countDown = new CountDownTimer(10001, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timer.setText("Temps Restant : "+millisUntilFinished / 1000);
                if ((millisUntilFinished / 1000) <= 3){
                    timer.setTextColor(getResources().getColor(R.color.rouge));
                } else {timer.setTextColor(getResources().getColor(R.color.noir));}
            }

            @Override
            public void onFinish() {
                timer.setText("Temps Restant : 00");
                currentBonneReponse = true;
                setUpQuestion(currentQuestion);
            }
        };

        // -------------------- Gestion du click sur le txtConsigne -----------------
        txtVConsigne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fadeOutAndFadeIn(txtVConsigne, layoutForGame, 1);
                txtVConsigne.setClickable(false);
                setUpQuestion(0);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        countDown.start();
                    }
                }, 4000);
                vraiFauxFragmentClassCallBack.handleBtnClick(false);
            }
        });

    }

    /**
     * Méthode gérant l'affichage de la question suivante
     * @param question
     */
    public void setUpQuestion(int question){
        switch (question){
            case 0 :
                txtVNbQuestion.setText(R.string.vf_numQuestion_1);
                timer.setText(R.string.vf_timer);
                txtVQuestion.setText(question1);
                currentQuestion = 1;

                break;
            case 1 :
                nextQuestionAnimation(question);
                reponse1 = currentBonneReponse;
                txtVNbQuestion.setText(R.string.vf_numQuestion_2);
                currentQuestion = 2;
                break;

            case 2 :
                nextQuestionAnimation(question);
                reponse2 = currentBonneReponse;
                txtVNbQuestion.setText(R.string.vf_numQuestion_3);
                currentQuestion = 3;
                break;

            case 3 :
                countDown.cancel();
                btnVrai.setClickable(false);
                btnVrai.setEnabled(false);
                btnFaux.setClickable(false);
                btnFaux.setEnabled(false);
                fadeOutAndFadeIn(layoutForGame, layoutResultat, 2);
                reponse3 = currentBonneReponse;
                animator.fadeOutAnimation(timer);
                break;
        }

    }

    /**
     * Cette méthode s'occupe de l'animation qui sort par la gauche la question et la fait revenir par la droite
     * Elle est activé a chaque changement de question
     */
    public void nextQuestionAnimation(final int question){


        countDown.cancel();
        timer.setTextColor(getResources().getColor(R.color.noir));

        txtVQuestion.animate().translationX(-1000).setDuration(450);



        final Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                txtVQuestion.setVisibility(View.INVISIBLE);
                txtVQuestion.animate().translationX(1000).setDuration(5);
            }
        }, 500);


        final Handler handler2 = new Handler();
        handler2.postDelayed(new Runnable() {
            @Override
            public void run() {
                txtVQuestion.setVisibility(View.VISIBLE);
                txtVQuestion.animate().translationX(0).setDuration(450);

                switch (question){
                    case 1 :
                        txtVQuestion.setText(question2);
                        break;

                    case 2 :
                        txtVQuestion.setText(question3);
                       break;

                }

                    timer.setText("Temps Restant : 11");
                    countDown.start();


            }

        }, 1000);
    }


    /**
     * Cette méthode fait sortir par la gauche notre question et la fait revenir par la droite
     * @param textView
     */
    private void outAndInAnimation(final TextView textView){

        textView.animate().translationX(-1000).setDuration(450);
        layoutResultat.setClickable(false);
        layoutResultat.setEnabled(false);

        final Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                textView.setVisibility(View.INVISIBLE);
                textView.animate().translationX(1000).setDuration(5);
            }
        }, 460);


        final Handler handler2 = new Handler();
        handler2.postDelayed(new Runnable() {
            @Override
            public void run() {
                textView.setVisibility(View.VISIBLE);
                layoutResultat.setClickable(true);
                layoutResultat.setEnabled(true);
                textView.animate().translationX(0).setDuration(450);
            }
        }, 1000);


    }

    // ------- Méthode de vérification de la réponse -------

    /**
     * Méthode comparant les réponses avec les bonnes
     * Permet d'obtenir le nombre de bonne réponse
     */
    public void checkAnswer(){
        nbReponseCorrect = 0;
        if (reponse1 == bonneReponse1){nbReponseCorrect++; correct1 = true;}
        if (reponse2 == bonneReponse2){nbReponseCorrect++; correct2 = true;}
        if (reponse3 == bonneReponse3){nbReponseCorrect++; correct3 = true;}
    }

    // -------   -------
    /**
     * Méthode gérant l'affichage des messages du fin
     * Montre la solution pour chaque question
     * Ajoute des points si 4 réponses ou plus sont correctes
     */
    public void showEndMessage(){

        currentQuestion = 1;
        setUpMessageAndImageForCorrection(question1, correct1, bonneReponse1, infos1);

        layoutResultat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (currentQuestion){
                    case 1:
                        outAndInAnimation(txtVResultQuestion);
                        setUpMessageAndImageForCorrection(question2, correct2, bonneReponse2, infos2);
                        currentQuestion = 2;
                        break;

                    case 2:
                        outAndInAnimation(txtVResultQuestion);
                        setUpMessageAndImageForCorrection(question3, correct3, bonneReponse3, infos3);
                        currentQuestion = 3;
                        break;

                    case 3:

                        outAndInAnimation(txtVResultQuestion);
                        animator.fadeOutAnimation(txtVResultAnswer);
                        animator.fadeOutAnimation(imageAnswer);
                        animator.fadeOutAnimation(isCorrectMessage);

                        currentQuestion = 100;

                        // Si le jeu est perdu
                        if(nbReponseCorrect < 2) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    imageAnswer.setVisibility(View.INVISIBLE);
                                    isCorrectMessage.setVisibility(View.INVISIBLE);
                                    txtVResultQuestion.setTextSize(30);
                                    txtVResultQuestion.setText("Vous avez obtenu "+ nbReponseCorrect + " bonne réponse.");
                                    txtVResultAnswer.setText(ConstantInfos.LOOSEMESSAGE_VRAIFAUX);
                                    animator.fadeInAnimation(txtVResultAnswer);
                                }
                            }, 750);

                        }

                        // Si le jeu est gagné
                        if(nbReponseCorrect >= 2) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    txtVResultQuestion.setText("Vous avez obtenu "+ nbReponseCorrect + " bonnes réponses.");
                                    txtVResultAnswer.setText(ConstantInfos.WINMESSAGE_VRAIFAUX);
                                    animator.fadeInAnimation(txtVResultAnswer);
                                }
                            }, 750);

                        }


                        resultat = nbReponseCorrect >= 2;

                        break;

                    case 100 :
                        vraiFauxFragmentClassCallBack.whenGameIsValidate(resultat);
                        break;
                }
            }
        });

    }


    private void setUpMessageAndImageForCorrection(final String question,final boolean correct, final boolean bonneReponse, final String info){

        animator.fadeOutAnimation(txtVResultAnswer);
        animator.fadeOutAnimation(isCorrectMessage);
        animator.fadeOutAnimation(imageAnswer);


        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                txtVResultQuestion.setText(question);

                if(bonneReponse){imageAnswer.setBackgroundResource(R.drawable.btn_choix_vrai);}
                else {imageAnswer.setBackgroundResource(R.drawable.btn_choix_faux);}

                if(correct){isCorrectMessage.setText("Bravo !!! Vous avez bien fait de répondre :");}
                else{isCorrectMessage.setText("Dommage !!! La bonne réponse était :");}

                txtVResultAnswer.setText(info);
            }
        }, 750);



        Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                animator.fadeInAnimation(txtVResultAnswer);
                animator.fadeInAnimation(isCorrectMessage);
                animator.fadeInAnimation(imageAnswer);
            }
        }, 750);


    }

    /**
     * Cette méthode fait disparaitre la view1 puis fait apparaitre la view2
     * @param view1
     * @param view2
     */
    public void fadeOutAndFadeIn(final View view1, final View view2, final int step){
        final Animation out = new AlphaAnimation(1.0f, 0.0f);
        out.setDuration(2000);

        final Animation in = new AlphaAnimation(0.0f, 1.0f);
        in.setDuration(2000);

        view1.startAnimation(out);

        out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view1.setVisibility(View.GONE);
                view2.setVisibility(View.INVISIBLE);
                view2.startAnimation(in);
                if (step == 2 ){
                    checkAnswer();
                    showEndMessage();
                    countDown.cancel();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        in.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view2.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }


    // ------- Méthode de gestion d'affichage de la réponse --------

    // --------- Selection du joueur ----------
    /**
     * Cette méthode prépare l'écran de séléction d'un joueur
     */
    public void setUpGamerSelection(){
        lPlayerSelection.setVisibility(View.VISIBLE);

        tGameTitle.setText(vraiFauxFragmentClassCallBack.getGameTitle());
        tGameTitle.setPaintFlags(tGameTitle.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        playersName = vraiFauxFragmentClassCallBack.getPlayerName();

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


}
