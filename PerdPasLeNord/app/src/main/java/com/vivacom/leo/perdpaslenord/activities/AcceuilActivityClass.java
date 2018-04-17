package com.vivacom.leo.perdpaslenord.activities;

import android.app.Activity;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vivacom.leo.perdpaslenord.constant.ConstantGuideMessage;
import com.vivacom.leo.perdpaslenord.constant.ConstantInfos;
import com.vivacom.leo.perdpaslenord.R;
import com.vivacom.leo.perdpaslenord.ViewAnimations;
import com.vivacom.leo.perdpaslenord.objects.MarkerOptionRealm;
import com.vivacom.leo.perdpaslenord.objects.SpotClass;
import com.vivacom.leo.perdpaslenord.objects.TeamClass;
import com.vivacom.leo.perdpaslenord.objects.ZoneClass;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

import static android.view.View.GONE;

/**
 * Created by Leo on 07/09/2017.
 * Classe permettant de gérer le comportement de l'activity acceuil
 */

public class AcceuilActivityClass extends Activity {

    // ------- Elements graphiques -------
    RelativeLayout lMainScreen, lMJ_Introduction, lMJ_textBox, lTeam_Creation, lBlackScreen;
    TextView tConsigne, tComposition, tTeamCreation_Title;
    EditText eTeamName, eMemberName;

    Button btn_addMember, btn_CreateTeam, btn_insigne1,btn_insigne2,btn_insigne3,btn_insigne4,btn_insigne5;
    Drawable image;

    // ------- Nos listes -------
    List<Button> btn_Insigne_List = new ArrayList<>();
    RealmList<String> memberList = new RealmList<>();
    String listMJMessage[] = ConstantGuideMessage.acceuilMessageList;

    // ------- Nos objets -------
    ViewAnimations animator = new ViewAnimations();
    Realm realm;
    TeamClass theTeam;


    // ------- Nos Paramètres -------
    int numText = 1;
    int nbMember = 0;
    int numInsigneSelected = 0;

    boolean insigneSelected = false;
    boolean canChange = true;

    String membresList = "";

    private static final String TAG = "Acceuil";

    // ----------------------------------------------------

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

    @Override
    public void onStop(){
        super.onStop();
        Log.d(TAG, "Activity onStop");
    }


    // ----------------------------------------------------


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acceuil_screen);
        Log.d(TAG, "----------- Activity Start");

        // ------- On init Realm -------
        initRealmBDD();

        // ------
        associateElement();

        // ------
        lMJ_Introduction.setVisibility(GONE);
        lMJ_textBox.setVisibility(GONE);

        // ------
        lTeam_Creation.setVisibility(GONE);
        lTeam_Creation.animate().translationX(1500).withLayer();
        lTeam_Creation.setClickable(false);
        // ------
        lMainScreen.setBackground(getResources().getDrawable(R.drawable.logo_flamantrosefull));
        startAnimation();
        // -----
        Bitmap myImage = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.acceuil_ecran_ppln);
        Bitmap myBtm = Bitmap.createScaledBitmap(myImage,(int)(myImage.getWidth()*0.5), (int)(myImage.getHeight()*0.5), true);
        image = new BitmapDrawable(getResources(), myBtm);

        // ------- On vérifie si on possède des donnée -------
        checkTeamInBDD();


        eMemberName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(!eMemberName.getText().toString().equals("")) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        btn_addMember.performClick();
                        return true;
                    }
                }
                return false;
            }
        });
                                              }

    // ------- Méthode de setUp -------
    /**
     * Cette méthode va associer les éléments graphiques avec leurs composants
     * Et ajoute les btn_insigne à la liste
     */
    private void associateElement(){
        lMainScreen =  findViewById(R.id.acceuil_mainScreen);
        lMJ_Introduction = findViewById(R.id.MJ_Introduction);
        lMJ_textBox = findViewById(R.id.MJ_textBox);
        tConsigne = findViewById(R.id.MJ_consigne);
        lTeam_Creation = findViewById(R.id.Team_Creation);
        eTeamName = findViewById(R.id.edit_TeamName);
        eMemberName = findViewById(R.id.edit_MemberName);
        btn_addMember = findViewById(R.id.btn_addMember);
        btn_CreateTeam = findViewById(R.id.btn_createTeam);
        btn_insigne1 = findViewById(R.id.btn_insigne1);
        btn_insigne2 = findViewById(R.id.btn_insigne2);
        btn_insigne3 = findViewById(R.id.btn_insigne3);
        btn_insigne4 = findViewById(R.id.btn_insigne4);
        btn_insigne5 = findViewById(R.id.btn_insigne5);

        tComposition = findViewById(R.id.txt_Composition);
        lBlackScreen = findViewById(R.id.blackScreen);

        btn_Insigne_List.add(btn_insigne1);
        btn_Insigne_List.add(btn_insigne2);
        btn_Insigne_List.add(btn_insigne3);
        btn_Insigne_List.add(btn_insigne4);
        btn_Insigne_List.add(btn_insigne5);

        tTeamCreation_Title = findViewById(R.id.creationTeam_title);




    }

    /**
     * Cette méthode va init Realm et vérifier si des données existe
     */
    private void initRealmBDD(){
        Realm.init(this);

        //deleteAllDataFromRealm();

        new Thread(new Runnable() {
            @Override
            public void run() {

                // Get a Realm instance for this thread
                realm = Realm.getDefaultInstance();

                try {
                    // ------- On regarde si il existe déja des données dans la base --------
                    ZoneClass zoneTheatreTest = realm.where(ZoneClass.class).equalTo("mZoneId", 1).findFirst();
                    if (zoneTheatreTest == null){
                        Log.i(TAG, "Aucune donnée PLACE DU THEATRE, lancement de la création de la zone");
                        createZonePlaceTheatreInRealm();
                    } else {Log.i(TAG, "Donnée trouvé pour : PLACE DU THEATRE");}

                    ZoneClass zoneLilleCentreTest = realm.where(ZoneClass.class).equalTo("mZoneId", 2).findFirst();
                    if (zoneLilleCentreTest == null){
                        Log.i(TAG, "Aucune donnée LILLE CENTRE, lancement de la création de la zone");
                        createZoneLilleCentreInRealm();
                    } else {Log.i(TAG, "Donnée trouvé pour : LILLE CENTRE");}

                    ZoneClass zoneGrandPlaceTest = realm.where(ZoneClass.class).equalTo("mZoneId", 3).findFirst();
                    if (zoneGrandPlaceTest == null){
                        Log.i(TAG, "Aucune donnée GRAND PLACE, lancement de la création de la zone");
                        createZoneGrandPlaceInRealm();
                    } else {Log.i(TAG, "Donnée trouvé pour : GRAND PLACE");}

                    ZoneClass zoneVieuxLilleTest = realm.where(ZoneClass.class).equalTo("mZoneId", 4).findFirst();
                    if (zoneVieuxLilleTest == null){
                        Log.i(TAG, "Aucune donnée VIEUX LILLE, lancement de la création de la zone");
                        createZoneVieuxLilleInRealm();
                    } else {Log.i(TAG, "Donnée trouvé pour : VIEUX LILLE");}

                    SpotClass spotSecret = realm.where(SpotClass.class).equalTo("mZoneName", "Secret").findFirst();
                    if (spotSecret == null){
                        Log.i(TAG, "Aucune donnée Spot Secret, lancement de la création des Spot");
                        createSecretSpotInRealm();
                    } else {Log.i(TAG, "Donnée trouvé pour : SPOT SECRET");}

                } finally {realm.close();}

            }
        }).start();





    }

    /**
     * Cette méthode va créer nos différents object pour la zone PLACE DU THEATRE
     * Puis elle va les ajouter a notre BDD Realm
     */
    private void createZonePlaceTheatreInRealm() {

        final ZoneClass zonePlaceDuTheatre = new ZoneClass(1, "PLACE DU THEATRE", 4, 500);

        RealmList<Integer> photoGalleryLOpera = new RealmList<>();
        RealmList<Integer> photoGalleryLaVieilleBourse = new RealmList<>();
        RealmList<Integer> photoGalleryLeRangBeauxregard = new RealmList<>();
        RealmList<Integer> photoGalleryLeBeffroi = new RealmList<>();
        RealmList<Integer> photoGalleryChambreCommerce = new RealmList<>();

        photoGalleryLOpera.add(R.drawable.pht_opera1);
        photoGalleryLOpera.add(R.drawable.pht_opera2);
        photoGalleryLOpera.add(R.drawable.pht_opera3);
        photoGalleryLOpera.add(R.drawable.pht_opera4);
        photoGalleryLOpera.add(R.drawable.pht_opera5);
        photoGalleryLOpera.add(R.drawable.pht_opera6);

        photoGalleryLaVieilleBourse.add(R.drawable.pht_vieille1);
        photoGalleryLaVieilleBourse.add(R.drawable.pht_vieille2);
        photoGalleryLaVieilleBourse.add(R.drawable.pht_vieille3);
        photoGalleryLaVieilleBourse.add(R.drawable.pht_vieille4);
        photoGalleryLaVieilleBourse.add(R.drawable.pht_vieille5);
        photoGalleryLaVieilleBourse.add(R.drawable.pht_vieille6);
        photoGalleryLaVieilleBourse.add(R.drawable.pht_vieille7);

        photoGalleryLeRangBeauxregard.add(R.drawable.pht_rang1);
        photoGalleryLeRangBeauxregard.add(R.drawable.pht_rang2);
        photoGalleryLeRangBeauxregard.add(R.drawable.pht_rang3);
        photoGalleryLeRangBeauxregard.add(R.drawable.pht_rang4);
        photoGalleryLeRangBeauxregard.add(R.drawable.pht_rang5);
        photoGalleryLeRangBeauxregard.add(R.drawable.pht_rang6);

        photoGalleryLeBeffroi.add(R.drawable.pht_beffroi1);
        photoGalleryLeBeffroi.add(R.drawable.pht_beffroi2);
        photoGalleryLeBeffroi.add(R.drawable.pht_beffroi3);
        photoGalleryLeBeffroi.add(R.drawable.pht_beffroi4);
        photoGalleryLeBeffroi.add(R.drawable.pht_beffroi5);

        photoGalleryChambreCommerce.add(R.drawable.pht_chambre1);
        photoGalleryChambreCommerce.add(R.drawable.pht_chambre2);
        photoGalleryChambreCommerce.add(R.drawable.pht_chambre3);

        // ----

        final SpotClass spotLaVieilleBourse = new SpotClass(1,0, zonePlaceDuTheatre.getmZoneName(), "La Vieille Bourse", "Calcul Mentaux", 3, ConstantInfos.INFO_LAVIEILLEBOURSE1, ConstantInfos.INFO_LAVIEILLEBOURSE2, ConstantInfos.INFO_LAVIEILLEBOURSE3, photoGalleryLaVieilleBourse);
        final SpotClass spotLeRangDuBeauregard = new SpotClass(1,15, zonePlaceDuTheatre.getmZoneName(), "Le Rang Du Beauregard","Photo !!", 3, ConstantInfos.INFO_LERANGBEAUREGARD1, ConstantInfos.INFO_LERANGBEAUREGARD2, ConstantInfos.INFO_LERANGBEAUREGARD3, photoGalleryLeRangBeauxregard);
        final SpotClass spotLeBeffroi = new SpotClass(1,7, zonePlaceDuTheatre.getmZoneName(), "Le Beffroi","Le Beffroi", 3,  ConstantInfos.INFO_LEBEFFROI1, ConstantInfos.INFO_LEBEFFROI2, ConstantInfos.INFO_LEBEFFROI3, photoGalleryLeBeffroi);
        final SpotClass spotLOpera = new SpotClass(1,9, zonePlaceDuTheatre.getmZoneName(), "L'Opera De Lille" ,"Pièce de Théatre", 3, ConstantInfos.INFO_OPERA1, ConstantInfos.INFO_OPERA2, ConstantInfos.INFO_OPERA3, photoGalleryLOpera);

        final SpotClass spotChambreCommerce = new SpotClass(2, 21, zonePlaceDuTheatre.getmZoneName(), "La Chambre des Commerces" , "PAS DE JEU", 1, ConstantInfos.CULTURE_CHAMBRECOMMERCE, null, null, photoGalleryChambreCommerce);

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                // This will create a new object in Realm or throw an exception if the
                // object already exists (same primary key)
                realm.copyToRealm(zonePlaceDuTheatre);

                realm.copyToRealm(spotLaVieilleBourse);
                realm.copyToRealm(spotLeRangDuBeauregard);
                realm.copyToRealm(spotLeBeffroi);
                realm.copyToRealm(spotLOpera);
                realm.copyToRealm(spotChambreCommerce);

                Log.i(TAG, "Création des données ZONE DU THEATRE terminé");

            }
        });

    }

    /**
     * Cette méthode va créer nos différents object pour la zone LILLE CENTRE
     * Puis elle va les ajouter a notre BDD Realm
     */
    private void createZoneLilleCentreInRealm() {

        final ZoneClass zoneLilleCentre = new ZoneClass(2,"LILLE CENTRE",  3, 300 );

        RealmList<Integer> photoGaleryNouveauSiecle = new RealmList<>();
        RealmList<Integer> photoGaleryStatueQuinquin = new RealmList<>();
        RealmList<Integer> photoGaleryRueNationale = new RealmList<>();
        RealmList<Integer> photoGaleryStatueFoch = new RealmList<>();
        RealmList<Integer> photoGaleryLe28 = new RealmList<>();
        RealmList<Integer> photoGaleryQuaiDuWault = new RealmList<>();

        photoGaleryNouveauSiecle.add(R.drawable.pht_nouveausiecle1);
        photoGaleryNouveauSiecle.add(R.drawable.pht_nouveausiecle2);
        photoGaleryNouveauSiecle.add(R.drawable.pht_nouveausiecle3);

        photoGaleryStatueQuinquin.add(R.drawable.pht_quinquin1);
        photoGaleryStatueQuinquin.add(R.drawable.pht_quinquin2);
        photoGaleryStatueQuinquin.add(R.drawable.pht_quinquin3);
        photoGaleryStatueQuinquin.add(R.drawable.pht_quinquin4);

        photoGaleryRueNationale.add(R.drawable.pht_ruenatio1);
        photoGaleryRueNationale.add(R.drawable.pht_ruenatio2);

        photoGaleryStatueFoch.add(R.drawable.pht_foch1);
        photoGaleryStatueFoch.add(R.drawable.pht_foch2);
        photoGaleryStatueFoch.add(R.drawable.pht_foch3);

        photoGaleryLe28.add(R.drawable.pht_281);
        photoGaleryLe28.add(R.drawable.pht_282);
        photoGaleryLe28.add(R.drawable.pht_283);
        photoGaleryLe28.add(R.drawable.pht_284);
        photoGaleryLe28.add(R.drawable.pht_285);

        photoGaleryQuaiDuWault.add(R.drawable.pht_quai1);
        photoGaleryQuaiDuWault.add(R.drawable.pht_quai2);
        photoGaleryQuaiDuWault.add(R.drawable.pht_quai3);
        photoGaleryQuaiDuWault.add(R.drawable.pht_quai4);

        // -----

        final SpotClass spotRueNationale = new SpotClass(1, 1, zoneLilleCentre.getmZoneName(), "Rue Nationale","Fête du Nord", 1,  ConstantInfos.INFO_RUENATIONALE, null, null, photoGaleryRueNationale);
        final SpotClass spotStatueQuinQuin = new SpotClass(1, 6, zoneLilleCentre.getmZoneName(), "La Statue du Ptit Quinquin","Ptit Quinquin", 3,  ConstantInfos.INFO_LASTATUEDUPTITQUINQUIN1, ConstantInfos.INFO_LASTATUEDUPTITQUINQUIN2, ConstantInfos.INFO_LASTATUEDUPTITQUINQUIN3 ,photoGaleryStatueQuinquin);
        final SpotClass spotNouveauSiecle = new SpotClass(1, 18, zoneLilleCentre.getmZoneName(), "Le Nouveau Siecle", "Question d'Observation", 3, ConstantInfos.INFO_LENOUVEAUSIECLE1, ConstantInfos.INFO_LENOUVEAUSIECLE2, ConstantInfos.INFO_LENOUVEAUSIECLE3, photoGaleryNouveauSiecle);

        final SpotClass spotStatueFoch = new SpotClass(2, 22,  zoneLilleCentre.getmZoneName(), "La Statue du General Foch","PAS DE JEU", 1, ConstantInfos.CULTURE_STATUEFOCH, null, null, photoGaleryStatueFoch );
        final SpotClass spotLe28 = new SpotClass(2, 23,  zoneLilleCentre.getmZoneName(), "Le 28 Thiers", "PAS DE JEU", 1, ConstantInfos.CULTURE_LE28, null, null, photoGaleryLe28 );
        final SpotClass spotQuaiDuWault = new SpotClass(2, 24, zoneLilleCentre.getmZoneName(), "Quai Du Wault", "PAS DE JEU", 1,  ConstantInfos.CULTURE_QUAIDUWAULT, null, null, photoGaleryQuaiDuWault );


        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                // This will create a new object in Realm or throw an exception if the
                // object already exists (same primary key)
                realm.copyToRealm(zoneLilleCentre);

                realm.copyToRealm(spotRueNationale);
                realm.copyToRealm(spotStatueQuinQuin);
                realm.copyToRealm(spotNouveauSiecle);
                realm.copyToRealm(spotStatueFoch);
                realm.copyToRealm(spotLe28);
                realm.copyToRealm(spotQuaiDuWault);


                Log.i(TAG, "Création des données ZONE LILLE CENTRE terminé");

            }
        });


    }

    /**
     * Cette méthode va créer nos différents object pour la zone LILLE CENTRE
     * Puis elle va les ajouter a notre BDD Realm
     */
    private void createZoneGrandPlaceInRealm() {

        final ZoneClass zoneGrandPlace = new ZoneClass(3,"GRAND PLACE", 4, 500 );

        RealmList<Integer> photoGaleryFuretDuNord = new RealmList<>();
        RealmList<Integer> photoGaleryGrandGarde = new RealmList<>();
        RealmList<Integer> photoGaleryVoixDuNord = new RealmList<>();
        RealmList<Integer> photoGaleryColonneDeLaDeesse = new RealmList<>();
        RealmList<Integer> photoGaleryPalaisRihour = new RealmList<>();
        RealmList<Integer> photoGaleryPyramide = new RealmList<>();

        photoGaleryFuretDuNord.add(R.drawable.pht_furet1);
        //photoGaleryFuretDuNord.add(R.drawable.pht_furet2); moche
        photoGaleryFuretDuNord.add(R.drawable.pht_furet3);
        photoGaleryFuretDuNord.add(R.drawable.pht_furet4);
        photoGaleryFuretDuNord.add(R.drawable.pht_furet5);

        photoGaleryGrandGarde.add(R.drawable.pht_grandgrade1);
        photoGaleryGrandGarde.add(R.drawable.pht_grandgrade2);
        photoGaleryGrandGarde.add(R.drawable.pht_grandgrade3);
        photoGaleryGrandGarde.add(R.drawable.pht_grandgrade4);
        photoGaleryGrandGarde.add(R.drawable.pht_grandgrade5);

        photoGaleryVoixDuNord.add(R.drawable.pht_voixdunord1);
        photoGaleryVoixDuNord.add(R.drawable.pht_voixdunord2);
        photoGaleryVoixDuNord.add(R.drawable.pht_voixdunord3);

        photoGaleryColonneDeLaDeesse.add(R.drawable.pht_colonne1);
        photoGaleryColonneDeLaDeesse.add(R.drawable.pht_colonne2);
        photoGaleryColonneDeLaDeesse.add(R.drawable.pht_colonne3);
        photoGaleryColonneDeLaDeesse.add(R.drawable.pht_colonne4);

        photoGaleryPalaisRihour.add(R.drawable.pht_palais1);
        photoGaleryPalaisRihour.add(R.drawable.pht_palais2);
        photoGaleryPalaisRihour.add(R.drawable.pht_palais3);
        photoGaleryPalaisRihour.add(R.drawable.pht_palais4);
        photoGaleryPalaisRihour.add(R.drawable.pht_palais5);

        photoGaleryPyramide.add(R.drawable.pht_pyramide1);
        photoGaleryPyramide.add(R.drawable.pht_pyramide2);

        // -----------

        final SpotClass spotLeFuretDuNord = new SpotClass(1, 2, zoneGrandPlace.getmZoneName(), "Le Furet Du Nord","Culture Générale", 3, ConstantInfos.INFO_FURETDUNORD1, ConstantInfos.INFO_FURETDUNORD2, ConstantInfos.INFO_FURETDUNORD3,  photoGaleryFuretDuNord);
        final SpotClass spotLaGrandGarde = new SpotClass(1, 17, zoneGrandPlace.getmZoneName(), "La Grand'Garde","Garde À Vous !!", 3, ConstantInfos.INFO_LAGRANDGARDE1, ConstantInfos.INFO_LAGRANDGARDE2, ConstantInfos.INFO_LAGRANDGARDE3, photoGaleryGrandGarde);
        final SpotClass spotLaVoixDuNord = new SpotClass(1, 5, zoneGrandPlace.getmZoneName(), "La Voix Du Nord","Le jeu des blasons", 3, ConstantInfos.INFO_LAVOIXDUNORD1, ConstantInfos.INFO_LAVOIXDUNORD2, ConstantInfos.INFO_LAVOIXDUNORD3,  photoGaleryVoixDuNord);
        final SpotClass spotLaColonneDeLaDeese = new SpotClass(1, 13, zoneGrandPlace.getmZoneName(), "La Colonne De La Deesse","Statue", 3,  ConstantInfos.INFO_LACOLONNEDELADEESSE1,  ConstantInfos.INFO_LACOLONNEDELADEESSE2,  ConstantInfos.INFO_LACOLONNEDELADEESSE3,  photoGaleryColonneDeLaDeesse);
        final SpotClass spotPalaisRihour = new SpotClass(1, 11, zoneGrandPlace.getmZoneName(), "Le Palais Rihour", "Vrai ou Feu ?", 3, ConstantInfos.INFO_PALAISRIHOUR1, ConstantInfos.INFO_PALAISRIHOUR2, ConstantInfos.INFO_PALAISRIHOUR3,  photoGaleryPalaisRihour);

        final SpotClass spotPyramideRihour = new SpotClass(2, 25,  zoneGrandPlace.getmZoneName(), "La Pyramide de Rihour","PAS DE JEU",  1, ConstantInfos.CULTURE_PYRAMIDE, null, null, photoGaleryPyramide);


        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                // This will create a new object in Realm or throw an exception if the
                // object already exists (same primary key)
                realm.copyToRealm(zoneGrandPlace);

                realm.copyToRealm(spotLeFuretDuNord);
                realm.copyToRealm(spotLaGrandGarde);
                realm.copyToRealm(spotLaVoixDuNord);
                realm.copyToRealm(spotLaColonneDeLaDeese);
                realm.copyToRealm(spotPalaisRihour);
                realm.copyToRealm(spotPyramideRihour);


                Log.i(TAG, "Création des données ZONE GRAND PLACE terminé");

            }
        });


    }

    /**
     * Cette méthode va créer nos différents object pour la zone VIEUX LILLE
     * Puis elle va les ajouter a notre BDD Realm
     */
    private void createZoneVieuxLilleInRealm() {

        final ZoneClass zoneVieuxLille = new ZoneClass(4, "VIEUX LILLE", 9, 1000 );

        RealmList<Integer> photoGaleryPlaceAuxOignons = new RealmList<>();
        RealmList<Integer> photoGaleryPlaceLouiseDeBettignies = new RealmList<>();
        RealmList<Integer> photoGaleryIlotComtesse = new RealmList<>();
        RealmList<Integer> photoGaleryHospiceComtesse = new RealmList<>();
        RealmList<Integer> photoGaleryRueEsquermoise = new RealmList<>();
        RealmList<Integer> photoGaleryRueGrandeChaussee = new RealmList<>();
        RealmList<Integer> photoGaleryNotreDameDeLaTreille = new RealmList<>();
        RealmList<Integer> photoGaleryHuitriere = new RealmList<>();
        RealmList<Integer> photoGaleryMeert = new RealmList<>();
        RealmList<Integer> photoGaleryCourInt = new RealmList<>();
        RealmList<Integer> photoGaleryRueWeppes = new RealmList<>();

        photoGaleryPlaceAuxOignons.add(R.drawable.pht_oignons1);
        photoGaleryPlaceAuxOignons.add(R.drawable.pht_oignons2);
        photoGaleryPlaceAuxOignons.add(R.drawable.pht_oignons3);
        photoGaleryPlaceAuxOignons.add(R.drawable.pht_oignons4);
        photoGaleryPlaceAuxOignons.add(R.drawable.pht_oignons5);

        photoGaleryPlaceLouiseDeBettignies.add(R.drawable.pht_louise1);
        photoGaleryPlaceLouiseDeBettignies.add(R.drawable.pht_louise2);
        photoGaleryPlaceLouiseDeBettignies.add(R.drawable.pht_louise3);

        photoGaleryIlotComtesse.add(R.drawable.pht_ilot1);
        photoGaleryIlotComtesse.add(R.drawable.pht_ilot2);
        //photoGaleryIlotComtesse.add(R.drawable.pht_ilot3); moche

        photoGaleryHospiceComtesse.add(R.drawable.pht_hospice1);
        photoGaleryHospiceComtesse.add(R.drawable.pht_hospice2);
        photoGaleryHospiceComtesse.add(R.drawable.pht_hospice3);
        photoGaleryHospiceComtesse.add(R.drawable.pht_hospice4);
        photoGaleryHospiceComtesse.add(R.drawable.pht_hospice5);
        photoGaleryHospiceComtesse.add(R.drawable.pht_hospice6);

        photoGaleryRueEsquermoise.add(R.drawable.pht_esquermoise1);
        photoGaleryRueEsquermoise.add(R.drawable.pht_esquermoise2);

        photoGaleryRueGrandeChaussee.add(R.drawable.pht_ruechaussee1);
        photoGaleryRueGrandeChaussee.add(R.drawable.pht_ruechaussee2);
        photoGaleryRueGrandeChaussee.add(R.drawable.pht_ruechaussee3);

        photoGaleryNotreDameDeLaTreille.add(R.drawable.pht_treille1);
        photoGaleryNotreDameDeLaTreille.add(R.drawable.pht_treille2);
        photoGaleryNotreDameDeLaTreille.add(R.drawable.pht_treille3);
        photoGaleryNotreDameDeLaTreille.add(R.drawable.pht_treille4);
        photoGaleryNotreDameDeLaTreille.add(R.drawable.pht_treille5);
        photoGaleryNotreDameDeLaTreille.add(R.drawable.pht_treille6);

        photoGaleryHuitriere.add(R.drawable.pht_huitriere1);
        photoGaleryHuitriere.add(R.drawable.pht_huitriere2);
        photoGaleryHuitriere.add(R.drawable.pht_huitriere3);
        photoGaleryHuitriere.add(R.drawable.pht_huitriere4);
        photoGaleryHuitriere.add(R.drawable.pht_huitriere5);
        photoGaleryHuitriere.add(R.drawable.pht_huitriere6);

        photoGaleryMeert.add(R.drawable.pht_meert1);
        photoGaleryMeert.add(R.drawable.pht_meert2);
        photoGaleryMeert.add(R.drawable.pht_meert3);
        photoGaleryMeert.add(R.drawable.pht_meert4);
        photoGaleryMeert.add(R.drawable.pht_meert5);

        photoGaleryCourInt.add(R.drawable.pht_grappe1);
        photoGaleryCourInt.add(R.drawable.pht_grappe2);

        photoGaleryRueWeppes.add(R.drawable.pht_weppes1);
        photoGaleryRueWeppes.add(R.drawable.pht_weppes2);

        // -------

        final SpotClass spotPlaceAuxOignons = new SpotClass(1, 3, zoneVieuxLille.getmZoneName(), "La Place aux Oignons", "Questions aux petits Oignons",3, ConstantInfos.INFO_LAPLACEAUXOIGNONS1,ConstantInfos.INFO_LAPLACEAUXOIGNONS2, ConstantInfos.INFO_LAPLACEAUXOIGNONS3 ,  photoGaleryPlaceAuxOignons);
        final SpotClass spotPlaceLouiseDeBettignies = new SpotClass(1,4, zoneVieuxLille.getmZoneName(), "La Place Louise De Bettignies","INFO ou INTOX ?", 1, ConstantInfos.INFO_PLACELOUISEDEBETTIGNIES, null, null , photoGaleryPlaceLouiseDeBettignies);
        final SpotClass spotIlotComtesse = new SpotClass(1, 8, zoneVieuxLille.getmZoneName(), "L'Ilot Comtesse","Tape la pose !!", 1, ConstantInfos.INFO_LILOTCOMTESSE, null, null, photoGaleryIlotComtesse);
        final SpotClass spotHospiceComtesse = new SpotClass(1, 10, zoneVieuxLille.getmZoneName(), "L'Hospice Comtesse","La Bière du Ch'Nord", 3,  ConstantInfos.INFO_LHOSPICECOMTESSE1,  ConstantInfos.INFO_LHOSPICECOMTESSE2,  ConstantInfos.INFO_LHOSPICECOMTESSE3,  photoGaleryHospiceComtesse);
        final SpotClass spotRueEsquermoise = new SpotClass(1, 12, zoneVieuxLille.getmZoneName(), "Rue Esquermoise","Ki C ?", 1, ConstantInfos.INFO_LARUEESQUERMOISE, null, null,  photoGaleryRueEsquermoise);
        final SpotClass spotRueGrandeChaussee = new SpotClass(1,  14, zoneVieuxLille.getmZoneName(), "Rue Grande Chaussee","Lille Mystère",3, ConstantInfos.INFO_LARUEGRANDECHAUSSEE1, ConstantInfos.INFO_LARUEGRANDECHAUSSEE2, ConstantInfos.INFO_LARUEGRANDECHAUSSEE3,  photoGaleryRueGrandeChaussee);
        final SpotClass spotNotreDameDeLaTreille = new SpotClass(1, 16, zoneVieuxLille.getmZoneName(), "Notre Dame De La Treille","Ca.Thé.Dra.Le", 3, ConstantInfos.INFO_NOTREDAMEDELATREILLE1, ConstantInfos.INFO_NOTREDAMEDELATREILLE2, ConstantInfos.INFO_NOTREDAMEDELATREILLE3,  photoGaleryNotreDameDeLaTreille);

        final SpotClass spotHuitriere = new SpotClass(2, 26, zoneVieuxLille.getmZoneName(), "(Anciennement) L'Huitriere","PAS DE JEU", 1,  ConstantInfos.CULTURE_HUITRIERE, null, null,   photoGaleryHuitriere);
        final SpotClass spotMeert = new SpotClass(2, 27,zoneVieuxLille.getmZoneName(), "Meert","PAS DE JEU", 1,  ConstantInfos.CULTURE_MEERT, null,null,  photoGaleryMeert);
        final SpotClass spotCourInt = new SpotClass(2,28, zoneVieuxLille.getmZoneName(), "Les Compagnons de la Grappe","PAS DE JEU", 1, ConstantInfos.CULTURE_COURINT, null, null,  photoGaleryCourInt);
        final SpotClass spotRueWeppes = new SpotClass(2, 29,  zoneVieuxLille.getmZoneName(), "La rue Weppes","PAS DE JEU", 1, ConstantInfos.CULTURE_RUEWEPPES, null,  null ,photoGaleryRueWeppes);

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                // This will create a new object in Realm or throw an exception if the
                // object already exists (same primary key)
                realm.copyToRealm(zoneVieuxLille);

                realm.copyToRealm(spotPlaceAuxOignons);
                realm.copyToRealm(spotPlaceLouiseDeBettignies);
                realm.copyToRealm(spotIlotComtesse);
                realm.copyToRealm(spotHospiceComtesse);
                realm.copyToRealm(spotRueEsquermoise);
                realm.copyToRealm(spotRueGrandeChaussee);
                realm.copyToRealm(spotNotreDameDeLaTreille);

                realm.copyToRealm(spotHuitriere);
                realm.copyToRealm(spotMeert);
                realm.copyToRealm(spotCourInt);
                realm.copyToRealm(spotRueWeppes);


                Log.i(TAG, "Création des données VIEUX LILLE terminé");

            }
        });

    }

    /**
     * Cette méthode va créer dans la BDD les Spot Secret
     */
    private void createSecretSpotInRealm(){
        final SpotClass spotSecret_Bras = new SpotClass(3, 41, "La Bras d'Or", 3,  ConstantInfos.SECRET_GOLDENARM1, ConstantInfos.SECRET_GOLDENARM2, ConstantInfos.SECRET_GOLDENARM3);
        final SpotClass spotSecret_Macon = new SpotClass(3,43,  "Le Temple Maçonnique", 3, ConstantInfos.SECRET_FRANCMACON1, ConstantInfos.SECRET_FRANCMACON2, ConstantInfos.SECRET_FRANCMACON3);
        final SpotClass spotSecter_Treille = new SpotClass(3, 44, "Le dos de la Cathédrale", 3, ConstantInfos.SECRET_BEHINDTREILLE1, ConstantInfos.SECRET_BEHINDTREILLE2,  ConstantInfos.SECRET_BEHINDTREILLE3);
        final SpotClass spotSecret_Slave = new SpotClass(3, 44, "Le Compostelle", 2, ConstantInfos.SECRET_COMPOSTELLE1, ConstantInfos.SECRET_COMPOSTELLE2,  null);

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                realm.copyToRealm(spotSecret_Bras);
                realm.copyToRealm(spotSecret_Macon);
                realm.copyToRealm(spotSecter_Treille);
                realm.copyToRealm(spotSecret_Slave);

                Log.i(TAG, "Création des données Spot Secret terminé");
            }
        });
    }

    /**
     * Cette méthode va supprimer tout les objets présent dans la BDD Realm
     */
    private void deleteAllDataFromRealm(){
        realm = Realm.getDefaultInstance();

        try{

            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmResults<TeamClass> rows = realm.where(TeamClass.class).findAll();
                    rows.deleteAllFromRealm();
                    Log.i(TAG, "Tous les objets TEAMCLASS supprimés ");
                }
            });

            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmResults<SpotClass> rows = realm.where(SpotClass.class).findAll();
                    rows.deleteAllFromRealm();
                    Log.i(TAG, "Tous les objets SPOTCLASS supprimés ");
                }
            });

            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmResults<MarkerOptionRealm> rows = realm.where(MarkerOptionRealm.class).findAll();
                    rows.deleteAllFromRealm();
                    Log.i(TAG, "Tous les objets MARKEROPTIONREALM supprimés ");
                }
            });

            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmResults<ZoneClass> rows = realm.where(ZoneClass.class).findAll();
                    rows.deleteAllFromRealm();
                    Log.i(TAG, "Tous les objets ZONECLASS supprimés ");
                }
            });


        } finally {
            realm.close();
        }




    }

    // ------- Gestion des animations -------
    /**
     * Cette méthode s'occupe de l'animation au lancement de l'application
     */
    private void startAnimation(){
        // On commence par faire apparaitre l'écran noir
        Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                animator.fadeInAnimation(lBlackScreen);
            }
        }, 2500);

        // Puis on le fait disparaitre et on change l'image en background
        Handler handler2 = new Handler();
        handler2.postDelayed(new Runnable() {
            @Override
            public void run() {
                lMainScreen.setBackground(image);
                animator.fadeOutAnimation(lBlackScreen);
            }
        }, 3500);


        // Puis on fait apparaitre le MJ
        Handler handler5 = new Handler();
        handler5.postDelayed(new Runnable() {
            @Override
            public void run() {
                startMJExplication();
            }
        }, 8000);

    }

    /**
     * Cette méthode va faire apparaitre le MJ et la consigne
     */
    private void startMJExplication(){
        // On fait apparaitre la bande noire et le MJ
        animator.fadeInAnimation(lMJ_Introduction);
        lMJ_textBox.setVisibility(View.INVISIBLE);

        // On setUp le text
        numText = 0;
        tConsigne.setText(listMJMessage[numText]);

        // On créer notre onClickListener
        lMJ_Introduction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (canChange){
                    changeConsigneText();
                    canChange = false;
                }

            }
        });

        // On fait apparaitre la consigne
        Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                animator.fadeInAnimation(lMJ_textBox);
            }
        }, 1000);
    }

    /**
     * Cette méthode va faire ré-apparaitre le MJ et la consigne
     */
    private void restartMJExplication(){
        // On fait apparaitre la bande noire et le MJ
        animator.fadeInAnimation(lMJ_Introduction);
        lMJ_textBox.setVisibility(View.INVISIBLE);
        canChange = true;

        // On setUp le text
        numText = 0;
        final String endMessage[] = {" Bravo !! \n Maintenant, vous êtes officielement : \n \n "+ theTeam.getmTeamName() + "","Vous allez bientôt pouvoir vous lancer dans l'aventure !! \n \n Juste avant ca, je vais vous expliquer les règles de ce jeu.", "Je vous conseille fortement de les lire, car sinon vous risquez d'être perdu une fois le jeu commencé. " };
        tConsigne.setText(endMessage[numText]);

        // On créer notre onClickListener
        lMJ_Introduction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (canChange){
                    canChange = false;
                    numText++;
                    if (numText == endMessage.length) {
                        goToRulesExplicationActivity();
                    } else {
                        animator.fadeOutAnimation(tConsigne);

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                tConsigne.setText(endMessage[numText]);
                                animator.fadeInAnimation(tConsigne);

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

            }
        });

        // On fait apparaitre la consigne
        Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                animator.fadeInAnimation(lMJ_textBox);
            }
        }, 1000);
    }

    /**
     * Cette méthode change le texte de la consigne
     */
    private void changeConsigneText(){
        numText++;
        if (numText == listMJMessage.length) {
            setUp_TeamCreation();
        } else {
            animator.fadeOutAnimation(tConsigne);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    tConsigne.setText(listMJMessage[numText]);
                    animator.fadeInAnimation(tConsigne);

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

    // ------- Gestion de la Création de l'équipe -------


    /**
     * Cette méthode modifie la couleur des boutons d'insigne
     */
    private void setClassicBtnBackground(Button btn, int btnTag){
        switch (btnTag){
            case 1:
                btn.setBackgroundResource(R.drawable.btn_equipe_biere_off);
                break;
            case 2:
                btn.setBackgroundResource(R.drawable.btn_equipe_frite_off);
                break;
            case 3:
                btn.setBackgroundResource(R.drawable.btn_equipe_hein_off);
                break;
            case 4:
                btn.setBackgroundResource(R.drawable.btn_equipe_chicon_off);
                break;
            case 5:
                btn.setBackgroundResource(R.drawable.btn_equipe_moule_off);
                break;
        }
    }

    /**
     * Cette méthode modifie la couleur des boutons d'insigne
     */
    private void setSelectedBtnBackground(View btn, int btnTag){
        switch (btnTag){
            case 1:
                btn.setBackgroundResource(R.drawable.btn_equipe_biere_on);
                break;
            case 2:
                btn.setBackgroundResource(R.drawable.btn_equipe_frite_on);
                break;
            case 3:
                btn.setBackgroundResource(R.drawable.btn_equipe_hein_on);
                break;
            case 4:
                btn.setBackgroundResource(R.drawable.btn_equipe_chicon_on);
                break;
            case 5:
                btn.setBackgroundResource(R.drawable.btn_equipe_moule_on);
                break;
        }
    }

    /**
     * Cette méthode prépare l'arrivé
     */
    private void setUp_TeamCreation(){
        // On crée nos OnClickListener pour nos boutons
        createBtnListener();

        // On sort le layout du MJ et on remet notre layout visible
        animator.fadeOutAnimation(lMJ_Introduction);

        // On prépare les textes
        tComposition.setText("L'équipe ne possède pas de joueur pour le moment.");

        Typeface type = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/steinem.ttf");
        tTeamCreation_Title.setTypeface(type);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // On fait entrer notre layout
                lTeam_Creation.setVisibility(View.VISIBLE);
                lTeam_Creation.animate().translationX(0).withLayer().setDuration(800);
            }
        }, 1000);
    }

    /**
     * Cette méthode crée les OnClickListener des boutons de notre layout TeamCreation
     */
    private void createBtnListener(){
        // ------- btn_addMember -------
        btn_addMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!eMemberName.getText().toString().equals("") ){
                    addMemberToList(eMemberName.getText().toString());
                    // on clean l'editText
                    eMemberName.setText("");
                } else {
                    Toasty.error(getApplicationContext(), "Vous devez choisir un nom de joueur à ajouter a l'équipe.", Toast.LENGTH_SHORT, true).show();


                }
            }
        });

        // ------- btn_createTeam -------
        btn_CreateTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!eTeamName.getText().toString().equals("") && insigneSelected && nbMember >= 3 && numInsigneSelected != 0 ){
                    // On crée l'équipe et on l'ajoute a la BDD
                    createTeamInBDD();

                    // On sort l'écran de création d'éuipe
                    lTeam_Creation.animate().translationX(1500).withLayer().setDuration(1000);
                    lTeam_Creation.setClickable(false);

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            lTeam_Creation.setVisibility(GONE);
                            restartMJExplication();
                        }
                    }, 1250);
                } else {
                    if (eTeamName.getText().toString().equals("")){
                        Toasty.error(getApplicationContext(), "Vous devez choisir un nom d'équipe.", Toast.LENGTH_SHORT, true).show();
                    } else if (nbMember < 3){
                        Toasty.error(getApplicationContext(), "Vous devez ajouter des membres à votre équipe.", Toast.LENGTH_SHORT, true).show();
                    } else if (!insigneSelected){
                        Toasty.error(getApplicationContext(), "Vous devez séléctionner un insigne pour votre équipe.", Toast.LENGTH_SHORT, true).show();

                    }


                }
            }
        });

        // ------- btn_insigne -------
        btn_insigne1.setOnClickListener(new MyClickListener());
        btn_insigne2.setOnClickListener(new MyClickListener());
        btn_insigne3.setOnClickListener(new MyClickListener());
        btn_insigne4.setOnClickListener(new MyClickListener());
        btn_insigne5.setOnClickListener(new MyClickListener());

        btn_insigne1.setTag(1);
        btn_insigne2.setTag(2);
        btn_insigne3.setTag(3);
        btn_insigne4.setTag(4);
        btn_insigne5.setTag(5);
    }

    /**
     * Cette méthode va crée un objet TeamClass et l'ajouter a notre BDD
     */
    private void createTeamInBDD(){

        new Thread(new Runnable() {
            @Override
            public void run() {

                // Get a Realm instance for this thread
                realm = Realm.getDefaultInstance();

                try{

                    final TeamClass team = new TeamClass(eTeamName.getText().toString().toUpperCase(), memberList, numInsigneSelected);
                    theTeam = team;
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            // This will create a new object in Realm or throw an exception if the
                            // object already exists (same primary key)
                            realm.copyToRealm(team);
                            Log.i(TAG, "Création de l'équipe terminé");
                        }
                    });

                } finally {
                    realm.close();
                }


            }
        }).start();

    }

    /**
     * Cette méthode va vérifié si un objet TeamClass existe
     * Si oui, on passe directement a l'activity InGame
     */
    private void checkTeamInBDD(){


        // Get a Realm instance for this thread
        realm = Realm.getDefaultInstance();

        try{

            TeamClass team = realm.where(TeamClass.class).findFirst();
            if (team != null){
                Intent goToingameActivity = new Intent (getApplicationContext(), InGameActivityClass.class);
                startActivity(goToingameActivity);
            }


        } finally {
            realm.close();
        }

    }

    /**
     * Cette méthode ajoute un nouveau membre à l'équipe
     */
    private void addMemberToList(String prenom) {
        nbMember++;
        if (nbMember <= 1) {
            membresList = "L'équipe est composée de : " + prenom;
        }
        if (nbMember >= 2) {
            membresList = membresList+" - "+prenom;
        }
        memberList.add(eMemberName.getText().toString().toUpperCase());
        tComposition.setText(membresList);
    }

    // ------- Méthode de changement d'activity -------

    private void goToRulesExplicationActivity(){

        animator.fadeOutAnimation(lMJ_Introduction);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                animator.fadeInAnimation(lBlackScreen);
            }
        },500);

        Handler handler2 = new Handler();
        handler2.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Puis on change d'activity
                Intent goToRulesActivity = new Intent (getApplicationContext(), RulesExplicationClass.class);
                startActivity(goToRulesActivity);
            }
        },1500);


    }

    // ------- OnClickListener -------
    /**
     * OnClickListener affecté aux bouton insigne
     */
    class MyClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            for(Button theBtn : btn_Insigne_List){
                setClassicBtnBackground(theBtn, (int)theBtn.getTag());
            }

            v.setSelected(true);
            setSelectedBtnBackground(v, (int)v.getTag());

            insigneSelected = true;

            numInsigneSelected = (int) v.getTag();

        }
    }



}





