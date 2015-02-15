package com.savajolchauvet.isima.sictomproject.backend.model;

/**
 * Created by Maxime on 27/01/2015.
 */
public class TUtilisateur {
    public static final String TUTILISATEUR_ENTITY = "TUtilisateur";
    public static final String NOM_PROPERTY = "Nom";
    public static final String PRENOM_PROPERTY = "Prenom";
    public static final String LOGIN_PROPERTY = "Login";
    public static final String MDP_PROPERTY = "Mdp";

    private long mId;
    private String mNom;
    private String mPrenom;
    private String mLogin;
    private String mMdp;

    public TUtilisateur(String nom, String prenom, String login, String mdp){
        mNom = nom;
        mPrenom = prenom;
        mLogin = login;
        mMdp = mdp;
    }

    public TUtilisateur(long id, String nom, String prenom, String login, String mdp){
        mId = id;
        mNom = nom;
        mPrenom = prenom;
        mLogin = login;
        mMdp = mdp;
    }

    public long getId(){
        return mId;
    }

    public String getNom(){
        return mNom;
    }

    public String getPrenom(){
        return mPrenom;
    }

    public String getLogin(){
        return mLogin;
    }

    public String getMdp(){
        return mMdp;
    }

    @Override
    public String toString() {
        return mNom + " " + mPrenom;
    }
}
