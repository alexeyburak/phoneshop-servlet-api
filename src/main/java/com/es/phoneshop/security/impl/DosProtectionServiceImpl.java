package com.es.phoneshop.security.impl;

import com.es.phoneshop.security.DosProtectionService;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.time.temporal.ChronoUnit.MINUTES;

public class DosProtectionServiceImpl implements DosProtectionService {
    private static final int THRESHOLD_VALUE = 20;
    private static final int MINUTES_LIMIT_VALUE = 1;
    private final Map<String, Long> amountOfRequestsMap = new ConcurrentHashMap<>();
    private final Map<String, LocalDateTime> lastRequestDateMap = new ConcurrentHashMap<>();

    private DosProtectionServiceImpl() {
    }

    private static final class SingletonHolder {
        private static final DosProtectionServiceImpl INSTANCE = new DosProtectionServiceImpl();
    }

    public static DosProtectionServiceImpl getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public boolean isAllowed(String ip) {
        Long counter = amountOfRequestsMap.computeIfAbsent(ip, k -> 0L);
        LocalDateTime lastDate = lastRequestDateMap.computeIfAbsent(ip, k -> null);
        long minutesActive = calculateActiveTime(lastDate);

        if (isRequestNew(counter, lastDate, minutesActive)) {
            updateRequest(ip, LocalDateTime.now(), 1L);
        } else {
            if (isToManyRequests(counter, minutesActive)) {
                return false;
            }
            updateRequest(ip, lastDate, counter + 1);
        }

        return true;
    }

    private long calculateActiveTime(LocalDateTime lastDate) {
        if (lastDate == null) {
            return 0;
        }
        return MINUTES.between(lastDate, LocalDateTime.now());
    }

    private boolean isRequestNew(Long counter, LocalDateTime lastDate, long minutesActive) {
        return counter == null || lastDate == null || minutesActive > MINUTES_LIMIT_VALUE;
    }

    private boolean isToManyRequests(long counter, long minutesActive) {
        return counter > THRESHOLD_VALUE && minutesActive < MINUTES_LIMIT_VALUE;
    }

    private void updateRequest(String ip, LocalDateTime lastDate, Long counter) {
        lastRequestDateMap.put(ip, lastDate);
        amountOfRequestsMap.put(ip, counter);
    }

}
