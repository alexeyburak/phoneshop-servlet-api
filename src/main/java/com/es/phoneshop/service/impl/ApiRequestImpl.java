package com.es.phoneshop.service.impl;

import com.es.phoneshop.exception.ApiRequestException;
import com.es.phoneshop.service.ApiRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static java.nio.charset.StandardCharsets.UTF_8;

public class ApiRequestImpl implements ApiRequest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiRequestImpl.class);
    private static final String REQUEST_METHOD = "GET";
    private static final int RESPONSE_CODE_OK = 200;
    private static final String CONTENT_TYPE_JSON = "application/json";

    private ApiRequestImpl() {
    }

    private static final class SingletonHolder {
        private static final ApiRequestImpl INSTANCE = new ApiRequestImpl();
    }

    public static ApiRequestImpl getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public String get(String url) throws ApiRequestException {
        StringBuilder builder = new StringBuilder();
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod(REQUEST_METHOD);
            connection.setRequestProperty("Accept", CONTENT_TYPE_JSON);

            if (connection.getResponseCode() != RESPONSE_CODE_OK) {
                throw new ApiRequestException();
            }

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), UTF_8));

            String output;
            while ((output = reader.readLine()) != null)
                builder.append(output);

        } catch (IOException e) {
            LOGGER.error("Api request error. Url: {}", url);
            throw new ApiRequestException();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    LOGGER.error("Error closing stream.");
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }

        return builder.toString();
    }

}
