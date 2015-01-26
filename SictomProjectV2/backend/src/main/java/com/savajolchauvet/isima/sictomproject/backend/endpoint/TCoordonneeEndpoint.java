package com.savajolchauvet.isima.sictomproject.backend.endpoint;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.savajolchauvet.isima.sictomproject.backend.Constante.ConstanteMetier;
import com.savajolchauvet.isima.sictomproject.backend.metier.TCoordonnee;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.inject.Named;

/**
 * An endpoint class we are exposing
 */
@Api(
        name = "tCoordonneeApi",
        version = "v1",
        resource = "tCoordonnee",
        namespace = @ApiNamespace(
                ownerDomain = "endpoint.backend.sictomproject.isima.savajolchauvet.com",
                ownerName = "endpoint.backend.sictomproject.isima.savajolchauvet.com",
                packagePath = ""
        )
)
public class TCoordonneeEndpoint {

    private static final Logger logger = Logger.getLogger(TCoordonneeEndpoint.class.getName());

    private DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

    /**
     * This method gets the <code>TCoordonnee</code> object associated with the specified <code>id</code>.
     *
     * @param id The id of the object to be returned.
     * @return The <code>TCoordonnee</code> associated with <code>id</code>.
     */
    @ApiMethod(name = "getTCoordonnee")
    public TCoordonnee getTCoordonnee(@Named("id") Long id) {
        // TODO: Implement this function
        logger.info("Calling getTCoordonnee method");
        return null;
    }


    @ApiMethod(name = "insertTCoordonnee")
    public TCoordonnee insertTCoordonnee(@Named("id") long id,
                                         @Named("latitude") double latitude,
                                         @Named("longitude") double longitude,
                                         @Named("date") String date) {
        // TODO: Implement this function
        logger.info("Calling insertTCoordonnee method");

        DateFormat df = new SimpleDateFormat(ConstanteMetier.STRING_DATE_FORMAT);

        try {
            Entity myCoord = new Entity(TCoordonnee.TCOORDONNE_ENTITY);
            myCoord.setProperty(TCoordonnee.LATITUDE_PROPERTY, latitude);
            myCoord.setProperty(TCoordonnee.LONGITUDE_PROPERTY, longitude);
            myCoord.setProperty(TCoordonnee.DATE_PROPERTY, df.parse(date));
            ds.put(myCoord);

            return new TCoordonnee(id, latitude, longitude, df.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}