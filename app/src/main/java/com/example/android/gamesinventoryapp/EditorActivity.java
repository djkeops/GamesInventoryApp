package com.example.android.gamesinventoryapp;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.gamesinventoryapp.data.GameContract.GameEntry;

import java.text.NumberFormat;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Identifier for the game data loader
     */
    private static final int EXISTING_GAME_LOADER = 0;

    /**
     * Content URI for the existing game (null if it's a new game)
     */
    private Uri mCurrentGameUri;

    /**
     * EditText field to enter the game name
     */
    private EditText mNameEditText;

    /**
     * Spinner for the game genre
     */
    private Spinner mGenreSpinner;

    /**
     * Spinner for the game platform
     */
    private Spinner mPlatformSpinner;

    /**
     * EditText field to enter the game price
     */
    private EditText mPriceEditText;

    /**
     * EditText field to enter the stock quantity
     */
    private EditText mStockEditText;

    /**
     * EditText field to enter the name of the game provider
     */
    private EditText mProviderNameEditText;

    /**
     * EditText field to enter the phone number of the game provider
     */
    private EditText mProviderPhoneEditText;

    /**
     * Game genre with the default value
     */
    private int mGenre = GameEntry.GENRE_UNKNOWN;

    /**
     * Game platform with the default value
     */
    private int mPlatform = GameEntry.PLATFORM_PC;

    /**
     * Boolean flag that keeps track of whether the current game has been edited (true) or not (false)
     */
    private boolean mGameHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mGameHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new game or editing an existing one.
        Intent intent = getIntent();
        mCurrentGameUri = intent.getData();

        // If the intent DOES NOT contain a game content URI, then we know that we are creating a new game.
        if (mCurrentGameUri == null) {
            // This is a new game, so change the app bar to say "Add a new Game"
            setTitle(getString(R.string.editor_activity_title_new_game));

            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            invalidateOptionsMenu();
        } else {
            // Otherwise this is an existing game, so change app bar to say "Edit Game"
            setTitle(getString(R.string.editor_activity_title_edit_game));

            // Initialize a loader to read the game data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_GAME_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mNameEditText = findViewById(R.id.edit_game_name);
        mGenreSpinner = findViewById(R.id.spinner_genre);
        mPlatformSpinner = findViewById(R.id.spinner_platform);
        mPriceEditText = findViewById(R.id.edit_game_price);
        mStockEditText = findViewById(R.id.edit_game_stock);
        mProviderNameEditText = findViewById(R.id.edit_game_provider_name);
        mProviderPhoneEditText = findViewById(R.id.edit_game_provider_phone);

        // Find the increase stock button
        ImageButton mIncreaseStockButton = findViewById(R.id.plus_stock);
        // Find the decrease stock button
        ImageButton mDecreaseStockButton = findViewById(R.id.minus_stock);

        // Setting the onClickListener for mIncreaseStockButton
        mIncreaseStockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentStock = mStockEditText.getText().toString();
                if (TextUtils.isEmpty(currentStock)) {
                    mStockEditText.setText(String.valueOf(1));
                } else if (Integer.parseInt(currentStock) == 99) {
                    Toast.makeText(EditorActivity.this, getString(R.string.increment_error_message), Toast.LENGTH_SHORT).show();
                } else {
                    int currentStockInt = Integer.parseInt(currentStock);
                    mStockEditText.setText(String.valueOf(currentStockInt + 1));
                }
            }
        });

        // Setting the onClickListener for mDecreaseStockButton
        mDecreaseStockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentStock = mStockEditText.getText().toString();
                if (TextUtils.isEmpty(currentStock)) {
                    mStockEditText.setText(String.valueOf(0));
                } else if (Integer.parseInt(currentStock) == 0) {
                    Toast.makeText(EditorActivity.this, getString(R.string.decrement_error_message), Toast.LENGTH_SHORT).show();
                } else {
                    int currentStockInt = Integer.parseInt(currentStock);
                    mStockEditText.setText(String.valueOf(currentStockInt - 1));
                }
            }
        });

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mNameEditText.setOnTouchListener(mTouchListener);
        mGenreSpinner.setOnTouchListener(mTouchListener);
        mPlatformSpinner.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mStockEditText.setOnTouchListener(mTouchListener);
        mProviderNameEditText.setOnTouchListener(mTouchListener);
        mProviderPhoneEditText.setOnTouchListener(mTouchListener);
        mIncreaseStockButton.setOnTouchListener(mTouchListener);
        mDecreaseStockButton.setOnTouchListener(mTouchListener);

        setupGenreSpinner();
        setupPlatformSpinner();
    }

    /**
     * Setup the dropdown spinner that allows the user to select the game genre.
     */
    private void setupGenreSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter genreSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_genre_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        genreSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mGenreSpinner.setAdapter(genreSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mGenreSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.genre_action))) {
                        mGenre = GameEntry.GENRE_ACTION;
                    } else if (selection.equals(getString(R.string.genre_strategy))) {
                        mGenre = GameEntry.GENRE_STRATEGY;
                    } else if (selection.equals(getString(R.string.genre_rpg))) {
                        mGenre = GameEntry.GENRE_RPG;
                    } else if (selection.equals(getString(R.string.genre_fps))) {
                        mGenre = GameEntry.GENRE_FPS;
                    } else if (selection.equals(getString(R.string.genre_sport))) {
                        mGenre = GameEntry.GENRE_SPORT;
                    } else {
                        mGenre = GameEntry.GENRE_UNKNOWN;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mGenre = GameEntry.GENRE_UNKNOWN;
            }
        });
    }

    /**
     * Setup the dropdown spinner that allows the user to select the game platform.
     */
    private void setupPlatformSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter platformSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_platform_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        platformSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mPlatformSpinner.setAdapter(platformSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mPlatformSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.platform_xbox_one))) {
                        mPlatform = GameEntry.PLATFORM_XBOX_ONE;
                    } else if (selection.equals(getString(R.string.platform_xbox_360))) {
                        mPlatform = GameEntry.PLATFORM_XBOX_360;
                    } else if (selection.equals(getString(R.string.platform_ps3))) {
                        mPlatform = GameEntry.PLATFORM_PS3;
                    } else if (selection.equals(getString(R.string.platform_ps4))) {
                        mPlatform = GameEntry.PLATFORM_PS4;
                    } else {
                        mPlatform = GameEntry.PLATFORM_PC;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mPlatform = GameEntry.PLATFORM_PC;
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define a projection that contains all columns from the games table.
        String[] projection = {
                GameEntry._ID,
                GameEntry.COLUMN_GAME_NAME,
                GameEntry.COLUMN_GAME_GENRE,
                GameEntry.COLUMN_GAME_PLATFORM,
                GameEntry.COLUMN_GAME_PRICE,
                GameEntry.COLUMN_QUANTITY,
                GameEntry.COLUMN_SUPPLIER_NAME,
                GameEntry.COLUMN_SUPPLIER_PHONE};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,
                mCurrentGameUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        if (cursor.moveToFirst()) {
            // Find the columns of game attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(GameEntry.COLUMN_GAME_NAME);
            int genreColumnIndex = cursor.getColumnIndex(GameEntry.COLUMN_GAME_GENRE);
            int platformColumnIndex = cursor.getColumnIndex(GameEntry.COLUMN_GAME_PLATFORM);
            int priceColumnIndex = cursor.getColumnIndex(GameEntry.COLUMN_GAME_PRICE);
            int stockColumnIndex = cursor.getColumnIndex(GameEntry.COLUMN_QUANTITY);
            int providerNameColumnIndex = cursor.getColumnIndex(GameEntry.COLUMN_SUPPLIER_NAME);
            int providerPhoneColumnIndex = cursor.getColumnIndex(GameEntry.COLUMN_SUPPLIER_PHONE);

            // Extract out the value from the Cursor for the given column index
            String gameName = cursor.getString(nameColumnIndex);
            int gameGenre = cursor.getInt(genreColumnIndex);
            int gamePlatform = cursor.getInt(platformColumnIndex);
            Double gamePrice = cursor.getDouble(priceColumnIndex);
            String gameStock = cursor.getString(stockColumnIndex);
            String providerName = cursor.getString(providerNameColumnIndex);
            String providerPhone = cursor.getString(providerPhoneColumnIndex);

            // Update the views on the screen with the values from the database
            mNameEditText.setText(gameName);
            mPriceEditText.setText(NumberFormat.getCurrencyInstance().format(gamePrice));
            mStockEditText.setText(gameStock);
            mProviderNameEditText.setText(providerName);
            mProviderPhoneEditText.setText(providerPhone);

            switch (gameGenre) {
                case GameEntry.GENRE_ACTION:
                    mGenreSpinner.setSelection(1);
                    break;
                case GameEntry.GENRE_STRATEGY:
                    mGenreSpinner.setSelection(2);
                    break;
                case GameEntry.GENRE_RPG:
                    mGenreSpinner.setSelection(3);
                    break;
                case GameEntry.GENRE_FPS:
                    mGenreSpinner.setSelection(4);
                    break;
                case GameEntry.GENRE_SPORT:
                    mGenreSpinner.setSelection(5);
                    break;
                default:
                    mGenreSpinner.setSelection(0);
                    break;
            }

            switch (gamePlatform) {
                case GameEntry.PLATFORM_PC:
                    mPlatformSpinner.setSelection(0);
                    break;
                case GameEntry.PLATFORM_XBOX_ONE:
                    mPlatformSpinner.setSelection(1);
                    break;
                case GameEntry.PLATFORM_XBOX_360:
                    mPlatformSpinner.setSelection(2);
                    break;
                case GameEntry.PLATFORM_PS3:
                    mPlatformSpinner.setSelection(3);
                    break;
                case GameEntry.PLATFORM_PS4:
                    mPlatformSpinner.setSelection(4);
                    break;
            }

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameEditText.setText("");
        mPriceEditText.setText("");
        mStockEditText.setText("");
        mProviderNameEditText.setText("");
        mProviderPhoneEditText.setText("");
        mGenreSpinner.setSelection(0);
        mPlatformSpinner.setSelection(0);
    }
}
