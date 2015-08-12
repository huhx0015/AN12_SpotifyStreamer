package com.huhx0015.spotifystreamer.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * -------------------------------------------------------------------------------------------------
 * [SSTracksDatabase] CLASS
 * PROGRAMMER: Michael Yoon Huh (HUHX0015)
 * DESCRIPTION: This is a class that is responsible for initializing, creating, and updating the
 * favorite tracks database table.
 * -------------------------------------------------------------------------------------------------
 */

public class SSTracksDatabase extends SQLiteOpenHelper {

    /** CLASS VARIABLES ________________________________________________________________________ **/

    // LOGGING VARIABLES
    private static final String LOG_TAG = SSTracksDatabase.class.getName();

    // TABLE VARIABLES
    private static final int DATABASE_VERSION = 1; // DATABASE VERSION
    private static final String DATABASE_NAME = "ss_tracks_shortcuts.db"; // DATABASE FILE NAME
    public static final String TABLE_SHORTCUTS = "ss_tracks_shortcuts"; // TABLE NAME
    public static final String COLUMN_ID = "_id"; // SHORTCUT ID
    public static final String COLUMN_NAME = "name"; // SHORTCUT NAME
    public static final String COLUMN_ADDRESS = "address"; // SHORTCUT ADDRESS
    public static final String COLUMN_TYPE = "type"; // SHORTCUT TYPE
    public static final String COLUMN_IMAGE_ID = "image_id"; // SHORTCUT IMAGE

    // SQL VARIABLES: SQL database creation statement.
    private static final String DATABASE_CREATE = "create table "+ TABLE_SHORTCUTS + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_NAME + " text not null,"
            + COLUMN_ADDRESS + " text not null,"
            + COLUMN_TYPE + " text not null,"
            + COLUMN_IMAGE_ID + " integer value);";

    /** INITIALIZATION FUNCTIONALITY ___________________________________________________________ **/

    // SSTracksDatabase(): Initializes the SSTracksDatabase class.
    public SSTracksDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /** SQL LITE OPEN HELPER FUNCTIONALITY _____________________________________________________ **/

    // onCreate(): This method is responsible for creating the database.
    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE); // SQL: Creates a new database table.
    }

    // onUpgrade(): This method is responsible for upgrading the database version.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.w(LOG_TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", "
                + "which will destroy all previous data.");

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SHORTCUTS); // SQL: Drops existing database table.
        onCreate(db); // Creates a new database table.
    }
}