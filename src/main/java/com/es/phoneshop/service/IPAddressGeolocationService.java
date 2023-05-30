package com.es.phoneshop.service;

import com.es.phoneshop.exception.ApiRequestException;

public interface IPAddressGeolocationService {
    /**
     * Gets ip address geolocation using <a href="https://ipapi.co/">ipapi.co API</a>.
     *
     * @param ip user ip address
     * @return ip address geolocation
     * @throws ApiRequestException the api request exception, when it is an error while making API call
     */
    String getIpAddressGeolocation(String ip) throws ApiRequestException;
}
