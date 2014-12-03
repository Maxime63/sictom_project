package com.savajolchauvet.api.entity;

import java.util.Date;

/**
 * Created by Maxime on 01/12/2014.
 */
public class TCoordonnee {
    private double mLatitude;
    private double mLongitude;
    private Date mDate;

    public TCoordonnee(double latitude, double longitude) {
        this.mLatitude = latitude;
        this.mLongitude = longitude;
        mDate = new Date(System.currentTimeMillis());
    }

    public double getLatitude(){
        return mLatitude;
    }

    public double getLongitude(){
        return mLongitude;
    }

    public Date getDate() { return mDate; }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("Date : ").append(mDate).append(" - ").append("(").append(mLatitude).append(";").append(mLongitude).append(")");
        return result.toString();
    }
}
