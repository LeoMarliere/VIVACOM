package com.vivacom.leo.perdpaslenord.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.vivacom.leo.perdpaslenord.OnSwipeTouchListener;
import com.vivacom.leo.perdpaslenord.R;

import java.util.ArrayList;


/**
 * Created by Leo on 18/09/2017.
 */

public class PictureGaleryFragmentClass extends Fragment {

    // ----------------------------------------------------------------------
    RelativeLayout lPhotoGalery;
    ImageView mPhoto1,mPhoto2,mPhoto3;
    // -------
    static final String IMG_PATH = "LISTE D'IMAGE";
    ArrayList<ImageView> listImageView = new ArrayList<>();
    ArrayList<Integer> maListe;
    int numPhoto = 0;
    boolean alreadyDone = false;
    boolean canMoove = true;
    // -------
    PictureGaleryFragmentClassCallBack pictureGaleryFragmentClassCallBack;

    public final String TAG = "PHOTOS";


    // ----------------------------------------------------------------------

    public PictureGaleryFragmentClass(){
        // Require empty public constructor
    }

    public interface PictureGaleryFragmentClassCallBack{
        void checkIfPhotoGalleryChecked();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof PictureGaleryFragmentClassCallBack)
            pictureGaleryFragmentClassCallBack = (PictureGaleryFragmentClassCallBack) activity;
        Log.d(TAG, "Fragment onAttach");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        pictureGaleryFragmentClassCallBack = null;
        Log.d(TAG, "Fragment onDetach");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Fragment onDestroy");
    }

    public static PictureGaleryFragmentClass newInstance(ArrayList<Integer> liste){
        PictureGaleryFragmentClass fragment = new PictureGaleryFragmentClass();
        Bundle args = new Bundle();
        args.putIntegerArrayList(IMG_PATH, liste);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_picturegallery,container,false);
        lPhotoGalery = view.findViewById(R.id.photoGalery);
        mPhoto1 = view.findViewById(R.id.pg_photo1);
        mPhoto2 = view.findViewById(R.id.pg_photo2);
        mPhoto3 = view.findViewById(R.id.pg_photo3);
        return view;
    }

    // ----------------------------------------------------------------------


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d(TAG, "Fragment onStart");

        // On récupère notre liste de photo
        if (getArguments() != null) {
            Bundle args = getArguments();
            if (args.containsKey(IMG_PATH))
                maListe = args.getIntegerArrayList(IMG_PATH);
        }

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

        try{
            if (maListe != null) {
                mPhoto1.setImageResource(maListe.get(numPhoto));// numPhoto = 0
                if (maListe.size() >= 2){
                    lPhotoGalery.setOnTouchListener(new OnSwipeTouchListener(this.getContext()){
                        @Override
                        public void onSwipeRight() {mooveOnRight();}

                        @Override
                        public void onSwipeLeft() {mooveOnLeft();}

                    });
                }
            }
        } catch (Exception e){
            Log.w(TAG, e);
        }

    }

    // ------- Méthode de gestion du glissement du doigt -------

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
            if(numPhoto == -1){
                numPhoto = maListe.size()-1;
            }

            if (!alreadyDone && numPhoto == 0) {
                pictureGaleryFragmentClassCallBack.checkIfPhotoGalleryChecked();
                alreadyDone = true;
            }

            // On récupère nos imageView que on associe
            for(ImageView myImage : listImageView){
                if(myImage.getTag().equals("Left")){
                    imageLeft = myImage;
                } else if(myImage.getTag().equals("Mid")){
                    imageCenter = myImage;
                } else if(myImage.getTag().equals("Right")){
                    imageRight = myImage;
                }
            }

            // On affecte l'image qui va s'afficher
            imageLeft.setImageResource(maListe.get(numPhoto));

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
            if(numPhoto == maListe.size()){
                numPhoto = 0;
                if (!alreadyDone) {
                    pictureGaleryFragmentClassCallBack.checkIfPhotoGalleryChecked();
                    alreadyDone = true;
                }
            }

            // On récupère nos imageView que on associe
            for(ImageView myImage : listImageView){
                if(myImage.getTag().equals("Left")){
                    imageLeft = myImage;
                } else if(myImage.getTag().equals("Mid")){
                    imageCenter = myImage;
                } else if(myImage.getTag().equals("Right")){
                    imageRight = myImage;
                }
            }

            // On affecte l'image qui va s'afficher
            imageRight.setImageResource(maListe.get(numPhoto));

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

        }
    }
}


    // ----------------------------------------------------------------------



