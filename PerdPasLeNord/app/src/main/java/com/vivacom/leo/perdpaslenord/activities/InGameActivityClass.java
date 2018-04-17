package com.vivacom.leo.perdpaslenord.activities;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.DragEvent;
import android.view.Menu;
import android.view.View;

import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vivacom.leo.perdpaslenord.R;
import com.vivacom.leo.perdpaslenord.ViewAnimations;
import com.vivacom.leo.perdpaslenord.fragments.MJFragmentClass;
import com.vivacom.leo.perdpaslenord.fragments.MenuCircleButtonFragment;
import com.vivacom.leo.perdpaslenord.fragments.NativeCameraFragment;
import com.vivacom.leo.perdpaslenord.fragments.RoadBookFragmentClass;
import com.vivacom.leo.perdpaslenord.fragments.game.DifferenceFragmentClass;
import com.vivacom.leo.perdpaslenord.fragments.game.DragNDropPuzzleFragmentClass;
import com.vivacom.leo.perdpaslenord.fragments.game.DragNDropAssociationFragmentClass;
import com.vivacom.leo.perdpaslenord.fragments.GoogleMapFragment;
import com.vivacom.leo.perdpaslenord.fragments.InformationFragmentClass;
import com.vivacom.leo.perdpaslenord.fragments.PictureGaleryFragmentClass;
import com.vivacom.leo.perdpaslenord.fragments.game.QCMFragmentClass;
import com.vivacom.leo.perdpaslenord.fragments.game.VraiFauxFragmentClass;
import com.vivacom.leo.perdpaslenord.objects.SpotClass;
import com.vivacom.leo.perdpaslenord.objects.TeamClass;
import com.vivacom.leo.perdpaslenord.objects.ZoneClass;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import es.dmoral.toasty.Toasty;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by Leo on 08/09/2017.
 */

public class InGameActivityClass extends AppCompatActivity implements GoogleMapFragment.GoogleMapFragmentCallBack, QCMFragmentClass.QCMFragmentClassCallBack ,
        DragNDropAssociationFragmentClass.DragNDropAssociationFragmentClassCallBack, DragNDropPuzzleFragmentClass.DragNDropPuzzleFragmentClassCallBack,
        PictureGaleryFragmentClass.PictureGaleryFragmentClassCallBack , VraiFauxFragmentClass.VraiFauxFragmentClassCallBack,
        MenuCircleButtonFragment.MenuCircleButtonFragmentCallBack, NativeCameraFragment.NativeCameraFragmentCallBack,
        InformationFragmentClass.InformationFragmentClassCallBack, MJFragmentClass.MJFragmentCallBack, DifferenceFragmentClass.DifferenceFragmentClassCallBack{



    // ------- Elements Graphiques -------
    RelativeLayout mainLayout, lMJ_Fragment;
    RelativeLayout layoutForAction, layoutForGame;
    RelativeLayout layoutForRoadBook;
    LinearLayout layoutForMap, layoutForUnableMap, menu;
    View whiteView;
    ProgressDialog progress;
    TextView tConsigne, tContinuer, txtVSpotName;

    // ------- Nos Objets -------
    ViewAnimations animator = new ViewAnimations();
    Realm realm;
    SpotClass spotSelected;
    ZoneClass zoneSelected;


    List<SpotClass> secretSpotList = new ArrayList<>();
    ArrayList<String> spotCompletedList = new ArrayList<>();
    ArrayList<Integer> listPhotoOfSpot = new ArrayList<>();

    // ------- Notre Team -------
    TeamClass theTeam;
    List<String> playerList = new ArrayList<>();

    // -------- Mes boolean -------
    boolean menuOpen = false;
    boolean interfaceVisible = false;
    boolean whiteViewActive = false;
    boolean firstGameWin = false;
    boolean interfaceWasVisible;

    // -------- Elements pour les STATISTIQUES ------------
    int nbSpotCompleted = 0;
    int nbZoneCompleted = 0;
    int nbGameWin = 0;
    int currentFragment = 0;
    int nbSecretDiscovered = 0;

    String statsRecap = "";
    public final String TAG = "In_Game";
    String actualFragment = "NOTHING";

    // -------------------------------------------

    @Override
    public  void onResume(){
        super.onResume();
        Log.d(TAG, "Activity onResume");
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.i("INGAME", "Activity onPause");
        //hideInGameInterface();
    }

    // -------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_game);

        Log.i(TAG, "Activity onCreate : ----------- Start");

        // On associe nos elements
        associateElements();

        // ----- SetUp du jeu -----
        // Placement du fragment GoogleMap dans le layout
        setUpGoogleMapFragment();
        placeMJFragment();

        // On sort le menu et le mainSreen
        slideOutMenu();
        slideMainScreenOut();

        // Config des éléments visibles
        layoutForMap.setVisibility(View.VISIBLE);
        layoutForGame.setVisibility(View.INVISIBLE);
        layoutForUnableMap.setVisibility(View.INVISIBLE);
        layoutForRoadBook.setVisibility(View.GONE);

        // On récupère notre objet Team
        getTeamFromBDD();

        // On crée un view vide pour plus tard (utile pour le DnD)
        whiteView = new View(this);

        // On lance notre progressBar
        startProgressBarProgression();

        Log.i(TAG, "Activity onCreate : ---------- End");


    }

    // ------------------------------------------------------------------------------------
    // ---------------- Méthodes de gestion de l'activity (initialisation) --------------

    /**
     * Méthode affectant a chaque object son équivalent graphique
     */
    private void associateElements(){
        // Association des éléments
        mainLayout = findViewById(R.id.IG_mainLayout);
        layoutForMap = findViewById(R.id.IG_layoutForMap);
        layoutForUnableMap = findViewById(R.id.IG_layoutForUnableMap);
        lMJ_Fragment = findViewById(R.id.MJ_Fragment);
        tConsigne = findViewById(R.id.MJ_consigne);
        tContinuer = findViewById(R.id.blink_continue);
        layoutForAction = findViewById(R.id.IG_layoutForAction);
        layoutForGame = findViewById(R.id.IG_layoutForGame);
        txtVSpotName =  findViewById(R.id.IG_txtVSpotName);
        menu = findViewById(R.id.IG_fragment_menu);
        layoutForRoadBook =  findViewById(R.id.IG_layoutForRoadBook);
    }

    /**
     * Méthode controllant le bouton back de la tablette
     * Ici, on ne mets rien a l'interieur afin de désactiver le bouton
     */
    @Override
    public void onBackPressed() {
        //Nothing
    }

    /**
     * Méthode controllant le bouton menu de la tablette
     * Ici, on ne mets rien a l'interieur afin de désactiver le bouton
     */
    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        return false;
    }

    /**
     * Méthode affichant la progressBart le temps que la map chaerge entierement
     * Appelé au lancement de l'activity
     */
    private void startProgressBarProgression(){
        layoutForUnableMap.setVisibility(View.VISIBLE);
        progress = new ProgressDialog(this);
        progress.setMessage("Préparation de la carte ....");
        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progress.setIndeterminate(false);
        progress.setCancelable(false);
        progress.setProgress(0);
        progress.show();

        final int totalProgressTime = 100;

        final Thread t = new Thread() {
            @Override
            public void run() {
                int jumpTime = 0;
                while(jumpTime < totalProgressTime) {
                    try {
                        sleep(150);
                        jumpTime += 5;
                        progress.setProgress(jumpTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progress.hide();
                        progress.dismiss();
                        layoutForUnableMap.setVisibility(View.GONE);
                        // On place le RoadBook
                        setUpRoadBook();
                        // On change le design du currentPositionMarker
                        GoogleMapFragment googleMapManager = (GoogleMapFragment) getSupportFragmentManager().findFragmentById(R.id.IG_layoutForMap);
                        googleMapManager.changeCurrentPosMarkerIcon(theTeam.getNumInsinge());
                    }
                });
            }
        };

        t.start();
    }

    /**
     * Cette méthode ajoute une view a tout l'écran (pour éviter problème de DnD)
     */
    public void setUpWhiteScreen(boolean activate){
        if (activate){
            layoutForGame.addView(whiteView);
            layoutForGame.setOnDragListener(new MyDragListener());
            whiteViewActive = true;
        } else {
            layoutForGame.removeView(whiteView);
            whiteViewActive = false;
        }

    }


    //  ------- Méthode de récupération de notre objet TeamClass -------
    /**
     * Cette méthode récupère et renvoye l'objet Team passé dans l'Intent
     * @return
     */
    private void getTeamFromBDD(){

      realm = Realm.getDefaultInstance();

      try{
          TeamClass team = realm.where(TeamClass.class).findFirst();
          if(team != null){
              theTeam = new TeamClass(team.getmTeamName(), team.getMembersList(), team.getNumInsinge());
              playerList.addAll(team.getMembersList());
          }

      } finally {
          realm.close();
      }
    }


    // -----------------------------------------------------------------------------------
    // ----------------- Méthodes controllant les changement CARTE / JEU et les actions de la carte --------------------
    /**
     * Méthode slidant les layout hors de l'écran
     * On attend que les layouts soient hors de l'ecran avec d'agir
     */
    public void hideInGameInterface(){
        // On fait disparaitre le carnet de bord si il est visible
        MenuCircleButtonFragment menuCircleButtonManager = (MenuCircleButtonFragment) getSupportFragmentManager().findFragmentById(R.id.IG_fragment_menu);
        if(menuCircleButtonManager.isCarnetVisible()){animator.fadeOutAnimation(layoutForRoadBook);}
        // On fait disparaitre "la couche" qui désactive la map
        animator.fadeOutAnimation(layoutForUnableMap);
        // On active la map et désactive le jeu
        layoutForGame.setClickable(false);
        layoutForMap.setClickable(true);
        if (whiteViewActive){setUpWhiteScreen(false);}
        // On réactive le fragment
        GoogleMapFragment googleMapManager = (GoogleMapFragment) getSupportFragmentManager().findFragmentById(R.id.IG_layoutForMap);
        googleMapManager.onResume();
        activateMenuAnimation();
        reinitSpotName();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                slideOutMenu();
                slideMainScreenOut();
                txtVSpotName.animate().translationX(-1500).setDuration(650).withLayer();
                hideRoadBook();
            }
        }, 750);
        interfaceVisible = false;
    }

    /**
     * Méthode slidant les layout au centre
     */
    private void showInGameInterface(){
        // On rend les éléments visibles
        animator.fadeInAnimation(layoutForUnableMap);
        layoutForGame.setVisibility(View.VISIBLE);
        layoutForGame.setClickable(true);
        layoutForMap.setClickable(false);
        GoogleMapFragment googleMapManager = (GoogleMapFragment) getSupportFragmentManager().findFragmentById(R.id.IG_layoutForMap);
        googleMapManager.onPause();
        // On fait rentrer les éléments dans l'écran
        setUpCircleButtonMenu();
        slideInMenu();
        // On fait apparaitre le nom du Spot
        animator.fadeInAnimation(txtVSpotName);
        // On le reduit ensuite et on le place dans le coin
        reduceSpotName();
        interfaceVisible = true;


        // On active l'animation du menu , après 1.5sec (temps pour faire appartaitre et animer le nom du spot)
        Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                activateMenuAnimation();
            }
        }, 1500);

        // On rentre le mainSreen après 2sec
        Handler handler2 = new Handler();
        handler2.postDelayed(new Runnable() {
            @Override
            public void run() {
                setInformationFragment();
                slideMainScreenIn();
            }
        }, 2000);

    }

    /**
     * Cette méthode sort l'écran principal à l'exterieur de l'ecran
     */
    private void slideMainScreenOut(){
        layoutForAction.animate().translationX(-2000).setDuration(650).withLayer();
    }

    /**
     * Cette méthode rentre l'écran principal à  l'interieur de l'ecran
     */
    private void slideMainScreenIn(){
        layoutForAction.animate().translationX(0).setDuration(650).withLayer();
    }

    /**
     * Cette méthode s'occupe de la transition entre deux modes (INFO / PHOTO / JEU)
     * @param currentFragment
     */
    public void slideOutLeftAndInRight(final int currentFragment){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hideRoadBook();
                layoutForAction.animate().translationX(-2000).setDuration(650);

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        switch (currentFragment){
                            case 1:
                                setInformationFragment();
                                break;
                            case 2:
                                setGaleriePhotoFragment();
                                break;
                            case 3:
                                setGameFragment();
                                break;
                        }
                        layoutForAction.animate().translationX(0).setDuration(750);
                    }
                }, 750);
            }
        });
    }

    /**
     * Cette méthode va animer notre View txtVSpotName pour reduire sa taille
     * et le déplacer vers le coin inférieur gauche de l'écran
     */
    private void reduceSpotName(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                txtVSpotName.animate().translationX(-390).withLayer();
                txtVSpotName.animate().translationY(330).withLayer();
                txtVSpotName.animate().scaleX((float)0.7).setDuration(500);
            }
        }, 1000);
    }

    /**
     * Cette méthode rénitialise la View txtVSpotName
     */
    private void reinitSpotName(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                txtVSpotName.setVisibility(View.GONE);
                txtVSpotName.animate().translationX(0).withLayer();
                txtVSpotName.animate().translationY(0).withLayer();
                txtVSpotName.animate().scaleX(1);
            }
        }, 1000);
    }



    // ------------------------------------------------------------------------------------
    // ----------------- Méthodes controllant le menu et son affichage --------------------

    /**
     * Méthode placant le fragment Menu1Action dans le layout menu1Flip
     * Est appellé lorsque le menu n'est pas affiché (aucune animation)
     */
    private void setUpCircleButtonMenu(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Boolean bool1 = spotSelected.isInfoChecked();
                Boolean bool2 = spotSelected.isImageChecked();
                Boolean bool3 = spotSelected.isGameChecked();
                try{
                    FragmentTransaction transactionManager = getSupportFragmentManager().beginTransaction();
                    transactionManager.replace(R.id.IG_fragment_menu, MenuCircleButtonFragment.newInstance(bool1,bool2,bool3, currentFragment))
                            .addToBackStack(null)
                            .commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }

    /**
     * Cette méthode s'occupe de l'animation du menu
     * True fait rentrer les boutons
     * False fait sortir les boutons
     */
    private void activateMenuAnimation (){
        MenuCircleButtonFragment menuCircleButtonManager = (MenuCircleButtonFragment) getSupportFragmentManager().findFragmentById(R.id.IG_fragment_menu);
        if (menuOpen){
            menuCircleButtonManager.closeMenu();
            menuOpen = false;
        } else {
            menuCircleButtonManager.openMenu();
            menuOpen = true;
        }

        menuCircleButtonManager.changeBtnVisibility(spotSelected.getmSpotType());


    }

    /**
     * Cette méthode sort le menu de l'écran
     */
    private void slideOutMenu(){
        menu.animate().translationX(200).withLayer();
    }

    /**
     * Cette méthode fait rentrer le menu dans l'écran
     */
    private void slideInMenu(){
        menu.animate().translationX(0).withLayer();
    }


    public void handleBtnClick(boolean canClick){
        MenuCircleButtonFragment menuCircleButtonManager = (MenuCircleButtonFragment) getSupportFragmentManager().findFragmentById(R.id.IG_fragment_menu);
        menuCircleButtonManager.setBtnClickable(canClick);
    }


    // -----------------------------------------------------------------------------------
    // ----------------- Méthodes gérant l'affichage du CARNET DE BORD -------------------

    /**
     * Methode créant notre fragment Carnet de Bord
     */
    private void setUpRoadBook(){
        try{
            FragmentTransaction transactionManager = getSupportFragmentManager().beginTransaction();
            transactionManager.replace(R.id.IG_layoutForRoadBook, RoadBookFragmentClass.newInstance())
                    .addToBackStack(null)
                    .commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        layoutForRoadBook.setVisibility(View.GONE);

    }

    /**
     * Methode qui permet de passer du Carnet de Bord au Jeu
     */
    public void transitionRoadBookToGame(){
        RoadBookFragmentClass roadBookManager = (RoadBookFragmentClass) getSupportFragmentManager().findFragmentById(R.id.IG_layoutForRoadBook);
        final MenuCircleButtonFragment menuCircleButtonManager = (MenuCircleButtonFragment) getSupportFragmentManager().findFragmentById(R.id.IG_fragment_menu);

        roadBookManager.onPause();
        animator.fadeOutAnimation(layoutForRoadBook);
        Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {slideMainScreenIn();}}, 1000);

        Handler handlerDelay = new Handler();
        handlerDelay.postDelayed(new Runnable() {
            @Override
            public void run() {menuCircleButtonManager.setCarnetAnimationOverTrue();}}, 2000);
    }

    /**
     * Methode qui permet de passer du jeu au Carnet de Bord
     */
    public void transitionGameToRoadBook(){
        RoadBookFragmentClass roadBookManager = (RoadBookFragmentClass) getSupportFragmentManager().findFragmentById(R.id.IG_layoutForRoadBook);
        final MenuCircleButtonFragment menuCircleButtonManager = (MenuCircleButtonFragment) getSupportFragmentManager().findFragmentById(R.id.IG_fragment_menu);

        roadBookManager.onResume();
        slideMainScreenOut();
        Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {animator.fadeInAnimation(layoutForRoadBook);}}, 1000);

        Handler handlerDelay = new Handler();
        handlerDelay.postDelayed(new Runnable() {
            @Override
            public void run() {menuCircleButtonManager.setCarnetAnimationOverTrue();}}, 2000);
    }

    /**
     * Cette méthode fait disparaitre le layout du RoadBook
     * Si ce dernier est visible
     */
    private void hideRoadBook(){
        MenuCircleButtonFragment menuCircleButtonManager = (MenuCircleButtonFragment) getSupportFragmentManager().findFragmentById(R.id.IG_fragment_menu);
        if(menuCircleButtonManager.isCarnetVisible()){transitionRoadBookToGame();}
    }

    /**
     * Cette méthode ajoute une photo a la liste des photos
     * Appelé depuis le fragment NativeCameraFragment
     * @param btm
     */
    public void addPhotoToGalery(Bitmap btm){
        RoadBookFragmentClass roadBookFragment = (RoadBookFragmentClass) getSupportFragmentManager().findFragmentById(R.id.IG_layoutForRoadBook);
        roadBookFragment.addPhotoToList(btm);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RoadBookFragmentClass roadBookManager = (RoadBookFragmentClass) getSupportFragmentManager().findFragmentById(R.id.IG_layoutForRoadBook);
                roadBookManager.setPhotoVisible();
            }
        });
    }

    // -----------------------------------------------------------------------------------
    // ------------------ Méthodes controllant l'affichage du guide ----------------------

    /**
     * Cette méthode place le Fragment MJFragment dans son Layout
     */
    private void placeMJFragment(){
        try{
            FragmentTransaction transactionManager = getSupportFragmentManager().beginTransaction();
            transactionManager.replace(R.id.MJ_Fragment, MJFragmentClass.newInstance())
                    .addToBackStack(null)
                    .commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        lMJ_Fragment.setVisibility(View.GONE);
    }

    /**
     * Cette méthode fait disparaitre la Fragment MJFragment
     */
    public void hideMJFragment(){
        lMJ_Fragment.animate().translationX(-1500).withLayer().setDuration(500);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                lMJ_Fragment.setVisibility(View.GONE);
                lMJ_Fragment.animate().translationX(0).withLayer();
                // On fait réapparaitre l'interface (si elle etait visible)
                if (interfaceWasVisible){showInGameInterface();}
                // On fait réapparaitre la légende (si cette derniere ainsi que l'interfarce ne sont pas visible)
                GoogleMapFragment googleMapManager = (GoogleMapFragment) getSupportFragmentManager().findFragmentById(R.id.IG_layoutForMap);
                if(!interfaceVisible && !googleMapManager.isLegendVisible()){googleMapManager.mooveLegend();}
                Log.d(TAG, "End MJ Message / InterfaceWasVisible :"+interfaceWasVisible);
            }
        }, 500);


    }

    /**
     * Cette méthode fait apparaitre le MJFragment avec les bons messages
     * @param messages
     */
    public void showMJFragment(final String[] messages, int numExpression){
        MJFragmentClass MJManager = (MJFragmentClass) getSupportFragmentManager().findFragmentById(R.id.MJ_Fragment);

        MJManager.setMessageList(messages);
        MJManager.showFirstMessage();
        MJManager.changeMJExpression(numExpression);

        if (interfaceVisible){
            hideInGameInterface();
            interfaceWasVisible = true;

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    animator.fadeInAnimation(lMJ_Fragment);
                }
            }, 1000);

        } else {
            interfaceWasVisible = false;
            animator.fadeInAnimation(lMJ_Fragment);
        }



        Log.d(TAG, "Start MJ Message / InterfaceWasVisible :"+interfaceWasVisible);
    }

    /**
     * Cette méthode va récuperer les messageses a donner au MJ
     */
    public void getMessageForMJ(){

        if(spotSelected.getmSpotNbInformation() == 3){
            String[] messages = new String[4];
            messages[0] = spotSelected.getmSpotName();
            messages[1] = spotSelected.getmSpotInformation1();
            messages[2] = spotSelected.getmSpotInformation2();
            messages[3] = spotSelected.getmSpotInformation3();
            showMJFragment(messages, 3);
        }

        if(spotSelected.getmSpotNbInformation() == 2){
            String[] messages = new String[3];
            messages[0] = spotSelected.getmSpotName();
            messages[1] = spotSelected.getmSpotInformation1();
            messages[2] = spotSelected.getmSpotInformation2();
            showMJFragment(messages, 3);
        }
    }

    // -----------------------------------------------------------------------------------
    // ------------------ Méthode appelé depuis les fragments ----------------------------

    /**
     * Appelé quand l'utilisateur clique sur un marker de la carte
     * Cette méthode va faire disparaitre la carte et mettre en place
     * Tout les éléments en fonction du spot séléctionné
     * @param title
     */
    public void whenMarkerIsSelected(String title) {
        if (!Objects.equals(title, "Vous êtes ici !!")) {

            // On va chercher le spot correspondant dans la BDD Realm
            realm =  Realm.getDefaultInstance();
            try{
                SpotClass mySpot = realm.where(SpotClass.class).equalTo("mSpotName", title).findFirst();
                if (mySpot != null) {
                    createSpotSelected(mySpot);

                    if(spotSelected.getmSpotType() == 1 || spotSelected.getmSpotType() == 2){
                        showInGameInterface();
                        getZoneSelectedFromBDD();
                        createListPhotoOfSpot(mySpot);
                    } else if (spotSelected.getmSpotType() == 3){getMessageForMJ();}

                    txtVSpotName.setText(spotSelected.getmSpotName());
                    Typeface type = Typeface.createFromAsset(getAssets(),"fonts/BeautyDemo.ttf");
                    txtVSpotName.setTypeface(type);

                } else {
                    Log.e(TAG, "Impossible de récuperer le spot dans la BDD");
                }
            } finally {
                realm.close();
            }

        }



    }

    /**
     * Cette méthode va créer un objet SpotClass avec les informations obtenu dans la BDD
     * @param spot
     */
    public void createSpotSelected(SpotClass spot){
        if (spot.getmSpotType() == 1 || spot.getmSpotType() == 2){
            spotSelected = new SpotClass(spot.getmSpotType(), spot.getmSpotId(), spot.getZoneName(), spot.getmSpotName(), spot.getGameTitle(), spot.getmSpotNbInformation(), spot.getmSpotInformation1(), spot.getmSpotInformation2(), spot.getmSpotInformation3(), spot.getPhotoGallery(), spot.isInfoChecked(), spot.isImageChecked(), spot.isGameChecked(), spot.isSpotCompleted());
        } else if(spot.getmSpotType() == 3){
            spotSelected = new SpotClass(3, spot.getmSpotId(), spot.getmSpotName(), spot.getmSpotNbInformation(), spot.getmSpotInformation1(), spot.getmSpotInformation2(), spot.getmSpotInformation3());
        }
    }

    /**
     * Cette méthode va créer une liste d'Integer a partir des photos de la RealmList de notre objet
     * @param spot
     */
    public void createListPhotoOfSpot(SpotClass spot){
        listPhotoOfSpot.clear();
        // On créer une arrayList avec les photo de notre RealmList
        RealmList<Integer> realmList = spot.getPhotoGallery();
        listPhotoOfSpot.addAll(realmList);

    }

    /**
     * Méthode affectant les points ou non on fonction du résultat du QCM
     * @param correct
     */
    public void whenGameIsValidate(Boolean correct){
        MenuCircleButtonFragment menuCircleButtonManager = (MenuCircleButtonFragment) getSupportFragmentManager().findFragmentById(R.id.IG_fragment_menu);

        checkIfGameChecked(correct);
        if(interfaceVisible){slideOutLeftAndInRight(1);}

        // On check le gameBtn et on vérifie si le spot est complété
        menuCircleButtonManager.setBtnClickable(true);
        menuCircleButtonManager.setGameBtnValidate();

        // On désactive la view transparent si elle existe
        if (whiteViewActive){setUpWhiteScreen(false);}
    }


    // -----------------------------------------------------------------------------------
    // ------------------------- Méthodes de vérification --------------------------------


    /**
     * Méthode ajoutant des points lorsque l'utiilsateur regarde les informations
     * Ne s'active qu'une seule fois
     */
    public void checkIfInformationsCompleted(){
        MenuCircleButtonFragment menuCircleButtonManager = (MenuCircleButtonFragment) getSupportFragmentManager().findFragmentById(R.id.IG_fragment_menu);
        boolean infosChecked = spotSelected.isInfoChecked();
        if (!infosChecked){
            spotSelected.setInfoChecked();
            setInfoCheckedInBDD();
            checkIfSpotCompleted();
            menuCircleButtonManager.setInfoBtnValidate();
        }
    }

    /**
     * Méthode ajoutant des points lorsque l'utiilsateur regarde l'image
     * Ne s'active qu'une seule fois
     */
    public void checkIfPhotoGalleryChecked(){
        MenuCircleButtonFragment menuCircleButtonManager = (MenuCircleButtonFragment) getSupportFragmentManager().findFragmentById(R.id.IG_fragment_menu);
        boolean imageChecked = spotSelected.isImageChecked();
        if (!imageChecked){
            spotSelected.setImageChecked();
            setPhotoCheckedInBDD();
            checkIfSpotCompleted();
            menuCircleButtonManager.setPhotoBtnValidate();
        }
    }

    /**
     * Méthode ajoutant des points lorsque l'utiilsateur gagne le mini jeux
     * Ne s'acrtive qu'une seule fois
     */
    public void checkIfGameChecked(boolean correct){

        try{
            boolean gameChecked = spotSelected.isGameChecked();
            if (!gameChecked){
                spotSelected.setGameChecked();
                setGameCheckedInBDD();
                checkIfSpotCompleted();
                if(correct){
                    incrementStats_NbVictoire();
                    addNumberToList_listWord(spotSelected.getmSpotId());
                } else { addNumberToList_UnlistWord(spotSelected.getmSpotId()); }
            }
        } catch(Exception e){System.out.print(e.getMessage());}

    }

    /**
     * Méthode ajoutant des points lorsque un spot est fini
     * Ne s'active qu'une seule fois
     */
    private void checkIfSpotCompleted(){
        GoogleMapFragment googleMapManager = (GoogleMapFragment) getSupportFragmentManager().findFragmentById(R.id.IG_layoutForMap);

        boolean infosChecked = spotSelected.isInfoChecked();
        boolean imageChecked = spotSelected.isImageChecked();
        boolean gameChecked = spotSelected.isGameChecked();
        boolean spotCompleted = spotSelected.isSpotCompleted();

        if (infosChecked & gameChecked & imageChecked & !spotCompleted & spotSelected.getmSpotType() == 1)
        {
            spotSelected.setSpotCompleted();
            setSpotCompletedInBDD();
            nbSpotCompleted++;
            spotCompletedList.add(spotSelected.getmSpotName());
            googleMapManager.setMarkerCompleted(spotSelected);
            googleMapManager.incrementNbPointLegendBot(spotSelected.getmSpotType());
            incrementStats_PointPrincipaux();
            checkIfZoneCompleted();
            //if (interfaceVisible){hideInGameInterface();}

            Toasty.success(getApplicationContext(), "Bravo, vous avez complété " +spotSelected.getmSpotName()+" !", Toast.LENGTH_SHORT, true).show();
        }

        else if (infosChecked  & imageChecked & !spotCompleted & spotSelected.getmSpotType() == 2)
        {
            setSpotCompletedInBDD();
            googleMapManager.setMarkerCompleted(spotSelected);
            googleMapManager.incrementNbPointLegendBot(spotSelected.getmSpotType());
            incrementStats_PointSecondaire();
            //if (interfaceVisible){hideInGameInterface();}

            Toasty.success(getApplicationContext(), "Bravo, vous avez complété " +spotSelected.getmSpotName()+" !", Toast.LENGTH_SHORT, true).show();
        }
    }

    /**
     * Cette méthode vérifie si chaque spot de la list est complété
     * Si oui, elle renvoie TRUE, sinon elle renvoie FALSE
     * @param maListe
     * @return
     */
    private boolean checkIfAllSpotCompleted(List<SpotClass> maListe){

        boolean allChecked = true;

        // On regarde sur tout les psot de la liste
        for(SpotClass monSpot : maListe){
            // Si un spot n'est pas complété, on passe a false
            if(!monSpot.isSpotCompleted()){allChecked = false;}
        }

        return allChecked;
    }

    /**
     * Méthode ajoutant des points lorsque une zone est finie
     * Ne s'active qu'une seule fois
     */
    private void checkIfZoneCompleted(){
        String zoneName = zoneSelected.getmZoneName();
        boolean allCompleted = true;

        realm = Realm.getDefaultInstance();
        try {
            // On récupère tout les spot associé a la zone
            RealmQuery<SpotClass> query = realm.where(SpotClass.class);
            query.equalTo("mZoneName", zoneName);
            RealmResults<SpotClass> result = query.findAll();

            // On regarde si ils sont tous compléter
            for (SpotClass spot : result) {
                if (!spot.isSpotCompleted() && spot.getmSpotType() == 1) {
                    allCompleted = false;
                }
            }

            if (allCompleted) {
                zoneSelected.setmZoneCompleted();
                incrementStats_NbZones();
                setZoneCompletedInBDD();
            }
        } finally {
            realm.close();
        }
    }


    // ---------------------------------------------------------------------------------
    // ----------------------- Méthodes de gestion du mainFragment ---------------------

    /**
     * Cette méthode va appélé la méthode onDestroy du fragment précédent
     */
    private void destroyActualFragment(){
        switch (actualFragment){
            case "NOTHING" :
                Log.d(TAG, "Nothing destroy");
                break;
            case "INFOS" :
                InformationFragmentClass informationFragmentClassManager = (InformationFragmentClass) getSupportFragmentManager().findFragmentById(R.id.IG_fragmentLayout);
                informationFragmentClassManager.onDestroy();
                break;
            case "PHOTOS" :
                PictureGaleryFragmentClass pictureGaleryFragmentClass = (PictureGaleryFragmentClass) getSupportFragmentManager().findFragmentById(R.id.IG_fragmentLayout);
                pictureGaleryFragmentClass.onDestroy();
                break;
            case "VF" :
                VraiFauxFragmentClass vraiFauxFragmentClass = (VraiFauxFragmentClass) getSupportFragmentManager().findFragmentById(R.id.IG_fragmentLayout);
                vraiFauxFragmentClass.onDestroy();
                break;
            case "ASSO" :
                DragNDropAssociationFragmentClass dragNDropAssociationFragmentClass = (DragNDropAssociationFragmentClass) getSupportFragmentManager().findFragmentById(R.id.IG_fragmentLayout);
                dragNDropAssociationFragmentClass.onDestroy();
                break;
            case "PUZZLE" :
                DragNDropPuzzleFragmentClass dragNDropPuzzleFragmentClass = (DragNDropPuzzleFragmentClass) getSupportFragmentManager().findFragmentById(R.id.IG_fragmentLayout);
                dragNDropPuzzleFragmentClass.onDestroy();
                break;
            case "QCM" :
                QCMFragmentClass qCMFragmentClass = (QCMFragmentClass) getSupportFragmentManager().findFragmentById(R.id.IG_fragmentLayout);
                qCMFragmentClass.onDestroy();
                break;
            case "CAMERA" :
                NativeCameraFragment nativeCameraFragment = (NativeCameraFragment) getSupportFragmentManager().findFragmentById(R.id.IG_fragmentLayout);
                nativeCameraFragment.onDestroy();
                break;
        }
    }

    /**
     * Méthode qui affiche la carte avec les spot completer en vert
     * S'execute lorsque un spot est complete
     */
    private void setUpGoogleMapFragment(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    FragmentTransaction transactionManager = getSupportFragmentManager().beginTransaction();
                    transactionManager.replace(R.id.IG_layoutForMap,GoogleMapFragment.newInstance())
                            .addToBackStack(null)
                            .commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * Méthode affichant le fragment InformationFragmentClass
     * Lui passe en paramètre les informations correspondant au spot selected
     */
    private void setInformationFragment(){
        if (whiteViewActive){setUpWhiteScreen(false);}
        currentFragment = 1;
        destroyActualFragment();
        actualFragment = "INFOS";
        new Thread(new Runnable() {
            @Override
            public void run() {

                // Récupération des informations
                String infosList[] = getMessageListFromSpotSelected();

                try{
                    FragmentTransaction transactionManager = getSupportFragmentManager().beginTransaction();
                    transactionManager.replace(R.id.IG_fragmentLayout,InformationFragmentClass.newInstance(infosList))
                            .addToBackStack(null)
                            .commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * Cette mérhode revoye une liste de message créer a partir des information du Spot
     * @return
     */
    public String[] getMessageListFromSpotSelected(){
        String[] message;

        if(spotSelected.getmSpotNbInformation() == 1){
            message = new String[2];
            message[0] = spotSelected.getmSpotName();
            message[1] = spotSelected.getmSpotInformation1();
            return message;
        }

        else if (spotSelected.getmSpotNbInformation() == 3){
            message = new String[4];
            message[0] = spotSelected.getmSpotName();
            message[1] = spotSelected.getmSpotInformation1();
            message[2] = spotSelected.getmSpotInformation2();
            message[3] = spotSelected.getmSpotInformation3();
            return message;
        }

        else {
            message = new String[2];
            message[0] = "Erreur dans création de message";
            message[1] = spotSelected.getmSpotInformation1();
        }

        return message;
    }


    /**
     * Méthode créant et affichant le fragment GALERIE PHOTO en fonction du spot
     */
    private void setGaleriePhotoFragment(){
        if (whiteViewActive){setUpWhiteScreen(false);}
        currentFragment = 2;
        destroyActualFragment();
        actualFragment = "PHOTOS";
        new Thread(new Runnable() {
            @Override
            public void run() {

                final ArrayList<Integer> maListe = listPhotoOfSpot;

                // On place le fragment
                try{
                    FragmentTransaction transactionManager = getSupportFragmentManager().beginTransaction();
                    transactionManager.replace(R.id.IG_fragmentLayout, PictureGaleryFragmentClass.newInstance(maListe))
                            .addToBackStack(null)
                            .commit();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    /**
     * Méthode créant et affichant le fragment JEUX en fonction du spot
     */
    private void setGameFragment(){
        if (whiteViewActive){setUpWhiteScreen(false);}
        currentFragment = 3;
        final String spotName = spotSelected.getmSpotName();
        destroyActualFragment();
        new Thread(new Runnable() {
            @Override
            public void run() {
                GoogleMapFragment googleMapManager = (GoogleMapFragment) getSupportFragmentManager().findFragmentById(R.id.IG_layoutForMap);
                if (googleMapManager.checkDistanceWithSpotForGameActivation(spotSelected)) {
                    if (spotName.equals("La Vieille Bourse") || spotName.equals("Le Nouveau Siecle") || spotName.equals("Rue Grande Chaussee") || spotName.equals("La Place aux Oignons")) {
                        actualFragment = "QCM";
                        setQCMGame(spotName);
                    } else if (spotName.equals("La Colonne De La Deesse") || spotName.equals("Rue Nationale") || spotName.equals("La Voix Du Nord") ||  spotName.equals("L'Hospice Comtesse")) {
                        actualFragment = "ASSO";
                        setDnDAssociationGame(spotName);
                    } else if (spotName.equals("La Place Louise De Bettignies") || spotName.equals("Le Beffroi") || spotName.equals("Le Furet Du Nord") || spotName.equals("Le Palais Rihour") || spotName.equals("Rue Esquermoise")) {
                        actualFragment = "VF";
                        setVraiFauxGame(spotName);
                    } else if (spotName.equals("L'Opera De Lille") || spotName.equals("La Grand'Garde") || spotName.equals("Notre Dame De La Treille")) {
                        actualFragment = "PUZZLE";
                        setDnDPuzzleGame(spotName);
                    } else if (spotName.equals("L'Ilot Comtesse") || spotName.equals("La Statue du Ptit Quinquin") || spotName.equals("Le Rang Du Beauregard")) {
                        actualFragment = "CAMERA";
                        setNativePhotoFragment();
                    } else {
                        actualFragment = "NOTHING";
                        setMessage("Pas encore de jeu");
                    }
                } else {
                    actualFragment = "INFOS";
                   setMessage("Vous devez vous rapprocher du lieux pour pouvoir accéder au jeux.");
                }
            }
        }).start();
    }

    /**
     * Cette méthode place le jeu d'association d'image dans le fragment
     * @param spotName
     */
    private void setDnDAssociationGame(final String spotName){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    FragmentTransaction transactionManager = getSupportFragmentManager().beginTransaction();
                    transactionManager.replace(R.id.IG_fragmentLayout, DragNDropAssociationFragmentClass.newInstance(spotName))
                            .addToBackStack(null)
                            .commit();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                setUpWhiteScreen(true);
            }
        });
    }

    /**
     * Cette méthode place le jeu du puzzle dans le fragment
     * @param spotName
     */
    private void setDnDPuzzleGame(final String spotName){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    FragmentTransaction transactionManager = getSupportFragmentManager().beginTransaction();
                    transactionManager.replace(R.id.IG_fragmentLayout, DragNDropPuzzleFragmentClass.newInstance(spotName))
                            .addToBackStack(null)
                            .commit();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                setUpWhiteScreen(true);
            }
        });
    }

    /**
     * Cette méthode place le jeu Vrai/Faux dans le fragment
     * @param spotName
     */
    private void setVraiFauxGame(String spotName){
        try {
            FragmentTransaction transactionManager = getSupportFragmentManager().beginTransaction();
            transactionManager.replace(R.id.IG_fragmentLayout, VraiFauxFragmentClass.newInstance(spotName))
                    .addToBackStack(null)
                    .commit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Cette méthode place le jeu QCM dans le fragment
     * @param spotName
     */
    private void setQCMGame(String spotName){
        try {
            FragmentTransaction transactionManager = getSupportFragmentManager().beginTransaction();
            transactionManager.replace(R.id.IG_fragmentLayout, QCMFragmentClass.newInstance(spotName))
                    .addToBackStack(null)
                    .commit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Cette méthode place le jeu photo dans le fragment
     */
    private void setNativePhotoFragment(){
        try {
            FragmentTransaction transactionManager = getSupportFragmentManager().beginTransaction();
            transactionManager.replace(R.id.IG_fragmentLayout, NativeCameraFragment.newInstance(1))
                    .addToBackStack(null)
                    .commit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Cette méthode place le jeu Difference dans le fragment
     */
    private void setDifferenceFragment(String spotName){
        try {
            FragmentTransaction transactionManager = getSupportFragmentManager().beginTransaction();
            transactionManager.replace(R.id.IG_fragmentLayout, DifferenceFragmentClass.newInstance(spotName))
                    .addToBackStack(null)
                    .commit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Cette méthode place le fragment Information avec un message passé en paramètre
     * @param message
     */
    private void setMessage(String message){
        String informationsList[] = {spotSelected.getmSpotName(), message};
        // Placement du fragment Information dans l'écran central
        try {
            FragmentTransaction transactionManager = getSupportFragmentManager().beginTransaction();
            transactionManager.replace(R.id.IG_fragmentLayout, InformationFragmentClass.newInstance(informationsList))
                    .addToBackStack(null)
                    .commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // --------------------------------------------------------------

    /**
     * Cette méthode renvoye la liste des joueurs de l'équipe
     * @return
     */
    public List<String> getPlayerName(){
        return playerList;
    }


    /**
     * Cette méthode renvoye le nom du jeu
     * @return
     */
    public String getGameTitle(){
        String gameTitle = spotSelected.getGameTitle().toUpperCase();
        return gameTitle;
    }

    // --------------------------------------------------------------
    // --------------- Méthode d'acces au données de la BDD --------------------------


    /**
     * Cette méthode va récupérer le spot actuel dans la BDD
     * Et va passer la variable isInfosChecked a TRUE
     */
    public void setInfoCheckedInBDD(){

        final String spotName = spotSelected.getmSpotName();
        realm =  Realm.getDefaultInstance();
        try{
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    SpotClass mySpot = realm.where(SpotClass.class).equalTo("mSpotName", spotName).findFirst();
                    if(mySpot != null){mySpot.setInfoChecked();}
                }
            });
        } finally {
            realm.close();
        }



    }

    /**
     * Cette méthode va récupérer le spot actuel dans la BDD
     * Et va passer la variable isImageChecked a TRUE
     */
    public void setPhotoCheckedInBDD(){

        final String spotName = spotSelected.getmSpotName();
        realm =  Realm.getDefaultInstance();
        try{
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    SpotClass mySpot = realm.where(SpotClass.class).equalTo("mSpotName", spotName).findFirst();
                    if(mySpot != null){mySpot.setImageChecked();}
                }
            });
        } finally {
            realm.close();
        }


    }

    /**
     * Cette méthode va récupérer le spot actuel dans la BDD
     * Et va passer la variable isGameChecked a TRUE
     */
    public void setGameCheckedInBDD(){

        final String spotName = spotSelected.getmSpotName();
        realm =  Realm.getDefaultInstance();
        try{
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    SpotClass mySpot = realm.where(SpotClass.class).equalTo("mSpotName", spotName).findFirst();
                    if(mySpot != null){mySpot.setGameChecked();}
                }
            });
        } finally {
            realm.close();
        }


    }

    /**
     * Cette méthode va récupérer le spot actuel dans la BDD
     * Et va passer la variable isSpotCompleted a TRUE
     */
    public void setSpotCompletedInBDD(){

        final String spotName = spotSelected.getmSpotName();
        realm =  Realm.getDefaultInstance();
        try{
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    SpotClass mySpot = realm.where(SpotClass.class).equalTo("mSpotName", spotName).findFirst();
                    if(mySpot != null){ mySpot.setSpotCompleted(); }

                }
            });
        } finally {
            realm.close();
        }

    }

    /**
     * Cette méthode va récupérer la zone actuelle dans la BDD
     * Et va passer la variable isZoneCompleted a TRUE
     */
    public void setZoneCompletedInBDD() {


        new Thread(new Runnable() {
            @Override
            public void run() {

                final String zoneName = zoneSelected.getmZoneName();
                realm = Realm.getDefaultInstance();
                try {
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            ZoneClass myZone = realm.where(ZoneClass.class).equalTo("mZoneName", zoneName).findFirst();
                            if (myZone != null) {
                                myZone.setmZoneCompleted();
                            }
                        }
                    });
                } finally {
                    realm.close();
                }

            }
        }).start();




    }

    /**
     * Cette méthode va récupérer dans la BDD la zone associé a notre spot
     * Puis va créer notre objet zoneSelected via les informations de l'objet récupérer
     */
    public void getZoneSelectedFromBDD(){
        final String zoneName = spotSelected.getmZoneName();

        realm = Realm.getDefaultInstance();
        try {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    ZoneClass myZone = realm.where(ZoneClass.class).equalTo("mZoneName", zoneName).findFirst();
                    if (myZone != null) {
                        zoneSelected = new ZoneClass(myZone.getmZoneId(), myZone.getmZoneName(), myZone.getmZoneNbSpots(), myZone.getmZonePoint(), myZone.ismZoneCompleted());
                    }
                }
            });
        } finally {
            realm.close();
        }

    }


    // ----------------------------------------------------------
    // ------------------ Modification des Stats -----------------------

    public void incrementStats_PointPrincipaux(){

                realm =  Realm.getDefaultInstance();
                try{
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            TeamClass team = realm.where(TeamClass.class).findFirst();
                            if (team != null){ team.setStats_NbSpot1(team.getStats_NbSpot1()+1); }
                        }
                    });
                } finally {
                    realm.close();
                }
            }

    public void incrementStats_PointSecondaire(){

                realm =  Realm.getDefaultInstance();
                try{
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            TeamClass team = realm.where(TeamClass.class).findFirst();
                            if (team != null){ team.setStats_NbSpot2(team.getStats_NbSpot2()+1); }
                        }
                    });
                } finally {
                    realm.close();
                }
            }

    public void incrementStats_NbZones(){

                realm =  Realm.getDefaultInstance();
                try{
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            TeamClass team = realm.where(TeamClass.class).findFirst();
                            if (team != null) { team.setStats_NbZones(team.getStats_NbZones() + 1); }
                        }
                    });
                } finally {
                    realm.close();
                }

    }

    public void incrementStats_NbVictoire(){

                realm =  Realm.getDefaultInstance();
                try{
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            TeamClass team = realm.where(TeamClass.class).findFirst();
                            if (team != null){team.setStats_NbVictoire(team.getStats_NbVictoire()+1); }
                        }
                    });
                } finally {
                    realm.close();
                }
    }

    public void addNumberToList_listWord(final int nb){

        realm =  Realm.getDefaultInstance();
        try{
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    TeamClass team = realm.where(TeamClass.class).findFirst();
                    if (team != null){ team.getList_DiscoverWord().add(nb); }
                    Toasty.success(getApplicationContext(), "Félicitations, vous venez de débloquer un mot !",Toast.LENGTH_SHORT, true).show();
                }
            });
        } finally {
            realm.close();
        }

    }

    public void addNumberToList_UnlistWord(final int nb){

        realm =  Realm.getDefaultInstance();
        try{
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    TeamClass team = realm.where(TeamClass.class).findFirst();
                    if (team != null){ team.getList_UnDiscoverWord().add(nb); }
                }
            });
        } finally {
            realm.close();
        }

    }


    // ---------------------------------------------------------------------------------
    // ------------------------ Event Listener ----------------------------------------

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
                    RelativeLayout container = (RelativeLayout) v;
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

}

