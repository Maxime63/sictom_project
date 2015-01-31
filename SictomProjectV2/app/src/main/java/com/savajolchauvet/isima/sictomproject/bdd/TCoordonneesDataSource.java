package com.savajolchauvet.isima.sictomproject.bdd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.appspot.speedy_baton_840.sictomApi.model.TCoordonnee;
import com.google.api.client.util.DateTime;
import com.savajolchauvet.isima.sictomproject.constante.ConstanteMetier;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by Maxime on 16/01/2015.
 */
public class TCoordonneesDataSource {
    private static final Logger logger = Logger.getLogger(TCoordonneesDataSource.class.getName());

    private static TCoordonneesDataSource mTCoordonneesDataSource = null;

    /*Table TCoordonnee infos*/
    public static final String TABLE_TCOORDONNEE = "TCoordonnee";
    public static final String COLUMN_ID = "Id";
    public static final String COLUMN_LAT = "Latitude";
    public static final String COLUMN_LNG = "Longitude";
    public static final String COLUMN_DATE = "Date";
    public static final String[] ALL_TCOORDONNEES_COLUMNS = {COLUMN_ID, COLUMN_LAT, COLUMN_LNG, COLUMN_DATE};

    //Nombre de coordonnées actuellement dans la base locale
    private int nbCoords;
    public static final int NB_MAX_COORDS = 5;

    private DatabaseHelper dbh;
    private SQLiteDatabase dbw;
    private SQLiteDatabase dbr;

    public boolean isOpen(){
        return ((dbw != null && dbw.isOpen()) || (dbr != null && dbr.isOpen()));
    }

    private TCoordonneesDataSource(Context context){
        dbh = new DatabaseHelper(context);
        this.nbCoords = 0;

        logger.info("TCoordonneesDataSource constructed");
    }

    public static synchronized TCoordonneesDataSource getInstance(Context context){
        if(mTCoordonneesDataSource == null){
            mTCoordonneesDataSource = new TCoordonneesDataSource(context);
        }

        return mTCoordonneesDataSource;
    }

    public void openWrite(){
        dbw = dbh.getWritableDatabase();
        logger.info("Database opened in writable mode");
    }

    public void openRead(){
        dbr = dbh.getReadableDatabase();
        logger.info("Database opened in readable mode");
    }

    public void close(){
        dbh.close();
        logger.info("Database closed");
    }



    public int getNbCoords(){
        return nbCoords;
    }

    public void createCoordonnee(double lat, double lng, Date date){
        logger.info("------------>START INSERT COORD<-------------");
        ContentValues values = new ContentValues();

        values.put(COLUMN_LAT, lat);
        values.put(COLUMN_LNG, lng);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(ConstanteMetier.STRING_DATE_FORMAT);
        values.put(COLUMN_DATE, simpleDateFormat.format(date));

        logger.info("Try to insert coord ==> (" + lat + " ; " + lng + ")");
        dbw.insert(TABLE_TCOORDONNEE, null, values);
        logger.info("Coord inserted ==> (" + lat + " ; " + lng + ")");

        nbCoords++;
        logger.info("Count of coords ==> " + nbCoords);

        logger.info("------------>END INSERT COORD<-----------");
    }

    public void deleteCoordonnee(long id){
        logger.info("------------>START DELETE COORD<-------------");
        logger.info("Try to delete coord ==> " + id);
        int result = dbw.delete(TABLE_TCOORDONNEE, COLUMN_ID + " = " + id, null);

        if(result == 1){
            logger.info("Coord deleted ==> " + id);
            nbCoords--;
        }
        else{
            logger.info("No coord corresponding to ==> " + id);
        }
        logger.info("------------>END DELETE COORD<-------------");
    }

    public List<TCoordonnee> getAllCoordonnees() throws ParseException {
        logger.info("------------>START RETRIEVE ALL COORD<-------------");
        List<TCoordonnee> coords = new ArrayList<>();

        Cursor result = dbr.query(TABLE_TCOORDONNEE, ALL_TCOORDONNEES_COLUMNS, null, null, null, null, null);
        result.moveToFirst();
        while(!result.isAfterLast()){
            DateFormat df = new SimpleDateFormat(ConstanteMetier.STRING_DATE_FORMAT);
            DateTime datetime = new DateTime(df.parse(result.getString(3)));

            logger.info("Create new TCoordonnee");
            TCoordonnee coord = new TCoordonnee();
            coord.setId(result.getLong(0));
            coord.setLatitude(result.getDouble(1));
            coord.setLongitude(result.getDouble(2));
            coord.setDate(datetime);

            logger.info("Add coord to list");
            coords.add(coord);
            result.moveToNext();
        }

        result.close();
        logger.info("------------>END RETRIEVE ALL COORD<-------------");

        return coords;
    }
}
