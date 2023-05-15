package dev.battlesweeper.backend.auth;

import dev.battlesweeper.backend.db.UserService;
import dev.battlesweeper.backend.objects.user.AnonymousUser;
import dev.battlesweeper.backend.objects.user.RegisteredUser;
import dev.battlesweeper.backend.objects.user.User;
import dev.battlesweeper.backend.rest.body.AuthRequestBody;
import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Service
public class AuthTokenManager {

    private static final long ACCESS_EXPIRE_MILLIS       = 1000 * 60 * 30; // 30분
    private static final long ACCESS_EXPIRE_MILLIS_ANON  = 1000 * 60 * 10; // 10분
    private static final long REFRESH_EXPIRE_MILLIS      = 1000 * 60 * 60 * 12; // 24시간

    public static final int TOKEN_VALID   = 0;
    public static final int TOKEN_INVALID = 1;
    public static final int TOKEN_EXPIRED = 2;

    private UserService userService;
    private final Key secret;

    @Autowired
    public void setUserService(UserService service) {
        this.userService = service;
    }

    public int validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secret).build().parseClaimsJws(token);
            return TOKEN_VALID;
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
            return TOKEN_EXPIRED;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);;
        }  catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        }
        return TOKEN_INVALID;
    }

    public boolean isTokenValid(String token) {
        return validateToken(token) == TOKEN_VALID;
    }

    public Optional<TokenInfo> validateRefreshToken(String refreshToken) {
        try {
            var claims = retrieveClaims(refreshToken);
            if (claims.getExpiration().after(new Date())) {
                var userId = claims.get("uid", Long.class);
                if (userId == null)
                    return Optional.empty();
                if (userId < 0) { // Anonymous user token
                    var name = claims.get("name", String.class);
                    return Optional.of(createAuthToken(new AnonymousUser(userId, name)));
                }

                var user = userService.findById(userId);
                return user.map(this::createAuthToken);
            }
        } catch (Exception e) {
            log.info("Failure", e);
            return Optional.empty();
        }
        return Optional.empty();
    }

    public Optional<User> getUserFromToken(String token) {
        var claims = retrieveClaims(token);
        Long userId = claims.get("uid", Long.class);
        if (userId == null)
            return Optional.empty();

        Optional<User> user;
        switch (claims.get("type", Integer.class)) {
            case AuthRequestBody.TYPE_ANONYMOUS -> {
                var anonymousUser = AnonymousUser.builder()
                        .id(userId)
                        .name(claims.get("name", String.class))
                        .build();
                user = Optional.of(anonymousUser);
            }
            case AuthRequestBody.TYPE_REGISTERED ->
                    user = userService.findById(userId).map(u -> u);
            default ->
                    user = Optional.empty();
        }

        return user;
    }

    private Claims retrieveClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secret)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    //public boolean invalidate(String token) {
    //    return validTokenHash.remove(token.hashCode());
    //}

    public TokenInfo createAuthToken(User user) {
        long now = System.currentTimeMillis();

        var accessToken = issueAccessToken(user);

        // Refresh Token 생성
        String refreshToken = Jwts.builder()
                .setExpiration(new Date(now + REFRESH_EXPIRE_MILLIS))
                .claim("uid", user.getId())
                .claim("name", user.getName())
                .signWith(secret, SignatureAlgorithm.HS256)
                .compact();

        return TokenInfo.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private String issueAccessToken(User user) {
        long now = System.currentTimeMillis();

        int userType;
        if (user instanceof AnonymousUser)
            userType = AuthRequestBody.TYPE_ANONYMOUS;
        else if (user instanceof RegisteredUser)
            userType = AuthRequestBody.TYPE_REGISTERED;
        else
            throw new IllegalArgumentException("Unknown user type: " + user.getClass().getName());

        long expiresIn = ACCESS_EXPIRE_MILLIS;
        if (userType == AuthRequestBody.TYPE_ANONYMOUS) {
            expiresIn = ACCESS_EXPIRE_MILLIS_ANON;
        }

        Date accessTokenExpiresIn = new Date(now + expiresIn);

        return Jwts.builder()
                .setSubject(user.getName())
                .claim("uid", user.getId())
                .claim("name", user.getName())
                .claim("type", userType)
                .setExpiration(accessTokenExpiresIn)
                .signWith(secret, SignatureAlgorithm.HS256)
                .compact();
    }

    private AuthTokenManager() {
        if (INSTANCE == null)
            INSTANCE = this;
        var rawKey = Dotenv.load().get("TOKEN_SECRET_KEY");
        secret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(rawKey));
    }
    private static AuthTokenManager INSTANCE;
    public static AuthTokenManager getInstance() {
        if (INSTANCE == null)
            new AuthTokenManager();

        return INSTANCE;
    }
}
