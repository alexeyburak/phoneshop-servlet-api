package com.es.phoneshop.service.impl;

import com.es.phoneshop.dto.geolocation.GeolocationResponseDTO;
import com.es.phoneshop.exception.ApiRequestException;
import com.es.phoneshop.service.ApiRequest;
import com.es.phoneshop.service.IPAddressGeolocationService;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IPAddressGeolocationServiceImpl implements IPAddressGeolocationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DistanceMatrixServiceImpl.class);
    private final ApiRequest apiRequest;

    private IPAddressGeolocationServiceImpl() {
        apiRequest = ApiRequestImpl.getInstance();
    }

    private static final class SingletonHolder {
        private static final IPAddressGeolocationServiceImpl INSTANCE = new IPAddressGeolocationServiceImpl();
    }

    public static IPAddressGeolocationServiceImpl getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public String getIpAddressGeolocation(String ip) throws ApiRequestException {
        String url = buildUrl(ip);
        String jsonString = apiRequest.get(url);
        if (jsonString.isEmpty()) {
            throw new ApiRequestException();
        }

        GeolocationResponseDTO response = getGeolocation(jsonString);

        LOGGER.debug("Parse ip. Value: {}", ip);
        return response.getCity();
    }

    private String buildUrl(String value) {
        return "https://ipapi.co/" +
                value +
                "/json";
    }

    private GeolocationResponseDTO getGeolocation(String jsonString) {
        return new Gson()
                .fromJson(jsonString, GeolocationResponseDTO.class);
    }

}
