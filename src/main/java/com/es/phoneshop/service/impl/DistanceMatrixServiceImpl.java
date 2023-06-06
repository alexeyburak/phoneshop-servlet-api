package com.es.phoneshop.service.impl;

import com.es.phoneshop.dto.distance.DistanceDTO;
import com.es.phoneshop.dto.distance.DistanceMatrixResponseDTO;
import com.es.phoneshop.dto.distance.ElementDTO;
import com.es.phoneshop.exception.ApiRequestException;
import com.es.phoneshop.service.ApiRequest;
import com.es.phoneshop.service.DistanceMatrixService;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.commons.lang3.StringUtils.EMPTY;

public class DistanceMatrixServiceImpl implements DistanceMatrixService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DistanceMatrixServiceImpl.class);
    private static final String STATUS_NOT_FOUND = "NOT_FOUND";
    private static final String DIGIT_REGEX = "\\D";
    private static final String GOOGLE_API_KEY = "your_key";
    private final ApiRequest apiRequest;

    private DistanceMatrixServiceImpl() {
        apiRequest = ApiRequestImpl.getInstance();
    }

    private static final class SingletonHolder {
        private static final DistanceMatrixServiceImpl INSTANCE = new DistanceMatrixServiceImpl();
    }

    public static DistanceMatrixServiceImpl getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public int getDistanceMatrix(String destination) throws ApiRequestException {
        validateApiKey();
        String json = apiRequest.get(buildUrl(destination));

        DistanceDTO distance = getDistance(destination, json);

        LOGGER.debug("Parse destination. Value: {}", destination);
        return Integer.parseInt(distance.getText().replaceAll(DIGIT_REGEX, EMPTY));
    }

    private void validateApiKey() throws ApiRequestException {
        if (GOOGLE_API_KEY.isEmpty() || GOOGLE_API_KEY.length() < 35) {
            throw new ApiRequestException();
        }
    }

    private String buildUrl(String value) {
        return "https://maps.googleapis.com/maps/api/distancematrix/json?origins=minsk&" +
                "destinations=" + value +
                "&mode=walking&" +
                "key=" + GOOGLE_API_KEY;
    }

    private DistanceDTO getDistance(String destination, String json) throws ApiRequestException {
        ElementDTO element = new GsonBuilder().create()
                .fromJson(json, DistanceMatrixResponseDTO.class)
                .getRows()[0]
                .getElements()[0];

        if (element.getStatus().equals(STATUS_NOT_FOUND)) {
            LOGGER.error("Destination not found. Value: {}", destination);
            throw new ApiRequestException();
        }

        return element.getDistance();
    }

}
