package com.vivacom.leo.perdpaslenord.fragments;

/**
 * Created by Leo on 08/11/2017.
 */


/*
 * Copyright (c) 2014 Rex St. John on behalf of AirPair.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;

import com.vivacom.leo.perdpaslenord.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Take a picture directly from inside the app using this fragment.
 *
 * Reference: http://developer.android.com/training/camera/cameradirect.html
 * Reference: http://stackoverflow.com/questions/7942378/android-camera-will-not-work-startpreview-fails
 * Reference: http://stackoverflow.com/questions/10913181/camera-preview-is-not-restarting
 *
 * Created by Rex St. John (on behalf of AirPair.com) on 3/4/14.
 */
public class NativeCameraFragment extends Fragment {

    // Native camera.
    private Camera mCamera;
    // View to display the camera output.
    private CameraPreview mPreview;
    // Reference to the containing view.
    private View mCameraView;
    private View actualView;
    // Notre object qui revupere la photo
    File pictureFile = null;
    // Notre photo
    byte[] photoTake;

    int numPhoto = 0;

    public final String TAG = "NATIVE_PHOTO";
    public static final String ARG_SECTION_NUMBER = "ARG_SECTION_NUMBER";


    Button captureButton, cancelButton, registerButton;
    FrameLayout preview;

    // CallBack
    NativeCameraFragmentCallBack nativeCameraFragmentCallBack;

    // ---------------------------------------------------------------

    /**
     * Default empty constructor.
     */
    public NativeCameraFragment(){
        super();
    }

    public interface NativeCameraFragmentCallBack{
        void addPhotoToGalery(Bitmap bmc);
        void whenGameIsValidate(Boolean correct);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof NativeCameraFragment.NativeCameraFragmentCallBack)
            nativeCameraFragmentCallBack = (NativeCameraFragment.NativeCameraFragmentCallBack) activity;
        Log.d(TAG, "onAttach");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        nativeCameraFragmentCallBack = null;
        if (mCamera != null) {
            // Call stopPreview() to stop updating the preview surface.
            mCamera.stopPreview();
            stopPreviewAndFreeCamera();
        }
        Log.i("APPAREIL_PHOTO", "onDetach");


    }

    /**
     * Static factory method
     * @param sectionNumber
     * @return
     */
    public static NativeCameraFragment newInstance(int sectionNumber) {
        NativeCameraFragment fragment = new NativeCameraFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onPause() {
        super.onPause();
        releaseCameraAndPreview();
        Log.d(TAG, "Fragment onPause");
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.d(TAG, "Fragment onResume");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseCameraAndPreview();
        captureButton.setVisibility(View.GONE);
        cancelButton.setVisibility(View.GONE);
        registerButton.setVisibility(View.GONE);
        Log.d(TAG, "Fragment onDestroy");
    }

    // ---------------------------------------------------------------


    /**
     * OnCreateView fragment override
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_native_camera, container, false);

        Log.d(TAG,"OnCreateView");

        captureButton = view.findViewById(R.id.button_capture);
        cancelButton = view.findViewById(R.id.button_cancel);
        registerButton = view.findViewById(R.id.button_register);

        preview =  view.findViewById(R.id.camera_preview);

        captureButton.setVisibility(View.VISIBLE);
        cancelButton.setVisibility(View.GONE);
        registerButton.setVisibility(View.GONE);

        // On récupère notre instance Camera pour l'utiliser plus tard
        actualView = view;
        boolean opened = safeCameraOpenInView(actualView);

        // Si on arrive pas a récuperer notre object Camera : false
        if(!opened){
            Log.d(TAG,"Error, Camera failed to open");
            return view;
        }

        createListener();

        return view;
    }
    // ----

    /**
     * Cette méthode créer les différents listener de nos boutons
     */
    public void createListener(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Trap the capture button.
                captureButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // get an image from the camera
                        mCamera.takePicture(null, null, mPicture);
                    }
                });

                registerButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       onRegister();
                    }
                });

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onCancel();
                    }
                });
            }
        }).start();
    }

    /**
     * Cette méthode permet de sécurisé l'ouverture de la caméra
     * (Problème lorsque la camera est deja ouverte par une autre application)
     * @param view
     * @return
     */
    private boolean safeCameraOpenInView(View view) {
        final boolean qOpened;

        /*

        */

        releaseCameraAndPreview();
        mCamera = getCameraInstance();

        mCameraView = view;
        qOpened = (mCamera != null);

        // Si on arrive a recuperer notre objet camera : true
        if(qOpened){
            mPreview = new CameraPreview(getActivity().getBaseContext(), mCamera,view);
            preview.addView(mPreview);
            mPreview.startCameraPreview();
        }

        return qOpened;

    }
    // ----
    /**
     * Cette methode renvoie une instance de notre objet Camera
     * @return
     */
    public static Camera getCameraInstance(){
        Camera c = null;

        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){e.printStackTrace();}

        return c; // returns null if camera is unavailable
    }
    // ----
    /**
     * Supprime toutes les instance des objets Camera et Preview qui peuvent exister
     */
    private void releaseCameraAndPreview() {
        // Camera
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        // Preview
        if(mPreview != null){
            mPreview.destroyDrawingCache();
            mPreview.mCamera = null;
        }
    }

    /**
     * When this function returns, mCamera will be null.
     */
    private void stopPreviewAndFreeCamera() {

        if (mCamera != null) {
            // Call stopPreview() to stop updating the preview surface.
            mCamera.stopPreview();

            // Important: Call release() to release the camera for use by other
            // applications. Applications should release the camera immediately
            // during onPause() and re-open() it during onResume()).
            mCamera.release();

            mCamera = null;
        }
    }

    /**
     * Cette méthode va créer une ImageView a artir du byte[] passé en parametre
     * Puis elle va l'envoyer vers le RoadBook
     * @param data
     */
    private void createAndSendImageView( final byte[] data ){
        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
        Log.i(TAG,"ImageView créer et envoyer");
        nativeCameraFragmentCallBack.addPhotoToGalery(bmp);
    }

    /**
     * Cette méthode s'occupe d'envoyé la photo au RoadBook
     * Ensuite, elle redemare la preview (normalement)
     */
    private void onRegister(){
        captureButton.setVisibility(View.VISIBLE);
        registerButton.setVisibility(View.GONE);
        cancelButton.setVisibility(View.GONE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                createAndSendImageView(photoTake);
                mPreview.startCameraPreview();
            }
        }).start();

        nativeCameraFragmentCallBack.whenGameIsValidate(true);

    }

    /**
     * Cette méthode réinitialise la preview (normalement)
     */
    private void onCancel(){
        captureButton.setVisibility(View.VISIBLE);
        registerButton.setVisibility(View.GONE);
        cancelButton.setVisibility(View.GONE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                mPreview.startCameraPreview();
            }
        }).start();


    }

    // ------------------------------------------------------------------------------------------------------------------------

    /**
     * Surface on which the camera projects it's capture results. This is derived both from Google's docs and the
     * excellent StackOverflow answer provided below.
     */
    class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

        // SurfaceHolder
        private SurfaceHolder mHolder;
        // Our Camera.
        private Camera mCamera;
        // Parent Context.
        private Context mContext;
        // Camera Sizing (For rotation, orientation changes)
        private Camera.Size mPreviewSize;
        // List of supported preview sizes
        private List<Camera.Size> mSupportedPreviewSizes;
        // Flash modes supported by this camera
        private List<String> mSupportedFlashModes;
        // View holding this camera.
        private View mCameraView;

        /**
         * Constructor
         * @param context
         * @param camera
         * @param cameraView
         */
        public CameraPreview(Context context, Camera camera, View cameraView) {
            super(context);
            // Capture the context
            mCameraView = cameraView;
            mContext = context;
            setCamera(camera);

            // Install a SurfaceHolder.Callback so we get notified when the
            // underlying surface is created and destroyed.
            mHolder = getHolder();
            mHolder.addCallback(this);
            mHolder.setKeepScreenOn(true);
        }

        /**
         * Begin the preview of the camera input.
         */
        public void startCameraPreview() {
            try{
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }

        /**
         * Extract supported preview and flash modes from the camera.
         * @param camera
         */
        private void setCamera(Camera camera) {
            // Source: http://stackoverflow.com/questions/7942378/android-camera-will-not-work-startpreview-fails
            mCamera = camera;
            mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
            mSupportedFlashModes = mCamera.getParameters().getSupportedFlashModes();

            // Set the camera to Auto Flash mode.
            if (mSupportedFlashModes != null && mSupportedFlashModes.contains(Camera.Parameters.FLASH_MODE_AUTO)){
                Camera.Parameters parameters = mCamera.getParameters();
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                mCamera.setParameters(parameters);
            }

            requestLayout();
        }

        /**
         * The Surface has been created, now tell the camera where to draw the preview.
         * @param holder
         */
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                mCamera.setPreviewDisplay(holder);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * Dispose of the camera preview.
         * @param holder
         */
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (mCamera != null){
                mCamera.stopPreview();
            }
        }

        /**
         * React to surface changed events
         * @param holder
         * @param format
         * @param w
         * @param h
         */
        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
            // If your preview can change or rotate, take care of those events here.
            // Make sure to stop the preview before resizing or reformatting it.

            if (mHolder.getSurface() == null){
                // preview surface does not exist
                return;
            }

            // stop preview before making changes
            try {
                Camera.Parameters parameters = mCamera.getParameters();
                parameters.flatten();
                mPreviewSize = parameters.getPreviewSize();


                // Set the auto-focus mode to "continuous"
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);

                // Preview size must exist.
                if(mPreviewSize != null) {
                    Camera.Size previewSize = mPreviewSize;
                    parameters.setPreviewSize(previewSize.width, previewSize.height);
                }

                //mCamera.setParameters(parameters);
                mCamera.startPreview();
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        /**
         * Calculate the measurements of the layout
         * @param widthMeasureSpec
         * @param heightMeasureSpec
         */
        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            // Source: http://stackoverflow.com/questions/7942378/android-camera-will-not-work-startpreview-fails
            final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
            final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
            setMeasuredDimension(width, height);

            if (mSupportedPreviewSizes != null){
                mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width, height);
            }
        }

        /**
         * Update the layout based on rotation and orientation changes.
         * @param changed
         * @param left
         * @param top
         * @param right
         * @param bottom
         */
        @Override
        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            if (changed) {
                final int width = right - left;
                final int height = bottom - top;

                int previewWidth = width;
                int previewHeight = height;

                if (mPreviewSize != null){
                    Display display = ((WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

                    switch (display.getRotation())
                    {
                        case Surface.ROTATION_0:
                            previewWidth = mPreviewSize.height;
                            previewHeight = mPreviewSize.width;
                            mCamera.setDisplayOrientation(90);
                            break;
                        case Surface.ROTATION_90:
                            previewWidth = mPreviewSize.width;
                            previewHeight = mPreviewSize.height;
                            break;
                        case Surface.ROTATION_180:
                            previewWidth = mPreviewSize.height;
                            previewHeight = mPreviewSize.width;
                            break;
                        case Surface.ROTATION_270:
                            previewWidth = mPreviewSize.width;
                            previewHeight = mPreviewSize.height;
                            mCamera.setDisplayOrientation(180);
                            break;
                    }
                }

                final int scaledChildHeight = previewHeight * width / previewWidth;
                mCameraView.layout(0, height - scaledChildHeight, width, height);
            }
        }

        /**
         * Modifie la taille de la preview
         * @param sizes
         * @param width
         * @param height
         * @return
         */
        private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int width, int height) {
            // Source: http://stackoverflow.com/questions/7942378/android-camera-will-not-work-startpreview-fails
            Camera.Size optimalSize = null;

            final double ASPECT_TOLERANCE = 0.1;
            double targetRatio = (double) height / width;

            // Try to find a size match which suits the whole screen minus the menu on the left.
            for (Camera.Size size : sizes){

                if (size.height != width) continue;
                double ratio = (double) size.width / size.height;
                if (ratio <= targetRatio + ASPECT_TOLERANCE && ratio >= targetRatio - ASPECT_TOLERANCE){
                    optimalSize = size;
                }
            }

            // If we cannot find the one that matches the aspect ratio, ignore the requirement.
            if (optimalSize == null) {
                // TODO : Backup in case we don't get a size.
            }

            return optimalSize;
        }
    }

    /**
     * Picture Callback for handling a picture capture and saving it out to a file.
     */
    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(final byte[] data, Camera camera) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // On récupère la photo
                    pictureFile = getOutputMediaFile();

                    // Si elle est null = probleme
                    if (pictureFile == null){
                        //Toast.makeText(getActivity(), "Image retrieval failed.", Toast.LENGTH_SHORT).show();
                        Log.w(TAG,  "Image retrieval failed.");
                        return;
                    }

                    // Sinon
                    try {
                        // On récupère la photo
                        // On ouvre le Stream
                        FileOutputStream fos = new FileOutputStream(pictureFile);
                        // On enregistre la photo pis on ferme le stream
                        fos.write(data);
                        fos.close();


                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }).start();

            photoTake = data;
            captureButton.setVisibility(View.GONE);
            cancelButton.setVisibility(View.VISIBLE);
            registerButton.setVisibility(View.VISIBLE);

        }
    };


    /**
     * Used to return the camera File output.
     * @return
     */
    private File getOutputMediaFile(){

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "PerdPasLeNord");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Required media storage does not exist");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss").format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "IMG_"+ timeStamp + ".jpg");

        // MediaFile correspond au fichier créer avec le direction + nom + date + photo
        return mediaFile;



    }



}