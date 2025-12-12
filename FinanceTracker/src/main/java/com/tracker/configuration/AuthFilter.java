package com.tracker.configuration;

import com.tracker.context.UserContext;
import com.tracker.entity.User;
import com.tracker.entity.UserSession;
import com.tracker.error.ErrorConstants;
import com.tracker.error.FinTrackerException;
import com.tracker.repo.UserRepo;
import com.tracker.service.SessionService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AuthFilter extends OncePerRequestFilter {
    @Autowired private SessionService sessionService;
    @Autowired private UserRepo userRepo;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            Cookie[] cookies = request.getCookies();
            String sessionId = null;

            if (cookies != null) {
                for (Cookie c : cookies) {
                    if ("SESSION_ID".equals(c.getName())) {
                        sessionId = c.getValue();
                        break;
                    }
                }
            }

            UserSession session = sessionService.validateSession(sessionId);

            if (session != null) {
                Long userId = session.getUserId();

                User user = userRepo.findById(userId).orElseThrow(() -> new FinTrackerException(ErrorConstants.ACCESS_DENIED));

                UserContext.setUserId(userId);
                UserContext.setUserTimeZone(user.getTimeZone());


                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userId, null, null);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }

            filterChain.doFilter(request, response);

        } finally {
            UserContext.clear();
        }
    }
}
