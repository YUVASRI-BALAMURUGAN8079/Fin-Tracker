package com.tracker.resolver;

import com.tracker.entity.User;
import com.tracker.error.ErrorConstants;
import com.tracker.error.FinTrackerException;
import com.tracker.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class UserResolver {

    private final UserRepo userRepo;

    @Autowired
    public UserResolver(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @QueryMapping
    public User getUser(@Argument Long userId) {
        return userRepo.findById(userId)
                .orElseThrow(() -> new FinTrackerException(ErrorConstants.INVALID_INPUT));
    }
}