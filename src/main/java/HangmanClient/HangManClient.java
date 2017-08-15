package HangmanClient;

/**
 * Created by dpandey on 7/28/17.
 */
import com.common.util.HangmanUtility;
import com.constants.HangManConstants;
import com.json.conversion.JsonConverter;
import com.service.HangManService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by dpandey on 7/28/17.
 */

/**
 * This is the HangMan Client. This class is responsible to start the HangMan game.
 */
@SpringBootApplication
@ComponentScan({"com.json.conversion,com.service.impl,com.service.impl.HangmanService, com.http.client, com.common.util"})
public class HangManClient implements CommandLineRunner {

    @Autowired
    private HangManService hangmanService;

    @Autowired
    private JsonConverter jsonConverter;

    @Autowired
    private ResourceLoader resourceLoader;

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(HangManClient.class);
        app.run(args);
    }

    /**
     * This method is responsible to start the HangMan game. It calls HangManService to send the HTTPPOST and HTTPGET
     * request to the server.
     *
     * @param args
     * @throws Exception
     */
    @Override
    public void run(String... args) throws Exception {
         /*
         * Outer while loop which will continue and start the new Game if user wants to play another new game.
         * For Exit: He can enter N or n.
         * For start new game he can enter Y or y.
         */
        while (true) {
            List<String> filteredDictWords;
            Map highFrequencyChar = null;
            String oldRegExpression = null;

            System.out.println("To start the hangman game please enter your valid emaild id: ");
            List<Character> inputCharacterList = new ArrayList<>();

            /*
             * Takes user email to start the game.
             */
            String email = hangmanService.readUserInput();

            if (email != null && !email.isEmpty()) {
                /*
                 * Get's the user email Id from HTTPPOST response.
                 */
                String gameId = getGameId(email);
                /*
                 * Checks if game is active or inactive.
                 */
                if (!gameId.equalsIgnoreCase("inactive")) {
                    /*
                     * Inner while loop checks the status of game after user input guesses.
                     */
                    while (hangmanService.checkGameToBeContinue(gameId)) {
                        char userInputChar;
                        if (highFrequencyChar != null && highFrequencyChar.size() > 0) {
                            userInputChar = hangmanService.playHangManGame(inputCharacterList, highFrequencyChar);
                        } else {
                            userInputChar = 'e';
                            inputCharacterList.add(userInputChar);
                        }
                        /*
                         * Takes userInput guesses and matches with the HangMan word. If it matches the character of the
                         * Hangman word then guesse count will not decremet and remain the same count if there is no match
                         * of the character it will decrement the count.
                         * Example: {"gameId":"t34gt8ki8","word":"e_i_h_lline","guessesLeft":0,"status":"inactive"}
                         */
                        String guessCorrectnessResponse = hangmanService.getGuessResponseCorrectness(userInputChar, gameId);

                        if (guessCorrectnessResponse != null) {
                            Map guessCorrectness = hangmanService.getJsonConversionToMap(guessCorrectnessResponse);
                            String status = (String) guessCorrectness.get(HangManConstants.GAME_STATUS);

                            String wordLengthToBeGuessed = (String) guessCorrectness.get(HangManConstants.WORD);
                            System.out.println("Word after guess " + wordLengthToBeGuessed);

                            String regNewExpression = wordLengthToBeGuessed.replaceAll("_", ".");

                            int wordLength = wordLengthToBeGuessed.length();
                            if (oldRegExpression == null || !oldRegExpression.equalsIgnoreCase(regNewExpression)) {
                                String fileName = wordLength + ".txt";
                                Resource res = resourceLoader.getResource("classpath:" + fileName);
                                List<String> dictWords = hangmanService.readDictionary(res.getURI());
                                filteredDictWords = getUpdatedList(dictWords, regNewExpression);
                                highFrequencyChar = getHighFrequencyCharInDict(filteredDictWords);
                            }
                            oldRegExpression = regNewExpression;
                             /*
                             * For every user input guess it checks if game status is active or not. If it is active it
                             * will continue the same game other wise it will get exit.
                             */
                            if (!HangManConstants.GAME_STATUS_ACTIVE.equalsIgnoreCase(status)) {
                                System.out.println("Game is over :" + guessCorrectness.get(HangManConstants.MESSAGE));
                                System.out.println("Word is : " + guessCorrectness.get(HangManConstants.WORD));
                                break;
                            }

                        }
                    }
                }
                /*
                 * Here it will ask if user want to play another game. User can enter Y or N.
                 * Y : To continue
                 * N : To exit
                 */
                System.out.println(" Do you want to play another game Y/N:");
                String userInput = hangmanService.readUserInput();
                if (userInput.substring(0).equalsIgnoreCase("N")) {
                    break;
                }
            }
        }
    }

    /**
     * This method is responsible to take input of user email id to start the game and fetch the HTTPPOST response
     * to start the new game. It returns the game Id if the game status is active other wise returns inactive.
     *
     * @param email type String as user emailId
     * @return String as
     */
    private String getGameId(String email) {
        String httpJsonResponse = hangmanService.getHttpPostToStartTheGame(email);
        Map gameStartParameter = hangmanService.getJsonConversionToMap(httpJsonResponse);
        String guessesLeft = (String) gameStartParameter.get(HangManConstants.GUESSES_LEFT);
        String gameId = (String) gameStartParameter.get(HangManConstants.GAME_ID_WITHOUT_BRACES);
        String gameActive = (String) gameStartParameter.get(HangManConstants.GAME_STATUS_ACTIVE);
        int noOfGuessesLeft = guessesLeft != null ? Integer.valueOf(guessesLeft) : 0;
        if (noOfGuessesLeft > 0) {
            return gameId;
        } else
            return "inactive";
    }

    /**
     * This function compile regualar expression.
     *
     * @param regExpr as type String
     * @return Returns as Predicate function. Used for matching String.
     */
    private Predicate<String> regularExpression(String regExpr) {
        return Pattern
                .compile(regExpr)
                .asPredicate();
    }

    /**
     * It filter the list of Strings based on input regular expression.
     *
     * @param lst    type List. Contains dictionary words.
     * @param strExp type String. String for regular expression.
     * @return List of filter based on regular expression.
     */
    private List<String> getUpdatedList(List<String> lst, String strExp) {
        return lst.parallelStream()
                .filter(regularExpression(strExp))
                .collect(Collectors.<String>toList());
    }

    /**
     * Calculates the frequency of characters from input list of Strings
     *
     * @param words type LIST. Takes input as list of Strings.
     * @return sorted map based on values. Key will be character and value will be count.
     */
    private Map<Character, Integer> getHighFrequencyCharInDict(List<String> words) {
        Map<Character, Integer> highFrequencyCharMap = new HashMap<>();

        StringBuilder strBuilder = new StringBuilder();
        for (String word : words) {
            strBuilder.append(word);
        }

        char[] charArray = strBuilder.toString().toCharArray();

        for (char c : charArray) {
            if (!highFrequencyCharMap.containsKey(c)) {
                highFrequencyCharMap.put(c, 1);
            } else {
                int counter = highFrequencyCharMap.get(c);
                highFrequencyCharMap.put(c, ++counter);
            }
        }
        return HangmanUtility.sortByValue(highFrequencyCharMap);
    }
}



