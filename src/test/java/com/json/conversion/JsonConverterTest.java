package com.json.conversion;

import junit.framework.TestCase;

import java.io.IOException;
import java.util.Map;

/**
 * Created by dpandey on 7/27/17.
 */
public class JsonConverterTest extends TestCase {

    public void testFetchGameStartParamters() throws IOException {
        JsonConverter jsonConverter = new JsonConverter();
        String gameStartParameters = "{\"gameId\":\"abcd1234\",\"word\":\"___________\",\"guessesLeft\":10}";
        Map gamesStartMap = jsonConverter.fetchGameStartParamters(gameStartParameters);
        assertEquals("abcd1234", gamesStartMap.get("gameId"));
        assertEquals("___________", gamesStartMap.get("word"));
        assertEquals("10", gamesStartMap.get("guessesLeft"));
    }

    /*
     * Test for exception. Unexpected input.
     */
    public void testJsonGenerationExceptionForFetchGameStartParamters() throws IOException {
        JsonConverter jsonConverter = new JsonConverter();
        String gameStartParameters = "$$$$$";
        try {
            Map gamesStartMap = jsonConverter.fetchGameStartParamters(gameStartParameters);
            fail("Expected Exception");
        } catch (RuntimeException exception) {
            assertNotNull(exception);
        }
    }

}
