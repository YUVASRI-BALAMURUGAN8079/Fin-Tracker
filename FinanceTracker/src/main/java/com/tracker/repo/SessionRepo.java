package com.tracker.repo;

import com.tracker.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SessionRepo extends JpaRepository<UserSession, String> {
    Optional<UserSession> findBySessionId(Long sessionId);

    UserSession findByCsrfToken(String csrfTokenValue);
}
