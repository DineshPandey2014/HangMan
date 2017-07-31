package com.resources;

/**
 * Created by dpandey on 7/24/17.
 */

/**
 * Class contains HangMan constants.
 */
public class HangManConstants {
    public static final String HTTP_POST_GAME_START_URL = "http://int-sys.usr.space/hangman/games/";
    public static final String HTTP_GET_GAME_STATUS_CHECK_URL = "http://int-sys.usr.space/hangman/games/{gameId}";
    public static final String HTTP_POST_BODY = "{\"email\":\"userEmail\"}";
    public static final String GUESSES_LEFT = "guessesLeft";
    public static final String GAME_ID_WITH_BRACES = "\\{gameId\\}";
    public static final String GAME_ID_WITHOUT_BRACES = "gameId";
    public static final String HANGMAN_USER_EMAIL = "userEmail";
    public static final String WORD = "word";
    public static final String GAME_STATUS = "status";
    public static final String GAME_STATUS_ACTIVE = "active";
    public static final String HTTP_POST_CHECK_GAME_GUESSES_CORRECTNESS =
            "http://int-sys.usr.space/hangman/games/{gameId}/guesses";
    public static final String HTTP_POST_BODY_TO_CHECK_GUESS = "{\"char\":\"userGuess\"}";
    public static final String User_Guess = "userGuess";
    public static final String MESSAGE = "msg";
}
