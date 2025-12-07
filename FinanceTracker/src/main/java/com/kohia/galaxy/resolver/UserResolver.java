package com.kohia.galaxy.resolver;

import com.kohia.galaxy.entity.User;
import com.kohia.galaxy.error.ErrorConstants;
import com.kohia.galaxy.error.FinTrackerException;
import com.kohia.galaxy.repo.UserRepo;
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