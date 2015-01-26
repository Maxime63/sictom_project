package com.savajolchauvet.isima.bdd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.savajolchauvet.isima.constante.ConstanteMetier;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by Maxime on 12/01/2015.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final Logger logger = Logger.getLogger(DatabaseHelper.class.getName());

    /*Database infos*/
    public static final String DATABASE_NAME = "SictomDB.db";
    private static final int DATABASE_VERSION = 1;


    /*SQL for create TCoordonnee table*/
    private static final String CREATE_TABLE_TCOORDONNEE_SQL = "CREATE TABLE " + TCoordonneesDataSource.TABLE_TCOORDONNEE +
            "(" + TCoordonneesDataSource.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + TCoordonneesDataSource.COLUMN_LAT + " DOUBLE NOT NULL," +
            TCoordonneesDataSource.COLUMN_LNG + " DOUBLE NOT NULL," + TCoordonneesDataSource.COLUMN_DATE + " STRING NOT NULL );";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        logger.info("DatabaseHelper constructed");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        logger.info("Create DB ==> " + CREATE_TABLE_TCOORDONNEE_SQL);
        db.execSQL(CREATE_TABLE_TCOORDONNEE_SQL);
        logger.info("DB created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.setVersion(newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + TCoordonneesDataSource.TABLE_TCOORDONNEE);
        logger.info("Upgrade database");
        onCreate(db);
    }
}
