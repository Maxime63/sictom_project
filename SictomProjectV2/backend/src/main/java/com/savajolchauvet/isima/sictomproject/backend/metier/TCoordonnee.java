package com.savajolchauvet.isima.sictomproject.backend.metier;

import java.util.Date;

/**
 * Created by Maxime on 20/01/2015.
 */
public class TCoordonnee {
    public static final String TCOORDONNE_ENTITY = "TCoordonnee";
    public static final String LATITUDE_PROPERTY = "Latitude";
    public static final String LONGITUDE_PROPERTY = "Longitude";
    public static final String DATE_PROPERTY = "Date";

    private long mId;
    private double mLatitude;
    private double mLongitude;
    private Date mDate;

    public TCoordonnee(long id, double latitude, double longitude, Date date){
        mId = id;
        mLatitude = latitude;
        mLongitude = longitude;
        mDate = date;
    }

    public TCoordonnee(double latitude, double longitude, Date date){
        mLatitude = latitude;
        mLongitude = longitude;
        mDate = date;
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

    @Override
    public String toString() {
        return "Coordonnées : (" + mLatitude + " ; " + mLongitude + ")";
    }
}
