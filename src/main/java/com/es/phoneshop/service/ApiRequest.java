package com.es.phoneshop.service;

import com.es.phoneshop.exception.ApiRequestException;

public interface ApiRequest {
    String get(String url) throws ApiRequestException;
}
