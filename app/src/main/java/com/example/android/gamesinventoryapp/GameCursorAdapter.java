package com.example.android.gamesinventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.gamesinventoryapp.data.GameContract.GameEntry;

import java.text.NumberFormat;

/**
 * {@link GameCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of game data as its data source. This adapter knows
 * how to create list items for each row of game data in the {@link Cursor}.
 */
public class GameCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link GameCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public GameCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the game data to the given list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        // Find the TextViews
        TextView nameTextView = view.findViewById(R.id.game_name);
        TextView genreTextView = view.findViewById(R.id.game_genre);
        TextView platformTextView = view.findViewById(R.id.game_platform);
        TextView priceTextView = view.findViewById(R.id.game_price);
        TextView stockTextView = view.findViewById(R.id.game_stock);

        // Find the columns indexes of the game attributes
        int nameColumnIndex = cursor.getColumnIndex(GameEntry.COLUMN_GAME_NAME);
        int genreColumnIndex = cursor.getColumnIndex(GameEntry.COLUMN_GAME_GENRE);
        int platformColumnIndex = cursor.getColumnIndex(GameEntry.COLUMN_GAME_PLATFORM);
        int priceColumnIndex = cursor.getColumnIndex(GameEntry.COLUMN_GAME_PRICE);
        int stockColumnIndex = cursor.getColumnIndex(GameEntry.COLUMN_QUANTITY);

        // Read the attributes from the Cursor for the current game
        String gameName = cursor.getString(nameColumnIndex);
        int gameGenre = cursor.getInt(genreColumnIndex);
        int gamePlatform = cursor.getInt(platformColumnIndex);
        Double gamePrice = cursor.getDouble(priceColumnIndex);
        final String gameStock = cursor.getString(stockColumnIndex);

        // Update the name TextView with the name of the current game
        nameTextView.setText(gameName);

        // Update the genre TextView with the genre of the current game
        String genreText = context.getString(R.string.before_genre_text);
        switch (gameGenre) {
            case GameEntry.GENRE_ACTION:
                genreText += context.getString(R.string.genre_action);
                break;
            case GameEntry.GENRE_FPS:
                genreText += context.getString(R.string.genre_fps);
                break;
            case GameEntry.GENRE_RPG:
                genreText += context.getString(R.string.genre_rpg);
                break;
            case GameEntry.GENRE_STRATEGY:
                genreText += context.getString(R.string.genre_strategy);
                break;
            case GameEntry.GENRE_SPORT:
                genreText += context.getString(R.string.genre_sport);
                break;
            default:
                genreText += context.getString(R.string.genre_unknown);
                break;
        }
        genreTextView.setText(genreText);

        // Update the platform TextView with the platform of the current game
        String platformText = context.getString(R.string.before_platform_text);
        switch (gamePlatform) {
            case GameEntry.PLATFORM_PC:
                platformText += context.getString(R.string.platform_pc);
                break;
            case GameEntry.PLATFORM_XBOX_ONE:
                platformText += context.getString(R.string.platform_xbox_one);
                break;
            case GameEntry.PLATFORM_XBOX_360:
                platformText += context.getString(R.string.platform_xbox_360);
                break;
            case GameEntry.PLATFORM_PS3:
                platformText += context.getString(R.string.platform_ps3);
                break;
            case GameEntry.PLATFORM_PS4:
                platformText += context.getString(R.string.platform_ps4);
                break;
        }
        platformTextView.setText(platformText);

        // Update the price TextView with the price of the current game
        String priceText = context.getString(R.string.before_price_text) + NumberFormat.getCurrencyInstance().format(gamePrice);
        priceTextView.setText(priceText);

        // Update the stock TextView with the available stock for the current game
        String stockText = context.getString(R.string.before_stock_text) + gameStock;
        stockTextView.setText(stockText);

        // Find the sell button
        Button sellButton = view.findViewById(R.id.sell_button);

        // Get the int value of the current stock quantity
        final int gameStockInt = Integer.valueOf(gameStock);
        // Get the id of the current game
        final int id = cursor.getInt(cursor.getColumnIndex(GameEntry._ID));


        // Create a click listener to handle the sell of an item by decreasing the stock quantity
        sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (gameStockInt > 0) {
                    int updatedGameStockInt = gameStockInt - 1;

                    // Update the stock of the game in the database
                    Uri currentGameUri = ContentUris.withAppendedId(GameEntry.CONTENT_URI, id);
                    ContentValues values = new ContentValues();
                    values.put(GameEntry.COLUMN_QUANTITY, updatedGameStockInt);
                    context.getContentResolver().update(currentGameUri, values, null, null);

                    // If the new quantity is 0 then inform the user
                    if (updatedGameStockInt == 0) {
                        Toast.makeText(context, R.string.out_of_stock_msg, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, R.string.out_of_stock_msg, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
