package com.savajolchauvet.testgoogledatastore.bdd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.savajolchauvet.testgoogledatastore.activity.MainActivity;
import com.savajolchauvet.testgoogledatastore.constante.ConstanteMetier;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Maxime on 29/12/2014.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Sictom_bdd";
    private static final String TABLE_TCOORDONNEE = "TCoordonnee";
    private static final String COLUMN_ID = "Id";
    private static final String COLUMN_LAT = "Latitude";
    private static final String COLUMN_LNG = "Longitude";
    private static final String COLUMN_DATE = "Date";

    private static final String[] allTcoordonneeColumns = {COLUMN_ID, COLUMN_LAT, COLUMN_LNG, COLUMN_DATE};


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        StringBuilder createTableQuery = new StringBuilder();

        createTableQuery.append("CREATE TABLE ").append(TABLE_TCOORDONNEE).append("(")
                        .append(COLUMN_ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT,")
                        .append(COLUMN_LAT).append(" DOUBLE NOT NULL,")
                        .append(COLUMN_LNG).append(" DOUBLE NOT NULL,")
                        .append(COLUMN_DATE).append(" STRING NOT NULL );");

        db.execSQL(createTableQuery.toString());
    }

    public void addCoordonnee(double lat, double lng, Date date){
        ContentValues values = new ContentValues();

        values.put(COLUMN_LAT, lat);
        values.put(COLUMN_LNG, lng);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        values.put(COLUMN_DATE, simpleDateFormat.format(date));

        getWritableDatabase().insert(TABLE_TCOORDONNEE, null, values);
    }

    public List<String> getAllCoordonnees() throws ParseException {
        List<String> coords = new ArrayList<>();

        Cursor result = getReadableDatabase().query(TABLE_TCOORDONNEE, allTcoordonneeColumns, null, null, null, null, null);

        while(!result.moveToNext()){
            String coord = result.getDouble(1) + ConstanteMetier.PARAMS_SEPARATOR +
                           result.getDouble(2) + ConstanteMetier.PARAMS_SEPARATOR +
                           result.getString(3);

            coords.add(coord);
        }

        result.close();

        return coords;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
