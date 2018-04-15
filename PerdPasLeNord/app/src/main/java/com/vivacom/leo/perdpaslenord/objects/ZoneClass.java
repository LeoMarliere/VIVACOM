package com.vivacom.leo.perdpaslenord.objects;

import io.realm.RealmObject;

/**
 * Created by Leo on 07/09/2017.
 * Classe chargée de la création et de la gestion des objets "Zone"
 * L'objet Zone permettera d'enregistrer les informations concernant une zone de la ville
 */

public class ZoneClass extends RealmObject {

    // Attributs
    private int mZoneId;
    private String mZoneName;
    private int mZoneNbSpots;
    private boolean mZoneCompleted;
    private int mZonePoint;

    public ZoneClass(){}

    // Constructor
    public ZoneClass(int id,String name, int nbSpot, int zonePoint){
        this.mZoneId = id;
        this.mZoneName = name;
        this.mZoneNbSpots = nbSpot;
        this.mZoneCompleted = false;
        this.mZonePoint = zonePoint;
    }

    public ZoneClass(int id,String name, int nbSpot, int zonePoint, boolean isZoneCompleted){
        this.mZoneId = id;
        this.mZoneName = name;
        this.mZoneNbSpots = nbSpot;
        this.mZoneCompleted = isZoneCompleted;
        this.mZonePoint = zonePoint;
    }


    // Getter and Setter
    // ---------------------------------------------------------------------------------------
    public int getmZoneId() {return mZoneId;}
    public void setmZoneId(int mZoneId) {this.mZoneId = mZoneId;}
    public String getmZoneName() {return mZoneName;}
    public void setmZoneName(String mZoneName) {this.mZoneName = mZoneName;}
    public int getmZoneNbSpots() {return mZoneNbSpots;}
    public void setmZoneNbSpots(int mZoneNbSpots) {this.mZoneNbSpots = mZoneNbSpots;}
    public boolean ismZoneCompleted() {return mZoneCompleted;}
    public void setmZoneCompleted() {this.mZoneCompleted = true;}
    public int getmZonePoint() {return mZonePoint;}
    public void setmZonePoint(int mZonePoint) {this.mZonePoint = mZonePoint;}

    // ---------------------------------------------------------------------------------------



}
