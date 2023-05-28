package dev.battlesweeper.backend.rest;

import dev.battlesweeper.backend.auth.AnonymousUserManager;
import dev.battlesweeper.backend.auth.AuthTokenManager;
import dev.battlesweeper.backend.auth.cypher.SHA256;
import dev.battlesweeper.backend.db.UserService;
import dev.battlesweeper.backend.objects.user.AnonymousUser;
import dev.battlesweeper.backend.rest.body.AuthRequestBody;
import dev.battlesweeper.backend.rest.body.RefreshRequestBody;
import dev.battlesweeper.backend.socket.WebSocketConfig;
import dev.battlesweeper.backend.objects.packet.ResultPacket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.NoSuchAlgorithmException;

@RestController @Slf4j
public class AuthHandler {

    private final UserService userService;

    public AuthHandler(UserService service) {
        userService = service;
    }

    @PostMapping(WebSocketConfig.ROUTE_PREFIX + "/auth")
    public ResultPacket requestLogin(@RequestBody AuthRequestBody form) {
        switch (form.type) {
            case AuthRequestBody.TYPE_ANONYMOUS -> {
                if (AnonymousUserManager.getInstance().nameExists(form.info.username))
                    return new ResultPacket(HttpStatus.CONFLICT, Message.NAME_EXISTS);
                var user = AnonymousUser.builder()
                        .id(AnonymousUserManager.getInstance().getIncrementalID())
                        .name(form.info.username)
                        .build();
                AnonymousUserManager.getInstance().registerUser(user);
                log.info("New anonymous user " + user.getName() + "(" + user.getId() + ")");

                var accessToken = AuthTokenManager.getInstance().createAuthToken(user);
                return new ResultPacket(ResultPacket.RESULT_OK, accessToken.toJsonString());
            }
            case AuthRequestBody.TYPE_REGISTERED -> {
                var query = userService.findByEmail(form.info.email);
                if (query.isEmpty())
                    return new ResultPacket(HttpStatus.NOT_FOUND, "USER_NOT_FOUND");

                var user = query.get();
                String pwHash;
                try {
                    pwHash = SHA256.encrypt(form.info.password);
                } catch (NoSuchAlgorithmException e) {
                    return new ResultPacket(HttpStatus.INTERNAL_SERVER_ERROR, Message.ENCRYPT_FAILURE);
                }

                if (!user.getPwHash().equals(pwHash))
                    return new ResultPacket(ResultPacket.RESULT_FAILURE, Message.PASSWORD_MISMATCH);

                var accessToken = AuthTokenManager.getInstance().createAuthToken(user);

                return new ResultPacket(ResultPacket.RESULT_OK, accessToken.toJsonString());
            }
        }

        return new ResultPacket(HttpStatus.CONFLICT, Message.NAME_EXISTS);
    }

    @PostMapping(WebSocketConfig.ROUTE_PREFIX + "/auth/refresh")
    public ResultPacket requestRefreshToken(@RequestBody RefreshRequestBody form) {
        if (form.token == null)
            return new ResultPacket(HttpStatus.NOT_ACCEPTABLE, "TOKEN_NOT_PROVIDED");

        //if (!AuthTokenManager.getInstance().isTokenValid(form.token))
        //    return new ResultPacket(HttpStatus.NOT_ACCEPTABLE, Message.TOKEN_INVALID);

        System.out.println(form.token);

        var newToken = AuthTokenManager.getInstance().validateRefreshToken(form.token);
        return newToken
                .map(tokenInfo -> new ResultPacket(HttpStatus.OK, tokenInfo.toJsonString()))
                .orElseGet(() -> new ResultPacket(HttpStatus.NOT_ACCEPTABLE, Message.TOKEN_INVALID));
    }
}
