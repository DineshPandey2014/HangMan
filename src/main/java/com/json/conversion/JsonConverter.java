package com.json.conversion;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dpandey on 7/23/17.
 */

/**
 * Class to convert input JSON string into key value of HashMap.
 */
@Component
public class JsonConverter {
    private static Logger logger = LoggerFactory.getLogger(JsonConverter.class);

    /**
     * @param httpJsonResponse String as input Json from Hangman GET and POST request.
     *                         GET request for Game Status "http://int-sys.usr.space/hangman/games/{gameId}";
     *                         POST request to start a game email as body http://int-sys.usr.space/hangman/games/";
     *                         POST request to check game correctness char as body
     *                         "http://int-sys.usr.space/hangman/games/{gameId}/guesses";
     * @return Map object.
     */
    public Map fetchGameStartParamters(String httpJsonResponse) {
        Map<String, Object> gameStartParameters = new HashMap<String, Object>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            gameStartParameters = mapper.readValue(httpJsonResponse, new TypeReference<Map<String, String>>() {
            });
        } catch (IOException e) {
            logger.info("Json IOException " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
        return gameStartParameters;
    }
}
