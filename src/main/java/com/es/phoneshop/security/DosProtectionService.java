package com.es.phoneshop.security;

@FunctionalInterface
public interface DosProtectionService {
    boolean isAllowed(String ip);
}
