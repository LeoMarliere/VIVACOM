package com.vivacom.leo.perdpaslenord.fragments;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vivacom.leo.perdpaslenord.OnSwipeTouchListener;
import com.vivacom.leo.perdpaslenord.R;
import com.vivacom.leo.perdpaslenord.objects.MarkerOptionRealm;
import com.vivacom.leo.perdpaslenord.objects.SpotClass;
import com.vivacom.leo.perdpaslenord.objects.TeamClass;
import com.vivacom.leo.perdpaslenord.objects.ZoneClass;

import java.util.ArrayList;
import java.util.Objects;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Created by Leo on 08/12/2017.
 */

public class RoadBookFragmentClass extends Fragment {

    // ----------- Nos Elements Graphiques -----------
    LinearLayout lSucces;
    TextView txtV_stats_Km , txtV_stats_pointPassage , txtV_stats_pointCulture, txtV_stats_pointSecret , txtV_stats_zone  , txtV_stats_jeu, txtV_title;

    LinearLayout lPhotoGalery;
    ImageView mPhoto1,mPhoto2,mPhoto3;

    Button btn_FinishGame;

    View mView;
    TextView pm_0, pm_1, pm_2, pm_3, pm_4, pm_5, pm_6, pm_7, pm_8, pm_9, pm_10, pm_11, pm_12, pm_13, pm_14, pm_15, pm_16, pm_17, pm_18;

    // ------- Nos paramètres -------
    int nb_Metre, nb_pointPrincipaux, nb_pointSecondaire, nb_pointSecret, nb_Jeu, nb_zone = 0;
    int numPhoto = 0;
    boolean canMoove = true;

    // ------- Nos Listes -------
    ArrayList<Bitmap> listPhoto_Bitmap = new ArrayList<>();
    ArrayList<byte[]> listPhoto_Byte = new ArrayList<>();
    ArrayList<ImageView> listImageView = new ArrayList<>();
    ArrayList<TextView> phraseMystereList = new ArrayList<>();
    ArrayList<Integer> list_motsDecouvert = new ArrayList<>();
    ArrayList<Integer> list_motsNonDecouvert = new ArrayList<>();

    // ---------- Element Globaux ------------

    Realm realm;
    public final String TAG= "ROADBOOK";

    RoadBookFragmentClassCallBack roadBookFragmentClassCallBack;

    // ---------------------------------------------------------

    // Require empty public constructor
    public RoadBookFragmentClass(){

    }

    //Interface de CallBack pour utiliser des méthodes de la class principale depuis le fragment
    public interface RoadBookFragmentClassCallBack{
    }

    // Instance
    public static RoadBookFragmentClass newInstance(){
        RoadBookFragmentClass fragment = new RoadBookFragmentClass();
        return fragment;
    }

    // ---------------------------------------------------------

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof RoadBookFragmentClass.RoadBookFragmentClassCallBack)
            roadBookFragmentClassCallBack = (RoadBookFragmentClass.RoadBookFragmentClassCallBack) activity;
        Log.d(TAG, "Activity onAttach");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        roadBookFragmentClassCallBack = null;
        Log.d(TAG, "Activity onDetach");
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.d(TAG, "Activity onPause");
    }

    @Override
    public  void onResume(){
        super.onResume();
        Log.d(TAG, "Activity onResume");
        getTeamFromBDD();
    }

    // ---------------------------------------------------------

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_roadbook,container,false);

        lSucces = mView.findViewById(R.id.rb_succes);
        txtV_stats_jeu = mView.findViewById(R.id.stats_Jeu);
        txtV_stats_zone = mView.findViewById(R.id.stats_zone);
        txtV_stats_Km = mView.findViewById(R.id.stats_Km);
        txtV_stats_pointPassage = mView.findViewById(R.id.stats_pointPassage);
        txtV_stats_pointCulture = mView.findViewById(R.id.stats_pointCulture);
        txtV_stats_pointSecret = mView.findViewById(R.id.stats_pointSecret);

        lPhotoGalery = mView.findViewById(R.id.rb_photographie);
        mPhoto1 = mView.findViewById(R.id.rb_photo1);
        mPhoto2 = mView.findViewById(R.id.rb_photo2);
        mPhoto3 = mView.findViewById(R.id.rb_photo3);

        txtV_title = mView.findViewById(R.id.RB_title);

        btn_FinishGame = mView.findViewById(R.id.finishGame);

        pm_0 = mView.findViewById(R.id.pm_0);
        pm_1 = mView.findViewById(R.id.pm_1);
        pm_2 = mView.findViewById(R.id.pm_2);
        pm_3 = mView.findViewById(R.id.pm_3);
        pm_4 = mView.findViewById(R.id.pm_4);
        pm_5= mView.findViewById(R.id.pm_5);
        pm_6= mView.findViewById(R.id.pm_6);
        pm_7 = mView.findViewById(R.id.pm_7);
        pm_8 = mView.findViewById(R.id.pm_8);
        pm_9 = mView.findViewById(R.id.pm_9);
        pm_10 = mView.findViewById(R.id.pm_10);
        pm_11 = mView.findViewById(R.id.pm_11);
        pm_12 = mView.findViewById(R.id.pm_12);
        pm_13 = mView.findViewById(R.id.pm_13);
        pm_14 = mView.findViewById(R.id.pm_14);
        pm_15 = mView.findViewById(R.id.pm_15);
        pm_16 = mView.findViewById(R.id.pm_16);
        pm_17 = mView.findViewById(R.id.pm_17);
        pm_18 = mView.findViewById(R.id.pm_18);

        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d(TAG, "Activity onStart");

        createBtnListener();

        setUpPhotos();
        setUpListForPhrase();

        Typeface type = Typeface.createFromAsset(getActivity().getAssets(),"fonts/steinem.ttf");
        txtV_title.setTypeface(type);
        txtV_title.setText(txtV_title.getText().toString().toUpperCase());

       showDiscoverdedWord();


        lPhotoGalery.setOnTouchListener(new OnSwipeTouchListener(this.getContext()){
            @Override
            public void onSwipeRight() {mooveOnRight();}

            @Override
            public void onSwipeLeft() {mooveOnLeft();}

        });

    }

    // -------------- Méthode de controle des STATISTIQUES ---------------

    /**
     * Cette méthode va récupérer otre objet Team dans la BDD
     */
    public void getTeamFromBDD(){

        new Thread(new Runnable() {
            @Override
            public void run() {

                realm = Realm.getDefaultInstance();

                try{

                    TeamClass team = realm.where(TeamClass.class).findFirst();
                    if(team != null){

                        RealmList<Integer> listMots = team.getList_DiscoverWord();
                        list_motsDecouvert.clear();
                        list_motsDecouvert.addAll(listMots);
                        Log.i(TAG, "Récupération de "+list_motsDecouvert.size()+" mots découvert");

                        RealmList<Integer> listNonMots = team.getList_UnDiscoverWord();
                        list_motsNonDecouvert.clear();
                        list_motsNonDecouvert.addAll(listNonMots);
                        Log.i(TAG, "Récupération de "+list_motsNonDecouvert.size()+" mots non découvert");

                        RealmList<byte[]> listBitmap = team.getList_photos();
                        listPhoto_Byte.clear();
                        listPhoto_Byte.addAll(listBitmap);
                        Log.i(TAG, "Récupération de "+listPhoto_Byte.size()+" photos prises");

                        nb_Jeu = team.getStats_NbVictoire();
                        nb_Metre = team.getStats_NbMetre();
                        nb_pointPrincipaux = team.getStats_NbSpot1();
                        nb_pointSecondaire = team.getStats_NbSpot2();
                        nb_pointSecret = team.getStats_NbSpot3();
                        nb_zone = team.getStats_NbZones();
                        Log.i(TAG, "Statistiques : " + nb_pointPrincipaux + " / " + nb_pointSecondaire + " / " + nb_pointSecret);

                        setUpStatistiques();
                        showDiscoverdedWord();
                        setPhotoGaleryVisible();
                    }

                } finally {
                    realm.close();
                }

            }
        }).start();

    }

    /**
     * Cette méthode met a jours les TextView STATISTIQUE
     */
    public void setUpStatistiques(){

        Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txtV_stats_pointPassage.setText("Nombre de Point Princiaux Terminé : "+ nb_pointPrincipaux +" / 20");
                txtV_stats_zone.setText("Nombre de Zone Terminé : "+nb_zone+ " / 4");
                txtV_stats_Km.setText("Nombre de mètre parcourus : "+ nb_Metre);
                txtV_stats_jeu.setText("Nombre de Jeu Gagné : "+nb_Jeu+" / 19");
                txtV_stats_pointCulture.setText("Nombre de Point Secondaire Terminé : "+ nb_pointSecondaire +" / 10");
                txtV_stats_pointSecret.setText("Nombre de Point Secret Terminé : "+nb_pointSecret+" / 5");
            }
        });


        listPhoto_Bitmap.clear();
        for(byte[] data : listPhoto_Byte){
            BitmapFactory.Options options=new BitmapFactory.Options();// Create object of bitmapfactory's option method for further option use
            options.inPurgeable = true; // inPurgeable is used to free up memory while required
            Bitmap bmp = BitmapFactory.decodeByteArray(data,0, data.length,options);//Decode image, "thumbnail" is the object of image file
            listPhoto_Bitmap.add(bmp);
        }

    }

    // ------- Méthode de gestion des PHOTOS  -------

    /**
     * Cette méthode prépare les différents éléments pour l'affichages des photos
     */
    private void setUpPhotos() {
        // On ajoute nos imageView dans la liste
        listImageView.clear();
        listImageView.add(mPhoto1);
        listImageView.add(mPhoto2);
        listImageView.add(mPhoto3);

        mPhoto1.setTag("Mid");
        mPhoto2.setTag("Left");
        mPhoto3.setTag("Right");

        // On déplace nos photos
        mPhoto2.animate().translationX(-1000).withLayer().setDuration(50);
        mPhoto3.animate().translationX(+1000).withLayer().setDuration(50);

        lPhotoGalery.setVisibility(View.GONE);

        numPhoto = 0;
    }

    /**
     * Cette méthode s'occupe de bouger les photos vers la droite
     */
    private void mooveOnRight(){
        if(canMoove){
            canMoove = false;

            ImageView imageLeft = new ImageView(getContext());
            ImageView imageRight = new ImageView(getContext());
            ImageView imageCenter = new ImageView(getContext());

            // On passse a la photo précédente
            numPhoto--;
            if(numPhoto == -1){numPhoto = listPhoto_Bitmap.size()-1;}

            // On récupère nos imageView que on associe
            for(int i = 0; i<listImageView.size(); i++){
                ImageView myImage = listImageView.get(i);
                if(myImage.getTag().equals("Left")){
                    imageLeft = myImage;
                } else if(myImage.getTag().equals("Mid")){
                    imageCenter = myImage;
                } else if(myImage.getTag().equals("Right")){
                    imageRight = myImage;
                }
            }

            // On affecte l'image qui va s'afficher
            imageLeft.setImageBitmap(listPhoto_Bitmap.get(numPhoto));

            // On modifie les visibilités
            imageCenter.setVisibility(View.VISIBLE);
            imageLeft.setVisibility(View.VISIBLE);
            imageRight.setVisibility(View.GONE);

            // On bouge les images
            imageCenter.animate().translationX(+1000).withLayer().setDuration(500);
            imageLeft.animate().translationX(0).withLayer().setDuration(500);
            imageRight.animate().translationX(-1000).withLayer().setDuration(50);

            // On change les tags
            imageCenter.setTag("Right");
            imageLeft.setTag("Mid");
            imageRight.setTag("Left");

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    canMoove = true;
                }
            }, 500);

            Log.d(TAG, "PHOTO_SwipeOnRight");
        }
    }

    /**
     * Cette méthode s'occupe de bouger les photos vers la gauche
     */
    private void mooveOnLeft(){
        if (canMoove){
            canMoove = false;

            ImageView imageLeft = new ImageView(getContext());
            ImageView imageRight = new ImageView(getContext());
            ImageView imageCenter = new ImageView(getContext());

            // On passse a la photo précédente
            numPhoto++;
            if (numPhoto == listPhoto_Bitmap.size()){numPhoto=0;}

            // On récupère nos imageView que on associe
            for(int i = 0; i<listImageView.size(); i++){
                ImageView myImage = listImageView.get(i);
                if(myImage.getTag().equals("Left")){
                    imageLeft = myImage;
                } else if(myImage.getTag().equals("Mid")){
                    imageCenter = myImage;
                } else if(myImage.getTag().equals("Right")){
                    imageRight = myImage;
                }
            }

            // On affecte l'image qui va s'afficher
            imageRight.setImageBitmap(listPhoto_Bitmap.get(numPhoto));

            // On modifie les visibilités
            imageCenter.setVisibility(View.VISIBLE);
            imageLeft.setVisibility(View.GONE);
            imageRight.setVisibility(View.VISIBLE);

            // On bouge les images
            imageCenter.animate().translationX(-1000).withLayer().setDuration(500);
            imageLeft.animate().translationX(+1000).withLayer().setDuration(50);
            imageRight.animate().translationX(0).withLayer().setDuration(500);

            // On change les tags
            imageCenter.setTag("Left");
            imageLeft.setTag("Right");
            imageRight.setTag("Mid");

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    canMoove = true;
                }
            }, 500);

            Log.d(TAG, "PHOTO_SwipeOnLeft");
        }
    }

    /**
     * Cette méthode va afficher les photos si il y en a
     */
    private void setPhotoGaleryVisible(){

        Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(listPhoto_Bitmap.size() == 0){
                    lPhotoGalery.setVisibility(View.GONE);
                    lPhotoGalery.setClickable(false);
                } else if (listPhoto_Bitmap.size() == 1){
                    lPhotoGalery.setVisibility(View.VISIBLE);
                    mPhoto1.setImageBitmap(listPhoto_Bitmap.get(0));
                    lPhotoGalery.setClickable(false);
                } else {
                    lPhotoGalery.setVisibility(View.VISIBLE);
                    mPhoto1.setImageBitmap(listPhoto_Bitmap.get(numPhoto));
                    lPhotoGalery.setClickable(true);
                }
            }
        });

    }


    // ------- Méthode de gestion de la phrase  -------

    /**
     * Cette méthode place nos TextView dans notre listView
     */
    private void setUpListForPhrase(){
        // On ajoute tout nos TextView a notre list
        phraseMystereList.add(pm_0);
        phraseMystereList.add(pm_1);
        phraseMystereList.add(pm_2);
        phraseMystereList.add(pm_3);
        phraseMystereList.add(pm_4);
        phraseMystereList.add(pm_5);
        phraseMystereList.add(pm_6);
        phraseMystereList.add(pm_7);
        phraseMystereList.add(pm_8);
        phraseMystereList.add(pm_9);
        phraseMystereList.add(pm_10);
        phraseMystereList.add(pm_11);
        phraseMystereList.add(pm_12);
        phraseMystereList.add(pm_13);
        phraseMystereList.add(pm_14);
        phraseMystereList.add(pm_15);
        phraseMystereList.add(pm_16);
        phraseMystereList.add(pm_17);
        phraseMystereList.add(pm_18);
    }

    /**
     * Cette méthode va rendre le mot corerespondant à l'index visible et rouge
     * @param index
     */
    public void showOneWord(int index){

        if(index <= 18) {

            final TextView mot = phraseMystereList.get(index);
            String texte = "ERROR";

            switch (index) {
                case 0:
                    texte = "Seul";
                    break;
                case 1:
                    texte = "on";
                    break;
                case 2:
                    texte = "va";
                    break;
                case 3:
                    texte = "plus";
                    break;
                case 4:
                    texte = "vite,";
                    break;
                case 5:
                    texte = "Ensemble";
                    break;
                case 6:
                    texte = "on";
                    break;
                case 7:
                    texte = "va";
                    break;
                case 8:
                    texte = "plus";
                    break;
                case 9:
                    texte = "loin.";
                    break;
                case 10:
                    texte = "J'ai";
                    break;
                case 11:
                    texte = "l'honneur";
                    break;
                case 12:
                    texte = "de";
                    break;
                case 13:
                    texte = "vous";
                    break;
                case 14:
                    texte = "nommer";
                    break;
                case 15:
                    texte = "\"Grand";
                    break;
                case 16:
                    texte = "Explorateur";
                    break;
                case 17:
                    texte = "des";
                    break;
                case 18:
                    texte = "Flandres\".";
                    break;
                default:
                    break;

            }

            final String text = texte;
            Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mot.setText(text);
                    mot.setTextColor(getResources().getColor(R.color.rouge));
                }
            });


        }
    }

    /**
     * Cette méthode va rendre le mot corerespondant à l'index visible et rouge
     * @param index
     */
    public void hideOneWord(int index){

        if(index <= 18) {


            final TextView mot = phraseMystereList.get(index);
            String texte = "ERROR";

            switch (index) {
                case 0:
                    texte = "****";
                    break;
                case 1:
                    texte = "**";
                    break;
                case 2:
                    texte = "**";
                    break;
                case 3:
                    texte = "****";
                    break;
                case 4:
                    texte = "****,";
                    break;
                case 5:
                    texte = "********";
                    break;
                case 6:
                    texte = "**";
                    break;
                case 7:
                    texte = "**";
                    break;
                case 8:
                    texte = "****";
                    break;
                case 9:
                    texte = "****.";
                    break;
                case 10:
                    texte = "*'**";
                    break;
                case 11:
                    texte = "*'*******";
                    break;
                case 12:
                    texte = "**";
                    break;
                case 13:
                    texte = "****";
                    break;
                case 14:
                    texte = "******";
                    break;
                case 15:
                    texte = "\"*****";
                    break;
                case 16:
                    texte = "***********";
                    break;
                case 17:
                    texte = "***";
                    break;
                case 18:
                    texte = "********\".";
                    break;

            }

            final String text = texte;
            Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mot.setText(text);
                    mot.setTextColor(getResources().getColor(R.color.rouge));
                }
            });

        }
    }

    /**
     * Cette méthode va appeler la méthode showOneWord pour chaque mot présent dans notre liste
     */
    public void showDiscoverdedWord(){

        if(list_motsDecouvert.size() != 0){
            for(Integer nb : list_motsDecouvert ){
                showOneWord(nb);
            }
        }

        if(list_motsNonDecouvert.size() != 0){
            for(Integer nb : list_motsNonDecouvert ){
                hideOneWord(nb);
            }
        }
    }

    // ------- Méthode de gestion du btn_Terminer -------

    /**
     * Cette méthode va créer les OnClickListener du btn_FinishGame
     */
    private void createBtnListener(){

        btn_FinishGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder builder = new AlertDialog.Builder(RoadBookFragmentClass.this.getContext());

                builder.setMessage("Êtes-vous sur d'avoir fini de jouer ?")
                        .setTitle("Terminer le jeu :");

                builder.setPositiveButton("Oui, nous avons fini !!", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        delateAlldataFromBDD();
                        Objects.requireNonNull(getActivity()).finish();
                        System.exit(0);
                    }
                });

                builder.setNegativeButton("Nous allons continuer de jouer ...", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    /**
     * Cette méthode va supprimer toutes les données présente en BDD
     */
    private void delateAlldataFromBDD(){
        realm = Realm.getDefaultInstance();

        try{

            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmResults<TeamClass> rows = realm.where(TeamClass.class).findAll();
                    rows.deleteAllFromRealm();
                }
            });

            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmResults<SpotClass> rows = realm.where(SpotClass.class).findAll();
                    rows.deleteAllFromRealm();
                }
            });

            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmResults<MarkerOptionRealm> rows = realm.where(MarkerOptionRealm.class).findAll();
                    rows.deleteAllFromRealm();
                }
            });

            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmResults<ZoneClass> rows = realm.where(ZoneClass.class).findAll();
                    rows.deleteAllFromRealm();
                }
            });


        } finally {
            realm.close();
        }


        System.exit(0);


    }
}
















