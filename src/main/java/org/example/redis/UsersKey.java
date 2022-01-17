package org.example.redis;

public class UsersKey extends BasePrefix {

    public static final int TOKEN_EXPIRE = 3600 * 24 * 2;

    public UsersKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static UsersKey token = new UsersKey(TOKEN_EXPIRE, "tk");
    public static UsersKey getById = new UsersKey(0,"id");
}
