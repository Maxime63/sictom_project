package com.example.laetitia.sictominterfaces;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;


public class Configuration extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_configuration);
        Spinner spinnerChauffeur = (Spinner) findViewById(R.id.spinnerChauffeur);
        List<String> lChauffeur = new ArrayList<String>();
        lChauffeur.add("Dédé");
        lChauffeur.add("Gérard");
        ArrayAdapter<String> dataAdapterR = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,lChauffeur);
        dataAdapterR.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerChauffeur.setAdapter(dataAdapterR);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_configuration, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void remplirListeCamions()
    {

    }

    public void remplirListesRippers()
    {

    }

    public void remplirListeTournéees()
    {

    }

}
