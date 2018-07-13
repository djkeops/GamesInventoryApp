package com.example.android.gamesinventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.gamesinventoryapp.data.GameContract.GameEntry;

/**
 * ContentProvider for Game Inventory App
 */
public class GameProvider extends ContentProvider {

    /** Tag for the log messages */
    public static final String LOG_TAG = GameProvider.class.getSimpleName();

    /** URI matcher code for the content URI for the games table */
    private static final int GAMES = 100;

    /** URI matcher code for the content URI for a single game from the games table */
    private static final int GAME_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The content URI of the form "com.example.android.gamesinventoryapp/games" will map to the
        // integer code {@link #GAMES}. This URI is used to provide access to MULTIPLE rows
        // of the games table.
        sUriMatcher.addURI(GameContract.CONTENT_AUTHORITY, GameContract.PATH_GAMES, GAMES);

        // In this case, the "#" wildcard is used where "#" can be substituted for an integer.
        // For example: "com.example.android.gamesinventoryapp/games/3"
        sUriMatcher.addURI(GameContract.CONTENT_AUTHORITY, GameContract.PATH_GAMES + "/#", GAME_ID);
    }

    /** Database helper object */
    private GameDbHelper mDbHelper;

    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        // Initialize a PetDbHelper object to gain access to the games database.
        mDbHelper = new GameDbHelper(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        //Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        //This cursor hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case GAMES:
                // For the GAMES code, query the games table directly
                cursor = database.query(GameEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case GAME_ID:
                // For the GAME_ID code, extract out the ID from the URI
                selection = GameEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri))};

                // This will perform a query on the pets table where the _id is the ID parsed from the URI
                cursor = database.query(GameEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        // Set the notificationUri on the cursor to know when data at this URI changed, to update the Cursor
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the cursor
        return cursor;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case GAMES:
                return insertGame(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a game into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertGame(Uri uri, ContentValues values){
        // Check that the name is not null
        String name = values.getAsString(GameEntry.COLUMN_GAME_NAME);
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Game requires a name");
        }

        // Check that the genre is valid
        Integer genre = values.getAsInteger(GameEntry.COLUMN_GAME_GENRE);
        if (genre == null || !GameEntry.isValidGenre(genre)) {
            throw new IllegalArgumentException("Game requires a valid genre");
        }

        // Check that the platform is valid
        Integer platform = values.getAsInteger(GameEntry.COLUMN_GAME_PLATFORM);
        if (platform == null || !GameEntry.isValidPlatform(platform)) {
            throw new IllegalArgumentException("Game requires a valid platform");
        }

        // Check that the price is valid
        Double price = values.getAsDouble(GameEntry.COLUMN_GAME_PRICE);
        if (price == null || price < 0) {
            throw new IllegalArgumentException("Game requires a valid price");
        }

        // Check that the quantity is valid
        Integer quantity = values.getAsInteger(GameEntry.COLUMN_QUANTITY);
        if (quantity == null || quantity < 0) {
            throw new IllegalArgumentException("Game requires a valid quantity");
        }

        // Check that the suppliername is valid
        String supplierName = values.getAsString(GameEntry.COLUMN_SUPPLIER_NAME);
        if (supplierName == null || supplierName.isEmpty()) {
            throw new IllegalArgumentException("Games requires a supplier name");
        }

        // No need to check the supplier phone, any value is valid (including null).

        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new game with the given values
        long id  = database.insert(GameEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the game content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case GAMES:
                return updateGame(uri, contentValues, selection, selectionArgs);
            case GAME_ID:
                // For the GAME_ID code, extract out the ID from the URI,
                selection = GameEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return updateGame(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateGame(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If the {@link GameEntry#COLUMN_GAME_NAME} key is present, check that the name is not null
        if (values.containsKey(GameEntry.COLUMN_GAME_NAME)) {
            String name = values.getAsString(GameEntry.COLUMN_GAME_NAME);
            if (name == null || name.isEmpty()) {
                throw new IllegalArgumentException("Game requires a name");
            }
        }

        // If the {@link GameEntry#COLUMN_GAME_GENRE} key is present, check that the genre is valid
        if (values.containsKey(GameEntry.COLUMN_GAME_GENRE)) {
            //
            Integer genre = values.getAsInteger(GameEntry.COLUMN_GAME_GENRE);
            if (genre == null || !GameEntry.isValidGenre(genre)) {
                throw new IllegalArgumentException("Game requires a valid genre");
            }
        }

        // If the {@link GameEntry#COLUMN_GAME_PLATFORM} key is present, check that the platform is valid
        if (values.containsKey(GameEntry.COLUMN_GAME_PLATFORM)) {
            Integer platform = values.getAsInteger(GameEntry.COLUMN_GAME_PLATFORM);
            if (platform == null || !GameEntry.isValidPlatform(platform)) {
                throw new IllegalArgumentException("Game requires a valid platform");
            }
        }

        // If the {@link GameEntry#COLUMN_GAME_PRICE} key is present, check that the price is valid
        if (values.containsKey(GameEntry.COLUMN_GAME_PRICE)) {
            Double price = values.getAsDouble(GameEntry.COLUMN_GAME_PRICE);
            if (price == null || price < 0) {
                throw new IllegalArgumentException("Game requires a valid price");
            }
        }

        // If the {@link GameEntry#COLUMN_QUANTITY} key is present, check that the quantity is valid
        if (values.containsKey(GameEntry.COLUMN_QUANTITY)) {
            Integer quantity = values.getAsInteger(GameEntry.COLUMN_QUANTITY);
            if (quantity == null || quantity < 0) {
                throw new IllegalArgumentException("Game requires a valid quantity");
            }
        }

        // If the {@link GameEntry#COLUMN_SUPPLIER_NAME} key is present, check that the suppliername is valid
        if (values.containsKey(GameEntry.COLUMN_SUPPLIER_NAME)) {
            String supplierName = values.getAsString(GameEntry.COLUMN_SUPPLIER_NAME);
            if (supplierName == null || supplierName.isEmpty()) {
                throw new IllegalArgumentException("Games requires a supplier name");
            }
        }

        // No need to check the supplier phone, any value is valid (including null).

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(GameEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows updated
        return rowsUpdated;
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Track the number of rows that were deleted
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case GAMES:
                rowsDeleted = database.delete(GameEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case GAME_ID:
                // Delete a single row given by the ID in the URI
                selection = GameEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(GameEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows deleted
        return rowsDeleted;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case GAMES:
                return GameEntry.CONTENT_LIST_TYPE;
            case GAME_ID:
                return GameEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
