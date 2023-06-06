package com.es.phoneshop.web.filter;

import com.es.phoneshop.security.DosProtectionService;
import com.es.phoneshop.security.impl.DosProtectionServiceImpl;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class DosProtectionFilter implements Filter {
    private static final int SC_TOO_MANY_REQUESTS = 429;
    private static final String REQUEST_HEADER_X_FORWARDED_FOR = "X-FORWARDED-FOR";
    private DosProtectionService dosProtectionService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        dosProtectionService = DosProtectionServiceImpl.getInstance();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String ip = getClientIP((HttpServletRequest) request);

        if (dosProtectionService.isAllowed(ip)) {
            chain.doFilter(request, response);
        } else {
            ((HttpServletResponse) response).setStatus(SC_TOO_MANY_REQUESTS);
        }
    }

    private String getClientIP(HttpServletRequest request) {
        String ip =  request.getHeader(REQUEST_HEADER_X_FORWARDED_FOR);

        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

}
