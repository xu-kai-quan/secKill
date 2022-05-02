package org.example.access;

import org.example.entity.SecKillUser;

public class UserContext {
    private static ThreadLocal<SecKillUser> userHolder = new ThreadLocal<SecKillUser>();

    public static void setUser(SecKillUser user) {
        userHolder.set(user);
    }
    public static SecKillUser getUser(){
        return userHolder.get();
    }
}
