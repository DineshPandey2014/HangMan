package com.service;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * Created by dpandey on 7/30/17.
 */
public interface HangManService {
    String getHttpPostToStartTheGame(String email);

    Map getJsonConversionToMap(String httpJsonResponse);

    boolean checkGameToBeContinue(String gameId);

    char playHangManGame(List<Character> previousInputCharactersList, Map<Character, Integer> charHighFrequancy);

    String getGuessResponseCorrectness(char userInputChar, String gameId);

    String readUserInput();

    boolean getGameStatus(String gameStatusJsonResponse);

    String checkGameStatus(String gameID) throws IOException;

    List<String> readDictionary(URI uri) throws IOException;
}
