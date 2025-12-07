package com.tracker.service;

import com.tracker.context.UserContext;
import com.tracker.error.ErrorConstants;
import com.tracker.error.FinTrackerException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserService {

    public Long getCurrentUserId() {
        Long userId = UserContext.getUserId();
        return Objects.requireNonNull(userId, () -> { throw new FinTrackerException(ErrorConstants.ACCESS_DENIED); });
    }

}
