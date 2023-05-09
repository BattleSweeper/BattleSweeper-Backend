package dev.battlesweeper.backend.auth;

import dev.battlesweeper.backend.utils.StringUtils;

import java.util.HashSet;
import java.util.Set;

public class AuthTokenManager {

    public static final int TOKEN_LENGTH = 36;

    private final Set<Integer> validTokenHash = new HashSet<>();

    public boolean isTokenValid(String token) {
        return validTokenHash.contains(token.hashCode());
    }

    public String createAndRegisterToken() {
        String token = createAuthToken();
        validTokenHash.add(token.hashCode());
        return token;
    }

    private String createAuthToken() {
        return StringUtils.randomAlphanumeric(TOKEN_LENGTH);
    }

    private AuthTokenManager() {}
    private static AuthTokenManager INSTANCE;
    public static AuthTokenManager getInstance() {
        if (INSTANCE == null)
            INSTANCE = new AuthTokenManager();

        return INSTANCE;
    }
}
