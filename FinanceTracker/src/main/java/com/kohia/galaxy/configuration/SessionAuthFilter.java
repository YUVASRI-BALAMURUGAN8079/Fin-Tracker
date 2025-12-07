package com.kohia.galaxy.configuration;

import com.kohia.galaxy.GalaxyApplication;
import com.kohia.galaxy.entity.UserSession;
import com.kohia.galaxy.error.ErrorConstants;
import com.kohia.galaxy.error.FinTrackerException;
import com.kohia.galaxy.repo.SessionRepo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@Component
public class SessionAuthFilter extends OncePerRequestFilter {

    private final SessionRepo sessionRepo;

    public SessionAuthFilter(@Lazy SessionRepo sessionRepo) {
        this.sessionRepo = sessionRepo;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            Optional<Cookie> csrfCookie = Arrays.stream(cookies)
                    .filter(c -> "X-CSRF-TOKEN".equals(c.getName()))
                    .findFirst();

            if (csrfCookie.isPresent()) {
                String csrfTokenValue = csrfCookie.get().getValue();
                UserSession session = sessionRepo.findByCsrfToken(csrfTokenValue);

                if (session.isExpired()) {
                    throw new FinTrackerException(ErrorConstants.SESSION_EXPIRED);
                }

                if (session.getExpiryTime().isAfter(java.time.LocalDateTime.now())) {
                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(
                                    session.getUserId(),
                                    null,
                                    null
                            );
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } else throw new FinTrackerException(ErrorConstants.ACCESS_DENIED);
        } else throw new FinTrackerException(ErrorConstants.ACCESS_DENIED);

        filterChain.doFilter(request, response);
    }
}