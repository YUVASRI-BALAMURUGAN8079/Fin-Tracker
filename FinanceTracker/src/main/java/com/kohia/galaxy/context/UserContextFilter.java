package com.kohia.galaxy.context;

import com.kohia.galaxy.configuration.CookieUtil;
import com.kohia.galaxy.entity.User;
import com.kohia.galaxy.error.ErrorConstants;
import com.kohia.galaxy.error.FinTrackerException;
import com.kohia.galaxy.repo.UserRepo;
import com.kohia.galaxy.service.SessionService;
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
