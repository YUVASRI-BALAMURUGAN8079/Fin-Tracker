package com.kohia.galaxy.controller;


import com.kohia.galaxy.entity.User;
import com.kohia.galaxy.entity.UserSession;
import com.kohia.galaxy.error.AppConstants;
import com.kohia.galaxy.error.ErrorConstants;
import com.kohia.galaxy.error.FinTrackerException;
import com.kohia.galaxy.pojo.SQLConstraint;
import com.kohia.galaxy.repo.SessionRepo;
import com.kohia.galaxy.repo.UserRepo;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import net.mguenther.idem.flake.Flake64L;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

@RestController
@RequestMapping("/users")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserRepo userRepo;
    private final Flake64L idGenerator;
    private final SessionRepo sessionRepo;
    private final DSLContext jooqContext;
    @Autowired
    public UserController(UserRepo userRepo,Flake64L idGenerator,SessionRepo sessionRepo,DSLContext jooqContext) {
        this.userRepo = userRepo;
        this.idGenerator = idGenerator;
        this.sessionRepo = sessionRepo;
        this.jooqContext = jooqContext;
    }
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
            jooqContext.insertInto(table(AppConstants.CATEGORY_ENTITY))
                    .columns(
                            field(AppConstants.CATEGORY_ID),
                            field(AppConstants.USER_ID),
                            field(AppConstants.NAME),
                            field(AppConstants.DESCRIPTION),
                            field(AppConstants.CREATED_TIME)
                    )
                    .values(
                        idGenerator.nextId(),
                        user.getUserId(),
                        "Food",
                        "Fruits and Vegetables",
                        LocalDateTime.now(ZoneOffset.UTC)
                    )
                    .execute();

            jooqContext.insertInto(table(AppConstants.CATEGORY_ENTITY))
                    .columns(
                            field(AppConstants.CATEGORY_ID),
                            field(AppConstants.USER_ID),
                            field(AppConstants.NAME),
                            field(AppConstants.DESCRIPTION),
                            field(AppConstants.CREATED_TIME)
                    )
                    .values(
                            idGenerator.nextId(),
                            user.getUserId(),
                            "Shopping",
                            "Clothing, Online orders",
                            LocalDateTime.now(ZoneOffset.UTC)
                    )
                    .execute();

            jooqContext.insertInto(table(AppConstants.CATEGORY_ENTITY))
                    .columns(
                            field(AppConstants.CATEGORY_ID),
                            field(AppConstants.USER_ID),
                            field(AppConstants.NAME),
                            field(AppConstants.DESCRIPTION),
                            field(AppConstants.CREATED_TIME)
                    )
                    .values(
                            idGenerator.nextId(),
                            user.getUserId(),
                            "Groceries",
                            "Supermarket, every essentials",
                            LocalDateTime.now(ZoneOffset.UTC)
                    )
                    .execute();

            jooqContext.insertInto(table(AppConstants.CATEGORY_ENTITY))
                    .columns(
                            field(AppConstants.CATEGORY_ID),
                            field(AppConstants.USER_ID),
                            field(AppConstants.NAME),
                            field(AppConstants.DESCRIPTION),
                            field(AppConstants.CREATED_TIME)
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
            jooqContext.insertInto(table(AppConstants.PAYMENT_METHOD_ENTITY))
                    .columns(
                            field(AppConstants.PAYMENT_METHOD_ID),
                            field(AppConstants.USER_ID),
                            field(AppConstants.TYPE),
                            field(AppConstants.CREATED_TIME)
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
            if(ex.getMessage().contains(SQLConstraint.USER_EMAIL_UK.getKey())){
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
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new FinTrackerException(ErrorConstants.INVALID_CREDENTIALS));

        if (!BCrypt.checkpw(password, user.getPassword())) {
            throw new FinTrackerException(ErrorConstants.INVALID_CREDENTIALS);
        }

        String csrfToken = UUID.randomUUID().toString();

        UserSession session = new UserSession(
                idGenerator.nextId(),
                user.getUserId(),
                csrfToken,
                LocalDateTime.now(ZoneOffset.UTC),
                LocalDateTime.now(ZoneOffset.UTC).plusHours(12)
        );
        sessionRepo.save(session);

        Cookie sessionCookie = new Cookie("SESSION_ID", session.getSessionId().toString());
        sessionCookie.setPath("/");
        sessionCookie.setHttpOnly(true);
        sessionCookie.setSecure(false);
        response.addCookie(sessionCookie);

        Cookie csrfCookie = new Cookie("X-CSRF-TOKEN", csrfToken);
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



