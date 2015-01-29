package com.savajolchauvet.isima.sictomproject.backend.model;

import java.util.Date;

/**
 * Created by Maxime on 27/01/2015.
 */
public class TTournee {
    public static final String TTOURNEE_ENTITY = "TTournee";
    public static final String ID_PROPERTY = "Id";
    public static final String NOM_PROPERTY = "Nom";
    public static final String DATE_DEBUT_PROPERTY = "DateDebut";
    public static final String DATE_FIN_PROPERTY = "DateFin";
    public static final String TCAMION_ID_PROPERTY = "TCamionId";
    public static final String CHAUFFEUR_ID_PROPERTY = "ChauffeurId";
    public static final String FIRST_RIPPER_ID_PROPERTY = "FirstRipperId";
    public static final String SECOND_RIPPER_ID_PROPERTY = "SecondRipperId";

    private long mId;
    private String mNom;
    private Date mDateDebut;
    private Date mDateFin;
    private long mTCamionId;
    private long mChauffeurId;
    private long mFirstRipperId;
    private long mSecondRipperId;

    public TTournee(String nom, Date dateDebut, long tCamionId, long chauffeurId, long firstRipperId, long secondRipperId){
        mNom = nom;
        mDateDebut = dateDebut;
        mTCamionId = tCamionId;
        mChauffeurId = chauffeurId;
        mFirstRipperId = firstRipperId;
        mSecondRipperId = secondRipperId;
    }

    public TTournee(long id, String nom, Date dateDebut, Date dateFin, long tCamionId, long chauffeurId, long firstRipperId, long secondRipperId){
        mId = id;
        mNom = nom;
        mDateDebut = dateDebut;
        mDateFin = dateFin;
        mTCamionId = tCamionId;
        mChauffeurId = chauffeurId;
        mFirstRipperId = firstRipperId;
        mSecondRipperId = secondRipperId;
    }

    public long getId(){
        return mId;
    }

    public String getNom(){
        return mNom;
    }

    public Date getDateDebut(){
        return mDateDebut;
    }

    public Date getDateFin(){
        return mDateFin;
    }

    public long getTCamionId(){
        return mTCamionId;
    }

    public long getChauffeurId(){
        return mChauffeurId;
    }

    public long getFirstRipperId(){
        return mFirstRipperId;
    }

    public long getSecondRipperId(){
        return mSecondRipperId;
    }
}
