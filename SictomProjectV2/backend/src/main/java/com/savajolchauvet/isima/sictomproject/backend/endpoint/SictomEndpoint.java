package com.savajolchauvet.isima.sictomproject.backend.endpoint;

import com.google.api.server.spi.Constant;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.savajolchauvet.isima.sictomproject.backend.Constante.ConstanteMetier;
import com.savajolchauvet.isima.sictomproject.backend.model.TCamion;
import com.savajolchauvet.isima.sictomproject.backend.model.TCoordonnee;
import com.savajolchauvet.isima.sictomproject.backend.model.TTournee;
import com.savajolchauvet.isima.sictomproject.backend.model.TUtilisateur;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.inject.Named;

/**
 * An endpoint class we are exposing
 */
@Api(
        name = "sictomApi",
        version = "v1"
)
public class SictomEndpoint {

    private static final Logger logger = Logger.getLogger(SictomEndpoint.class.getName());
    private static final DateFormat df = new SimpleDateFormat(ConstanteMetier.STRING_DATE_FORMAT);

    private DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

    /**
     * This method gets the <code>TCoordonnee</code> object associated with the specified <code>id</code>.
     *
     * @param id The id of the object to be returned.
     * @return The <code>TCoordonnee</code> associated with <code>id</code>.
     */
    @ApiMethod(name = "getCoords",
               path = "coords/{id}",
               httpMethod = ApiMethod.HttpMethod.GET)
    public TCoordonnee getTCoordonnee(@Named("id") Long id) {
        TCoordonnee coord = null;

        Key key = KeyFactory.createKey(TCoordonnee.TCOORDONNE_ENTITY, id);

        try {
            try {
                Entity coordEntity = ds.get(key);
                double latitude = (double) coordEntity.getProperty(TCoordonnee.LATITUDE_PROPERTY);
                double longitude = (double) coordEntity.getProperty(TCoordonnee.LONGITUDE_PROPERTY);
                Date dateDebut = df.parse((String) coordEntity.getProperty(TCoordonnee.DATE_PROPERTY));
                long tourneeId = (long) coordEntity.getProperty(TCoordonnee.TOURNEE_ID_PROPERTY);

                coord = new TCoordonnee(coordEntity.getKey().getId(), latitude, longitude, dateDebut, tourneeId);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }

        return coord;
    }

    @ApiMethod(name = "getAllCoords",
            path = "coords",
            httpMethod = ApiMethod.HttpMethod.GET)
    public List<TCoordonnee> getAllTCoordonnees(){
        List<TCoordonnee> coordonnees = new ArrayList<>();

        Query q = new Query(TCoordonnee.TCOORDONNE_ENTITY);
        PreparedQuery pq = ds.prepare(q);

        for(Entity coord : pq.asIterable()){
            try {
                double latitude = (double) coord.getProperty(TCoordonnee.LATITUDE_PROPERTY);
                double longitude = (double) coord.getProperty(TCoordonnee.LONGITUDE_PROPERTY);
                Date dateDebut = df.parse((String) coord.getProperty(TCoordonnee.DATE_PROPERTY));
                long tourneeId = (long) coord.getProperty(TCoordonnee.TOURNEE_ID_PROPERTY);

                coordonnees.add(new TCoordonnee(coord.getKey().getId(), latitude, longitude, dateDebut, tourneeId));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return coordonnees;
    }


    @ApiMethod(name = "insertCoord",
            path = "coords",
            httpMethod = ApiMethod.HttpMethod.POST)
    public TCoordonnee insertTCoordonnee(@Named("id") long id,
                                         @Named("latitude") double latitude,
                                         @Named("longitude") double longitude,
                                         @Named("date") String date) {
        try {
            Entity myCoord = new Entity(TCoordonnee.TCOORDONNE_ENTITY);
            myCoord.setProperty(TCoordonnee.LATITUDE_PROPERTY, latitude);
            myCoord.setProperty(TCoordonnee.LONGITUDE_PROPERTY, longitude);
            myCoord.setProperty(TCoordonnee.DATE_PROPERTY, df.parse(date));
            ds.put(myCoord);

            return new TCoordonnee(id, latitude, longitude, df.parse(date), 0);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    @ApiMethod(name = "getAllCamions",
            path = "camions",
            httpMethod = ApiMethod.HttpMethod.GET)
    public List<TCamion> getAllTCamions(){
        List<TCamion> camions = new ArrayList<>();

        Query q = new Query(TCamion.TCAMION_ENTITY);
        PreparedQuery pq = ds.prepare(q);

        for(Entity camion : pq.asIterable()){
            String nom = (String) camion.getProperty(TCamion.NOM_PROPERTY);
            double poidsCourant = (double) camion.getProperty(TCamion.POIDS_COURANT_PROPERTY);
            double poidsMax = (double) camion.getProperty(TCamion.POIDS_MAX_PROPERTY);

            camions.add(new TCamion(camion.getKey().getId(), nom, poidsCourant, poidsMax));
        }

        return camions;
    }

    @ApiMethod(name = "getCamion",
            path = "camions/{id}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public TCamion getTCamion(@Named("id") long id){
        TCamion camion = null;

        Key key = KeyFactory.createKey(TCamion.TCAMION_ENTITY, id);
        try {
            Entity camionEntity = ds.get(key);
            String nom = (String) camionEntity.getProperty(TCamion.NOM_PROPERTY);
            double poidsCourant = (double) camionEntity.getProperty(TCamion.POIDS_COURANT_PROPERTY);
            double poidsMax = (double) camionEntity.getProperty(TCamion.POIDS_MAX_PROPERTY);

            camion = new TCamion(camionEntity.getKey().getId(), nom, poidsCourant, poidsMax);
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }

        return camion;
    }

    @ApiMethod(name = "getAllTournees",
            path = "tournees",
            httpMethod = ApiMethod.HttpMethod.GET)
    public List<TTournee> getAllTournees(){
        List<TTournee> tournees = new ArrayList<>();

        Query q = new Query(TTournee.TTOURNEE_ENTITY);
        PreparedQuery pq = ds.prepare(q);

        for(Entity tournee : pq.asIterable()){
            try {
                String nom  = (String) tournee.getProperty(TTournee.NOM_PROPERTY);
                Date dateDebut = df.parse((String) tournee.getProperty(TTournee.DATE_DEBUT_PROPERTY));
                Date dateFin = df.parse((String) tournee.getProperty(TTournee.DATE_DEBUT_PROPERTY));
                long camionId = (long) tournee.getProperty(TTournee.TCAMION_ID_PROPERTY);
                long chauffeurId = (long) tournee.getProperty(TTournee.CHAUFFEUR_ID_PROPERTY);
                long firstRipperId = (long) tournee.getProperty(TTournee.FIRST_RIPPER_ID_PROPERTY);
                long secondRipperId = (long) tournee.getProperty(TTournee.SECOND_RIPPER_ID_PROPERTY);

                tournees.add(new TTournee(tournee.getKey().getId(), nom, dateDebut, dateFin, camionId, chauffeurId, firstRipperId, secondRipperId));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return tournees;
    }

    @ApiMethod(name = "getTournee",
            path = "tournees/{id}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public TTournee getTTournee(@Named("id") long id){
        TTournee tournee = null;

        Key key = KeyFactory.createKey(TTournee.TTOURNEE_ENTITY, id);

        try {
            Entity tourneeEntity = ds.get(key);

            try {
                String nom  = (String) tourneeEntity.getProperty(TTournee.NOM_PROPERTY);
                Date dateDebut = df.parse((String) tourneeEntity.getProperty(TTournee.DATE_DEBUT_PROPERTY));
                Date dateFin = df.parse((String) tourneeEntity.getProperty(TTournee.DATE_DEBUT_PROPERTY));
                long camionId = (long) tourneeEntity.getProperty(TTournee.TCAMION_ID_PROPERTY);
                long chauffeurId = (long) tourneeEntity.getProperty(TTournee.CHAUFFEUR_ID_PROPERTY);
                long firstRipperId = (long) tourneeEntity.getProperty(TTournee.FIRST_RIPPER_ID_PROPERTY);
                long secondRipperId = (long) tourneeEntity.getProperty(TTournee.SECOND_RIPPER_ID_PROPERTY);

                tournee = new TTournee(tourneeEntity.getKey().getId(), nom, dateDebut, dateFin, camionId, chauffeurId, firstRipperId, secondRipperId);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String nom  = (String) tourneeEntity.getProperty(TTournee.NOM_PROPERTY);

        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }

        return tournee;
    }

    @ApiMethod(name = "getAllUtilisateurs",
            path = "utilisateurs",
            httpMethod = ApiMethod.HttpMethod.GET)
    public List<TUtilisateur> getAllTUtilisateurs(){
        List<TUtilisateur> utilisateurs = new ArrayList<>();
        Query q = new Query(TUtilisateur.TUTILISATEUR_ENTITY);
        PreparedQuery pq = ds.prepare(q);

        for(Entity utilisateur : pq.asIterable()){
            String nom = (String) utilisateur.getProperty(TUtilisateur.NOM_PROPERTY);
            String prenom = (String) utilisateur.getProperty(TUtilisateur.PRENOM_PROPERTY);
            String login = (String) utilisateur.getProperty(TUtilisateur.LOGIN_PROPERTY);
            String mdp = (String) utilisateur.getProperty(TUtilisateur.MDP_PROPERTY);

            utilisateurs.add(new TUtilisateur(utilisateur.getKey().getId(), nom, prenom, login, mdp));
        }
        return utilisateurs;
    }

    @ApiMethod(name = "getUtilisateur",
            path = "utilisateurs/{id}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public TUtilisateur getTUtilisateur(@Named("id") long id){
        TUtilisateur utilisateur = null;

        Key key = KeyFactory.createKey(TUtilisateur.TUTILISATEUR_ENTITY, id);

        try {
            Entity utilisateurEntity = ds.get(key);
            String nom = (String) utilisateurEntity.getProperty(TUtilisateur.NOM_PROPERTY);
            String prenom = (String) utilisateurEntity.getProperty(TUtilisateur.PRENOM_PROPERTY);
            String login = (String) utilisateurEntity.getProperty(TUtilisateur.LOGIN_PROPERTY);
            String mdp = (String) utilisateurEntity.getProperty(TUtilisateur.MDP_PROPERTY);

            utilisateur = new TUtilisateur(utilisateurEntity.getKey().getId(), nom, prenom, login, mdp);
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }

        return utilisateur;
    }
}