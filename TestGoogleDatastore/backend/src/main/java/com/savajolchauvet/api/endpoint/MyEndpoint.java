/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Endpoints Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloEndpoints
*/

package com.savajolchauvet.api.endpoint;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.savajolchauvet.api.entity.TCoordonnee;

import javax.inject.Named;

/**
 * An endpoint class we are exposing
 */
@Api(name = "myApi", version = "v1", namespace = @ApiNamespace(ownerDomain = "api.savajolchauvet.com",
                                                               ownerName = "api.savajolchauvet.com",
                                                               packagePath = ""))
public class MyEndpoint {
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

    @ApiMethod(name = "addCoordonnee")
    public TCoordonnee addCoordonnee(@com.google.api.server.spi.config.Named("latitude") double latitude,
                                     @com.google.api.server.spi.config.Named("longitude") double longitude){
        TCoordonnee coordonnee = new TCoordonnee(latitude, longitude);

        Entity entity = new Entity("TCoordonnee");
        entity.setProperty("latitude", latitude);
        entity.setProperty("longitude", longitude);
        entity.setProperty("date", coordonnee.getDate());

        ds.put(entity);

        return coordonnee;
    }
}
