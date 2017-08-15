package com.service;

import com.http.client.HangManHttpClient;
import com.json.conversion.JsonConverter;
import com.constants.HangManConstants;
import com.service.impl.HangmanServiceImpl;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.TextFromStandardInputStream;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.testng.annotations.BeforeMethod;

import java.io.IOException;
import java.util.*;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.contrib.java.lang.system.TextFromStandardInputStream.emptyStandardInputStream;
import static org.mockito.Mockito.when;

/**
 * Created by dpandey on 7/27/17.
 */

@RunWith(MockitoJUnitRunner.class)
public class HangManServiceTest {
    private List<Character> inputCharacter;
    private String hangManGetURL = "http://test/hangman/games/";
    private String gameId = "test1234";
    private char userInputGuess = 'a';
    private String httpPostBody = "{\"char\":\"a\"}";
    private String userEmailIdToStartGame = "test@gmail.com";
    private String guessResponse = "{\"gameId\":\"test123\",\"word\":\"__________\",\"guessesLeft\":10}";
    private String userEmail = "test@gmail.com";
    private String httpBody = "{\"email\":\"test@gmail.com\"}";
    private String gameStartParameters = "{\"gameId\":\"abcd1234\",\"word\":\"___________\",\"guessesLeft\":10}";
    private String httpJsonResponse = "{\"gameId\":\"abcd1234\",\"word\":\"___________\",\"guessesLeft\":10}";
    private String jsonResponse = "{\"gameId\":\"test1234\",\"status\":\"active\",\"word\":\"a___r__gh_\"," +
            "\"guessesLeft\":9,\"msg\":\"You have guessed r\"}";
    private String jsonResponseOne = "{\"gameId\":\"abcd45\",\"status\":\"active\",\"word\":\"a___r__gh_\"," +
            "\"guessesLeft\":9,\"msg\":\"You have guessed r\"}";
    private String guessUrl = "http://int-sys.usr.space/hangman/games/{gameId}/guesses";
    private String httpBodyOne = "{\"char\":\"a\"}";

    @InjectMocks
    private HangmanServiceImpl hangmanServiceMock;

    @Mock
    private JsonConverter jsonConverterMock;

    @Mock
    private HangManHttpClient hangManHttpClientMock;

    @BeforeMethod
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Rule
    public final TextFromStandardInputStream systemInMock = emptyStandardInputStream();

    @Test
    public void readUserInput() {
        HangmanServiceImpl hangmanService = new HangmanServiceImpl();
        systemInMock.provideLines(userEmail);
        assertEquals(userEmail, hangmanService.readUserInput());
    }

    @Test
    public void testGetHttpPostToStartTheGame() throws Exception {
          when(hangManHttpClientMock.executeHttpPostRequestForHangman(HangManConstants.HTTP_POST_GAME_START_URL,
                  httpBody)).thenReturn(gameStartParameters);
        String httpResponse = hangmanServiceMock.getHttpPostToStartTheGame(userEmailIdToStartGame);
        assertEquals(gameStartParameters, httpResponse);
    }

    @Test
    public void testGetJsonConversionToMap() throws IOException{
        Map jsonResponseOne = getGameResponse(httpJsonResponse);
        when(jsonConverterMock.fetchGameStartParamters(httpJsonResponse)).thenReturn(jsonResponseOne);
        Map outPutMap = hangmanServiceMock.getJsonConversionToMap(httpJsonResponse);
        assertEquals(outPutMap.size(), 3);
        assertEquals(outPutMap.get("gameId"), "abcd1234");
        assertEquals(outPutMap.get("word"), "___________");
        assertEquals(outPutMap.get("guessesLeft"), "10");
    }

    @Test
    public void testCheckGameStatus() throws IOException {
        String gameID = "test123";
        String gameStatusCheckGetURL = "http://int-sys.usr.space/hangman/games/test123";
        when(hangManHttpClientMock.executeHTTPGetRequestForHangman(gameStatusCheckGetURL)).thenReturn(guessResponse);
        String gameStatusResponse = hangmanServiceMock.checkGameStatus(gameID);
        assertEquals(guessResponse, gameStatusResponse);
    }

    @Test
    public void testCheckGameToBeContinue() throws IOException {
        String gameID = "test123";
        String gameStatusCheckGetURL = "http://int-sys.usr.space/hangman/games/test123";
        Map jsonResponseMap = getGameResponse(jsonResponse);
        when(jsonConverterMock.fetchGameStartParamters(jsonResponse)).thenReturn(jsonResponseMap);
        when(hangManHttpClientMock.executeHTTPGetRequestForHangman(gameStatusCheckGetURL)).thenReturn(jsonResponse);
        assertTrue(hangmanServiceMock.checkGameToBeContinue(gameID));
    }


    @Test
    public void testPlayHangManGame() {
        Map<Character, Integer> dictCharacter = new HashMap<>();
        dictCharacter.put('a', 3);
        dictCharacter.put('b', 1);
        dictCharacter.put('g', 2);

        HangmanServiceImpl hangmanService = new HangmanServiceImpl();
        List<Character> previousInputCharactersList = new ArrayList<>();
        previousInputCharactersList.add('a');
        previousInputCharactersList.add('e');
        previousInputCharactersList.add('f');
        hangmanService.playHangManGame(previousInputCharactersList, dictCharacter);
        assertNotNull(hangmanService);
    }

    @Test
    public void testGetGameStatusActive() throws IOException{
        Map jsonResponseMapOne = getGameResponse(jsonResponseOne);
        when(jsonConverterMock.fetchGameStartParamters(jsonResponseOne)).thenReturn(jsonResponseMapOne);
        assertTrue(hangmanServiceMock.getGameStatus(jsonResponseOne));
    }

    @Test
    public void testPrintUserInputChar() {
        HangmanServiceImpl hangmanService = new HangmanServiceImpl();
        String userInputGuesses = hangmanService.printUserInputChar(addUserInputGuessesForHangManWord());
        assertEquals("a, k, o, u, ", userInputGuesses);
    }


    @Test
    public void testGetGuessResponseCorrectness() throws Exception {
        when(hangManHttpClientMock.executeHttpPostRequestForHangman(guessUrl, httpBodyOne)).thenReturn(httpJsonResponse);
        String httpResponse = hangmanServiceMock.getGuessResponseCorrectness('a', "abcd1234");
        assertEquals("{\"gameId\":\"abcd1234\",\"word\":\"___________\",\"guessesLeft\":10}", httpJsonResponse);
    }

    private List<Character> addUserInputGuessesForHangManWord() {
        inputCharacter = new ArrayList<>();
        inputCharacter.add('a');
        inputCharacter.add('k');
        inputCharacter.add('o');
        inputCharacter.add('u');
        return inputCharacter;
    }

    private Map getGameResponse(String jsonResponse) throws IOException{
        Map<String, Object> gameStartParameters = new HashMap<String, Object>();
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(jsonResponse, new TypeReference<Map<String, String>>() {});
    }

    @Test
    public void readFile() throws IOException {
        String path = this.getClass().getResource("").getPath();
        //res.getPath();
        HangmanServiceImpl hangmanService = new HangmanServiceImpl();
                                //Users/dpandey/Hangman_Solver/HangMan-master/src/test/java/com/service/HangManServiceTest.java

    }
}
