package com.icube.sim.tichu.common;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class LoggingFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(
            HttpServletRequest request, @NonNull HttpServletResponse response, FilterChain filterChain
    ) throws ServletException, IOException {
        System.out.println(request.getMethod() + " " + request.getRequestURI());
        filterChain.doFilter(request, response);
        System.out.println(response.getStatus());
    }
}
