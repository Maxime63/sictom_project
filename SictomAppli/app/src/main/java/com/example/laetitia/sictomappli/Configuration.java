package com.example.laetitia.sictomappli;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;


public class Configuration extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);
        remplirListeCamions();
        remplirListeRippers();
        remplirListeTournee();
        final Button loginButton = (Button) findViewById(R.id.buttonDemarrage);
        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Configuration.this, PositionCourante.class);
                startActivity(intent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_configuration, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void remplirListeCamions()
    {
        Spinner spinnerCamion = (Spinner) findViewById(R.id.spinnerCamion);
        List<String> lCamion = new ArrayList<String>();
        lCamion.add("Camion 1");
        lCamion.add("Camion 2");
        lCamion.add("Camion 3");
        ArrayAdapter<String> adapterChauffeur = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,lCamion);
        adapterChauffeur.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCamion.setAdapter(adapterChauffeur);
    }

    private void remplirListeRippers()
    {
        Spinner spinnerChauffeur = (Spinner) findViewById(R.id.spinnerChauffeur);
        Spinner spinnerRipper1 = (Spinner) findViewById(R.id.spinnerRipper1);
        Spinner spinnerRipper2 = (Spinner) findViewById(R.id.spinnerRipper2);
        List<String> lChauffeur = new ArrayList<String>();
        lChauffeur.add("Marc");
        lChauffeur.add("Gérard");
        lChauffeur.add("Didier");
        ArrayAdapter<String> adapterChauffeur = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,lChauffeur);
        adapterChauffeur.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerChauffeur.setAdapter(adapterChauffeur);
        spinnerRipper1.setAdapter(adapterChauffeur);
        spinnerRipper2.setAdapter(adapterChauffeur);
    }


    private void remplirListeTournee()
    {
        Spinner spinnerTournee = (Spinner) findViewById(R.id.spinnerTournee);
        List<String> lTournee = new ArrayList<String>();
        lTournee.add("Tournée 1");
        lTournee.add("Tournée 2");
        lTournee.add("Tournée 3");
        ArrayAdapter<String> adapterChauffeur = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,lTournee);
        adapterChauffeur.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTournee.setAdapter(adapterChauffeur);
    }

}
