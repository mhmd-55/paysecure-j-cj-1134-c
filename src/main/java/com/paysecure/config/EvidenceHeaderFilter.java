package com.paysecure.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Adds the evidence tag to every response header so it shows up in
 * Postman/curl/DevTools/Burp/ZAP captures without you having to add it manually each time.
 */
@Component
public class EvidenceHeaderFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        response.setHeader("X-Evidence-Tag", "CJ-1134-C");
        chain.doFilter(request, response);
    }
}
