package com.vivacom.leo.perdpaslenord.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.vivacom.leo.perdpaslenord.R;
import com.vivacom.leo.perdpaslenord.ViewAnimations;
import com.vivacom.leo.perdpaslenord.constant.ConstantLatLng;
import com.vivacom.leo.perdpaslenord.objects.MarkerOptionRealm;
import com.vivacom.leo.perdpaslenord.objects.SpotClass;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;


/**
 * Created by Leo on 11/09/2017.
 */

public class GoogleMapFragment extends android.support.v4.app.Fragment implements GoogleMap.OnMarkerClickListener, OnMapReadyCallback {

    // --------------------------------------------------------------------------------
    // ----------------------- Mes Objects GoogleMaps ----------------------------
    GoogleMap mGoogleMap;
    MapView mMapView;
    View mView;
    Button centerBtn;

    LinearLayout layoutForBlockMap;

    // ------------------ Objet pour la localisation ------------------

    private LocationManager mLocationManager = null;
    public Marker currentPositionMarker;
    public Realm realm;

    private static final int LOCATION_INTERVAL = 400;
    private static final float LOCATION_DISTANCE = 10f;

    public Location mLastLocationGoogleMap;

    // --------- Notre listes de markers ---------
    public List<Marker> markerList_Actual = new ArrayList<>();

    // -------- Nos listes de markersOption -----------
    public List<MarkerOptions> markerOptionList_Secret = new ArrayList<>(); // reçois les spots secret
    public List<MarkerOptions> markerOptionList_Completed = new ArrayList<>(); // reçois les spots completer

    // ---- Polygon ----
    Polygon polygonOpera, polygonGrandPlace, polygonVieuxLille, polygonLilleCentre, polygonLille;

    // --------------------- Legendes -------------------------
    // Legende 1
    LinearLayout legendTopLayout, pointPassage, pointMystere, pointSecret, generalLegend;
    Button legendTopBtn;
    TextView zoneNameTxtV;
    String currentZoneName = "";

    // Legende 2
    LinearLayout legendBotLayout;
    TextView nbPointPassageTxtV, nbPointCultureTxtV, nbPointSecretTxtV;
    String sNbPointPassage, sNbPointCulture, sNbPointSecret;
    int iNbPointPassage, iNbPointCulture, iNbPointSecret = 0;

    // ------- Variable -------

    boolean legendVisible, centerBtnVisible = true;
    boolean zoneChanged = false;

    int currentZone = 0;
    int currentLegend = 1;

    int spotState = 0;

    boolean cameraMooveFinish = true;

    ViewAnimations animator = new ViewAnimations();
    public static final String TAG = "GOOGLE_MAP";

    // ------------------ Object pour la communication Activity/Fragment ------------------------------
    static GoogleMapFragmentCallBack googleMapFragmentCallBack;


    // -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    /**
     * Call Back récupérant une action effectué dans la fragment
     * pour ensuite l'envoyer a l'activity mère
     */
    public interface GoogleMapFragmentCallBack {
        void whenMarkerIsSelected(String title);
        void increase_NbMetre(int metre);
        void showMJFragment(final String message[], int num);
        String[] getMessageForSecretSpot(String titre);
        void whenASecretIsDiscover();
    }


    /**
     * Instance GoogleMapFragment permettant de recréer le fragment depuis l'activity
     * @return
     */
    public static GoogleMapFragment newInstance() {
        GoogleMapFragment fragment = new GoogleMapFragment();
        return fragment;
    }

    /**
     * Constructeur vide
     */
    public GoogleMapFragment() {
        // require empty public constructor
    }


    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        Log.e(TAG, "Activity onCreate");
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_googlemap, container, false);

        legendTopBtn = mView.findViewById(R.id.legendBtn);
        legendTopLayout = mView.findViewById(R.id.layoutForLegend);
        pointPassage = mView.findViewById(R.id.legend_passage);
        pointMystere = mView.findViewById(R.id.legend_mystere);
        pointSecret = mView.findViewById(R.id.legend_secret);
        generalLegend = mView.findViewById(R.id.legend_general);
        zoneNameTxtV = mView.findViewById(R.id.legend_ZoneName);
        centerBtn = mView.findViewById(R.id.centerBtn);
        legendBotLayout = mView.findViewById(R.id.layoutForLegend2);
        nbPointCultureTxtV = mView.findViewById(R.id.legend_nbPointMystere);
        nbPointPassageTxtV = mView.findViewById(R.id.legend_nbPointPassage);
        nbPointSecretTxtV = mView.findViewById(R.id.legend_nbPointSecret);
        layoutForBlockMap = mView.findViewById(R.id.layoutForBlockMap);

        return mView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof GoogleMapFragmentCallBack)
            googleMapFragmentCallBack = (GoogleMapFragmentCallBack) activity;
        Log.d(TAG, "Activity onAttach");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        googleMapFragmentCallBack = null;
        Log.d(TAG, "Activity onDetach");
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.d(TAG, "Activity onPause");
        if (centerBtnVisible){changeCenterBtnVisibility();}
    }

    @Override
    public  void onResume(){
        super.onResume();
        Log.i(TAG, "Activity onResume");
        if (!centerBtnVisible){changeCenterBtnVisibility();}
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.e(TAG, "Activity onViewCreated");


        mMapView = mView.findViewById(R.id.googleMap);
        if (mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }

        legendTopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mooveLegend();
            }
        });

        centerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(currentPositionMarker.getPosition()).zoom(17.5f).bearing(0).tilt(30).build()), new GoogleMap.CancelableCallback() {
                    @Override
                    public void onFinish() {

                    }

                    @Override
                    public void onCancel() {

                    }
                });
            }
        });
    }

    // -----------------------------------

    /**
     * Cette méthode va s'executer au démarrage du Fragment
     * Elle va appeler les méthode a executer pour setUp le Fragment
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        Log.i(TAG, "Activity onMapReady : ---------- Start");

        // ------------- Initialisation de la carte Google Map ---------------
        mapInitialisation(googleMap);
        mapConfiguration(googleMap);
        initMarker();

        // -------- Placement des markers completed si il y en a --------
        if(markerOptionList_Completed.size() != 0){placeMarkersOnTheMap(markerOptionList_Completed);}

        // -------------------- Géolocalisation ----------------------------
        createLocationListener();

        // -------------------- Gestion des zones ---------------------
        addPolygonOnMap();
        createListenerForPolygon();

        // --------- Permet de centrer la caméra sur le spot voulu -------------
        googleMap.setOnMarkerClickListener(this);

        // ------ On place la légende ----------
        setUpLegendForGeneral();
        actualizeLegendBot();
        mooveLegend();

        layoutForBlockMap.setVisibility(View.GONE);
        layoutForBlockMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        Log.i(TAG, "Activity onMapReady : ---------- End");
    }

    // ----------------------- Méthode de setUp --------------------------------------

    /**
     * On initialise la map en lui attribuant un type et un style
     * @param googleMap
     */
    public void mapInitialisation(GoogleMap googleMap) {
        // On initialise l'objet googleMap
        MapsInitializer.initialize(getContext());
        mGoogleMap = googleMap;
        // On défini son style
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.setMapStyle(new MapStyleOptions((getResources().getString(R.string.map_style))));
    }

    /**
     * Cette méthode affecte les options voulu a notre GoogleMap
     * @param googleMap
     */
    public void mapConfiguration(GoogleMap googleMap) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ConstantLatLng.latlng_LILLE, 16f));
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.getUiSettings().setCompassEnabled(false);
        googleMap.setMinZoomPreference(15f);
        googleMap.setMaxZoomPreference(20f);
        googleMap.getUiSettings().setZoomGesturesEnabled(true);

        legendTopLayout.animate().translationX(+365).withLayer().setDuration(50);
        legendBotLayout.animate().translationX(+750).withLayer().setDuration(50);
        legendTopBtn.setBackgroundResource(R.drawable.btn_legende_in);

        legendVisible = false;
    }

    /**
     * Cette méthode vérifie si il existe des données dans la base
     * Si oui, elle va les ranger dans des listes
     * Si non, elle va les créer
     */
    public void initMarker(){
        realm = Realm.getDefaultInstance();

        try{

            MarkerOptionRealm markerTest = realm.where(MarkerOptionRealm.class).equalTo("titre", "La Voix Du Nord").findFirst();

            if (markerTest == null){
                markerOptionList_Completed.clear();
                createMarkerOptionForBDD();
            }

            putMarkerCompletedInTheGoodList();
            putSecretMarkerInTheGoodList();

        } finally{
            realm.close();
        }

        if (markerOptionList_Completed.size() != 0){placeMarkersOnTheMap(markerOptionList_Completed);}

    }

    // ----------------- Méthodes controllant les markers de la BDD ----------------------------

    /**
     * Cette méthode va créer nos MarkerOptionRealm
     * Et va ensuite les stocké en BDD
     */
    private void createMarkerOptionForBDD(){

        // ------- Grand Place -------
        final MarkerOptionRealm markerOptionPalaisRihour = new MarkerOptionRealm(1, "Grand Place", ConstantLatLng.latlng_palaisRihour.latitude, ConstantLatLng.latlng_palaisRihour.longitude, "Le Palais Rihour", R.drawable.marker_principal);
        final MarkerOptionRealm markerOptionLeFuretDuNord = new MarkerOptionRealm(2, "Grand Place", ConstantLatLng.latlng_furetDuNord.latitude, ConstantLatLng.latlng_furetDuNord.longitude, "Le Furet Du Nord", R.drawable.marker_principal);
        final MarkerOptionRealm markerOptionLaGrandGare = new MarkerOptionRealm(3, "Grand Place", ConstantLatLng.latlng_laGrandGarde.latitude, ConstantLatLng.latlng_laGrandGarde.longitude, "La Grand'Garde", R.drawable.marker_principal);
        final MarkerOptionRealm markerOptionLaVoixDuNord = new MarkerOptionRealm(4, "Grand Place", ConstantLatLng.latlng_laVoixDuNord.latitude, ConstantLatLng.latlng_laVoixDuNord.longitude, "La Voix Du Nord", R.drawable.marker_principal);
        final MarkerOptionRealm markerOptionLaColonneDeLaDeesse = new MarkerOptionRealm(5, "Grand Place", ConstantLatLng.latlng_laColonneDeLaDeesse.latitude, ConstantLatLng.latlng_laColonneDeLaDeesse.longitude, "La Colonne De La Déesse", R.drawable.marker_principal);

        final MarkerOptionRealm markerOptionPyramide = new MarkerOptionRealm(6, "Grand Place", ConstantLatLng.latlng_pyramide.latitude, ConstantLatLng.latlng_pyramide.longitude, "La Pyramide de Rihour", R.drawable.marker_secondaire);

        // ------- Place du Théatre --------
        final MarkerOptionRealm markerOptionLaVieilleBourse = new MarkerOptionRealm(7, "Place du Théatre", ConstantLatLng.latlng_laVieilleBourse.latitude, ConstantLatLng.latlng_laVieilleBourse.longitude, "La Vieille Bourse", R.drawable.marker_principal);
        final MarkerOptionRealm markerOptionLeRangDuBeauxregard = new MarkerOptionRealm(8, "Place du Théatre", ConstantLatLng.latlng_leRangBeauxRegard.latitude, ConstantLatLng.latlng_leRangBeauxRegard.longitude, "Le Rang Du Beauregard", R.drawable.marker_principal);
        final MarkerOptionRealm markerOptionLeBeffroi = new MarkerOptionRealm(9, "Place du Théatre", ConstantLatLng.latlng_leBeffroi.latitude, ConstantLatLng.latlng_leBeffroi.longitude, "Le Beffroi", R.drawable.marker_principal);
        final MarkerOptionRealm markerOptionOperaDeLille = new MarkerOptionRealm(10, "Place du Théatre", ConstantLatLng.latlng_opera.latitude, ConstantLatLng.latlng_opera.longitude, "L'Opéra De Lille", R.drawable.marker_principal);

        final MarkerOptionRealm markerOptionChambreCommerce = new MarkerOptionRealm(11, "Place du Théatre", ConstantLatLng.LATLNG_COMMERCE.latitude, ConstantLatLng.LATLNG_COMMERCE.longitude, "La Chambre des Commerces", R.drawable.marker_secondaire);

        // ------- Lille Centre --------
        final MarkerOptionRealm markerOptionLeNouveauSiecle = new MarkerOptionRealm(12, "Lille Centre", ConstantLatLng.latlng_leNouveauSiecle.latitude, ConstantLatLng.latlng_leNouveauSiecle.longitude, "Le Nouveau Siècle", R.drawable.marker_principal);
        final MarkerOptionRealm markerOptionLaStatueDuPtitQuinquin = new MarkerOptionRealm(13, "Lille Centre", ConstantLatLng.latlng_ptitQuinquin.latitude, ConstantLatLng.latlng_ptitQuinquin.longitude, "La Statue du Ptit Quinquin", R.drawable.marker_principal);
        final MarkerOptionRealm markerOptionRueNationale = new MarkerOptionRealm(14, "Lille Centre", ConstantLatLng.latlng_rueNationale.latitude, ConstantLatLng.latlng_rueNationale.longitude, "Rue Nationale", R.drawable.marker_principal);

        final MarkerOptionRealm markerOptionLe28 = new MarkerOptionRealm(15, "Lille Centre", ConstantLatLng.LATLNG_LE28.latitude, ConstantLatLng.LATLNG_LE28.longitude, "Le 28 Thiers", R.drawable.marker_secondaire);
        final MarkerOptionRealm markerOptionStatueFoch = new MarkerOptionRealm(16, "Lille Centre", ConstantLatLng.LATLNG_STATUEFOCH.latitude, ConstantLatLng.LATLNG_STATUEFOCH.longitude, "La Statue du Général Foch", R.drawable.marker_secondaire);
        final MarkerOptionRealm markerOptionQuaiDuWault = new MarkerOptionRealm(17, "Lille Centre", ConstantLatLng.LATLNG_QUAIDUWAULT.latitude, ConstantLatLng.LATLNG_QUAIDUWAULT.longitude, "Quai Du Wault", R.drawable.marker_secondaire);

        // ------- Vieux Lille --------
        final MarkerOptionRealm markerOptionLaPlaceAuxOignons = new MarkerOptionRealm(18, "Vieux Lille", ConstantLatLng.latlng_placeAuxOignons.latitude, ConstantLatLng.latlng_placeAuxOignons.longitude, "La Place aux Oignons", R.drawable.marker_principal);
        final MarkerOptionRealm markerOptionLaPlaceLouiseDeBettignies = new MarkerOptionRealm(19, "Vieux Lille", ConstantLatLng.latlng_placeLouiseDeBettignies.latitude, ConstantLatLng.latlng_placeLouiseDeBettignies.longitude, "La Place Louise De Bettignies", R.drawable.marker_principal);
        final MarkerOptionRealm markerOptionIlotComtesse = new MarkerOptionRealm(20, "Vieux Lille", ConstantLatLng.latlng_ilotComtesse.latitude, ConstantLatLng.latlng_ilotComtesse.longitude, "L'Ilot Comtesse", R.drawable.marker_principal);
        final MarkerOptionRealm markerOptionHospiceComtesse = new MarkerOptionRealm(21, "Vieux Lille", ConstantLatLng.latlng_hospiceComtesse.latitude, ConstantLatLng.latlng_hospiceComtesse.longitude, "L'Hospice Comtesse", R.drawable.marker_principal);
        final MarkerOptionRealm markerOptionLaRueEsquermoise = new MarkerOptionRealm(22, "Vieux Lille", ConstantLatLng.latlng_rueEsquermoise.latitude, ConstantLatLng.latlng_rueEsquermoise.longitude, "Rue Esquermoise", R.drawable.marker_principal);
        final MarkerOptionRealm markerOptionLaRueGrandeChaussee = new MarkerOptionRealm(23, "Vieux Lille", ConstantLatLng.latlng_rueGrandeChaussee.latitude, ConstantLatLng.latlng_rueGrandeChaussee.longitude, "Rue Grande Chaussee", R.drawable.marker_principal);
        final MarkerOptionRealm markerOptionNotreDameDeLaTreille = new MarkerOptionRealm(24, "Vieux Lille", ConstantLatLng.latlng_notreDameDeLaTreille.latitude, ConstantLatLng.latlng_notreDameDeLaTreille.longitude, "Notre Dame De La Treille", R.drawable.marker_principal);

        final MarkerOptionRealm markerOptionHuitriere = new MarkerOptionRealm(25, "Vieux Lille", ConstantLatLng.LATLNG_HUITRIERE.latitude, ConstantLatLng.LATLNG_HUITRIERE.longitude, "(Anciennement) L'Huitrière", R.drawable.marker_secondaire);
        final MarkerOptionRealm markerOptionMeert = new MarkerOptionRealm(26, "Vieux Lille", ConstantLatLng.LATLNG_MEERT.latitude, ConstantLatLng.LATLNG_MEERT.longitude, "Meert", R.drawable.marker_secondaire);
        final MarkerOptionRealm markerOptionCour = new MarkerOptionRealm(27, "Vieux Lille", ConstantLatLng.LATLNG_COURSINTERIEURE.latitude, ConstantLatLng.LATLNG_COURSINTERIEURE.longitude, "Les Compagnons de la Grappe", R.drawable.marker_secondaire);
        final MarkerOptionRealm markerOptionRueWeppes = new MarkerOptionRealm(28, "Vieux Lille", ConstantLatLng.LATLNG_RUEWEPPES.latitude, ConstantLatLng.LATLNG_RUEWEPPES.longitude, "Rue Weppes", R.drawable.marker_secondaire);

        // ------- Secret --------
        final MarkerOptionRealm secretMarker_goldenArm = new MarkerOptionRealm(29, "Secret", ConstantLatLng.LATLNG_GOLDENARM.latitude, ConstantLatLng.LATLNG_GOLDENARM.longitude, "Le Bras d'Or", R.drawable.marker_secret);
        final MarkerOptionRealm secretMarker_Francmacon = new MarkerOptionRealm(30, "Secret", ConstantLatLng.LATLNG_FRANCMACON.latitude, ConstantLatLng.LATLNG_FRANCMACON.longitude, "Temple Maconnique de Lille", R.drawable.marker_secret);
        final MarkerOptionRealm secretMarker_BehindTreille = new MarkerOptionRealm(31, "Secret", ConstantLatLng.LATLNG_BEHINDTREILLE.latitude, ConstantLatLng.LATLNG_BEHINDTREILLE.longitude, "Le Flanc caché de la Cathédrale", R.drawable.marker_secret);
        final MarkerOptionRealm secretMarker_Compostelle = new MarkerOptionRealm(32, "Secret", ConstantLatLng.LATLNG_COMPOSTELLE.latitude, ConstantLatLng.LATLNG_COMPOSTELLE.longitude, "Le Compostelle", R.drawable.marker_secret);

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                // This will create a new object in Realm or throw an exception if the
                // object already exists (same primary key)
                realm.copyToRealm(markerOptionPalaisRihour);
                realm.copyToRealm(markerOptionLeFuretDuNord);
                realm.copyToRealm(markerOptionLaGrandGare);
                realm.copyToRealm(markerOptionLaVoixDuNord);
                realm.copyToRealm(markerOptionLaColonneDeLaDeesse);
                realm.copyToRealm(markerOptionPyramide);

                realm.copyToRealm(markerOptionLaVieilleBourse);
                realm.copyToRealm(markerOptionLeRangDuBeauxregard);
                realm.copyToRealm(markerOptionLeBeffroi);
                realm.copyToRealm(markerOptionOperaDeLille);
                realm.copyToRealm(markerOptionChambreCommerce);

                realm.copyToRealm(markerOptionLeNouveauSiecle);
                realm.copyToRealm(markerOptionLaStatueDuPtitQuinquin);
                realm.copyToRealm(markerOptionRueNationale);
                realm.copyToRealm(markerOptionLe28);
                realm.copyToRealm(markerOptionStatueFoch);
                realm.copyToRealm(markerOptionQuaiDuWault);

                realm.copyToRealm(markerOptionLaPlaceAuxOignons);
                realm.copyToRealm(markerOptionLaPlaceLouiseDeBettignies);
                realm.copyToRealm(markerOptionIlotComtesse);
                realm.copyToRealm(markerOptionHospiceComtesse);
                realm.copyToRealm(markerOptionLaRueEsquermoise);
                realm.copyToRealm(markerOptionLaRueGrandeChaussee);
                realm.copyToRealm(markerOptionNotreDameDeLaTreille);
                realm.copyToRealm(markerOptionHuitriere);
                realm.copyToRealm(markerOptionMeert);
                realm.copyToRealm(markerOptionCour);
                realm.copyToRealm(markerOptionRueWeppes);

                realm.copyToRealm(secretMarker_goldenArm);
                realm.copyToRealm(secretMarker_Francmacon);
                realm.copyToRealm(secretMarker_BehindTreille);
                realm.copyToRealm(secretMarker_Compostelle);

                Log.i(TAG, "Création des données MarkerOptionRealm terminé");

            }
        });


    }

    /**
     * Cette méthode va récuperer tous les markerOptionRealm ayant TRUE pour isCompleted
     * Pour ensuite créer des markerOptions et les stocker dans la liste markerOptionList_Completed
     */
    private void putMarkerCompletedInTheGoodList(){

        markerOptionList_Completed.clear();

        RealmQuery<MarkerOptionRealm> query = realm.where(MarkerOptionRealm.class);
        query.equalTo("isCompleted", true);
        RealmResults<MarkerOptionRealm> markerList = query.findAll();

        for(MarkerOptionRealm markerRealm : markerList){
            MarkerOptions markerOption = new MarkerOptions().position(new LatLng(markerRealm.getPositionX(), markerRealm.getPositionY())).title(markerRealm.getTitre()).icon(BitmapDescriptorFactory.fromResource(markerRealm.getIconPath()));
            markerOptionList_Completed.add(markerOption);
        }
    }

    /**
     * Cette méthode va récuperer tout les markerOptionRealm dans la BDD qui sont SECRET
     * Puis va créer des markerOption et les mettre dans la liste markerOptionList_Secret
     */
    private void putSecretMarkerInTheGoodList(){

        markerOptionList_Secret.clear();

        RealmQuery<MarkerOptionRealm> query = realm.where(MarkerOptionRealm.class);
        query.equalTo("zoneName", "Secret");
        RealmResults<MarkerOptionRealm> markerList = query.findAll();

        for(MarkerOptionRealm markerRealm : markerList){
            MarkerOptions markerOption = new MarkerOptions().position(new LatLng(markerRealm.getPositionX(), markerRealm.getPositionY())).title(markerRealm.getTitre()).icon(BitmapDescriptorFactory.fromResource(markerRealm.getIconPath()));
            markerOptionList_Secret.add(markerOption);
        }

    }

    /**
     * Cette méthode va récuperer dans la base tout les markerOptionRealm ayant le même zoneName
     * Puis va créer des markerOption pour les placer dans une liste
     * La méthode fini par renvoyer la liste
     * @param zoneName
     * @return
     */
    private List<MarkerOptions> getMarkerOptionList(String zoneName){

        realm = Realm.getDefaultInstance();
        List<MarkerOptions> maListe = new ArrayList<>();

        try{

            RealmQuery<MarkerOptionRealm> query = realm.where(MarkerOptionRealm.class);
            query.equalTo("zoneName", zoneName);
            RealmResults<MarkerOptionRealm> markerList = query.findAll();

            for(MarkerOptionRealm markerRealm : markerList) {
                if (!markerRealm.isCompleted()) {
                    MarkerOptions markerOption = new MarkerOptions().position(new LatLng(markerRealm.getPositionX(), markerRealm.getPositionY())).title(markerRealm.getTitre()).icon(BitmapDescriptorFactory.fromResource(markerRealm.getIconPath()));
                    maListe.add(markerOption);
                }
            }

        } finally{
            realm.close();
        }


        return maListe;
    }

    /**
     * Cette méthode passe le marker du spot passé en paramètre en vert
     * @param spot
     */
    public void setMarkerCompleted(SpotClass spot) {

        final int typeSpot = spot.getmSpotType();
        final String spotName = spot.getmSpotName();
        realm = Realm.getDefaultInstance();

        try {

            // Parmis les markers actuel, on passe celui séléctionné en vert
            for (Marker theMarker : markerList_Actual) {
                if (spotName.equalsIgnoreCase(theMarker.getTitle())) {
                    if(typeSpot == 1){theMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_principal_valide));}
                    if(typeSpot == 2){theMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_secondaire_valide));}
                    markerList_Actual.remove(theMarker);
                }
            }
        } catch (Exception e){
            System.out.print(e.getMessage());
        } finally {
            realm.close();
        }

        setMarkerCompletedInBDD(spot);

    }

    /**
     * Cette méthode va récupérer le bon MarkerOptionsRealm dnas la BDD
     * Pour modifier ca valeur isCompleted
     * On créer ensuite un markerOption que l'on ajoute dans notre liste markerOptionList_Completed
     * @param spot
     */
    private void setMarkerCompletedInBDD(SpotClass spot){

        final int typeSpot = spot.getmSpotType();
        final String spotName = spot.getmSpotName();
        realm = Realm.getDefaultInstance();

        try {

            // Puis on récupère dans la base le markerOptionRealm correspondant
            // Et on modifie ses données dans la base
            // On crée ensuite un MarkerOptions que l'on va mettre dans notre liste markerOptionList_Completed
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    MarkerOptionRealm markerRealm = realm.where(MarkerOptionRealm.class).equalTo("titre", spotName).findFirst();
                    if (markerRealm != null) {
                        markerRealm.setCompleted(true);
                        if (typeSpot == 1){markerRealm.setIconPath(R.drawable.marker_principal_valide);}
                        if (typeSpot == 2){markerRealm.setIconPath(R.drawable.marker_secondaire_valide);}
                        MarkerOptions markerOption = new MarkerOptions().position(new LatLng(markerRealm.getPositionX(), markerRealm.getPositionY())).title(markerRealm.getTitre()).icon(BitmapDescriptorFactory.fromResource(markerRealm.getIconPath()));
                        markerOptionList_Completed.add(markerOption);
                    }

                }
            });

        } catch (Exception e){
            System.out.print(e.getMessage());
        } finally {
            realm.close();
        }

    }

    /**
     * Cette méthode va chercher dans la BDD un markerOptionRealm avec pour titre le spotName
     * Elle va ensuite créer un markerOption a partir de notre objet et le renvoyer
     * @param spotName
     * @return
     */
    public MarkerOptions getMarkerOptionFromBDD(String spotName){

        realm = Realm.getDefaultInstance();
        MarkerOptions markerOption = new MarkerOptions();
        try {

            MarkerOptionRealm markerRealm = realm.where(MarkerOptionRealm.class).equalTo("titre", spotName).findFirst();
            if (markerRealm != null) {
                markerOption = new MarkerOptions().position(new LatLng(markerRealm.getPositionX(), markerRealm.getPositionY())).title(markerRealm.getTitre()).icon(BitmapDescriptorFactory.fromResource(markerRealm.getIconPath()));
            }


        } finally{
            realm.close();
        }

        return markerOption;

    }



    // ------- Méthode de gestion de la localisation -------

    /**
     * Cette méthode crée notre locationListener
     * @return
     */
    public void createLocationListener(){

        initializeLocationManager();
        LocationListener locationListenerGPS = new LocationListener(LocationManager.GPS_PROVIDER);

        // -----
        try {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE, locationListenerGPS);
            Log.i(TAG, "mLocationMaganer with GPS is succes" );
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }

        addCurrentPositionMarker();

    }

    /**
     * Cette méthode va créer notre locationManager
     */
    private void initializeLocationManager() {
        Log.i(TAG, "initializeLocationManager - LOCATION_INTERVAL: "+ LOCATION_INTERVAL + " LOCATION_DISTANCE: " + LOCATION_DISTANCE);
        //mLocationManager = (LocationManager) .getSystemService(Context.LOCATION_SERVICE);
        mLocationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
    }

    /**
     * Cette méthode va ajouter un marker pour notre position
     */
    public void addCurrentPositionMarker() {
        // On vérifie si la tablette a la permission d'utiliser les fonctionnalitées GPS
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            mGoogleMap.setMyLocationEnabled(true);
            return;

        } else {
            // On place le marker current position
            Location currentLocation = getLastKnownLocation(mLocationManager);
            if (currentLocation == null) {
            Log.d(TAG, "currentLocationGPS is null");
                LatLng currentLatLngGPS = new LatLng(50.637936894877875, 3.0545537313446403);
                currentPositionMarker = mGoogleMap.addMarker(new MarkerOptions().title("Position Actuelle").position(currentLatLngGPS).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_equipe_biere)));
                currentPositionMarker.setTitle("Vous êtes ici !!");
            } else {
                mLastLocationGoogleMap = currentLocation;
                Log.d(TAG, "currentLocationGPS is not null");
                LatLng currentLatLngGPS = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                if (currentPositionMarker != null){currentPositionMarker.remove();}
                currentPositionMarker = mGoogleMap.addMarker(new MarkerOptions().title("Position Actuelle").position(currentLatLngGPS).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_equipe_biere)));
                currentPositionMarker.setTitle("Vous êtes ici !!");
            }

            initLocation();

        }
    }

    /**
     * Cette méthode renvoie la dernière location connue
     * @param mLocationManager
     * @return
     */
    private Location getLastKnownLocation(LocationManager mLocationManager) {
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            @SuppressLint("MissingPermission") Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    /**
     * Cette méthode lance un premier calcul de distance fictif pour initialiser la position
     */
    private void initLocation(){

        MarkerOptions markerOptionOperaDeLille = (new MarkerOptions().position(ConstantLatLng.latlng_opera).title("L'Opéra De Lille"));
        Location markerLocation = new Location("");
        markerLocation.setLatitude(markerOptionOperaDeLille.getPosition().latitude);
        markerLocation.setLongitude(markerOptionOperaDeLille.getPosition().longitude);
        float distance = markerLocation.distanceTo(mLastLocationGoogleMap);
        Log.d(TAG, "Initialisation de la distance / distance : "+ distance);


    }

    /**
     * Cette méthode modifie le design du markers currentPosition en fonction du numéro
     * @param btnTag
     */
    public void changeCurrentPosMarkerIcon(int btnTag){
        switch (btnTag){
            case 1:
                currentPositionMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_equipe_biere));
                centerBtn.setBackgroundResource(R.drawable.btn_equipe_biere_on);
                break;
            case 2:
                currentPositionMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_equipe_frites));
                centerBtn.setBackgroundResource(R.drawable.btn_equipe_frite_on);
                break;
            case 3:
                currentPositionMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_equipe_hein));
                centerBtn.setBackgroundResource(R.drawable.btn_equipe_hein_on);
                break;
            case 4:
                currentPositionMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_equipe_chicon));
                centerBtn.setBackgroundResource(R.drawable.btn_equipe_chicon_on);
                break;
            case 5:
                currentPositionMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_equipe_moule));
                centerBtn.setBackgroundResource(R.drawable.btn_equipe_moule_on);
                break;
        }
    }


        // ----------- Placement -------------------

    /**
     * Cette méthode s'occupe de l'animation des markers
     * Le marker "glisse" vers sa nouvelle position
     * @param marker
     * @param toPosition
     */
    public  void animateMarker(final Marker marker, final LatLng toPosition) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = mGoogleMap.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 500;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 5);
                }
            }
        });
    }


    // ------- Gestion de la visibilité des markers -------

    /**
     * Cette méthode reçois une liste des markerOption en parametre
     * Place les markers sur la map
     * @param markerOptionList
     */
    public void placeMarkersOnTheMap(final List<MarkerOptions> markerOptionList){

        if(markerOptionList.size() != 0) {
            long time = 3000 / markerOptionList.size();
            long delay = 0;
            float index = 0;
            for (final MarkerOptions theMarker : markerOptionList) {
                index++;
                delay = delay + time;
                final float myIndex = index;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Marker marker = getGoogleMap().addMarker(theMarker.zIndex(myIndex));
                        animator.fadeInMarkerAnimation(marker);
                        if (markerOptionList != markerOptionList_Completed) {
                            markerList_Actual.add(marker);
                        }
                    }
                }, delay);
            }
        }

    }

    /**
     * Cette méthode supprime de la carte les markers
     */
    public void removeActualMarkerFromMap(){
        if (markerList_Actual.size() != 0) {
            for (Marker theMarker : markerList_Actual) {
                animator.fadeOutMarkerAnimation(theMarker);
            }
            markerList_Actual.clear();
        }

    }

    // ------------------ Gestion de la légende ----------------------

    /**
     * Cette méthode affiche/cache les éléments de la légende en fonction de la zone
     * @param zoneName
     */
    public void setUpLegend(String zoneName){

        currentZoneName = zoneName;

        pointMystere.setVisibility(View.VISIBLE);
        pointSecret.setVisibility(View.VISIBLE);
        pointPassage.setVisibility(View.VISIBLE);

        generalLegend.setVisibility(View.GONE);
        currentLegend = 2;
        zoneNameTxtV.setText(currentZoneName);

        if(Objects.equals(zoneName, "Ville de Lille")){
            pointMystere.setVisibility(View.GONE);
            pointSecret.setVisibility(View.GONE);
            pointPassage.setVisibility(View.GONE);
            generalLegend.setVisibility(View.VISIBLE);
            currentLegend = 1;
        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!legendVisible){mooveLegend();}
            }
        }, 1000);

    }

    /**
     * Cette méthode affiche la légende générale
     */
    public void setUpLegendForGeneral(){
        zoneNameTxtV.setText("Ville de Lille");
        pointMystere.setVisibility(View.GONE);
        pointSecret.setVisibility(View.GONE);
        pointPassage.setVisibility(View.GONE);
        generalLegend.setVisibility(View.VISIBLE);
        currentLegend = 1;
    }

    /**
     * Cette méthode s'occupe d'afficher/cacher la legende
     */
    public void mooveLegend(){
        if(legendVisible){
            legendTopLayout.animate().translationX(+365).withLayer().setDuration(400);
            legendBotLayout.animate().translationX(+750).withLayer().setDuration(400);
            legendVisible = false;
            legendTopBtn.setBackgroundResource(R.drawable.btn_legende_in);
        } else {
            legendTopLayout.animate().translationX(0).withLayer().setDuration(400);
            legendBotLayout.animate().translationX(0).withLayer().setDuration(400);
            legendVisible = true;
            legendTopBtn.setBackgroundResource(R.drawable.btn_legende_out);
        }
    }

    /**
     * Cette méthode va augmenter le nombre de point découvert
     * En fonction du spotType
     * Puis met a jour la legend
     * @param spotType
     */
    public void incrementNbPointLegendBot(int spotType){
        switch (spotType){
            case 1 :
                iNbPointPassage++;
                break;
            case 2 :
                iNbPointCulture++;
                break;
            case 3 :
                iNbPointSecret++;
                break;
        }
        actualizeLegendBot();
    }

    /**
     * Cette méthode met a jour la legende bot
     */
    public void actualizeLegendBot(){
        // Point Passage
        sNbPointPassage = ""+iNbPointPassage+" / 19";
        nbPointPassageTxtV.setText(sNbPointPassage);
        // Point Mystere
        sNbPointCulture =""+ iNbPointCulture +" / 10";
        nbPointCultureTxtV.setText(sNbPointCulture);
        // Point Secret
        sNbPointSecret=""+iNbPointSecret+" / 5";
        nbPointSecretTxtV.setText(sNbPointSecret);
    }

    // --------- Gestion d'event ---------------

    /**
     * Cette méthode ajoute a la map les zones
     */
    public void addPolygonOnMap(){

        // ------------------ Zone de Lille -----------------
        polygonLille = getGoogleMap().addPolygon(new PolygonOptions().clickable(true).add(
                new LatLng(50.64738785906999, 3.035917451115438), // haut gauche
                new LatLng(50.64769743518098, 3.0683803463085724),
                new LatLng(50.62167021345641, 3.0737274774362504),
                new LatLng(50.62357607421975, 3.027002378659404)
        ));
        polygonLille.setTag("Ville de Lille");
        polygonLille.setStrokeWidth(0);
        //polygonLille.setStrokeColor(getResources().getColor(R.color.violet));
        //polygonLille.setFillColor(getResources().getColor(R.color.violet_trans));

        // ------------- Vieux Lille ------------
        polygonVieuxLille = getGoogleMap().addPolygon(new PolygonOptions().clickable(true).add(
                new LatLng(50.63719010288768,3.062814474105835), // Debut VieuxLille - Lille Centre
                new LatLng(50.63761198228113,3.062240481376648), // Frontiere
                new LatLng(50.637765082737445,3.061969578266144), // Frontiere
                new LatLng(50.638129119597465,3.061071038246155), // Frontiere
                new LatLng(50.638596919833894,3.059593141078949),
                new LatLng(50.63880275046285,3.0589815974235535),
                new LatLng(50.63895244489951,3.0587106943130493),
                new LatLng(50.64009555033,3.057798743247986),
                new LatLng(50.64060925829862,3.0589306354522705),
                new LatLng(50.64136450087719,3.0602288246154785),
                new LatLng(50.64188159690389,3.0615001916885376),
                new LatLng(50.64249394010323,3.063082695007324),
                new LatLng(50.642650426529585,3.0635493993759155),
                new LatLng(50.641959841201796,3.064262866973877 ),
                new LatLng(50.64130666741518,3.065359890460968 ),
                new LatLng(50.64039663150928,3.064512312412262 ),
                new LatLng(50.64020271503863,3.0645713210105896 ),
                new LatLng(50.6399679729773,3.064485490322113 ),
                new LatLng(50.63955632102431,3.0641931295394897 ),
                new LatLng(50.63932327679134,3.06394100189209 ),
                new LatLng(50.63864284906063,3.0640295147895813 ),
                new LatLng(50.6380236512646, 3.0638618767261505), // Frontiere Opera
                new LatLng(50.637715750422664,3.0636151134967804 ),
                new LatLng(50.63747759369183,3.06331604719162 ),
                new LatLng(50.637302377612194,3.0632878839969635 ), // Frontiere commun opera - grand place
                new LatLng(50.637294722525866,3.063097447156906 )
        ));
        polygonVieuxLille.setTag("Vieux Lille");
        polygonVieuxLille.setStrokeWidth(3);
        polygonVieuxLille.setStrokeColor(getResources().getColor(R.color.vert));
        polygonVieuxLille.setFillColor(getResources().getColor(R.color.vert_trans));



        // ------------- Lille Centre ------------
        polygonLilleCentre = getGoogleMap().addPolygon(new PolygonOptions().clickable(true).add(
                new LatLng(50.63719010288768,3.062814474105835), // Debut VieuxLille - Lille Centre
                new LatLng(50.63761198228113,3.062240481376648), // Frontiere
                new LatLng(50.637765082737445,3.061969578266144), // Frontiere
                new LatLng(50.638129119597465,3.061071038246155), // Frontiere
                new LatLng(50.638636044733566,3.059467077255249),// Frontiere
                new LatLng(50.63880955476576,3.058968186378479),
                new LatLng(50.6389048149032,3.0587750673294067),
                new LatLng(50.63877553324131,3.0581796169281006),
                new LatLng(50.638615631746504,3.057042360305786),
                new LatLng(50.638380881757904,3.0528366565704346),
                new LatLng(50.63727515952178,3.0517101287841797),
                new LatLng(50.636945138921476,3.0522358417510986),
                new LatLng(50.63678523119925,3.0525684356689453),
                new LatLng(50.636649139092235,3.05269718170166),
                new LatLng(50.635696483309694,3.0545318126678467), //
                new LatLng(50.634468209323565,3.0567097663879395),
                new LatLng(50.6356862761789,3.059665560722351),
                new LatLng(50.63584618763991,3.05988147854805),
                new LatLng(50.6367120817407,3.0619481205940247), //grandplace
                new LatLng(50.637086333585636,3.0628037452697754)
        ));
        polygonLilleCentre.setTag("Lille Centre");
        polygonLilleCentre.setStrokeWidth(3);
        polygonLilleCentre.setStrokeColor(getResources().getColor(R.color.blue));
        polygonLilleCentre.setFillColor(getResources().getColor(R.color.blue_trans));
        // ------------- GrandPlace ------------
        polygonGrandPlace = getGoogleMap().addPolygon(new PolygonOptions().clickable(true).add(
                new LatLng(50.637302377612194,3.0632878839969635 ), // Frontiere commun opera - grand place
                new LatLng(50.637294722525866,3.063097447156906 ),
                new LatLng(50.63719010288768,3.062814474105835),
                new LatLng(50.637086333585636,3.0628037452697754),
                new LatLng(50.6367120817407,3.0619481205940247), //grandplace
                new LatLng(50.63662532293298,3.0619212985038757),
                new LatLng(50.635965695584055,3.062640130519867), // CDN
                new LatLng(50.63572667939198,3.062056750059128),
                new LatLng(50.63573050706297,3.061772435903549),
                new LatLng(50.63545746574865,3.061329200863838),
                new LatLng(50.63514274326284,3.0616745352745056),
                new LatLng(50.63517931922752,3.062198907136917),
                new LatLng(50.63493434478152,3.062484562397003),
                new LatLng(50.634981128194475,3.0627675354480743),
                new LatLng(50.636445000192865,3.0655020475387573),
                new LatLng(50.63700212718037,3.0647899210453033), // opera
                new LatLng(50.636733771417624,3.0642186105251312),
                new LatLng(50.63672909325284,3.0640623718500137),
                new LatLng(50.636806495555376,3.0639182031154633),
                new LatLng(50.63726325160228,3.063417971134186)
        ));
        polygonGrandPlace.setTag("Grand Place");
        polygonGrandPlace.setStrokeWidth(3);
        polygonGrandPlace.setStrokeColor(getResources().getColor(R.color.rouge));
        polygonGrandPlace.setFillColor(getResources().getColor(R.color.rouge_trans));
        // ------------- Opera ------------
        polygonOpera = getGoogleMap().addPolygon(new PolygonOptions().clickable(true).add(
                new LatLng(50.6380236512646, 3.0638618767261505), // Frontiere Opera
                new LatLng(50.637715750422664,3.0636151134967804 ),
                new LatLng(50.63747759369183,3.06331604719162 ),
                new LatLng(50.637302377612194,3.0632878839969635 ), // Frontiere commun opera - grand place
                new LatLng(50.63726325160228,3.063417971134186),
                new LatLng(50.636806495555376,3.0639182031154633),
                new LatLng(50.63672909325284,3.0640623718500137),
                new LatLng(50.636733771417624,3.0642186105251312),
                new LatLng(50.63700212718037,3.0647899210453033), // opera
                new LatLng(50.637202010825725,3.065227121114731),
                new LatLng(50.63724879198167,3.0652888119220734),
                new LatLng(50.63787225276015,3.0658158659934998),
                new LatLng(50.63806702939791,3.065251260995865),
                new LatLng(50.638132521797836,3.0651815235614777)
        ));
        polygonOpera.setTag("Place du Théatre");
        polygonOpera.setStrokeWidth(3);
        polygonOpera.setStrokeColor(getResources().getColor(R.color.orange));
        polygonOpera.setFillColor(getResources().getColor(R.color.orange_trans));

    }

    /**
     * Cette méthode créer les OnClickListener pour les Polygons
     * Lors d'un click sur une zone, on centre la carte sur la zone et on affiche les marqueurs de cette derniere
     */
    public void createListenerForPolygon(){
        getGoogleMap().setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
            @Override
            public void onPolygonClick(final Polygon polygon) {

                int numZone = 0;
                CameraUpdate camUpdate = CameraUpdateFactory.newCameraPosition(ConstantLatLng.CAMPOS_LILLE);

                // On récupère la bonne position en fonction du polygon cliqué
                if (polygon.getTag() != null){
                    switch ((String) polygon.getTag()){
                        case "Vieux Lille":
                            numZone = 1;
                            camUpdate = CameraUpdateFactory.newCameraPosition(ConstantLatLng.CAMPOS_VIEUXLILLE);
                            break;

                        case "Lille Centre":
                            numZone = 2;
                            camUpdate = CameraUpdateFactory.newCameraPosition(ConstantLatLng.CAMPOS_LILLECENTRE);
                            break;

                        case "Grand Place":
                            numZone = 3;
                            camUpdate = CameraUpdateFactory.newCameraPosition(ConstantLatLng.CAMPOS_GRANDPLACE);
                            break;

                        case "Place du Théatre":
                            numZone = 4;
                            camUpdate = CameraUpdateFactory.newCameraPosition(ConstantLatLng.CAMPOS_OPERA);
                            break;

                        case "Ville de Lille" :
                            numZone = 0;
                            camUpdate = CameraUpdateFactory.newCameraPosition(ConstantLatLng.CAMPOS_LILLE);
                    }
                }

                // On récupere une liste avec les bons markerOptions
                List<MarkerOptions> currentMarkerOption = getMarkerOptionList((String) polygon.getTag());

                if(polygonSelection(numZone) && cameraMooveFinish && numZone != currentZone ){

                    Log.d(TAG, "Changement de zone");

                    try {

                        if (legendVisible){mooveLegend();}

                        currentZone = numZone;
                        // On bloque la chagement de zone
                        cameraMooveFinish = false;

                        // On désactive la carte
                        blockMapActionForXSeconde(5000);

                        // On créer nos variable en final
                        final List<MarkerOptions> finalCurrentMarkerOption = currentMarkerOption;
                        changeCamera(camUpdate, new GoogleMap.CancelableCallback() {

                            @Override
                            public void onFinish() {
                                placeMarkersOnTheMap(finalCurrentMarkerOption);
                                setUpLegend((String) polygon.getTag());
                                layoutForBlockMap.setVisibility(View.GONE);
                            }

                            @Override
                            public void onCancel() {
                                layoutForBlockMap.setVisibility(View.GONE);
                            }
                        });


                    } catch (Exception e){System.out.print(e.getMessage());}


                }

            }
        });
    }

    /**
     * Cette méthode vérifie si on click sur une zone différentes
     * Affecte la bonne valeur a la variable currentZone
     * @param numZone
     */
    private boolean polygonSelection(final int numZone){
        // Si currentZone = 0, c'est le premier changement de zone
        if(currentZone == 0){zoneChanged = true;}

        // Si currentZone est différent du numZone, on change de zone
        else if (currentZone != numZone) {
            zoneChanged = true;
            if (cameraMooveFinish){removeActualMarkerFromMap();}
        }

        // Sinon, on ne change pas de zone
        else {zoneChanged = false;}

        return zoneChanged;
    }

    /**
     * Méthode gérant l'event onClick d'un marker
     * @param marker
     * @return
     */
    @Override
    public boolean onMarkerClick(final Marker marker) {
        if (googleMapFragmentCallBack != null) {
            // On vérifie que ce n'est pas un markerSecret
            if(marker.getSnippet() == null){
                Log.d(TAG, marker.getTitle() + " selected");
                googleMapFragmentCallBack.whenMarkerIsSelected(marker.getTitle());
                if (legendVisible) {mooveLegend();}
            }

            return true;
        } else {
            return false;
        }

    }

    /**
     * Cette méthode va faire apparaitre le layout bloquant la carte
     * Elle le fait ensuite disparaitre au bout d'un delay entré
     * @param delay
     */
    private void blockMapActionForXSeconde(int delay){

        layoutForBlockMap.setVisibility(View.VISIBLE);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                cameraMooveFinish = true;
            }
        }, delay);
    }


    // --------- Modification de caméra -------------------

    /**
     * Change the camera position by moving or animating the camera depending on the state of the
     * animate toggle button.
     */
    private void changeCamera(CameraUpdate update, GoogleMap.CancelableCallback callback) {
        mGoogleMap.animateCamera(update, callback);
    }

    /**
     * Cette méthode s'occupe d'afficher ou de cacher le bouton de recentrage
     */
    public void changeCenterBtnVisibility(){
        if (centerBtnVisible){
            centerBtn.setVisibility(View.GONE);
            centerBtnVisible = false;
        } else {
            centerBtn.setVisibility(View.VISIBLE);
            centerBtnVisible = true;
        }
    }


    // ----------------- Méthodes de calcul de distance ----------------------------

    /**
     * Méthode vérifiant si la position actuelle est proche d'un spot secret
     * Si oui, on affiche un message pour prévenir l'utilisateur et on affiche e spot sur la carte
     */
    public void checkDistanceWithSecretSpot() {

        for ( MarkerOptions myMarkerOption : markerOptionList_Secret) {
            Location markerLocation = new Location("");
            markerLocation.setLatitude(myMarkerOption.getPosition().latitude);
            markerLocation.setLongitude(myMarkerOption.getPosition().longitude);
            // On calcul la distance
            Log.d(TAG, "Distance du spot "+ myMarkerOption.getTitle() + " : " + mLastLocationGoogleMap.distanceTo(markerLocation));
            if ((int) mLastLocationGoogleMap.distanceTo(markerLocation) < 20) {
                // On fait apparaitre la marker
                markerOptionList_Secret.remove(myMarkerOption);
                getGoogleMap().addMarker(myMarkerOption);
                markerOptionList_Completed.add(myMarkerOption);
                incrementNbPointLegendBot(3);
                // On fait apparaitre le texte
                String message[] = googleMapFragmentCallBack.getMessageForSecretSpot(myMarkerOption.getTitle());
                googleMapFragmentCallBack.showMJFragment(message, 3);
                googleMapFragmentCallBack.whenASecretIsDiscover();

            }
        }
    }

    /**
     * Méthode qui calcule la distance entre la position actuelle de l'utilisateur
     * et la position d'un spot passé en paramètre.
     * Cette méthode est appelé lorsque l'utilisateur clique sur l'onglet MINI-JEUX d'un spot
     * Si l'utilisateur est considerer comme proche (<100m), renvoie true, sinon false
     * @param spot
     * @return
     */
    public boolean checkDistanceWithSpotForGameActivation(SpotClass spot) {
        boolean proche = false;
        float distance = 0;
        String spotName = spot.getmSpotName();

        // On récupère le marker et on calcul la distance
        if (mLastLocationGoogleMap != null) {

            // On récupère dans la BDD le bon markerOption
            MarkerOptions markerOption = getMarkerOptionFromBDD(spotName);
            Location markerLocation = new Location("");

            // On récupère la position
            markerLocation.setLatitude(markerOption.getPosition().latitude);
            markerLocation.setLongitude(markerOption.getPosition().longitude);
            // On calcul la distance
            distance = markerLocation.distanceTo(mLastLocationGoogleMap);

        } else {
            proche = true;
        }

        if (distance != 0) {proche = distance < 750000;} // inferieur a 40m

        Log.w(TAG, "Distance du spot "+ spotName + " : "+ distance + " / "+ proche + " / Précision : "+ mLastLocationGoogleMap.getAccuracy() );
        //return proche;
        return true;
    }




    // --------------------- Autres -----------------------------------
    
    /**
     * Cette méthode retourne notre objet GoogleMap
     * @return
     */
    public GoogleMap getGoogleMap(){
        return mGoogleMap;
    }

    /**
     * Cette méthode renvoie la valeur de la variable legendVisible
     * @return
     */
    public boolean isLegendVisible(){
        return legendVisible;
    }

    // ------- LocationListener -------

    private class LocationListener implements android.location.LocationListener {

        Location mLastLocation;
        int nbChangementPosition = 0;

        public LocationListener(String provider) {
            Log.d(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
            mLastLocationGoogleMap = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            nbChangementPosition++;
            if (location.getAccuracy() <= 10) {

                // On calcul la distance et la nouvelle LatLng
                float distance = mLastLocation.distanceTo(location);
                googleMapFragmentCallBack.increase_NbMetre((int) distance);

                // On déplace la marker
                LatLng newLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                animateMarker(currentPositionMarker, newLatLng);


                Log.e(TAG, nbChangementPosition+" = onLocationChanged: Accuracy : " + location.getAccuracy() + " / Distance parcouru :" + distance + " / Provider : " + mLastLocation.getProvider() );

                // On vérifie les spots secrets
                checkDistanceWithSecretSpot();

                // On actualise
                mLastLocation.set(location);
                mLastLocationGoogleMap = mLastLocation;
            } else {
                Log.e(TAG, "onLocationChanged: Accuracy : " + location.getAccuracy());
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.v(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.v(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.v(TAG, "onStatusChanged: " + provider + "status : "+ status);
        }
    }


}
