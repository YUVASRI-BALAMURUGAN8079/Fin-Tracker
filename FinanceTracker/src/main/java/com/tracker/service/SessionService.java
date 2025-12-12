package com.tracker.service;

import com.tracker.entity.UserSession;
import com.tracker.error.ErrorConstants;
import com.tracker.error.FinTrackerException;
import com.tracker.repo.UserSessionRepo;
import net.mguenther.idem.flake.Flake64L;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
public class SessionService {
    @Autowired private UserSessionRepo userSessionRepository;
    @Autowired private Flake64L idGenerator;

    public UserSession createSession(Long userId) {
        UserSession session = new UserSession();
        session.setSessionId(idGenerator.nextId());
        session.setUserId(userId);
        session.setCsrfToken(UUID.randomUUID().toString());
        session.setCreatedTime(LocalDateTime.now(ZoneOffset.UTC));
        session.setExpiresAt(LocalDateTime.now(ZoneOffset.UTC).plusHours(24));

        return userSessionRepository.save(session);
    }

    public UserSession validateSession(String sessionId) {
        if (sessionId == null) return null;
        long sid= Long.parseLong(sessionId);
        UserSession session = userSessionRepository.findBySessionId(sid).orElse(null);
        if (session == null) return null;

        if (session.getExpiresAt().isBefore(LocalDateTime.now(ZoneOffset.UTC))) {
            userSessionRepository.deleteBySessionId(sid);
            throw new FinTrackerException(ErrorConstants.SESSION_EXPIRED);
        }

        return session;
    }

}
