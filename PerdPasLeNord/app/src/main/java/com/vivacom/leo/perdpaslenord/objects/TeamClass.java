package com.vivacom.leo.perdpaslenord.objects;

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

    public TeamClass(){}

    // Constructeur
    public TeamClass(String name, RealmList<String> list){
        this.mTeamName = name;
        this.membersList = list;
        numInsigne = 1;
    }

    // Constructeur
    public TeamClass(String name, RealmList<String> list, int numInsigne){
        this.mTeamName = name;
        this.membersList = list;
        this.numInsigne = numInsigne;
    }

    // Getter and Setter
    // ---------------------------------------------------------------------------------------
    public String getmTeamName() {return mTeamName;}

    public RealmList<String> getMembersList() {
        return membersList;
    }

    public int getNumInsinge() {return numInsigne;}

    // ---------------------------------------------------------------------------------------
}
