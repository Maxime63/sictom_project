package com.savajolchauvet.isima.sictomproject.backend.model;

import java.util.Date;

/**
 * Created by Maxime on 20/01/2015.
 */
public class TCoordonnee {
    public static final String TCOORDONNE_ENTITY = "TCoordonnee";
    public static final String LATITUDE_PROPERTY = "Latitude";
    public static final String LONGITUDE_PROPERTY = "Longitude";
    public static final String DATE_PROPERTY = "Date";
    public static final String TOURNEE_ID_PROPERTY = "TourneeId";

    private long mId;
    private double mLatitude;
    private double mLongitude;
    private Date mDate;
    private long mTourneeId;

    public TCoordonnee(long id, double latitude, double longitude, Date date, long tourneeId){
        mId = id;
        mLatitude = latitude;
        mLongitude = longitude;
        mDate = date;
        mTourneeId = tourneeId;
    }

    public TCoordonnee(double latitude, double longitude, Date date, long tourneeId){
        mLatitude = latitude;
        mLongitude = longitude;
        mDate = date;
        mTourneeId = tourneeId;
    }

    public long getId(){
        return mId;
    }

    public double getLatitude(){
        return mLatitude;
    }

    public double getLongitude(){
        return mLongitude;
    }

    public Date getDate(){
        return mDate;
    }

    public long getTourneeId(){
        return mTourneeId;
    }

    @Override
    public String toString() {
        return "Coordonnées : (" + mLatitude + " ; " + mLongitude + ")";
    }
}
