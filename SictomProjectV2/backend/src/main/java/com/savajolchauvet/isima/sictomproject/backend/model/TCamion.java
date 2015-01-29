package com.savajolchauvet.isima.sictomproject.backend.model;

/**
 * Created by Maxime on 27/01/2015.
 */
public class TCamion {
    public static final String TCAMION_ENTITY = "TCamion";
    public static final String ID_PROPERTY = "Id";
    public static final String NOM_PROPERTY = "Nom";
    public static final String POIDS_COURANT_PROPERTY = "PoidsCourant";
    public static final String POIDS_MAX_PROPERTY = "PoidsMax";

    private long mId;
    private String mNom;
    private double mPoidsCourant;
    private double mPoidsMax;

    public TCamion(String nom, double poidsMax){
        mNom = nom;
        mPoidsMax = poidsMax;
    }

    public TCamion(long id, String nom, double poidsCourant, double poidsMax){
        mId = id;
        mNom = nom;
        mPoidsCourant = poidsCourant;
        mPoidsMax = poidsMax;
    }

    public long getId(){
        return mId;
    }

    public String getNom(){
        return mNom;
    }

    public double getPoidsCourant(){
        return mPoidsCourant;
    }

    public double getPoidsMax(){
        return mPoidsMax;
    }
}
