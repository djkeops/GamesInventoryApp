package com.example.android.gamesinventoryapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.gamesinventoryapp.data.GameContract.GameEntry;

/**
 * Displays list of games that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = CatalogActivity.class.getSimpleName();

    /**
     * Identifier for the games data loader
     */
    private static final int GAMES_LOADER = 0;

    GameCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // Find the ListView which will be populated with the games data
        ListView gamesListView = findViewById(R.id.list);

        // TODO: Find and set empty view on the ListView, so that it only shows when the list has 0 items.

        // Setup an Adapter to create a list item for each row of game data in Cursor.
        // There is no game data yet (until the loader finishes) so pass in null for the Cursor
        mCursorAdapter = new GameCursorAdapter(this, null);
        gamesListView.setAdapter(mCursorAdapter);

        // Setup item click listener
        gamesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Create new intent to go to {@link EditorActivity}
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);

                // Form the content URI that represents the specific game that was clicked on,
                // by appending the "id" (passed as input to this method) onto the
                // {@link GameEntry#CONTENT_URI}.
                Uri currentGameUri = ContentUris.withAppendedId(GameEntry.CONTENT_URI, id);

                // Set the URI on the data field of the intent
                intent.setData(currentGameUri);

                // Launch the {@link EditorActivity] to display tha data for the current game.
                startActivity(intent);
            }
        });

        // Kick off the loader
        getLoaderManager().initLoader(GAMES_LOADER, null, this);
    }

    /**
     * Helper method to delete all games in the database.
     */
    private void deleteAllGames() {
        int rowsDeleted = getContentResolver().delete(GameEntry.CONTENT_URI, null, null);
        Log.v(LOG_TAG, rowsDeleted + " rows deleted from games database");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertData();
                return true;
            // Respond to a click on the "Delete All Games" menu option
            case R.id.action_delete_all_entries:
                deleteAllGames();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Helper method to insert dummy game data into the database.
     */
    private void insertData() {

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(GameEntry.COLUMN_GAME_NAME, getString(R.string.tools_game_name));
        values.put(GameEntry.COLUMN_GAME_GENRE, GameEntry.GENRE_ACTION);
        values.put(GameEntry.COLUMN_GAME_PLATFORM, GameEntry.PLATFORM_XBOX_ONE);
        values.put(GameEntry.COLUMN_GAME_PRICE, 19.22);
        values.put(GameEntry.COLUMN_QUANTITY, 5);
        values.put(GameEntry.COLUMN_SUPPLIER_NAME, "2K");
        values.put(GameEntry.COLUMN_SUPPLIER_PHONE, "+1 (000) 000-0000");

        // Insert the new row and receive the new content URI
        Uri newUri = getContentResolver().insert(GameEntry.CONTENT_URI, values);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                GameEntry._ID,
                GameEntry.COLUMN_GAME_NAME,
                GameEntry.COLUMN_GAME_GENRE,
                GameEntry.COLUMN_GAME_PLATFORM,
                GameEntry.COLUMN_GAME_PRICE,
                GameEntry.COLUMN_QUANTITY};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,
                GameEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Update {@link GameCursorAdapter} with this new cursor containing updated games data
        mCursorAdapter.swapCursor(cursor);
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mCursorAdapter.swapCursor(null);
    }
}
