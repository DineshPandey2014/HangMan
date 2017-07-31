package com.service.impl;

import com.common.util.HangmanUtility;
import com.http.client.HangManHttpClient;
import com.json.conversion.JsonConverter;
import com.resources.HangManConstants;
import com.service.HangManService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by dpandey on 7/27/17.
 */

/**
 * This is a service class responsible to play Hangman game.This class do following task.
 * 1. Send HTTPPOST request to the HangMan server to start the game.
 * 2. Check Game status it checks number of guesses left, Is game is active, Is user able to guess the the right word
 * 3. It has method which will take a user input character and send as HTTPPOST request as input to check if his guess
 *    is right or not.
 * 4. It has a JSON conversion which converts JSON input String to Map of key and values.
 *
 */
@Service
@ComponentScan({"com.http.client", "com.common.util"})
public class HangmanServiceImpl implements HangManService {

    @Autowired
    private HangmanUtility hangmanUtility;

    @Autowired
    private JsonConverter jsonConverter;

    @Autowired
    HangManHttpClient hangManHttpClient;

    /**
     * Takes user input guess as a String object.
     *
     * @return as a String object
     */
    public String readUserInput() {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

    /**
     * Prepare HTTP POST Body
     *
     * @param email as a String. It's the user email id which will be the input for HTTPPOST request to Hangman server.
     *              It's the user identification to start the game. Which will have unique gameId.
     * @return JSON String object Example: {"gameId":"12h3k4abcd","word":"__________","guessesLeft":10}
     */
    public String getHttpPostToStartTheGame(String email) {
        String httpBody = hangmanUtility.parsePostBodyForGameStart(HangManConstants.HTTP_POST_BODY, email);
        return hangManHttpClient.executeHttpPostRequestForHangman(HangManConstants.HTTP_POST_GAME_START_URL, httpBody);
    }

    /**
     * This method take JSON object as a String and converts into Map of KEY/Value.
     *
     * @param httpJsonResponse type as String. Take as httpJsonResponse from GET or POST request.
     *                         Example:  "{\"gameId\":\"abcd1234\",\"word\":\"___________\",\"guessesLeft\":10}";
     * @return as Map object of Key and Value:
     */
    public Map getJsonConversionToMap(String httpJsonResponse) {
        return jsonConverter.fetchGameStartParamters(httpJsonResponse);
    }

    /**
     * It check's the game status. Like if game is active or not. Or number of guesses got finished Or user has guess
     * the word correctly.
     *
     * @param gameId type as String. It's the gameId which is mapped with user email Id. This is needed to check the game status.
     *               Example: 7rf69ut89erh
     * @return as boolean vale. Returns true if game to be continued other wise false means game is over and not active.
     */
    public boolean checkGameToBeContinue(String gameId) {
        try {
            String gameStatusJsonResponse = checkGameStatus(gameId);
            System.out.println(gameStatusJsonResponse);
            if (gameStatusJsonResponse == null) {
                throw new RuntimeException("Hangman server is down.");
            }
        } catch (IOException ioException) {

        }
        return true;
    }

    /**
     * This method sends HTTPGET request to HangMan server. It returns a JSON String. Which is needed to check the game
     * status.
     *
     * @param gameID type String object. It's the gameId which unique and attached with the userEmail Id.
     * @return String as JSON object. Example "{\"gameId\":\"test123\",\"word\":\"a_________\",\"guessesLeft\":9}";
     * @throws IOException
     */
    public String checkGameStatus(String gameID) throws IOException {
        String gameStatusCheckGetURL = hangmanUtility.parseGetBodyToCheckGameStatus(
                HangManConstants.HTTP_GET_GAME_STATUS_CHECK_URL, gameID);
        return hangManHttpClient.executeHTTPGetRequestForHangman(gameStatusCheckGetURL);
    }

    /**
     * This method takes userInput as the guesss for the word. It keeps the list of previous user guesses and checks
     * if the new guesses is not the previous entered. If the new guesses match the old guesses. It will not send the
     * userInput to the HangMan server and ask user to re-enter the new character which should to different from the
     * previous guesses. It prevents the network calls.
     *
     * @param previousInputCharactersList type List. List of old guesses. Which is use to compare the new guess. If it
     *                                    same it will ask user to enter the input character which should be different
     *                                    from the old guesses.
     * @return The user input guess as a character.
     */
    public char playHangManGame(List<Character> previousInputCharactersList) {
        Character userInputCharacter;
        while (true) {
            System.out.println("Please enter only alphabetical letters");

            if (previousInputCharactersList.size() > 0) {
                String userPreviousInput = printUserInputChar(previousInputCharactersList);
                System.out.println("Please enter the new character which is not part of the previous guesses " +
                        "of list :" + userPreviousInput);
            }

            Scanner scanner = new Scanner(System.in);
            String inputUserGuess = scanner.nextLine();
            if (inputUserGuess != null && !inputUserGuess.isEmpty()) {
                userInputCharacter = inputUserGuess.charAt(0);
                if (Character.isLetter(userInputCharacter) &&
                        !previousInputCharactersList.contains(userInputCharacter)) {
                    previousInputCharactersList.add(userInputCharacter);
                    break;
                }
            }
        }
        return userInputCharacter;
    }

    /**
     *
     * @param userInputChar
     * @param gameId
     * @return
     */
    public String getGuessResponseCorrectness(char userInputChar, String gameId) {
        String guessUrl = hangmanUtility.parsePostURLToCheckGameCorrectness(
                HangManConstants.HTTP_POST_CHECK_GAME_GUESSES_CORRECTNESS, gameId);
        String guessUrlHttpPostBody = hangmanUtility.parseHttpPostBodyForGuess(
                HangManConstants.HTTP_POST_BODY_TO_CHECK_GUESS, String.valueOf(userInputChar));
        return hangManHttpClient.executeHttpPostRequestForHangman(guessUrl, guessUrlHttpPostBody);
    }

    /**
     * Takes input as a List of character and add them to StringBuffer.
     *
     * @param inputCharacter type List of characters.
     * @return as a StringBuffer which contains user input character as guesses.
     *
     */
    public String printUserInputChar(List<Character> inputCharacter) {
        StringBuffer strBuff = new StringBuffer();
        for (Character inputChar : inputCharacter) {
            strBuff.append(inputChar);
            strBuff.append(", ");
        }
        return strBuff.toString();
    }

    /**
     * This method takes input as String JSON response from Hangman server of HTTPGET and HTTPPOST response.
     * and check if game needs to be continue or not. This based on game status if it is active or not.
     * Example of inactive : {"gameId":"1668a3d841dc","word":"e_i_h_lline","guessesLeft":0,"status":"inactive"}
     *
     * @param gameStatusJsonResponse type as String object. Takes input of HTTPGET request.
     *                               Example for active: {"gameId":"1668a3d841dc","word":"e_i_h_lline","guessesLeft":1,
     *                               "status":"active"}
     *
     * @return true if game is active and to be continue. Otherwise it returns false if game is inactive.
     */
    public boolean getGameStatus(String gameStatusJsonResponse) {
        Map gameStatusParameter = getJsonConversionToMap(gameStatusJsonResponse);
        String word = (String) gameStatusParameter.get(HangManConstants.WORD);
        String guessesLeft = (String) gameStatusParameter.get(HangManConstants.GUESSES_LEFT);
        String status = (String) gameStatusParameter.get(HangManConstants.GAME_STATUS);
        System.out.println("************* Game Status ***************");
        System.out.println("Word : " + word);
        System.out.println("Guesses Left : " + guessesLeft);
        System.out.println("Game Status : " + status);
        System.out.println("****************************************");
        if (!status.equalsIgnoreCase(HangManConstants.GAME_STATUS_ACTIVE) || guessesLeft.equals("0")) {
            return false;
        }
        return true;
    }
}
