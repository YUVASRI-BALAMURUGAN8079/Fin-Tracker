package com.tracker.context;

import com.tracker.configuration.CookieUtil;
import com.tracker.entity.User;
import com.tracker.error.ErrorConstants;
import com.tracker.error.FinTrackerException;
import com.tracker.repo.UserRepo;
import com.tracker.service.SessionService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class UserContextFilter extends OncePerRequestFilter {

    private final SessionService sessionService;
    private final UserRepo userRepo;

    @Autowired
    public UserContextFilter(SessionService sessionService, UserRepo userRepo){
        this.sessionService = sessionService;
        this.userRepo = userRepo;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String sessionId = CookieUtil.getSessionId(request);

            if (sessionId != null) {
                Long userId = sessionService.getUserIdFromSession(sessionId);
                if (userId != null) {
                    User user = userRepo.findById(userId)
                            .orElseThrow(() -> new FinTrackerException(ErrorConstants.ACCESS_DENIED));

                    UserContext.setUserId(userId);
                    UserContext.setUserTimeZone(user.getTimeZone());
                }
            }

            filterChain.doFilter(request, response);
        } finally {
            UserContext.clear();
        }
    }
}
