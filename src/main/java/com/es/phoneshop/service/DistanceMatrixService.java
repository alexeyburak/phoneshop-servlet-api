package com.es.phoneshop.service;

import com.es.phoneshop.exception.ApiRequestException;

public interface DistanceMatrixService {
    /**
     * Gets distance matrix with required destination using <a href="https://maps.googleapis.com/">Google API</a>
     *
     * @param destination city, the distance to which will be considered
     * @return int value of distance
     * @throws ApiRequestException the api request exception, when it is an error while making API call
     */
    int getDistanceMatrix(String destination) throws ApiRequestException;
}
