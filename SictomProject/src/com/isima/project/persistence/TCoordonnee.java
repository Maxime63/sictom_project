package com.isima.project.persistence;

import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(identityType=IdentityType.APPLICATION)
public class TCoordonnee {
	
	public TCoordonnee(long id, double longitude, double latitude,
			Date date_enregistrement) 
	{
		this.id = id;
		this.longitude = longitude;
		this.latitude = latitude;
		this.date_enregistrement = date_enregistrement;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public Date getDate_enregistrement() {
		return date_enregistrement;
	}

	public void setDate_enregistrement(Date date_enregistrement) {
		this.date_enregistrement = date_enregistrement;
	}

	@Persistent
	private double longitude;
	
	@Persistent
	private double latitude;
	
	@Persistent
	private Date date_enregistrement;
	
	@PrimaryKey
	@Persistent(valueStrategy=IdGeneratorStrategy.IDENTITY)
	private long id;
	
	public TCoordonnee() {
		
	}

}
