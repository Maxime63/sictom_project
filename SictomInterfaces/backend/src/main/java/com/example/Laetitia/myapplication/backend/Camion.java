package com.example.Laetitia.myapplication.backend;

/**
 * The object model for the data we are sending through endpoints
 */
public class Camion {

    private long id;
    private String nom;

    public Camion(long num, String n)
    {
        id =  num;
        nom = n;
    }

    public String getNom() {
        return nom;
    }

    public void setData(String data) {
        nom = data;
    }
}