package com.kohia.galaxy.service;

import com.kohia.galaxy.context.UserContext;
import com.kohia.galaxy.error.ErrorConstants;
import com.kohia.galaxy.error.FinTrackerException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserService {

    public Long getCurrentUserId() {
        Long userId = UserContext.getUserId();
        return Objects.requireNonNull(userId, () -> { throw new FinTrackerException(ErrorConstants.ACCESS_DENIED); });
    }

}
