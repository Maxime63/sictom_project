package com.savajolchauvet.isima.sictomproject.backend.endpoint;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
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
            Entity coordEntity = ds.get(key);
            double latitude = (double) coordEntity.getProperty(TCoordonnee.LATITUDE_PROPERTY);
            double longitude = (double) coordEntity.getProperty(TCoordonnee.LONGITUDE_PROPERTY);
            Date date = (Date) coordEntity.getProperty(TCoordonnee.DATE_PROPERTY);
            long tourneeId = (long) coordEntity.getProperty(TCoordonnee.TOURNEE_ID_PROPERTY);

            coord = new TCoordonnee(coordEntity.getKey().getId(), latitude, longitude, date, tourneeId);
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
            double latitude = (double) coord.getProperty(TCoordonnee.LATITUDE_PROPERTY);
            double longitude = (double) coord.getProperty(TCoordonnee.LONGITUDE_PROPERTY);
            Date date = (Date) coord.getProperty(TCoordonnee.DATE_PROPERTY);
            long tourneeId = (long) coord.getProperty(TCoordonnee.TOURNEE_ID_PROPERTY);

            coordonnees.add(new TCoordonnee(coord.getKey().getId(), latitude, longitude, date, tourneeId));
        }

        return coordonnees;
    }


    @ApiMethod(name = "insertCoord",
            path = "coords",
            httpMethod = ApiMethod.HttpMethod.POST)
    public TCoordonnee insertTCoordonnee(@Named("id") long id,
                                         @Named("latitude") double latitude,
                                         @Named("longitude") double longitude,
                                         @Named("date") String date,
                                         @Named("tourneeId") long tourneeId) {
        try {
            Entity myCoord = new Entity(TCoordonnee.TCOORDONNE_ENTITY, KeyFactory.createKey(TTournee.TTOURNEE_ENTITY, tourneeId));
            myCoord.setProperty(TCoordonnee.LATITUDE_PROPERTY, latitude);
            myCoord.setProperty(TCoordonnee.LONGITUDE_PROPERTY, longitude);
            myCoord.setProperty(TCoordonnee.DATE_PROPERTY, df.parse(date));
            myCoord.setProperty(TCoordonnee.TOURNEE_ID_PROPERTY, tourneeId);
            ds.put(myCoord);

            return new TCoordonnee(id, latitude, longitude, df.parse(date), tourneeId);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    @ApiMethod(name = "getCoordsByTournee",
            path = "coordsByTournee/{tourneeId}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public List<TCoordonnee> getCoordsByTournee(@Named("tourneeId") long tourneeId){
        Query query = new Query(TCoordonnee.TCOORDONNE_ENTITY).setAncestor(KeyFactory.createKey(TTournee.TTOURNEE_ENTITY, tourneeId));
        PreparedQuery pq = ds.prepare(query);

        List<TCoordonnee> coords = new ArrayList<>();

        for(Entity coord : pq.asIterable()){
            double latitude = (double) coord.getProperty(TCoordonnee.LATITUDE_PROPERTY);
            double longitude = (double) coord.getProperty(TCoordonnee.LONGITUDE_PROPERTY);
            Date date = (Date) coord.getProperty(TCoordonnee.DATE_PROPERTY);

            coords.add(new TCoordonnee(coord.getKey().getId(), latitude, longitude, date, tourneeId));
        }

        return coords;
    }

    @ApiMethod(
            name = "insertTournee",
            path = "tournees",
            httpMethod = ApiMethod.HttpMethod.POST
    )
    public TTournee insertTTournee(@Named("numero") long numero,
                                   @Named("nom") String nom,
                                   @Named("dateDebut") String dateDebut,
                                   @Named("camionId") long camionId,
                                   @Named("chauffeurId") long chauffeurId,
                                   @Named("firstRipperId") long firstRipperId,
                                   @Named("secondRipperId") long secondRipperId){
        try {
            Entity myTournee = new Entity(TTournee.TTOURNEE_ENTITY);
            myTournee.setProperty(TTournee.NUMERO_PROPERTY, numero);
            myTournee.setProperty(TTournee.NOM_PROPERTY, nom);
            myTournee.setProperty(TTournee.DATE_DEBUT_PROPERTY, df.parse(dateDebut));
            myTournee.setProperty(TTournee.TCAMION_ID_PROPERTY, camionId);
            myTournee.setProperty(TTournee.CHAUFFEUR_ID_PROPERTY, chauffeurId);
            myTournee.setProperty(TTournee.FIRST_RIPPER_ID_PROPERTY, firstRipperId);
            myTournee.setProperty(TTournee.SECOND_RIPPER_ID_PROPERTY, secondRipperId);

            ds.put(myTournee);

            return new TTournee(myTournee.getKey().getId(), nom, numero, df.parse(dateDebut), camionId, chauffeurId, firstRipperId, secondRipperId);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    @ApiMethod(
            name = "updateTournee",
            path = "tournees/{id}",
            httpMethod = ApiMethod.HttpMethod.POST
    )
    public TTournee updateTTournee(@Named ("id") long id,
                                   @Named ("dateFin") String dateFin,
                                   @Named ("chargeCamion") double chargeCamion){
        Key tourneeKey = KeyFactory.createKey(TTournee.TTOURNEE_ENTITY, id);
        try {
            Entity tournee = ds.get(tourneeKey);
            tournee.setProperty(TTournee.DATE_FIN_PROPERTY, df.parse(dateFin));
            tournee.setProperty(TTournee.CHARGE_CAMION_PROPERTY, chargeCamion);

            ds.put(tournee);

            String nom  = (String) tournee.getProperty(TTournee.NOM_PROPERTY);
            long numero  = (long) tournee.getProperty(TTournee.NUMERO_PROPERTY);
            Date dateDebut = (Date) tournee.getProperty(TTournee.DATE_DEBUT_PROPERTY);
            long camionId = (long) tournee.getProperty(TTournee.TCAMION_ID_PROPERTY);
            long chauffeurId = (long) tournee.getProperty(TTournee.CHAUFFEUR_ID_PROPERTY);
            long firstRipperId = (long) tournee.getProperty(TTournee.FIRST_RIPPER_ID_PROPERTY);
            long secondRipperId = (long) tournee.getProperty(TTournee.SECOND_RIPPER_ID_PROPERTY);

            return new TTournee(tournee.getKey().getId(), numero, nom, dateDebut, df.parse(dateFin), camionId, chauffeurId, firstRipperId, secondRipperId, chargeCamion);
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
            return null;
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
            String nom  = (String) tournee.getProperty(TTournee.NOM_PROPERTY);
            long numero  = (long) tournee.getProperty(TTournee.NUMERO_PROPERTY);
            Date dateDebut = (Date) tournee.getProperty(TTournee.DATE_DEBUT_PROPERTY);
            Date dateFin = (Date) tournee.getProperty(TTournee.DATE_FIN_PROPERTY);
            long camionId = (long) tournee.getProperty(TTournee.TCAMION_ID_PROPERTY);
            long chauffeurId = (long) tournee.getProperty(TTournee.CHAUFFEUR_ID_PROPERTY);
            long firstRipperId = (long) tournee.getProperty(TTournee.FIRST_RIPPER_ID_PROPERTY);
            long secondRipperId = (long) tournee.getProperty(TTournee.SECOND_RIPPER_ID_PROPERTY);
            double chargeCamion = (double) tournee.getProperty(TTournee.CHARGE_CAMION_PROPERTY);

            tournees.add(new TTournee(tournee.getKey().getId(), numero, nom, dateDebut, dateFin, camionId, chauffeurId, firstRipperId, secondRipperId, chargeCamion));
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

            String nom  = (String) tourneeEntity.getProperty(TTournee.NOM_PROPERTY);
            long numero = (long) tourneeEntity.getProperty(TTournee.CHARGE_CAMION_PROPERTY);
            Date dateDebut = (Date) tourneeEntity.getProperty(TTournee.DATE_DEBUT_PROPERTY);
            Date dateFin = (Date) tourneeEntity.getProperty(TTournee.DATE_FIN_PROPERTY);
            long camionId = (long) tourneeEntity.getProperty(TTournee.TCAMION_ID_PROPERTY);
            long chauffeurId = (long) tourneeEntity.getProperty(TTournee.CHAUFFEUR_ID_PROPERTY);
            long firstRipperId = (long) tourneeEntity.getProperty(TTournee.FIRST_RIPPER_ID_PROPERTY);
            long secondRipperId = (long) tourneeEntity.getProperty(TTournee.SECOND_RIPPER_ID_PROPERTY);
            double chargeCamion = (double) tourneeEntity.getProperty(TTournee.CHARGE_CAMION_PROPERTY);

            tournee = new TTournee(tourneeEntity.getKey().getId(), numero, nom, dateDebut, dateFin, camionId, chauffeurId, firstRipperId, secondRipperId, chargeCamion);
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }

        return tournee;
    }

    @ApiMethod(name = "getTourneesByCamion",
            path = "tourneesByCamion/{id}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public List<TTournee> getTTourneeByCamion(@Named("id") long id){
        List<TTournee> tournees = new ArrayList<TTournee>();

        Query.Filter camionFilter =
                new Query.FilterPredicate(TTournee.TCAMION_ID_PROPERTY,
                        Query.FilterOperator.EQUAL, id);

        Query q = new Query(TTournee.TTOURNEE_ENTITY).setFilter(camionFilter);

// Use PreparedQuery interface to retrieve results
        PreparedQuery pq = ds.prepare(q);

        for (Entity tourneeEntity : pq.asIterable()) {
            String nom = (String) tourneeEntity.getProperty(TTournee.NOM_PROPERTY);
            long numero = (long) tourneeEntity.getProperty(TTournee.NUMERO_PROPERTY);
            Date dateDebut = (Date) tourneeEntity.getProperty(TTournee.DATE_DEBUT_PROPERTY);
            Date dateFin = (Date) tourneeEntity.getProperty(TTournee.DATE_FIN_PROPERTY);
            long camionId = (long) tourneeEntity.getProperty(TTournee.TCAMION_ID_PROPERTY);
            long chauffeurId = (long) tourneeEntity.getProperty(TTournee.CHAUFFEUR_ID_PROPERTY);
            long firstRipperId = (long) tourneeEntity.getProperty(TTournee.FIRST_RIPPER_ID_PROPERTY);
            long secondRipperId = (long) tourneeEntity.getProperty(TTournee.SECOND_RIPPER_ID_PROPERTY);
            double chargeCamion = (double) tourneeEntity.getProperty(TTournee.CHARGE_CAMION_PROPERTY);

            tournees.add(new TTournee(tourneeEntity.getKey().getId(), numero, nom, dateDebut, dateFin, camionId, chauffeurId, firstRipperId, secondRipperId, chargeCamion));
        }

        return tournees;
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

    @ApiMethod(name = "getUtilisateurByLoginPassword",
            path = "utilisateurs/{login}/{password}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public TUtilisateur getUtilisateurByLoginPassword(@Named("login") String login, @Named("password") String password){
        TUtilisateur utilisateur = null;

        Query.Filter filter = new Query.FilterPredicate(TUtilisateur.LOGIN_PROPERTY, Query.FilterOperator.EQUAL, login);
        Query q = new Query(TUtilisateur.TUTILISATEUR_ENTITY).setFilter(filter);
        PreparedQuery pq = ds.prepare(q);

        for(Entity entity : pq.asIterable()){
            String nom = (String) entity.getProperty(TUtilisateur.NOM_PROPERTY);
            String prenom = (String) entity.getProperty(TUtilisateur.PRENOM_PROPERTY);
            String lgn = (String) entity.getProperty(TUtilisateur.LOGIN_PROPERTY);
            String mdp = (String) entity.getProperty(TUtilisateur.MDP_PROPERTY);

            utilisateur = new TUtilisateur(entity.getKey().getId(), nom, prenom, lgn, mdp);
        }

        return utilisateur;
    }
}