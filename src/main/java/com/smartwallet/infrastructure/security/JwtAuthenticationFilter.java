package com.smartwallet.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final RedisTokenBlacklistService blacklistService;

    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider, RedisTokenBlacklistService blacklistService) {
        this.tokenProvider = tokenProvider;
        this.blacklistService = blacklistService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);


            if (tokenProvider.validateToken(token) && !blacklistService.isBlacklisted(token)) {
                Optional<UUID> userIdOpt = tokenProvider.getUserIdFromToken(token);

                if (userIdOpt.isPresent() && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UUID userId = userIdOpt.get();

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userId, null, Collections.emptyList()
                    );
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));


                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}