package com.vivacom.leo.perdpaslenord.fragments;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.Marker;
import com.vivacom.leo.perdpaslenord.OnSwipeTouchListener;
import com.vivacom.leo.perdpaslenord.R;
import com.vivacom.leo.perdpaslenord.objects.MarkerOptionRealm;
import com.vivacom.leo.perdpaslenord.objects.SpotClass;
import com.vivacom.leo.perdpaslenord.objects.TeamClass;
import com.vivacom.leo.perdpaslenord.objects.ZoneClass;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Leo on 08/12/2017.
 */

public class RoadBookFragmentClass extends Fragment {

    // ----------- Nos Elements Graphiques -----------
    LinearLayout lSucces;
    TextView txtV_stats_Km , txtV_stats_pointPassage , txtV_stats_pointCulture, txtV_stats_pointSecret , txtV_stats_zone  , txtV_stats_jeu;

    RelativeLayout lPhotoGalery;
    ImageView mPhoto1,mPhoto2,mPhoto3;

    Button bSendMail;

    View mView;
    TextView pm_0, pm_1, pm_2, pm_3, pm_4, pm_5, pm_6, pm_7, pm_8, pm_9, pm_10, pm_11, pm_12, pm_13, pm_14, pm_15, pm_16, pm_17, pm_18;

    // ------- Nos paramètres -------
    int nb_Metre, nb_pointPassage, nb_pointCulture, nb_pointSecret, nb_Jeu, nb_zone = 0;
    int numPhoto = 0;
    boolean canMoove = true;

    // ------- Nos Listes -------
    ArrayList<Bitmap> playersPhotoList = new ArrayList<>();
    ArrayList<ImageView> listImageView = new ArrayList<>();
    ArrayList<TextView> phraseMystereList = new ArrayList<>();

    // ---------- Element Globaux ------------

    public TeamClass theTeam;
    Realm realm;
    public final String TAG= "ROADBOOK";

    RoadBookFragmentClassCallBack roadBookFragmentClassCallBack;


    /**
     * Constructeur vide
     */
    public RoadBookFragmentClass(){
        // Require empty public constructor
    }

    /**
     * Interface de CallBack pour utiliser des méthodes de la class principale depuis le fragment
     */
    public interface RoadBookFragmentClassCallBack{
        TeamClass getTeamFromGame();
    }

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


    public static RoadBookFragmentClass newInstance(){
        RoadBookFragmentClass fragment = new RoadBookFragmentClass();
        return fragment;
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.d(TAG, "Activity onPause");
    }

    @Override
    public  void onResume(){
        super.onResume();
        setUpStatistiques();
        setUpPhotos();
        Log.d(TAG, "Activity onResume");
    }


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


        bSendMail = mView.findViewById(R.id.sendMail);

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

        setUpStatistiques();
        setUpPhotos();
        setUpListForPhrase();

        getTeamFromBDD();

        bSendMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String teamName = theTeam.getmTeamName();
                String body = getMailBody();

                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"leomarliere@hotmail.fr"});
                //emailIntent.putExtra(Intent.EXTRA_CC, new String[]{"leomarliere@hotmail.fr"});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Récapitulatif de l'équipe : "+teamName);
                emailIntent.putExtra(Intent.EXTRA_TEXT, body);
                emailIntent.setType("message/rfc822");
                startActivity(Intent.createChooser(emailIntent, "Choose email client ...."));

                delateAlldataFromBDD();

            }
        });





    }

    // -------------- Méthode de controle des STATISTIQUES ---------------

    /**
     * Cette méthode met a jours les TextView STATISTIQUE
     */
    public void setUpStatistiques(){

        txtV_stats_pointPassage.setText("Nombre de Point de Passage Terminé : "+nb_pointPassage+" / 19");
        txtV_stats_zone.setText("Nombre de Zone Terminé : "+nb_zone+ " / 4");
        txtV_stats_Km.setText("Nombre de mètre parcourus : "+ nb_Metre);
        txtV_stats_jeu.setText("Nombre de Jeu Gagné : "+nb_Jeu+" / 20");
        txtV_stats_pointCulture.setText("Nombre de Point Mystère Terminé : "+ nb_pointCulture +" / 10");
        txtV_stats_pointSecret.setText("Nombre de Point Secret Terminé : "+nb_pointSecret+" / 5");

    }

    public void increase_Metre(int nbMetre){
        nb_Metre = nb_Metre + nbMetre;
        setUpStatistiques();
    }

    public void increase_Jeu(){
        nb_Jeu++;
        setUpStatistiques();
    }

    public void increase_zone(){
        nb_zone++;
        setUpStatistiques();
    }

    public void increase_pointPassage(){
        nb_pointPassage++;
        setUpStatistiques();
    }

    public void increase_pointCulture(){
        nb_pointCulture++;
        setUpStatistiques();
    }

    public void increase_pointSecret(){
        nb_pointSecret++;
        setUpStatistiques();
    }

    // --------------------

    public void getTeamFromBDD(){
        realm = Realm.getDefaultInstance();

        try{
            TeamClass team = realm.where(TeamClass.class).findFirst();
            if(team != null){
                theTeam = new TeamClass(team.getmTeamName(), team.getMembersList(), team.getNumInsinge());
            }

        } finally {
            realm.close();
        }
    }

    public String getMailBody(){
        getTeamFromBDD();

        String teamMember = "";
        for(int i =0; i<theTeam.getMembersList().size(); i++){
            teamMember = teamMember + "\n " + theTeam.getMembersList().get(i) + "";
        }

        return "Membres de l'équipe : " +
                ""+ teamMember + "" +
                "\n     --------    "+
                "\n Récapitulatifs des succès de l'équipe :" +
                "\n Point de Passage Complété : "+nb_pointPassage+""+
                "\n Point Culture Complété : "+nb_pointPassage+""+
                "\n Point Secret découvert : "+nb_pointSecret+""+
                "\n Zone Terminé : "+ nb_zone +"" +
                "\n Mètre Parcourus : "+ nb_Metre +"" +
                "\n Jeu gagné : "+nb_Jeu+"" +
                "\n     --------    ";

    }

    // ------- Méthode de gestion des PHOTOS  -------

    /**
     * Cette méthode prépare les différents éléments pour l'affichages des photos
     */
    private void setUpPhotos() {
        // On ajoute nos imageView dans la liste
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

        if (playersPhotoList.size() == 0){
            lPhotoGalery.setClickable(false);
        } else if (playersPhotoList.size() == 1){
            mPhoto1.setImageBitmap(playersPhotoList.get(numPhoto));
            lPhotoGalery.setClickable(false);
        } else {
            mPhoto1.setImageBitmap(playersPhotoList.get(numPhoto));
            lPhotoGalery.setClickable(true);

            lPhotoGalery.setOnTouchListener(new OnSwipeTouchListener(this.getContext()){
                @Override
                public void onSwipeRight() {mooveOnRight();}

                @Override
                public void onSwipeLeft() {mooveOnLeft();}

            });
        }

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
            if(numPhoto == -1){numPhoto = playersPhotoList.size()-1;}

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
            imageLeft.setImageBitmap(playersPhotoList.get(numPhoto));

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
            if (numPhoto == playersPhotoList.size()){numPhoto=0;}

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
            imageRight.setImageBitmap(playersPhotoList.get(numPhoto));

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
     * Cette méthode recoie une ImageView et l'ajoute à la liste
     * @param bmc
     */
    public void addPhotoToList(Bitmap bmc){
        playersPhotoList.add(bmc);
        Log.i(TAG, "ImageView reçu et ajouté");
        Log.i(TAG, "Nombre de photos enregistré : "+ playersPhotoList.size());
    }


    public void setPhotoVisible(){
        lPhotoGalery.setVisibility(View.VISIBLE);
    }


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

    // ------- Méthode de gestion de la phrase  -------

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

    public void showOneWord(int index){
        TextView mot = phraseMystereList.get(index);
        String texte = "ERROR";

        switch (index){
            case 0 :
                texte = "Seul";
                break;
            case 1 :
                texte = "on";
                break;
            case 2 :
                texte = "va";
                break;
            case 3 :
                texte = "plus";
                break;
            case 4 :
                texte = "loin,";
                break;
            case 5 :
                texte = "Ensemble";
                break;
            case 6 :
                texte = "on";
                break;
            case 7 :
                texte = "va";
                break;
            case 8 :
                texte = "plus";
                break;
            case 9 :
                texte = "loin.";
                break;
            case 10 :
                texte = "J'ai";
                break;
            case 11 :
                texte = "l'honneur";
                break;
            case 12 :
                texte = "de";
                break;
            case 13 :
                texte = "vous";
                break;
            case 14 :
                texte = "nommer";
                break;
            case 15 :
                texte = "\"Grand";
                break;
            case 16 :
                texte = "Explorateur";
                break;
            case 17 :
                texte = "des";
                break;
            case 18 :
                texte = "Flandres\".";
                break;

        }

        mot.setText(texte);
        mot.setTextColor(getResources().getColor(R.color.rouge));
    }

}
















