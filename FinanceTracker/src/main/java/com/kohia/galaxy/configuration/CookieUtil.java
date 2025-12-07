package com.kohia.galaxy.configuration;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

public class CookieUtil {
    public static String getSessionId(HttpServletRequest request) {
        if (request.getCookies() == null) return null;

        for (Cookie c : request.getCookies()) {
            if ("SESSION_ID".equals(c.getName())) {
                return c.getValue();
            }
        }
        return null;
    }
}
