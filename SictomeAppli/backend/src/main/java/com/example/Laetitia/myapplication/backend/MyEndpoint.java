/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Endpoints Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloEndpoints
*/

package com.example.Laetitia.myapplication.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;

/**
 * An endpoint class we are exposing
 */
@Api(name = "myApi", version = "v1", namespace = @ApiNamespace(ownerDomain = "backend.myapplication.Laetitia.example.com", ownerName = "backend.myapplication.Laetitia.example.com", packagePath = ""))
public class MyEndpoint {
    DatastoreService datastoreService;
    /**
     * A simple endpoint method that takes a name and says Hi back
     */
    @ApiMethod(name = "getListeCamions")
    public List<Camion> getAllCamions(@Named("name") String name) {

        Query q = new Query(ConstantesMetier.CAMION_ENTITY);
        PreparedQuery pq = datastoreService.prepare(q);

        List<Camion> camions = new ArrayList<>();

        for (Entity c : pq.asIterable()) {
            String nom = (String) c.getProperty(ConstantesMetier.CAMION_ENTITY_NOM);
            Camion newCam = new Camion(c.getKey().getId(), nom);
            camions.add(newCam);
        }
        return camions;
    }

}
