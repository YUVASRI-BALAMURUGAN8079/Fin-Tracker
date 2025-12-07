package com.tracker.context;

public class UserContext {

    private static final ThreadLocal<Long> currentUser = new ThreadLocal<>();
    private static final ThreadLocal<String> currentUserTimeZone = new ThreadLocal<>();

    public static void setUserId(Long id) {
        currentUser.set(id);
    }

    public static void setUserTimeZone(String timeZone) { currentUserTimeZone.set(timeZone); }

    public static Long getUserId() { return currentUser.get(); }

    public static String getTimeZone() {return currentUserTimeZone.get(); }

    public static void clear() {
        currentUser.remove();
    }
}
