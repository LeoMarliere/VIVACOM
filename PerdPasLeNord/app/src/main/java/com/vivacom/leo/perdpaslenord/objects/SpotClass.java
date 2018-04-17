package com.vivacom.leo.perdpaslenord.objects;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by Leo on 07/09/2017.
 */

public class SpotClass extends RealmObject {

    // Attributs
    private int mSpotId;
    private int mSpotType;
    private String mZoneName;
    private String mSpotName;

    private int mSpotNbInformation;
    private String mSpotInformation1;
    private String mSpotInformation2;
    private String mSpotInformation3;

    private boolean isSpotCompleted;
    private int mSpotPoint;
    private boolean infoChecked;
    private boolean imageChecked;
    private boolean gameChecked;
    private RealmList<Integer> photoGallery;
    private String gameTitle;


    public SpotClass(){}

    // Point de passage -- Type 1
    public SpotClass(int type, int id, String zoneName, String name,  String gameTitle, int nbInfos, String infos1, String infos2, String infos3 , RealmList<Integer> photoGallery){
        this.mSpotType = type;
        this.mSpotId = id;
        this.mZoneName = zoneName;
        this.mSpotName = name;
        this.mSpotNbInformation = nbInfos;
        this.mSpotInformation1 = infos1;
        this.mSpotInformation2 = infos2;
        this.mSpotInformation3 = infos3;
        this.photoGallery = photoGallery;
        this.gameTitle = gameTitle;
        this.isSpotCompleted = false;
        this.infoChecked = false;
        this.gameChecked = false;
        this.imageChecked = false;
    }

    // Point de passage -- Type 1
    public SpotClass(int type, int id, String zoneName, String name,  String gameTitle, int nbInfos, String infos1, String infos2, String infos3 , RealmList<Integer> photoGallery, boolean isInfoCheck, boolean isPhotoCheck, boolean isGameCheck, boolean isSpotCompleted){
        this.mSpotType = type;
        this.mSpotId = id;
        this.mZoneName = zoneName;
        this.mSpotName = name;
        this.mSpotNbInformation = nbInfos;
        this.mSpotInformation1 = infos1;
        this.mSpotInformation2 = infos2;
        this.mSpotInformation3 = infos3;
        this.photoGallery = photoGallery;
        this.gameTitle = gameTitle;
        this.isSpotCompleted = isSpotCompleted;
        this.infoChecked = isInfoCheck;
        this.gameChecked = isGameCheck;
        this.imageChecked = isPhotoCheck;
    }

    // Point Secret -- Type 3
    public SpotClass(int type, int id, String name,  int nbInfos, String infos1, String infos2, String infos3){
        this.mSpotType = type;
        this.mSpotId = id;
        this.mSpotName = name;
        this.mZoneName = "Secret";
        this.mSpotNbInformation = nbInfos;
        this.mSpotInformation1 = infos1;
        this.mSpotInformation2 = infos2;
        this.mSpotInformation3 = infos3;
        this.isSpotCompleted = false;
    }




    // Getter and Setter

    public int getmSpotId() {return mSpotId;}
    public void setmSpotId(int mSpotId) {this.mSpotId = mSpotId;}

    public int getmSpotType() {return mSpotType;}
    public void setmSpotType(int mSpotType) {this.mSpotType = mSpotType;}

    public String getZoneName() {return mZoneName;}
    public void setmZone(String mZoneName) {this.mZoneName = mZoneName;}

    public String getmSpotName() {return mSpotName;}
    public void setmSpotName(String mSpotName) {this.mSpotName = mSpotName;}

    public int getmSpotNbInformation() {return mSpotNbInformation;}
    public void setmSpotNbInformation(int mSpotNbInformation) {this.mSpotNbInformation = mSpotNbInformation;}

    public String getmSpotInformation1() {return mSpotInformation1;}
    public void setmSpotInformation1(String mSpotInformation1) {this.mSpotInformation1 = mSpotInformation1;}

    public String getmSpotInformation2() {return mSpotInformation2;}
    public void setmSpotInformation2(String mSpotInformation2) {this.mSpotInformation2 = mSpotInformation2;}

    public String getmSpotInformation3() {return mSpotInformation3;}
    public void setmSpotInformation3(String mSpotInformation3) {this.mSpotInformation3 = mSpotInformation3;}


    public boolean isSpotCompleted() {return isSpotCompleted;}
    public void setSpotCompleted() {isSpotCompleted = true;}

    public int getmSpotPoint() {return mSpotPoint;}
    public void setmSpotPoint(int mSpotPoint) {this.mSpotPoint = mSpotPoint;}

    public boolean isInfoChecked() {return infoChecked;}
    public void setInfoChecked() {this.infoChecked = true;}

    public boolean isImageChecked() {return imageChecked;}
    public void setImageChecked() {this.imageChecked = true;}

    public boolean isGameChecked() {return gameChecked;}
    public void setGameChecked() {this.gameChecked = true;}

    public String getGameTitle() {return gameTitle;}
    public void setGameTitle(String gameTitle) {this.gameTitle = gameTitle;}

    public String getmZoneName() {return mZoneName;}

    public RealmList<Integer> getPhotoGallery() {return photoGallery;}

    // ---------------------------------------------------------------------------------------


}
