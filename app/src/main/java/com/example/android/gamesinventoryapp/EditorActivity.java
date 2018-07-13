package com.example.android.gamesinventoryapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.gamesinventoryapp.data.GameContract.GameEntry;

public class EditorActivity extends AppCompatActivity {

    /**
     * Spinner for the game genre
     */
    private Spinner mGenreSpinner;

    /**
     * Spinner for the game platform
     */
    private Spinner mPlatformSpinner;

    /**
     * EditText field to enter the stock quantity
     */
    private EditText mGameStock;

    /**
     * EditText field to enter the stock quantity
     */
    private EditText mGamePrice;

    /**
     * Game genre
     */
    private int mGenre = GameEntry.GENRE_UNKNOWN;

    /**
     * Game platforrm
     */
    private int mPlatform = GameEntry.PLATFORM_PC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Find all relevant views that we will need to read user input from
        mGenreSpinner = findViewById(R.id.spinner_genre);
        mPlatformSpinner = findViewById(R.id.spinner_platform);
        mGameStock = findViewById(R.id.edit_game_stock);
        mGamePrice = findViewById(R.id.edit_game_price);

        // Find the increase stock button
        ImageButton mIncreaseStockButton = findViewById(R.id.plus_stock);
        // Find the decrease stock button
        ImageButton mDecreaseStockButton = findViewById(R.id.minus_stock);

        // Setting the onClickListener for mIncreaseStockButton
        mIncreaseStockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentStock = mGameStock.getText().toString();
                if (TextUtils.isEmpty(currentStock)) {
                    mGameStock.setText(String.valueOf(1));
                } else if (Integer.parseInt(currentStock) == 99) {
                    Toast.makeText(EditorActivity.this, getString(R.string.increment_error_message), Toast.LENGTH_SHORT).show();
                } else {
                    int currentStockInt = Integer.parseInt(currentStock);
                    mGameStock.setText(String.valueOf(currentStockInt + 1));
                }
            }
        });

        // Setting the onClickListener for mDecreaseStockButton
        mDecreaseStockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentStock = mGameStock.getText().toString();
                if (TextUtils.isEmpty(currentStock)) {
                    mGameStock.setText(String.valueOf(0));
                } else if (Integer.parseInt(currentStock) == 0) {
                    Toast.makeText(EditorActivity.this, getString(R.string.decrement_error_message), Toast.LENGTH_SHORT).show();
                } else {
                    int currentStockInt = Integer.parseInt(currentStock);
                    mGameStock.setText(String.valueOf(currentStockInt - 1));
                }
            }
        });

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
}
