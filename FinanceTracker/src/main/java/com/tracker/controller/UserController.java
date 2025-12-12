package com.tracker.controller;


import com.tracker.entity.User;
import com.tracker.entity.UserSession;
import com.tracker.error.ReusableConstants;
import com.tracker.error.ErrorConstants;
import com.tracker.error.FinTrackerException;
import com.tracker.pojo.SQLConstraintErrorConstant;
import com.tracker.repo.UserRepo;
import com.tracker.repo.UserSessionRepo;
import com.tracker.service.SessionService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import net.mguenther.idem.flake.Flake64L;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;



import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired private UserRepo userRepo;
    @Autowired private Flake64L idGenerator;
    @Autowired private UserSessionRepo sessionRepo;
    @Autowired private DSLContext jooqContext;
    @Autowired  private SessionService sessionService;

    @GetMapping("/health")
    private void accountHealth(HttpServletResponse response) {
        response.setStatus(HttpStatus.OK.value());
    }

    @PostMapping("/signUp")
    private ResponseEntity<?> signUp(
            @RequestParam("email")String email,
            @RequestParam("password")String password,
            @RequestParam("name")String name
    ){
        User user = new User(idGenerator.nextId(),name,email,hashPassword(password),true, LocalDateTime.now(ZoneOffset.UTC));
        try{
            userRepo.save(user);

            // populating demo data for category
            jooqContext.insertInto(table(ReusableConstants.CATEGORY_ENTITY))
                    .columns(
                            field(ReusableConstants.CATEGORY_ID),
                            field(ReusableConstants.USER_ID),
                            field(ReusableConstants.NAME),
                            field(ReusableConstants.DESCRIPTION),
                            field(ReusableConstants.CREATED_TIME)
                    )
                    .values(
                        idGenerator.nextId(),
                        user.getUserId(),
                        "Food",
                        "Fruits and Vegetables",
                        LocalDateTime.now(ZoneOffset.UTC)
                    )
                    .execute();

            jooqContext.insertInto(table(ReusableConstants.CATEGORY_ENTITY))
                    .columns(
                            field(ReusableConstants.CATEGORY_ID),
                            field(ReusableConstants.USER_ID),
                            field(ReusableConstants.NAME),
                            field(ReusableConstants.DESCRIPTION),
                            field(ReusableConstants.CREATED_TIME)
                    )
                    .values(
                            idGenerator.nextId(),
                            user.getUserId(),
                            "Shopping",
                            "Clothing, Online orders",
                            LocalDateTime.now(ZoneOffset.UTC)
                    )
                    .execute();

            jooqContext.insertInto(table(ReusableConstants.CATEGORY_ENTITY))
                    .columns(
                            field(ReusableConstants.CATEGORY_ID),
                            field(ReusableConstants.USER_ID),
                            field(ReusableConstants.NAME),
                            field(ReusableConstants.DESCRIPTION),
                            field(ReusableConstants.CREATED_TIME)
                    )
                    .values(
                            idGenerator.nextId(),
                            user.getUserId(),
                            "Groceries",
                            "Supermarket, every essentials",
                            LocalDateTime.now(ZoneOffset.UTC)
                    )
                    .execute();

            jooqContext.insertInto(table(ReusableConstants.CATEGORY_ENTITY))
                    .columns(
                            field(ReusableConstants.CATEGORY_ID),
                            field(ReusableConstants.USER_ID),
                            field(ReusableConstants.NAME),
                            field(ReusableConstants.DESCRIPTION),
                            field(ReusableConstants.CREATED_TIME)
                    )
                    .values(
                            idGenerator.nextId(),
                            user.getUserId(),
                            "Outing",
                            "Restaurant, cafe, online food",
                            LocalDateTime.now(ZoneOffset.UTC)
                    )
                    .execute();

            //  populating demo data for payment methods
            jooqContext.insertInto(table(ReusableConstants.PAYMENT_METHOD_ENTITY))
                    .columns(
                            field(ReusableConstants.PAYMENT_METHOD_ID),
                            field(ReusableConstants.USER_ID),
                            field(ReusableConstants.TYPE),
                            field(ReusableConstants.CREATED_TIME)
                    )
                    .values(
                            idGenerator.nextId(),
                            user.getUserId(),
                            "CASH",
                            LocalDateTime.now(ZoneOffset.UTC)
                    )
                    .execute();

            return ResponseEntity.ok(new SignUpResponse(true));
        } catch (Exception ex){
            if(ex.getMessage().contains(SQLConstraintErrorConstant.USER_EMAIL_UK.getKey())){
                throw new FinTrackerException(ErrorConstants.DUPLICATE);
            }
        }
        return ResponseEntity.ok(new SignUpResponse(false));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            HttpServletResponse response
    ) {
        User user = userRepo.findByEmail(email).orElseThrow(() -> new FinTrackerException(ErrorConstants.INVALID_CREDENTIALS));

        if (!BCrypt.checkpw(password, user.getPassword())) {
            throw new FinTrackerException(ErrorConstants.INVALID_CREDENTIALS);
        }

        UserSession session = sessionService.createSession(user.getUserId());

        Cookie sessionCookie = new Cookie("SESSION_ID", session.getSessionId().toString());
        sessionCookie.setPath("/");
        sessionCookie.setHttpOnly(true);
        sessionCookie.setSecure(false);
        response.addCookie(sessionCookie);

        Cookie csrfCookie = new Cookie("X-CSRF-TOKEN", session.getCsrfToken());
        csrfCookie.setPath("/");
        csrfCookie.setHttpOnly(false);
        csrfCookie.setSecure(false);
        response.addCookie(csrfCookie);

        return ResponseEntity.ok("Login successful, cookies set.");
    }


    private String hashPassword(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
    }
}



