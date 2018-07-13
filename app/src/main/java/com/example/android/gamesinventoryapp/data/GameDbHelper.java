package com.example.android.gamesinventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.gamesinventoryapp.data.GameContract.GameEntry;

public class GameDbHelper extends SQLiteOpenHelper {

    /**
     * Name of the database file
     */
    private static final String DATABASE_NAME = "inventory.db";

    /**
     * Database version.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link GameDbHelper}.
     *
     * @param context of the app
     */
    public GameDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the games table
        String SQL_CREATE_GAMES_TABLE = "CREATE TABLE " + GameEntry.TABLE_NAME + " ("
                + GameEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + GameEntry.COLUMN_GAME_NAME + " TEXT NOT NULL, "
                + GameEntry.COLUMN_GAME_GENRE + " INTEGER NOT NULL, "
                + GameEntry.COLUMN_GAME_PLATFORM + " INTEGER NOT NULL, "
                + GameEntry.COLUMN_GAME_PRICE + " REAL NOT NULL DEFAULT 0, "
                + GameEntry.COLUMN_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + GameEntry.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL, "
                + GameEntry.COLUMN_SUPPLIER_PHONE + " TEXT);";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_GAMES_TABLE);
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // Nothing to do here for now
    }

}
