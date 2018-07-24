package com.vivacom.leo.perdpaslenord.objects;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.List;
import java.util.Timer;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by Leo on 07/09/2017.
 * Classe chargée de la création et de la gestion des objets "Team"
 * L'objet Team permettera d'enregistrer les informations concernant l'équipe de joueurs
 */

public class TeamClass extends RealmObject {

    // Attributs
    private String mTeamName;
    private RealmList<String> membersList;
    private int numInsigne;
    private RealmList<Integer> list_DiscoverWord;
    private RealmList<Integer> list_UnDiscoverWord;
    private RealmList<byte[]> list_photos;
    private int stats_NbSpot1;
    private int stats_NbSpot2;
    private int stats_NbSpot3;
    private int stats_NbZones;
    private int stats_NbMetre;
    private int stats_NbVictoire;


    public TeamClass(){
        // Empty
    }

    // Constructeur
    public TeamClass(String name, RealmList<String> list, int numInsigne){
        this.mTeamName = name;
        this.membersList = list;
        this.numInsigne = numInsigne;
        this.list_DiscoverWord = new RealmList<>();
        this.list_UnDiscoverWord = new RealmList<>();
        this.list_photos = new RealmList<>();
        this.stats_NbSpot1 = 0;
        this.stats_NbSpot2 = 0;
        this.stats_NbSpot3 = 0;
        this.stats_NbVictoire = 0;
        this.stats_NbZones = 0;
        this.stats_NbMetre = 0;
    }

    // Getter and Setter
    // ---------------------------------------------------------------------------------------
    public String getmTeamName() {return mTeamName;}
    public RealmList<String> getMembersList() {
        return membersList;
    }
    public int getNumInsinge() {return numInsigne;}

    public RealmList<Integer> getList_DiscoverWord() {
        return list_DiscoverWord;
    }
    public void setList_DiscoverWord(RealmList<Integer> list_DiscoverWord) {
        this.list_DiscoverWord = list_DiscoverWord;
    }

    public int getStats_NbSpot1() {
        return stats_NbSpot1;
    }
    public void setStats_NbSpot1(int stats_NbSpot1) {
        this.stats_NbSpot1 = stats_NbSpot1;
    }

    public int getStats_NbSpot2() {
        return stats_NbSpot2;
    }
    public void setStats_NbSpot2(int stats_NbSpot2) {
        this.stats_NbSpot2 = stats_NbSpot2;
    }

    public int getStats_NbSpot3() {
        return stats_NbSpot3;
    }
    public void setStats_NbSpot3(int stats_NbSpot3) {
        this.stats_NbSpot3 = stats_NbSpot3;
    }

    public int getStats_NbZones() {
        return stats_NbZones;
    }
    public void setStats_NbZones(int stats_NbZones) {
        this.stats_NbZones = stats_NbZones;
    }

    public int getStats_NbMetre() {
        return stats_NbMetre;
    }
    public void setStats_NbMetre(int stats_NbMetre) {
        this.stats_NbMetre = stats_NbMetre;
    }

    public int getStats_NbVictoire() {
        return stats_NbVictoire;
    }
    public void setStats_NbVictoire(int stats_NbVictoire) {
        this.stats_NbVictoire = stats_NbVictoire;
    }

    public RealmList<Integer> getList_UnDiscoverWord() {
        return list_UnDiscoverWord;
    }
    public void setList_UnDiscoverWord(RealmList<Integer> list_UnDiscoverWord) {
        this.list_UnDiscoverWord = list_UnDiscoverWord;
    }

    public RealmList<byte[]> getList_photos() {
        return list_photos;
    }
    public void setList_photos(RealmList<byte[]> list_photos) {
        this.list_photos = list_photos;
    }

    // ---------------------------------------------------------------------------------------
}
