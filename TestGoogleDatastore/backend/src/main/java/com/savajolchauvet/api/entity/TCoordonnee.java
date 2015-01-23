package com.savajolchauvet.api.entity;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiNamespace;

import java.util.Date;

/**
 * Created by Maxime on 01/12/2014.
 */
public class TCoordonnee {
    private int mId;
    private double mLatitude;
    private double mLongitude;
    private Date mDate;

    public TCoordonnee() {
    }

    public TCoordonnee(double latitude, double longitude) {
        this.mLatitude = latitude;
        this.mLongitude = longitude;
        mDate = new Date(System.currentTimeMillis());
    }

    public TCoordonnee(int id, double latitude, double longitude, Date date) {
        this.mId = id;
        this.mLatitude = latitude;
        this.mLongitude = longitude;
        this.mDate = date;
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
