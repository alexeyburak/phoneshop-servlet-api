package com.es.phoneshop.security.impl;

import com.es.phoneshop.security.DosProtectionService;
import org.junit.Before;
import org.junit.Test;

import java.util.stream.IntStream;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DosProtectionServiceImplTest {
    private static final int THRESHOLD_VALUE = 20;
    private DosProtectionService dosProtectionService;

    @Before
    public void setUp() {
        dosProtectionService = DosProtectionServiceImpl.getInstance();
    }

    @Test
    public void testBlockedRequest() {
        // given
        final String ip = "1.1.1.1";
        IntStream.rangeClosed(0, THRESHOLD_VALUE)
                .forEach(i -> dosProtectionService.isAllowed(ip));

        // when
        boolean result = dosProtectionService.isAllowed(ip);

        // then
        assertFalse(result);
    }

    @Test
    public void testInactiveIp() {
        // given
        final String ip = "2.2.2.2";

        // when
        boolean result = dosProtectionService.isAllowed(ip);

        // then
        assertTrue(result);
    }

}
