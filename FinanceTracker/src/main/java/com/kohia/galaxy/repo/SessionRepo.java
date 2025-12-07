package com.kohia.galaxy.repo;

import com.kohia.galaxy.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SessionRepo extends JpaRepository<UserSession, String> {
    Optional<UserSession> findBySessionId(Long sessionId);

    UserSession findByCsrfToken(String csrfTokenValue);
}
