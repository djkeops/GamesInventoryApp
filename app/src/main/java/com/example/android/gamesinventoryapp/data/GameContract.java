package com.example.android.gamesinventoryapp.data;

import android.provider.BaseColumns;

public final class GameContract {

    // Empty constructor
    private GameContract() {
    }

    public static final class GameEntry implements BaseColumns {

        /**
         * Name of database table for games
         */
        public static final String TABLE_NAME = "games";

        /**
         * Unique ID number for the game (only for use in the database table).
         * Type: INTEGER
         */
        public static final String _ID = BaseColumns._ID;

        /**
         * Name of the game.
         * Type: TEXT
         */
        public static final String COLUMN_GAME_NAME = "name";

        /**
         * Genre of the game.
         * Type: INTEGER
         * <p>
         * The only possible values are: {@link #GENRE_UNKNOWN}, {@link #GENRE_ACTION}, {@link #GENRE_STRATEGY}, {@link #GENRE_RPG}, {@link #GENRE_FPS}, {@link #GENRE_SPORT}
         */
        public static final String COLUMN_GAME_GENRE = "genre";

        /**
         * Platform of the game.
         * Type: INTEGER
         * <p>
         * The only possible values are: {@link #PLATFORM_PC}, {@link #PLATFORM_XBOX_ONE}, {@link #PLATFORM_XBOX_360}, {@link #PLATFORM_PS3}, {@link #PLATFORM_PS4}
         */
        public static final String COLUMN_GAME_PLATFORM = "platform";

        /**
         * Game price
         * Type: INTEGER
         */
        public static final String COLUMN_GAME_PRICE = "price";

        /**
         * Available quantity for a game
         * Type: INTEGER
         */
        public static final String COLUMN_QUANTITY = "quantity";

        /**
         * Supplier of the game
         * Type: TEXT
         */
        public static final String COLUMN_SUPPLIER_NAME = "supplier_name";

        /**
         * The phone number of the supplier
         * Type: TEXT
         */
        public static final String COLUMN_SUPPLIER_PHONE = "supplier_phone";

        /**
         * Possible values for the genre of the game
         */
        public static final int GENRE_RPG = 0;

        /**
         * Possible values for the platform of the game
         */
        public static final int PLATFORM_XBOX_ONE = 0;

    }
}
