package com.common.util;

import junit.framework.TestCase;

/**
 * Created by dpandey on 7/27/17.
 */
public class HangmanUtilityTest extends TestCase {

    private String statusCheckURL;
    private String gameId;
    private String httpPostBodyToStartGame;
    private String userEmailIdToStartGame;
    private String userGuess;
    private String httpPostBodyCheck;

    protected void setUp() throws Exception {
        super.setUp();
        gameId = "1234abcdtest";
        statusCheckURL = "http://int-sys.usr.space/hangman/games/{gameId}";
        httpPostBodyToStartGame = "{\"email\":\"userEmail\"}";
        userEmailIdToStartGame = "test@gmail.com";
        userGuess = "d";
        httpPostBodyCheck = "{\"char\":\"userGuess\"}";
    }

    public void testParsePostBodyForGameStart() {
        String httpPostBody = HangmanUtility.parsePostBodyForGameStart(httpPostBodyToStartGame, userEmailIdToStartGame);
        assertEquals("{\"email\":\"test@gmail.com\"}", httpPostBody);
    }

    public void testParseGetBodyToCheckGameStatus() {
        String gameStartUrl = HangmanUtility.parseGetBodyToCheckGameStatus(statusCheckURL, gameId);
        assertEquals("http://int-sys.usr.space/hangman/games/1234abcdtest", gameStartUrl);
    }

    public void testParseHttpPostBodyForGuess() {
        String  httpPostBodyGuess = HangmanUtility.parseHttpPostBodyForGuess(httpPostBodyCheck, userGuess);
        assertEquals("{\"char\":\"d\"}", httpPostBodyGuess);
    }
}