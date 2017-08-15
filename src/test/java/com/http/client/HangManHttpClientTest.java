package com.http.client;

/**
 * Created by dpandey on 7/23/17.
 */

import junit.framework.TestCase;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.IOException;
import java.net.UnknownHostException;

//import org.testng.annotations.Test;

public class HangManHttpClientTest extends TestCase {

    public void testGetRequestConfig() {
        String getURL = "http://testhangman.com";
        HangManHttpClient hangManHttpClient = new HangManHttpClient();
        RequestConfig config = hangManHttpClient.getRequestConfig();
        assertEquals(config.getConnectionRequestTimeout(), 1000);
        assertEquals(config.getConnectTimeout(), 1000);
        assertEquals(config.getSocketTimeout(), 1000);
    }

    public void testCreateHttpPoolConnection() {
        HangManHttpClient hangManHttpClient = new HangManHttpClient();
        CloseableHttpClient closeableHttpClient = hangManHttpClient.createHttpPoolConnection();
        assertNotNull(closeableHttpClient);
    }

    public void testsetEntity() {
        String url = "http://testhangman.com";
        HttpPost hangmanHttpPostTest = new HttpPost(url);
        HangManHttpClient hangManHttpClient = new HangManHttpClient();
        String httpPostBodyToStartGame = "{\"email\":\"userEmail\"}";
        hangManHttpClient.setEntity(hangmanHttpPostTest, httpPostBodyToStartGame);
        assertNotNull(hangmanHttpPostTest.getAllHeaders());
    }

    public void testSetHangmanHeaders() {
        String url = "http://testhangman.com";
        HttpPost hangmanHttpPostTest = new HttpPost(url);
        HangManHttpClient hangManHttpClient = new HangManHttpClient();
        hangManHttpClient.setHangmanHeaders(hangmanHttpPostTest);
        assertNotNull(hangmanHttpPostTest.getAllHeaders());
    }

    public void testExecuteHTTPGetRequestForHangman() throws IOException {
        String url = "http://testhangman.com";
        HangManHttpClient hangManHttpClient = new HangManHttpClient();
        try {
            String response = hangManHttpClient.executeHTTPGetRequestForHangman(url);
            fail("Expected Exception");
        } catch (UnknownHostException exception) {
            assertNotNull(exception);
        }
    }

    public void testExecuteHttpPostRequestForHangman() {
        String url = "http://testhangman.com";
        String postBody = "{\"char\":\"a\"}";
        try {
            HangManHttpClient hangManHttpClient = new HangManHttpClient();
            String response = hangManHttpClient.executeHttpPostRequestForHangman(url, postBody);
            fail("Expected Exception");
        } catch (Exception unknownHostException) {
            assertNotNull(unknownHostException);
        }
    }

}