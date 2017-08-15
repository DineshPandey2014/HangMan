package com.http.client;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;


/**
 * Created by dpandey on 7/22/17.
 */

/**
 * This class is responsible for making HTTPGET and HTTPPOST request.
 * Returns as Json String.
 */
@Component
public class HangManHttpClient {

    private static final String ACCEPT = "Accept";
    private static final String CONTENT_TYPE = "application/json";
    private static final String CHAR_SET = "UTF-8";
    private static final int SOCKET_TIME_OUT = 1000;
    private static final int CONNECTION_TIME_OUT = 1000;
    private static final int CONNECTION_REQUEST_TIME_OUT = 1000;
    private static final int HTTP_MAX_TOTAL_CONECTION = 200;
    private static final int DEFAULT_MAX_PER_ROUTE = 200;
    private  RequestConfig requestConfig;
    private  CloseableHttpClient httpCloseableClient;

    private static Logger logger = LoggerFactory.getLogger(HangManHttpClient.class);

    HangManHttpClient() {

        if (requestConfig == null) {
            requestConfig = getRequestConfig();
        }

        if (httpCloseableClient == null) {
            httpCloseableClient = createHttpPoolConnection();
        }
    }

    /**
     * Used to set http headers for http request.
     * Like contentType. It's key value combination.
     *
     * @param httpPost type HttpPost object.
     */
    void setHangmanHeaders(HttpPost httpPost) {
        httpPost.setHeader(ACCEPT, CONTENT_TYPE);
    }

    /**
     * This method is used to set the entity for HTTPPost request. Entity is the body in json format.
     * Takes two parameter.
     *
     * @param httpPost     type HttpPost object.
     * @param entityObject type String object. It's in Json fromat. Will be input as a body for HTTP POST request.
     */
    void setEntity(HttpPost httpPost, String entityObject) {
        StringEntity entity = new StringEntity(entityObject, ContentType.create(CONTENT_TYPE, CHAR_SET));
        httpPost.setEntity(entity);
    }

    /**
     * This method execute the HTTPGET request. Input as the URL
     *
     * @param getUrl type String. It's the server URL from where we get the Hangman response.
     * @return String as Json object
     * <p>
     * Example : {"gameId":"test1234","word":"________","guessesLeft":10}
     * @throws IOException
     */
    public String executeHTTPGetRequestForHangman(String getUrl) throws IOException {
        HttpGet hangmanHttpGet = new HttpGet(getUrl);
        HttpResponse getResponse = httpCloseableClient.execute(hangmanHttpGet);
        int responseCode = getResponse.getStatusLine().getStatusCode();
        if (responseCode != 200) {
            throw new RuntimeException("Http server is not respondng error code : " + responseCode);
        }
        HttpEntity getEntity = getResponse.getEntity();
        return EntityUtils.toString(getEntity);
    }

    /**
     * This method execute HTTPPOST request. Input as URL and body.
     *
     * @param url      type as String. HTTPPOST URL. Example: "http://int-sys.usr.space/hangman/games/";
     * @param httpBody as String. Input as httpBody Example: HTTP_POST_BODY = "{\"email\":\"test1234@gmail.com\"}";
     * @return As a String JSON object. Example "{\"gameId\":\"abcd1234\",\"word\":\"___________\",\"guessesLeft\":10}";
     */
    public String executeHttpPostRequestForHangman(String url, String httpBody) {
        HttpPost hangmanHttpPost = new HttpPost(url);
        setHangmanHeaders(hangmanHttpPost);
        setEntity(hangmanHttpPost, httpBody);

        String httpResponse = null;
        try {
            httpResponse = executeHttpPost(httpCloseableClient, hangmanHttpPost);
        } catch (ConnectionPoolTimeoutException connectionPoolTimeoutException) {
            logger.error("HttpConnectionPool timeout ={}", connectionPoolTimeoutException.getMessage());
            throw new RuntimeException("ConnectionPoolTimeoutException exception" + connectionPoolTimeoutException.getMessage());
        } catch (ConnectTimeoutException connectTimeoutException) {
            logger.error("ConnectTimeoutException not able to connect with hangman server {}",
                    connectTimeoutException.getMessage());
            throw new RuntimeException("ConnectTimeoutException exception" + connectTimeoutException.getMessage());
        } catch (NoHttpResponseException noHttpResponseException) {
            logger.error("Hangman server failed to respond ={}", noHttpResponseException.getMessage());
            throw new RuntimeException("NoHttpResponseException exception" + noHttpResponseException.getMessage());
        } catch (SocketTimeoutException socketTimeoutException) {
            logger.error("Socket timeout exception occured. Hangman server is taking longer time to respond ={}",
                    socketTimeoutException.getMessage());
            throw new RuntimeException("SocketTimeoutException exception" + socketTimeoutException.getMessage());
        } catch (UnknownHostException unknownHostException) {
            logger.error("UnknownHostException not able to determined hostname ={}", unknownHostException.getMessage());
            throw new RuntimeException("UnknownHostException exception" + unknownHostException.getMessage());
        } catch (SSLException sslException) {
            logger.error("SSLException exception ={}", sslException.getMessage());
            throw new RuntimeException("SSLException exception" + sslException.getMessage());
        } catch (IOException ioException) {
            logger.error("Error due to failed failed IO operation ={}", ioException.getMessage());
            throw new RuntimeException("IOException exception" + ioException.getMessage());
        }
        return httpResponse;
    }

    /**
     * This method sends HTTPOST request to the server.
     *
     * @param httpclient is a CloseableHttpClient object.
     * @param httppost   is a HttpPost object.
     * @return As a String JSON object. Example "{\"gameId\":\"abcd1234\",\"word\":\"___________\",\"guessesLeft\":10}";
     * @throws IOException
     */
    String executeHttpPost(CloseableHttpClient httpclient, HttpPost httppost) throws IOException {
        CloseableHttpResponse response = httpclient.execute(httppost);
        int statusCode = response.getStatusLine().getStatusCode();

        if (statusCode != 200 && statusCode != 201) {
            throw new RuntimeException("Http server is not respondng error code : " + statusCode);
        }

        try {
            HttpEntity entity = response.getEntity();
            return EntityUtils.toString(entity);
        } finally {
            response.close();
        }
    }

    /**
     * This method set the configuration for HTTPGET and HTTPPOST object
     *
     * @return RequestConfig  object.
     */
    RequestConfig getRequestConfig() {
        return RequestConfig.custom()
                .setSocketTimeout(SOCKET_TIME_OUT)
                .setConnectTimeout(CONNECTION_TIME_OUT)
                .setConnectionRequestTimeout(CONNECTION_REQUEST_TIME_OUT)
                .build();
    }

    /**
     * This method creates HTTPPool connection. Which can be used to connect to the server
     *
     * @return CloseableHttpClient object.
     */
    CloseableHttpClient createHttpPoolConnection() {
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
        connManager.setMaxTotal(HTTP_MAX_TOTAL_CONECTION);
        connManager.setDefaultMaxPerRoute(DEFAULT_MAX_PER_ROUTE);
        // Build the client.
        HttpClientBuilder builder = HttpClientBuilder.create();
        builder.setDefaultRequestConfig(requestConfig);
        builder.setConnectionManager(connManager);
        return builder.build();
    }
}
