package com.es.phoneshop.web.filter;

import com.es.phoneshop.security.DosProtectionService;
import com.es.phoneshop.security.impl.DosProtectionServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DosProtectionFilterTest {
    @Mock
    private FilterChain filterChain;
    @Mock
    private HttpServletResponse response;
    @Mock
    private HttpServletRequest request;
    @Mock
    private DosProtectionService dosProtectionService;
    @InjectMocks
    private final DosProtectionFilter filter = new DosProtectionFilter();

    @Before
    public void setup() throws NoSuchFieldException, IllegalAccessException {
        Field dosProtectionServiceField = filter.getClass().getDeclaredField("dosProtectionService");
        dosProtectionServiceField.setAccessible(true);
        dosProtectionServiceField.set(filter, dosProtectionService);
    }

    @Test
    public void init_ShouldCreateValidInstanceOfClass()
            throws NoSuchFieldException, IllegalAccessException, ServletException {
        // given
        FilterConfig config = mock(FilterConfig.class);
        filter.init(config);
        DosProtectionService dosProtectionService = DosProtectionServiceImpl.getInstance();

        // when
        Field dosProtectionServiceField = DosProtectionFilter.class.getDeclaredField("dosProtectionService");
        dosProtectionServiceField.setAccessible(true);

        // then
        assertEquals(dosProtectionService.getClass(), dosProtectionServiceField.get(filter).getClass());
    }

    @Test
    public void doFilter_ShouldAllowRequest() throws ServletException, IOException {
        // given
        when(dosProtectionService.isAllowed(any())).thenReturn(true);

        // when
        filter.doFilter(request, response, filterChain);

        // then
        verify(filterChain, only()).doFilter(request, response);
    }

    @Test
    public void doFilter_ShouldSetToManyRequestsStatus() throws ServletException, IOException {
        // given
        final int EXPECTED_STATUS = 429;
        when(dosProtectionService.isAllowed(any())).thenReturn(false);

        // when
        filter.doFilter(request, response, filterChain);

        // then
        verify(response).setStatus(EXPECTED_STATUS);
    }

}
