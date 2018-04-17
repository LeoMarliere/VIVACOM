package com.vivacom.leo.perdpaslenord;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.Marker;

/**
 * Created by Leo on 22/11/2017.
 * Cette class contient toutes les méthodes d'animation de View
 * Ainsi, il suffit de créer une instance de cette classe pour pouvoir appeler les méthodes
 */

public class ViewAnimations {


    public ViewAnimations(){
        // Empty Constructor
    }


    // ------------ Méthodes -------------

    /**
     * Cette méthode fait clignotter la View passé en paramètre
     * @param view
     */
    public void classicBlink(View view){
        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(900);
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        view.startAnimation(anim);
    }

    /**
     * Cette méthode stop l'animation de la view passé en parametre
     * Elle la fait ensuite disparaitre
     * @param view
     */
    public void stopAnimation(View view){
        view.clearAnimation();
        view.setVisibility(View.GONE);
    }

    /**
     * Cette méthode fait disparaitre la View passé en paramètre
     * @param view
     */
    public void fadeOutAnimation(final View view){
        final Animation out = new AlphaAnimation(1.0f, 0.0f);
        out.setDuration(750);

        out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


        view.startAnimation(out);

    }

    /**
     * Cette méthode fait apparaître la View passé en paramètre
     * @param view
     */
    public void fadeInAnimation(final View view){
        final Animation in = new AlphaAnimation(0.0f, 1.0f);
        in.setDuration(750);

        in.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }
            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.VISIBLE);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        view.setVisibility(View.INVISIBLE);
        view.startAnimation(in);
    }


    /**
     * Animation de fade In pour les markers
     * @param marker
     */
    public void fadeInMarkerAnimation(final Marker marker){
        Animator animator = ObjectAnimator.ofFloat(marker, "alpha", 0f, 1f);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
                marker.setVisible(true);
            }
            @Override public void onAnimationStart(Animator animator) {}
            @Override public void onAnimationCancel(Animator animator) {}
            @Override public void onAnimationRepeat(Animator animator) {}
        });
        animator.setDuration(1000).start();
    }

    /**
     * Animation de fade Out pour les markers
     * @param marker
     */
    public void fadeOutMarkerAnimation(final Marker marker){
        Animator animator = ObjectAnimator.ofFloat(marker, "alpha", 1f, 0f);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
                marker.remove();
            }
            @Override public void onAnimationStart(Animator animator) {}
            @Override public void onAnimationCancel(Animator animator) {}
            @Override public void onAnimationRepeat(Animator animator) {}
        });
        animator.setDuration(750).start();
    }

    /**
     * Cette méthode fait disparaitre notre view1 pour ensuite faire apparaitre notre view2
     * @param view1
     * @param view2
     */
    public void fadeOutFadeInAnimation(final View view1, final View view2){
        final Animation out = new AlphaAnimation(1.0f, 0.0f);
        out.setDuration(750);
        final Animation in = new AlphaAnimation(0.0f, 1.0f);
        in.setDuration(750);

        out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }
            @Override
            public void onAnimationEnd(Animation animation) {
                view1.setVisibility(View.GONE);
                fadeInAnimation(view2);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        view1.startAnimation(out);
    }





    /**
     * Animation d'une view qui grossi et rapetisse en boucle la view
     * @param view
     */
    public void changeSizeAnimation ( View view) {
        Animation mAnimation;
        mAnimation = new ScaleAnimation(0.3f,0.5f,0.3f,0.5f,Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.45f);
        mAnimation.setDuration(300);
        mAnimation.setRepeatCount(-1);
        mAnimation.setRepeatMode(Animation.REVERSE);
        mAnimation.setInterpolator(new AccelerateInterpolator());
        mAnimation.setAnimationListener(new Animation.AnimationListener(){

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        view.setAnimation(mAnimation);
    }


    /**
     *  Cette méthode va sortir une vue par la gauche et la faire re-entrer par la droite
     */
    public void slideOutAndIn (final View view){
        view.setVisibility(View.VISIBLE);
        view.animate().translationX(-1000).withLayer().setDuration(500);

        Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setVisibility(View.INVISIBLE);
                view.animate().translationX(1000).withLayer().setDuration(100);
            }
        }, 600);

        Handler handler2 = new Handler();
        handler2.postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setVisibility(View.VISIBLE);
                view.animate().translationX(0).withLayer().setDuration(500);
            }
        }, 700);
    }


    /**
     *  Cette méthode va sortir une vue par la gauche et la faire re-entrer par la droite
     */
    public void slideOutAndInWithMessage (final TextView view, final String message) {
        view.setVisibility(View.VISIBLE);
        view.animate().translationX(-1000).withLayer().setDuration(500);

        Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setVisibility(View.INVISIBLE);
                view.animate().translationX(1000).withLayer().setDuration(100);
            }
        }, 600);

        Handler handler2 = new Handler();
        handler2.postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setVisibility(View.VISIBLE);
                view.setText(message);
                view.animate().translationX(0).withLayer().setDuration(500);
            }
        }, 700);
    }









}

