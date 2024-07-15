package com.onevoker.timetracker.controllers.filters;

import com.onevoker.timetracker.security.services.jwt.JwtDecoderService;
import com.onevoker.timetracker.security.services.jwt.JwtToPrincipalConverterService;
import com.onevoker.timetracker.security.userPrincipal.UserPrincipalAuthenticationToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtDecoderService jwtDecoderService;
    private final JwtToPrincipalConverterService converter;

    private static final String AUTH_HEADER = "Authorization";
    private static final String AUTH_TOKEN_TYPE = "Bearer";
    private static final int START_TOKEN_INDEX = 7;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        extractTokenFromRequest(request)
                .map(jwtDecoderService::decode)
                .map(converter::convert)
                .map(UserPrincipalAuthenticationToken::new)
                .ifPresent(auth -> SecurityContextHolder.getContext().setAuthentication(auth));
        filterChain.doFilter(request, response);
    }

    private Optional<String> extractTokenFromRequest(HttpServletRequest request) {
        String token = request.getHeader(AUTH_HEADER);

        if (StringUtils.hasText(token) && token.startsWith(AUTH_TOKEN_TYPE)) {
            return Optional.of(token.substring(START_TOKEN_INDEX));
        }

        return Optional.empty();
    }
}
