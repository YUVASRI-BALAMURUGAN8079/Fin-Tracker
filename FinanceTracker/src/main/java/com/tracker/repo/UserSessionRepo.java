package com.tracker.repo;

import com.tracker.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserSessionRepo extends JpaRepository<UserSession, Long> {
    Optional<UserSession> findBySessionId(Long sessionId);
    void deleteBySessionId(Long sessionId);
    UserSession findByCsrfToken(String csrfTokenValue);
}
