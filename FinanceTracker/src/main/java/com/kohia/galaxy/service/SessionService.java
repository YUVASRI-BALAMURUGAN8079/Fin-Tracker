package com.kohia.galaxy.service;

import com.kohia.galaxy.entity.UserSession;
import com.kohia.galaxy.repo.UserSessionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

@Service
public class SessionService {
    @Autowired
    private UserSessionRepo userSessionRepository;

    public Long getUserIdFromSession(String sessionId) {
        if (sessionId == null) return null;

        Long sessionIdLong =  Long.parseLong(sessionId);

        Optional<UserSession> sessionOpt = userSessionRepository.findBySessionId(sessionIdLong);

        if (sessionOpt.isEmpty()) return null;

        UserSession session = sessionOpt.get();


        if (session.getExpiresAt().isBefore(LocalDateTime.now(ZoneOffset.UTC))) {
            userSessionRepository.deleteBySessionId(sessionIdLong);
            return null;
        }

        return session.getUserId();
    }

    public String createSession(Long userId) {
        long sessionId = generateSessionId();
        UserSession session = new UserSession();
        session.setSessionId(sessionId);
        session.setUserId(userId);
        session.setCsrfToken(generateCsrfToken());
        session.setCreatedTime(LocalDateTime.now(ZoneOffset.UTC));
        session.setExpiresAt(LocalDateTime.now(ZoneOffset.UTC).plusHours(24));

        userSessionRepository.save(session);

        return String.valueOf(sessionId);
    }

    public void deleteSession(String sessionId) {
        try {
            Long id = Long.parseLong(sessionId);
            userSessionRepository.deleteBySessionId(id);
        } catch (NumberFormatException ignored) {
        }
    }

    private long generateSessionId() {
        return Math.abs(new java.security.SecureRandom().nextLong());
    }

    private String generateCsrfToken() {
        return java.util.UUID.randomUUID().toString();
    }
}
