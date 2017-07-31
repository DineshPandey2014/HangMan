package com.common.util;

import com.resources.HangManConstants;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * Created by dpandey on 7/25/17.
 */

/**
 * This class contains utlity method for parsing the string and replace the required substring.
 */
@Component
public class HangmanUtility {

    /**
     * This method parses the String and replace the required String.
     *
     * @param httpPostBody type String. Used as a body for HTTP POST URL : "http://int-sys.usr.space/hangman/games/";
     *                     Example of httpPostBody : "{\"email\":\"userEmail\"}";
     * @param email        type String. Used as value for the httpPostBody.
     *                     Example test@gmail.com
     * @return String, It will be the httpPostBody. Use for the URL "http://int-sys.usr.space/hangman/games/";
     * to start the game.
     * Example: "{\"email\":\"test@gmail.com\"}"
     */
    public static String parsePostBodyForGameStart(String httpPostBody, String email) {
        return httpPostBody.replaceAll(HangManConstants.HANGMAN_USER_EMAIL, email);
    }

    /**
     * This method parses the String and replace the required String.
     *
     * @param httpGetURL type String. This GET url "http://int-sys.usr.space/hangman/games/{gameId}", It's used to get
     *                   Status of the game.
     * @param gameId     type String. It's unique game Id which is attached with every email id of the user. It is used in
     *                   the GET url. If game id is 1234abcdtest.
     *                   Example: http://int-sys.usr.space/hangman/games/1234abcdtest
     * @return String. GET complete url with game Id. like http://int-sys.usr.space/hangman/games/1234abcdtest.
     */
    public static String parseGetBodyToCheckGameStatus(String httpGetURL, String gameId) {
        return httpGetURL.replaceAll(HangManConstants.GAME_ID_WITH_BRACES, gameId);
    }

    /**
     * This method parses the String and replace the required String.
     *
     * @param httpPostURL type String. This is the POST url "http://int-sys.usr.space/hangman/games/{gameId}/guesses";
     *                    To fetch the game status.
     * @param gameId      type String. This game Id is unique attached with emailId.
     *                    Example: "test123abc"
     * @return String, It is the httpPostURL to get game Correctness after user started playing.
     * Example "http://int-sys.usr.space/hangman/games/test123abc/guesses";
     */
    public static String parsePostURLToCheckGameCorrectness(String httpPostURL, String gameId) {
        return httpPostURL.replaceAll(HangManConstants.GAME_ID_WITH_BRACES, gameId);
    }

    /**
     * This method parses the String and replace the required String.
     *
     * @param httpPostBody type String. It's the body for httpPost URL "http://int-sys.usr.space/hangman/games/{gameId}/guesses";
     *                     Example: "{\"char\":\"userGuess\"}";
     * @param userGuess    type String. It's the user guess for Hangman word. Only accepts alphabets.
     *                     Example: a
     * @return String, Used as HTTPPost body for the url "http://int-sys.usr.space/hangman/games/{gameId}/guesses";
     * Example: "{\"char\":\"a\"}"
     */
    public static String parseHttpPostBodyForGuess(String httpPostBody, String userGuess) {
        return httpPostBody.replaceAll(HangManConstants.User_Guess, userGuess);
    }
}
