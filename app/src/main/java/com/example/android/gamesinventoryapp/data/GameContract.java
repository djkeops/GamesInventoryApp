package com.example.android.gamesinventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class GameContract {

    // Empty constructor
    private GameContract() {
    }

    public static final String CONTENT_AUTHORITY = "com.example.android.gamesinventoryapp";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Path for games data: com.example.android.gamesinventoryapp/games/
     */
    public static final String PATH_GAMES = "games";

    /**
     * Inner class that defines constant values for the games database table.
     * Each entry in the table represents a game.
     */
    public static final class GameEntry implements BaseColumns {

        /**
         * The content URI to access the games data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_GAMES);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of games.
         */
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_GAMES;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single game.
         */
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_GAMES;

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
        public static final int GENRE_UNKNOWN = 0;
        public static final int GENRE_ACTION = 1;
        public static final int GENRE_STRATEGY = 2;
        public static final int GENRE_RPG = 3;
        public static final int GENRE_FPS = 4;
        public static final int GENRE_SPORT = 5;

        /**
         * Possible values for the platform of the game
         */
        public static final int PLATFORM_PC = 0;
        public static final int PLATFORM_XBOX_ONE = 1;
        public static final int PLATFORM_XBOX_360 = 2;
        public static final int PLATFORM_PS3 = 3;
        public static final int PLATFORM_PS4 = 4;

        /**
         * Returns whether or not the given game genre is one of those allowed
         */
        public static boolean isValidGenre(int genre) {
            if (genre == GENRE_UNKNOWN || genre == GENRE_ACTION || genre == GENRE_STRATEGY || genre == GENRE_RPG || genre == GENRE_FPS || genre == GENRE_SPORT) {
                return true;
            }
            return false;
        }

        /**
         * Returns whether or not the given platform is one of those allowed
         */
        public static boolean isValidPlatform(int platform) {
            if (platform == PLATFORM_PC || platform == PLATFORM_XBOX_ONE || platform == PLATFORM_XBOX_360 || platform == PLATFORM_PS3 || platform == PLATFORM_PS4) {
                return true;
            }
            return false;
        }

    }
}
