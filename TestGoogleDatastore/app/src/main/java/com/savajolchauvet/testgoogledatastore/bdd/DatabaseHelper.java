package com.savajolchauvet.testgoogledatastore.bdd;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Maxime on 29/12/2014.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "";
    private static final String TABLE_TCOORDONNEE= "TCoordonnee";
    private static final String COLUMN_ID= "Id";
    private static final String COLUMN_LAT= "Latitude";
    private static final String COLUMN_LNG= "Longitude";
    private static final String COLUMN_DATE= "Date";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        StringBuilder createTableQuery = new StringBuilder();

        createTableQuery.append("CREATE TABLE ").append(TABLE_TCOORDONNEE).append(" ( ")
                        .append(COLUMN_ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
                        .append(COLUMN_LAT).append(" DOUBLE NOT NULL, ")
                        .append(COLUMN_LNG).append(" DOUBLE NOT NULL, ")
                        .append(COLUMN_DATE).append(" STRING NOT NULL");

        db.execSQL(createTableQuery.toString());
    }

    public void addCoordonnee(double lat, double lng, Date date){
        ContentValues values = new ContentValues();

        values.put(COLUMN_LAT, lat);
        values.put(COLUMN_LNG, lng);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        values.put(COLUMN_DATE, simpleDateFormat.format(date));

        getWritableDatabase().insert(TABLE_TCOORDONNEE, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
