package com.example.android.gamesinventoryapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.android.gamesinventoryapp.data.GameContract.GameEntry;
import com.example.android.gamesinventoryapp.data.GameDbHelper;

public class CatalogActivity extends AppCompatActivity {

    private GameDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Not yet implemented.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // Instantiate the subclass of SQLiteOpenHelper and pass the context, which is the current activity
        mDbHelper = new GameDbHelper(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_insert_dummy_data) {
            insertData();
            displayDatabaseInfo();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Helper method to insert dummy game data into the database.
     */
    private void insertData() {
        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(GameEntry.COLUMN_GAME_NAME, "Diablo III");
        values.put(GameEntry.COLUMN_GAME_GENRE, GameEntry.GENRE_RPG);
        values.put(GameEntry.COLUMN_GAME_PLATFORM, GameEntry.PLATFORM_XBOX_ONE);
        values.put(GameEntry.COLUMN_GAME_PRICE, 19);
        values.put(GameEntry.COLUMN_QUANTITY, 1);
        values.put(GameEntry.COLUMN_SUPPLIER_NAME, "Blizzard");
        values.put(GameEntry.COLUMN_SUPPLIER_PHONE, "+1 (000) 000-0000");

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(GameEntry.TABLE_NAME, null, values);
        Log.v("CatalogActivity", "New row ID " + newRowId);
    }

    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the games database.
     */
    private void displayDatabaseInfo() {

        // Instantiate the subclass of SQLiteOpenHelper and pass the context, which is the current activity
        GameDbHelper mDbHelper = new GameDbHelper(this);

        // Create and/or open a database to read from it
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                GameEntry._ID,
                GameEntry.COLUMN_GAME_NAME,
                GameEntry.COLUMN_GAME_GENRE,
                GameEntry.COLUMN_GAME_PLATFORM,
                GameEntry.COLUMN_GAME_PRICE,
                GameEntry.COLUMN_QUANTITY,
                GameEntry.COLUMN_SUPPLIER_NAME,
                GameEntry.COLUMN_SUPPLIER_PHONE
        };

        Cursor cursor = db.query(
                GameEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        // Find the TextView where the query result will be displayed
        TextView resultView = findViewById(R.id.temporary_result_view);

        try {
            String countText = getString(R.string.result_before_count) + cursor.getCount() + getString(R.string.result_after_count);
            resultView.setText(countText);
            resultView.append(GameEntry._ID + " - " +
                    GameEntry.COLUMN_GAME_NAME + " - " +
                    GameEntry.COLUMN_GAME_GENRE + " - " +
                    GameEntry.COLUMN_GAME_PLATFORM + " - " +
                    GameEntry.COLUMN_GAME_PRICE + " - " +
                    GameEntry.COLUMN_QUANTITY + " - " +
                    GameEntry.COLUMN_SUPPLIER_NAME + " - " +
                    GameEntry.COLUMN_SUPPLIER_PHONE + "\n");

            // Identify the index of each column
            int idColumnIndex = cursor.getColumnIndex(GameEntry._ID);
            int gameNameColumnIndex = cursor.getColumnIndex(GameEntry.COLUMN_GAME_NAME);
            int gameGenreColumnIndex = cursor.getColumnIndex(GameEntry.COLUMN_GAME_GENRE);
            int gamePlatformColumnIndex = cursor.getColumnIndex(GameEntry.COLUMN_GAME_PLATFORM);
            int gamePriceColumnIndex = cursor.getColumnIndex(GameEntry.COLUMN_GAME_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(GameEntry.COLUMN_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(GameEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(GameEntry.COLUMN_SUPPLIER_PHONE);

            // For each row in the cursor display the values
            while (cursor.moveToNext()) {
                // Extract the values from the current row
                int currentId = cursor.getInt(idColumnIndex);
                String currentGameName = cursor.getString(gameNameColumnIndex);
                int currentGameGenre = cursor.getInt(gameGenreColumnIndex);
                int currentGamePlatform = cursor.getInt(gamePlatformColumnIndex);
                int currentGamePrice = cursor.getInt(gamePriceColumnIndex);
                int currentQuantity = cursor.getInt(quantityColumnIndex);
                String currentSupplierName = cursor.getString(supplierNameColumnIndex);
                String currentSupplierPhone = cursor.getString(supplierPhoneColumnIndex);

                // Display in the resultView the values for each column
                resultView.append(("\n" + currentId + " - " +
                        currentGameName + " - " +
                        currentGameGenre + " - " +
                        currentGamePlatform + " - " +
                        currentGamePrice + " - " +
                        currentQuantity + " - " +
                        currentSupplierName + " - " +
                        currentSupplierPhone));
            }
        } finally {
            // Close the cursor
            cursor.close();
        }
    }

}
