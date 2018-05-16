package com.vivacom.leo.perdpaslenord.objects;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.vivacom.leo.perdpaslenord.R;
import com.vivacom.leo.perdpaslenord.constant.ConstantLatLng;

import io.realm.RealmObject;

/**
 * Created by Leo on 07/03/2018.
 */

public class MarkerOptionRealm extends RealmObject {


    private int id;
    private String zoneName;
    private double positionX;
    private double positionY;
    private String titre;
    private int iconPath;
    private boolean isCompleted;

    public MarkerOptionRealm(){}

    public MarkerOptionRealm(int id, String zoneName, double posX, double posY, String titre, int path ){
        this.id = id;
        this.zoneName = zoneName;
        this.positionX = posX;
        this.positionY = posY;
        this.titre = titre;
        this.iconPath = path;
        this.isCompleted = false;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getZoneName() {return zoneName;}
    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }

    public double getPositionX() {
        return positionX;
    }
    public void setPositionX(double positionX) {
        this.positionX = positionX;
    }

    public double getPositionY() {
        return positionY;
    }
    public void setPositionY(double positionY) {
        this.positionY = positionY;
    }

    public String getTitre() {
        return titre;
    }
    public void setTitre(String titre) {
        this.titre = titre;
    }

    public int getIconPath() {
        return iconPath;
    }
    public void setIconPath(int iconPath) {
        this.iconPath = iconPath;
    }

    public boolean isCompleted() {
        return isCompleted;
    }
    public void setCompleted() {
        this.isCompleted = true;
    }
}
